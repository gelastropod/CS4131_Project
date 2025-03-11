package com.example.cs4131_project.components.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import com.example.cs4131_project.R
import com.example.cs4131_project.model.graphics.EBO
import com.example.cs4131_project.model.graphics.Grid
import com.example.cs4131_project.model.graphics.ShaderProgram
import com.example.cs4131_project.model.graphics.VAO
import com.example.cs4131_project.model.graphics.VBO
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GraphGLRenderer(val context: Context) : GLSurfaceView.Renderer {
    private lateinit var grid: Grid
    var width: Int = 0
    var height: Int = 0

    // Square vertices (2 triangles)
    private val squareCoords = floatArrayOf(
        -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f, // Bottom-left
        0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,  // Bottom-right
        -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f,   // Top-left
        0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f     // Top-right
    )

    // Indices to use the vertices for the two triangles
    private val indices = shortArrayOf(
        0, 1, 2,  // First triangle
        1, 3, 2   // Second triangle
    )

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set the clear color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        grid = Grid(context, -1f, -1f, 100, 100, 0.01f, 0.01f * height / width, floatArrayOf(1f, 1f, 1f))

        checkGlError("onSurfaceCreated")
    }

    override fun onDrawFrame(gl: GL10?) {
        // Clear the screen
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        grid.draw()

        checkGlError("onDrawFrame")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height

        // Set the viewport size
        GLES30.glViewport(0, 0, width, height)
    }

    private fun checkGlError(label: String) {
        var error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR) {
            Log.e("OpenGL", "$label: glError $error")
        }
    }
}