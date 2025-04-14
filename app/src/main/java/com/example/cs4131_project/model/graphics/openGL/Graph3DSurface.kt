package com.example.cs4131_project.model.graphics.openGL

import android.opengl.GLES20
import android.opengl.Matrix
import android.renderscript.Matrix4f
import android.util.Log
import com.example.cs4131_project.model.graph.Equation3
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.utility.Point
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.ArrayList
import kotlin.math.min

class Graph3DSurface(
    val graphViewModel: Graph3ViewModel,
    val backgroundColorPoint: Point,
    darkTheme: Boolean,
    val equation: Equation3 = graphViewModel.equation.value,
    var quality: Float,
    otherScale: Float
) {
    private val lineColorPoint = if (darkTheme) Point(1.0, 1.0, 1.0) else Point(0.0, 0.0, 0.0)

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer
    private lateinit var normalBuffer: FloatBuffer

    val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    var scale = 1f

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        uniform mat4 uModelMatrix;
        
        attribute vec4 vPosition;
        attribute vec4 aColor;
        attribute vec3 aNormal;
        
        varying vec4 vColor;
        varying vec3 vNormal;
        varying vec3 vFragPos;
        varying vec4 vWorldPos;
        varying vec4 vOriginalPosition;
        
        void main() {
            vWorldPos = uModelMatrix * vPosition;
            vOriginalPosition = vPosition;
            gl_Position = uMVPMatrix * vPosition;
        
            vColor = aColor;
            vFragPos = vWorldPos.xyz;
            vNormal = mat3(uModelMatrix) * aNormal;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;

        varying vec4 vColor;
        varying vec3 vNormal;
        varying vec3 vFragPos;
        varying vec4 vWorldPos;
        varying vec4 vOriginalPosition;
        
        uniform float uScale;
        uniform vec3 uCameraPosObject;
        
        void main() {
            vec4 debugColor = vColor;
        
            vec4 scaledPos = vOriginalPosition * uScale;
            if (abs(scaledPos.x) > 1.0 || abs(scaledPos.y) > 1.0 || abs(scaledPos.z) > 1.0) {
                debugColor = vec4(0.0, 0.0, 0.0, 1.0);
                discard;
            }
        
            vec3 lightPos = vec3(0.0, 0.0, 10.0);
        
            vec3 norm = normalize(vNormal);
            vec3 lightDir = normalize(lightPos - vFragPos);
            vec3 viewDir = normalize(-vFragPos);
            vec3 halfDir = normalize(lightDir + viewDir);
        
            float diff = max(dot(-norm, lightDir), 0.0);
            float spec = pow(max(dot(-norm, halfDir), 0.0), 32.0) * 0.25;
        
            vec3 ambient = 0.2 * debugColor.rgb;
            vec3 diffuse = diff * debugColor.rgb;
            vec3 specular = spec * vec3(1.0);
        
            vec3 result = ambient + diffuse + specular;
        
            gl_FragColor = vec4(result, debugColor.a);
        }
    """

    private val program: Int
    private var vertexCount = 0

    init {
        generateSurfaceMesh(otherScale)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val error = GLES20.glGetProgramInfoLog(program)
            GLES20.glDeleteProgram(program)
            Log.e("OpenGL", error)
        }
    }

    private fun calculateNormal(p0: FloatArray, p1: FloatArray, p2: FloatArray): FloatArray {
        val u = floatArrayOf(
            p1[0] - p0[0],
            p1[1] - p0[1],
            p1[2] - p0[2]
        )
        val v = floatArrayOf(
            p2[0] - p0[0],
            p2[1] - p0[1],
            p2[2] - p0[2]
        )

        val normal = floatArrayOf(
            u[1] * v[2] - u[2] * v[1],
            u[2] * v[0] - u[0] * v[2],
            u[0] * v[1] - u[1] * v[0]
        )

        val length = Math.sqrt((normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]).toDouble()).toFloat()
        return if (length != 0f) {
            floatArrayOf(normal[0] / length, normal[1] / length, normal[2] / length)
        } else {
            floatArrayOf(0f, 1f, 0f)
        }
    }

    private lateinit var indexBuffer: ShortBuffer

    fun generateSurfaceMesh(otherScale: Float) {
        Log.e("rege", "💩")

        val vertexMap = mutableMapOf<String, Int>()
        val vertexList = mutableListOf<FloatArray>()
        val normalSums = mutableListOf<FloatArray>()
        val indices = mutableListOf<Short>()
        val color = floatArrayOf(0.1f, 0.6f, 1.0f, 1.0f)

        val power10 = Math.pow(10.0, graphViewModel.power10.toDouble()).toFloat()
        val stepX = graphViewModel.minorSpace.x.toFloat() * power10 * quality * 0.75f * otherScale / scale
        val stepZ = graphViewModel.minorSpace.z.toFloat() * power10 * quality * 0.75f * otherScale / scale
        val halfWidth = graphViewModel.size.x.toFloat() * otherScale / scale
        val halfDepth = graphViewModel.size.z.toFloat() * otherScale / scale

        Log.e("rege", "$halfWidth $halfDepth")

        val numStepsX = (halfWidth / stepX).toInt()
        val numStepsZ = (halfDepth / stepZ).toInt()

        fun getOrAddVertex(p: FloatArray): Short {
            val key = "${p[0]},${p[1]},${p[2]}"
            return vertexMap.getOrPut(key) {
                val index = vertexList.size
                vertexList.add(p)
                normalSums.add(floatArrayOf(0f, 0f, 0f))
                index
            }.toShort()
        }

        val equationFunc = Equation3.parseLatexToFunction(equation.equationString)

        for (x in -numStepsX..numStepsX) {
            for (z in -numStepsZ..numStepsZ) {
                val x0 = x * stepX
                val z0 = z * stepZ
                val x1 = x0 + stepX
                val z1 = z0 + stepZ

                val height0 = equationFunc(x0.toDouble(), z0.toDouble())
                val height1 = equationFunc(x1.toDouble(), z0.toDouble())
                val height2 = equationFunc(x0.toDouble(), z1.toDouble())
                val height3 = equationFunc(x1.toDouble(), z1.toDouble())

                if (height0 == null || height1 == null || height2 == null || height3 == null) continue

                val p0 = floatArrayOf(x0, height0.toFloat(), z0)
                val p1 = floatArrayOf(x1, height1.toFloat(), z0)
                val p2 = floatArrayOf(x0, height2.toFloat(), z1)
                val p3 = floatArrayOf(x1, height3.toFloat(), z1)

                val i0 = getOrAddVertex(p0)
                val i1 = getOrAddVertex(p1)
                val i2 = getOrAddVertex(p2)
                val i3 = getOrAddVertex(p3)

                indices.addAll(listOf(i0, i1, i2))
                val n1 = calculateNormal(p0, p1, p2)
                listOf(i0, i1, i2).forEach {
                    normalSums[it.toInt()][0] += n1[0]
                    normalSums[it.toInt()][1] += n1[1]
                    normalSums[it.toInt()][2] += n1[2]
                }

                indices.addAll(listOf(i1, i3, i2))
                val n2 = calculateNormal(p1, p3, p2)
                listOf(i1, i3, i2).forEach {
                    normalSums[it.toInt()][0] += n2[0]
                    normalSums[it.toInt()][1] += n2[1]
                    normalSums[it.toInt()][2] += n2[2]
                }
            }
        }

        val normals = normalSums.map { n ->
            val len = kotlin.math.sqrt(n[0] * n[0] + n[1] * n[1] + n[2] * n[2])
            if (len != 0f) floatArrayOf(n[0] / len, n[1] / len, n[2] / len)
            else floatArrayOf(0f, 1f, 0f)
        }

        val vertexData = vertexList.flatMap { it.toList() }
        val normalData = normals.flatMap { it.toList() }
        val colorData = List(vertexList.size) { color.toList() }.flatten()

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(vertexData.toFloatArray())
                position(0)
            }

        normalBuffer = ByteBuffer.allocateDirect(normalData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(normalData.toFloatArray())
                position(0)
            }

        colorBuffer = ByteBuffer.allocateDirect(colorData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(colorData.toFloatArray())
                position(0)
            }

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder()).asShortBuffer().apply {
                put(indices.toShortArray())
                position(0)
            }

        vertexCount = indices.size
    }

    fun draw(modelMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val colorHandle = GLES20.glGetAttribLocation(program, "aColor")
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer)

        val normalHandle = GLES20.glGetAttribLocation(program, "aNormal")
        GLES20.glEnableVertexAttribArray(normalHandle)
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)

        val scaleHandle = GLES20.glGetUniformLocation(program, "uScale")
        GLES20.glUniform1f(scaleHandle, scale * 0.1f)

        val cameraPosHandle = GLES20.glGetUniformLocation(program, "uCameraPosObject")
        val inverseModelMatrix = FloatArray(16)
        Matrix.invertM(inverseModelMatrix, 0, modelMatrix, 0)
        val cameraObjectSpace = FloatArray(4)
        Matrix.multiplyMV(cameraObjectSpace, 0, inverseModelMatrix, 0, floatArrayOf(0f, 0f, 0f, 1f), 0)
        GLES20.glUniform3f(cameraPosHandle, cameraObjectSpace[0], cameraObjectSpace[1], cameraObjectSpace[2])

        //Log.e("AAA", (scale * 0.1f).toString())

        val modelMatrixHandle = GLES20.glGetUniformLocation(program, "uModelMatrix")
        GLES20.glUniformMatrix4fv(modelMatrixHandle, 1, false, modelMatrix, 0)

        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0f, 0f, 0f, -5f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        indexBuffer.position(0)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            vertexCount,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val error = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            Log.e("Shader", error)
        }

        return shader
    }
}
