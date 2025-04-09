package com.example.cs4131_project.components.graphics

import android.content.Context
import android.graphics.Paint
import android.opengl.GLSurfaceView
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.graphics.CubeRenderer

class GraphGLSurfaceView(context: Context, background: Paint, graphViewModel: Graph3ViewModel, darkTheme: Boolean) : GLSurfaceView(context) {

    private val renderer: CubeRenderer

    init {
        setEGLContextClientVersion(2)

        renderer = CubeRenderer(background, graphViewModel, darkTheme)

        setRenderer(renderer)
    }
}