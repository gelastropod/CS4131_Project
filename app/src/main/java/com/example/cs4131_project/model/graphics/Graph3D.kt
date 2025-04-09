package com.example.cs4131_project.model.graphics

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.graph.GraphViewModel
import com.example.cs4131_project.model.utility.Point
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Graph3D(graphViewModel: Graph3ViewModel, backgroundColorPoint: Point) {
    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

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
        0f, 0f, -1f
    )

    private val colors = floatArrayOf(
        backgroundColorPoint.x.toFloat(), backgroundColorPoint.y.toFloat(), backgroundColorPoint.z.toFloat(), 1f,
        backgroundColorPoint.x.toFloat(), backgroundColorPoint.y.toFloat(), backgroundColorPoint.z.toFloat(), 1f,
        backgroundColorPoint.x.toFloat(), backgroundColorPoint.y.toFloat(), backgroundColorPoint.z.toFloat(), 1f,
        backgroundColorPoint.x.toFloat(), backgroundColorPoint.y.toFloat(), backgroundColorPoint.z.toFloat(), 1f,
        backgroundColorPoint.x.toFloat(), backgroundColorPoint.y.toFloat(), backgroundColorPoint.z.toFloat(), 1f,
        backgroundColorPoint.x.toFloat(), backgroundColorPoint.y.toFloat(), backgroundColorPoint.z.toFloat(), 1f
    )

    private val indices = shortArrayOf(
        0, 1, 2, 1, 3, 2,
        1, 5, 3, 5, 7, 3,
        5, 4, 7, 4, 6, 7,
        4, 0, 6, 0, 2, 6,
        4, 5, 0, 5, 1, 0,
        2, 3, 6, 3, 7, 6
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

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also {
            GLES20.glShaderSource(it, shaderCode)
            GLES20.glCompileShader(it)
        }
    }
}