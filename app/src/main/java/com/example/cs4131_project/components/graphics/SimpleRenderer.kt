package com.example.cs4131_project.components.graphics

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.cs4131_project.model.graphics.ShaderProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SimpleRenderer(val context: Context) : GLSurfaceView.Renderer {
    private lateinit var shaderProgram: ShaderProgram
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer

    // Square vertex data
    private val vertices = floatArrayOf(
        // X, Y, Z coordinates
        -0.5f,  0.5f, 0.0f,  // Top left
        -0.5f, -0.5f, 0.0f,  // Bottom left
        0.5f, -0.5f, 0.0f,  // Bottom right
        0.5f,  0.5f, 0.0f   // Top right
    )

    private val indices = shortArrayOf(
        0, 1, 2,  // First triangle
        0, 2, 3   // Second triangle
    )

    private val color = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)  // Red color

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        shaderProgram = ShaderProgram(context, "fshader", "vshader")
        shaderProgram.bind()

        positionHandle = shaderProgram.getAttribute("vPosition")
        colorHandle = shaderProgram.getUniform("uColor")

        // Create a buffer for the vertex data
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        // Create a buffer for the index data
        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)

        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glEnableVertexAttribArray(positionHandle)

        // Check for OpenGL errors
        checkGlError("onSurfaceCreated")
    }

    override fun onDrawFrame(gl: GL10?) {
        // Clear the screen with black
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        // Set the color to red
        GLES30.glUniform4fv(colorHandle, 1, color, 0)

        GLES30.glEnableVertexAttribArray(positionHandle)

        // Draw the elements (square)
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)

        // Disable the position attribute after drawing
        GLES30.glDisableVertexAttribArray(positionHandle)

        // Check for OpenGL errors
        checkGlError("onDrawFrame")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    private fun checkGlError(label: String) {
        var error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR) {
            Log.e("OpenGL", "$label: glError $error")
        }
    }
}