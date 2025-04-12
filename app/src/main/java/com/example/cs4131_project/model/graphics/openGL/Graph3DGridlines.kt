package com.example.cs4131_project.model.graphics.openGL

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.utility.Point
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.ArrayList

class Graph3DGridlines(val graphViewModel: Graph3ViewModel, val backgroundColorPoint: Point, darkTheme: Boolean) {
    private val lineColorPoint = if (darkTheme) Point(1.0, 1.0, 1.0) else Point(0.0, 0.0, 0.0)

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer

    val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val epsilon = 0.02f
    var scale = 1f

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec4 aColor;
        varying vec4 vColor;
        varying vec4 vWorldPos;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vColor = aColor;
            vWorldPos = vPosition;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec4 vColor;
        varying vec4 vWorldPos;
        uniform float uScale;
        void main() {
            vec4 scaledPos = vWorldPos * uScale;  // Use a local variable
            if (abs(scaledPos.x) > 1.0 || abs(scaledPos.y) > 1.0 || abs(scaledPos.z) > 1.0) {
                discard;
            }
            gl_FragColor = vColor;
        }
    """

    private val program: Int

    private var vertexCount = 0

    fun generateGrid() {
        val minorLineColor = lineColorPoint * 0.1 + backgroundColorPoint * 0.9
        val majorLineColor = lineColorPoint * 0.25 + backgroundColorPoint * 0.75

        val gridLines = mutableListOf<Float>()
        val gridColors = mutableListOf<Float>()

        val power10 = Math.pow(10.0, graphViewModel.power10.toDouble())

        val numSpacesX = (graphViewModel.size.x / graphViewModel.minorSpace.x / power10).toInt()
        val numSpacesZ = (graphViewModel.size.z / graphViewModel.minorSpace.z / power10).toInt()

        for (i in -numSpacesX..numSpacesX) {
            val isMajor = i % graphViewModel.majorSpace.x.toInt() == 0
            val color = if (isMajor) majorLineColor else minorLineColor

            gridLines.addAll(listOf((i * graphViewModel.minorSpace.x * power10).toFloat(), 0f, -graphViewModel.size.z.toFloat()))
            gridLines.addAll(listOf((i * graphViewModel.minorSpace.x * power10).toFloat(), 0f, graphViewModel.size.z.toFloat()))
            repeat(2) { gridColors.addAll(color.toList()) }
        }

        for (i in -numSpacesZ..numSpacesZ) {
            val isMajor = i % graphViewModel.majorSpace.z.toInt() == 0
            val color = if (isMajor) majorLineColor else minorLineColor

            gridLines.addAll(listOf(-graphViewModel.size.x.toFloat(), 0f, (i * graphViewModel.minorSpace.z * power10).toFloat()))
            gridLines.addAll(listOf(graphViewModel.size.x.toFloat(), 0f, (i * graphViewModel.minorSpace.z * power10).toFloat()))
            repeat(2) { gridColors.addAll(color.toList()) }
        }

        vertexCount = gridLines.size / 3

        vertexBuffer = ByteBuffer.allocateDirect(gridLines.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(gridLines.toFloatArray())
                position(0)
            }

        colorBuffer = ByteBuffer.allocateDirect(gridColors.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(gridColors.toFloatArray())
                position(0)
            }
    }

    init {
        generateGrid()

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

    fun draw(modelMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val colorHandle = GLES20.glGetAttribLocation(program, "aColor")
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer)

        val scaleHandle = GLES20.glGetUniformLocation(program, "uScale")
        GLES20.glUniform1f(scaleHandle, scale * 0.1f)

        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0f, 0f, 0f, -5f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount)

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
            Log.e("OpenGL", error)
        }

        return shader
    }
}