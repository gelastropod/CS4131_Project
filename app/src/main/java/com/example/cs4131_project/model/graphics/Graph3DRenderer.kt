package com.example.cs4131_project.model.graphics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.utility.Point
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig

class Graph3DRenderer(background: Paint, val graphViewModel: Graph3ViewModel, val darkTheme: Boolean) : GLSurfaceView.Renderer {
    val backgroundColorPoint = Point.toPoint(background)
    private lateinit var graph: Graph3D
    private val graphLabels: ArrayList<Graph3DLabel>
    var modelMatrix = FloatArray(16)

    init {
        val textureIDs = IntArray(6)
        GLES20.glGenTextures(6, textureIDs, 0)

        graphLabels = arrayListOf(
            Graph3DLabel(graphViewModel, backgroundColorPoint, darkTheme, Point(1.0, 0.0, 0.0), false, textureIDs[0], "+x"),
            Graph3DLabel(graphViewModel, backgroundColorPoint, darkTheme, Point(-1.0, 0.0, 0.0), false, textureIDs[1], "-x"),
            Graph3DLabel(graphViewModel, backgroundColorPoint, darkTheme, Point(0.0, 0.0, 1.0), false, textureIDs[2], "+y"),
            Graph3DLabel(graphViewModel, backgroundColorPoint, darkTheme, Point(0.0, 0.0, -1.0), false, textureIDs[3], "-y"),
            Graph3DLabel(graphViewModel, backgroundColorPoint, darkTheme, Point(0.0, 1.0, 0.0), true, textureIDs[4], "+z"),
            Graph3DLabel(graphViewModel, backgroundColorPoint, darkTheme, Point(0.0, -1.0, 0.0), true, textureIDs[5], "-z")
        )
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(backgroundColorPoint.x.toFloat(), backgroundColorPoint.y.toFloat(), backgroundColorPoint.z.toFloat(), 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        graph = Graph3D(graphViewModel, backgroundColorPoint, darkTheme)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(graph.projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)

        for (graphLabel in graphLabels) {
            Matrix.frustumM(graphLabel.projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("Graph3DLabel", "OpenGL error before loading texture: $error")
        }

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        graph.draw(modelMatrix)

        for (graphLabel in graphLabels) {
            graphLabel.draw(modelMatrix)
        }
    }
}