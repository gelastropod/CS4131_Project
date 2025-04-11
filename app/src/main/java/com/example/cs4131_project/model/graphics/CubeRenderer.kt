package com.example.cs4131_project.model.graphics

import android.graphics.Paint
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.utility.Point
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig

class CubeRenderer(background: Paint, graphViewModel: Graph3ViewModel, darkTheme: Boolean) : GLSurfaceView.Renderer {
    val backgroundColorPoint = Point.toPoint(background)
    private lateinit var cube: Cube
    private var angle = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(backgroundColorPoint.x.toFloat(), backgroundColorPoint.y.toFloat(), backgroundColorPoint.z.toFloat(), 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        cube = Cube()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(cube.projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        angle += 1f
        val modelMatrix = FloatArray(16).also {
            Matrix.setIdentityM(it, 0)
            Matrix.translateM(it, 0, 0f, 0f, -5f)
            Matrix.rotateM(it, 0, angle, 1f, 1f, 0f)
        }
        cube.draw(modelMatrix)
    }
}
