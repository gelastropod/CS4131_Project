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

class Graph3DGridlines(graphViewModel: Graph3ViewModel, backgroundColorPoint: Point, darkTheme: Boolean) {
    private val lineColorPoint = if (darkTheme) Point(1.0, 1.0, 1.0) else Point(0.0, 0.0, 0.0)

    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val epsilon = 0.02f

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec4 aColor;
        varying vec4 vColor;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vColor = aColor;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """

    private val program: Int

    private val vertices = floatArrayOf(
        1f, 0f, 0f,
        -1f, 0f, 0f,
        0f, 1f, 0f,
        0f, -1f, 0f,
        0f, 0f, 1f,
        0f, 0f, -1f,
        -epsilon, 0f, epsilon - 1f,
        epsilon, 0f, epsilon - 1f,
        -epsilon, 1f - epsilon, 0f,
        epsilon, 1f - epsilon, 0f,
        1f - epsilon, 0f, -epsilon,
        1f - epsilon, 0f, epsilon,
        -1f, -1f, -1f,
        -1f, -1f, 1f,
        -1f, 1f, 1f,
        -1f, 1f, -1f,
        1f, 1f, -1f,
        1f, -1f, -1f,
        1f, -1f, 1f,
        1f, 1f, 1f
    )

    private val colors = ArrayList(List(vertices.size / 3) {
        arrayListOf(
            lineColorPoint.x.toFloat(), lineColorPoint.y.toFloat(), lineColorPoint.z.toFloat(), 1f
        )
    }.flatten()).toFloatArray()

    private val indices = shortArrayOf(
        0, 1, 2, 3, 4, 5,
        5, 6, 5, 7, 2, 8,
        2, 9, 0, 10, 0, 11,
        12, 13, 12, 15, 12, 17,
        14, 15, 14, 13, 14, 19,
        16, 19, 16, 17, 16, 15,
        18, 17, 18, 19, 18, 13
    )

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(vertices)
                position(0)
            }

        colorBuffer = ByteBuffer.allocateDirect(colors.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(colors)
                position(0)
            }

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder()).asShortBuffer().apply {
                put(indices)
                position(0)
            }

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

        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0f, 0f, 0f, -5f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glDrawElements(GLES20.GL_LINES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

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