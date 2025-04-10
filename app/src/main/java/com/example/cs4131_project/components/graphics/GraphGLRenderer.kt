package com.example.cs4131_project.components.graphics

import android.graphics.Paint
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.utility.Point

class GraphGLRenderer(background: Paint, graphViewModel: Graph3ViewModel, darkTheme: Boolean) : GLSurfaceView.Renderer {
    private val backgroundColorPoint = Point.toPoint(background)

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(backgroundColorPoint.x.toFloat(), backgroundColorPoint.y.toFloat(), backgroundColorPoint.z.toFloat(), 1.0f)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }
}