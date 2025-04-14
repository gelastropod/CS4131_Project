package com.example.cs4131_project.model.graphics.openGL

import android.graphics.Paint
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.utility.Point
import com.google.common.graph.Graph
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig
import kotlin.math.sin

class Graph3DRenderer(background: Paint, val graphViewModel: Graph3ViewModel, val darkTheme: Boolean, var quality: Int) : GLSurfaceView.Renderer {
    val backgroundColorPoint = Point.toPoint(background)
    private lateinit var graph: Graph3D
    private lateinit var graphLabels: ArrayList<Graph3DLabel>
    lateinit var graphSurface: Graph3DSurface
    private var graphNumLabels = arrayListOf<Graph3DLabel>()
    lateinit var graphGridlines: Graph3DGridlines
    lateinit var graphPlane: Graph3DPlane
    var initialized = false
    var modelMatrix = FloatArray(16)
    var innerScale = 1f
    private val textureIDs = IntArray(1000)
    private var generatingLabels = false

    fun generateLabels() {
        generatingLabels = true

        val newGraphNumLabels = arrayListOf<Graph3DLabel>()

        var textureIndex = 3
        val power10 = Math.pow(10.0, graphViewModel.power10.toDouble())

        val numSpacesX = (graphViewModel.size.x / graphViewModel.minorSpace.x / power10).toInt()
        val numSpacesZ = (graphViewModel.size.z / graphViewModel.minorSpace.z / power10).toInt()

        for (i in -numSpacesX..numSpacesX) {
            val isMajor = i % graphViewModel.majorSpace.x.toInt() == 0
            if (!isMajor) continue

            newGraphNumLabels.add(
                Graph3DLabel(
                    graphViewModel,
                    backgroundColorPoint,
                    darkTheme,
                    Point(i * graphViewModel.minorSpace.x * power10, 0.0, 0.0),
                    false,
                    textureIDs[textureIndex],
                    "a",
                    scale = (graphViewModel.minorSpace.x * power10).toFloat() * 25f
                )
            )
            textureIndex++
        }

        for (i in -numSpacesZ..numSpacesZ) {
            val isMajor = i % graphViewModel.majorSpace.z.toInt() == 0
            if (!isMajor) continue

            newGraphNumLabels.add(
                Graph3DLabel(
                    graphViewModel,
                    backgroundColorPoint,
                    darkTheme,
                    Point(0.0, 0.0, i * graphViewModel.minorSpace.z * power10),
                    false,
                    textureIDs[textureIndex],
                    "a",
                    scale = (graphViewModel.minorSpace.x * power10).toFloat() * 25f
                )
            )
            textureIndex++
        }

        graphNumLabels = newGraphNumLabels

        generatingLabels = false
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(backgroundColorPoint.x.toFloat(), backgroundColorPoint.y.toFloat(), backgroundColorPoint.z.toFloat(), 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        graph = Graph3D(graphViewModel, backgroundColorPoint, darkTheme)

        graphSurface = Graph3DSurface(graphViewModel, backgroundColorPoint, darkTheme, quality = quality.toFloat(), otherScale = innerScale)

        GLES20.glGenTextures(1000, textureIDs, 0)

        graphLabels = arrayListOf(
            Graph3DLabel(graphViewModel, backgroundColorPoint, darkTheme, Point(1.03, 0.0, 0.01), false, textureIDs[0], "x").apply{initialize()},
            Graph3DLabel(graphViewModel, backgroundColorPoint, darkTheme, Point(0.0, 0.0, -1.03), false, textureIDs[1], "y").apply{initialize()},
            Graph3DLabel(graphViewModel, backgroundColorPoint, darkTheme, Point(0.0, 1.03, 0.0), true, textureIDs[2], "z").apply{initialize()}
        )

        graphGridlines = Graph3DGridlines(graphViewModel, backgroundColorPoint, darkTheme)

        graphPlane = Graph3DPlane(graphViewModel, backgroundColorPoint, darkTheme)

        initialized = true
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        generatingLabels = true

        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(graph.projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)
        Matrix.frustumM(graphSurface.projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)
        Matrix.frustumM(graphPlane.projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)

        for (graphLabel in graphLabels) {
            Matrix.frustumM(graphLabel.projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)
        }

        for (graphLabel in graphNumLabels) {
            Matrix.frustumM(graphLabel.projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)
        }

        Matrix.frustumM(graphGridlines.projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)

        generatingLabels = false
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

        val scaledModelMatrix = modelMatrix.copyOf().also {
            Matrix.scaleM(it, 0, innerScale * 0.1f, innerScale * 0.1f, innerScale * 0.1f)
        }

        graphGridlines.draw(scaledModelMatrix)

        val otherScaledModelMatrix = modelMatrix.copyOf().also {
            Matrix.scaleM(it, 0, graphSurface.scale * 0.1f, graphSurface.scale * 0.1f, graphSurface.scale * 0.1f)
        }

        graphSurface.draw(otherScaledModelMatrix)

        if (!generatingLabels) {
            for (graphLabel in graphNumLabels) {
                graphLabel.initialize()
                graphLabel.draw(scaledModelMatrix, innerScale)
            }
        }

        graphPlane.draw(modelMatrix)

        for (graphLabel in graphLabels) {
            graphLabel.draw(modelMatrix, innerScale)
        }
    }
}