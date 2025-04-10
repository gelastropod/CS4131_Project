package com.example.cs4131_project.components.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.graphics.CubeRenderer
import com.example.cs4131_project.model.graphics.Graph3DRenderer
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point2D
import com.example.cs4131_project.model.utility.Point4D

class GraphGLSurfaceView(context: Context, background: Paint, val graphViewModel: Graph3ViewModel, darkTheme: Boolean) : GLSurfaceView(context) {
    private val renderer: Graph3DRenderer
    private val drawer = Drawer(Canvas())
    private var modelMatrix = FloatArray(16)
    private var correctionFactor = Point2D(1.0, -1.0)
    private val lineColorPoint = if (darkTheme) Point(1.0, 1.0, 1.0) else Point(0.0, 0.0, 0.0)

    private val labelLocations = hashMapOf(
        floatArrayOf(1f, 0f, 0f, 1f) to "+x",
        floatArrayOf(-1f, 0f, 0f, 1f) to "-x",
        floatArrayOf(0f, 1f, 0f, 1f) to "+z",
        floatArrayOf(0f, -1f, 0f, 1f) to "-z",
        floatArrayOf(0f, 0f, 1f, 1f) to "+y",
        floatArrayOf(0f, 0f, -1f, 1f) to "-y"
    )

    init {
        setEGLContextClientVersion(2)

        renderer = Graph3DRenderer(background, graphViewModel, darkTheme)

        setRenderer(renderer)

        renderMode = RENDERMODE_CONTINUOUSLY
    }

    private val handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            modelMatrix.also {
                Matrix.setIdentityM(it, 0)
                Matrix.translateM(it, 0, 0f, 0f, -5f)
                Matrix.scaleM(it, 0, scale, scale, scale)
                Matrix.rotateM(it, 0, elevation, 1f, 0f, 0f)
                Matrix.rotateM(it, 0, azimuth, 0f, 1f, 0f)
            }

            renderer.modelMatrix = modelMatrix

            invalidate()
            handler.postDelayed(this, 0)
        }
    }

    init {
        setBackgroundColor(Color.TRANSPARENT)

        handler.post(updateRunnable)
    }

    private var previousX = 0f
    private var previousY = 0f
    private var azimuth = 0f
    private var elevation = 0f
    private var scale = 1f
    private var lastTouchPoint = Point2D()
    private var scaling = 0

    private val scaleGestureDetector =
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                lastTouchPoint = Point2D(detector.focusX.toDouble(), detector.focusY.toDouble())
                return true
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (graphViewModel == null) return false

                scale *= detector.scaleFactor
                scale = scale.coerceIn(0.01f, 100.0f)

                val focusPoint = Point2D(detector.focusX.toDouble(), detector.focusY.toDouble())
                val dPoint = (focusPoint - lastTouchPoint) * 0.5
                azimuth += dPoint.x.toFloat()
                elevation += dPoint.y.toFloat()
                lastTouchPoint = focusPoint

                graphViewModel.size /= scale.toDouble()

                invalidate()
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                scaling = 1
            }
        })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)

        if (!scaleGestureDetector.isInProgress) {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (scaling == 1) {
                        scaling = 0
                        previousX = event.x
                        previousY = event.y
                    }

                    val dx = event.x - previousX
                    val dy = event.y - previousY
                    azimuth += dx * 0.5f
                    elevation += dy * 0.5f
                }
            }
            previousX = event.x
            previousY = event.y
        }

        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        graphViewModel.screenSpace = Point2D(w.toDouble(), h.toDouble())
    }

    override fun onDraw(canvas: Canvas) {
        drawer.canvas = canvas

        canvas.clipRect(0f, 0f, width.toFloat(), height.toFloat())

        for (item in labelLocations) {
            val finalLocation = Point4D(FloatArray(4).also {
                Matrix.multiplyMV(it, 0, modelMatrix, 0, item.key, 0)
            })
            val ndc = Point2D(finalLocation / finalLocation.w)
            val screen = (ndc * correctionFactor + 1.0) * 0.5 * graphViewModel.screenSpace
            //drawer.drawText(item.value, screen, lineColorPoint.toTextPaint(40f))
        }
    }
}