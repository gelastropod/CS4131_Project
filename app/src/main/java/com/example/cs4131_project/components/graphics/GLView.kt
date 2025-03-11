package com.example.cs4131_project.components.graphics

import android.content.Context
import android.opengl.GLSurfaceView

class GLView(context: Context) : GLSurfaceView(context) {
    private val renderer: GraphGLRenderer

    init {
        setEGLContextClientVersion(3)
        renderer = GraphGLRenderer(context)
        setRenderer(renderer)
    }
}