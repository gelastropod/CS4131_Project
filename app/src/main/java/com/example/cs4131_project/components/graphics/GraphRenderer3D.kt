package com.example.cs4131_project.components.graphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cs4131_project.components.graphics.Drawer.Companion.toPaint
import com.example.cs4131_project.model.graph.Equation
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.graph.GraphViewModel
import com.example.cs4131_project.model.graphics.GridDrawer
import com.example.cs4131_project.model.utility.Matrix
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point.Companion.toPoint
import com.example.cs4131_project.model.utility.Point2D
import com.example.cs4131_project.model.utility.Point2D.Companion.point
import com.example.cs4131_project.model.utility.Point4D

class GraphRenderer3D(context: Context, background: Paint, private val graphViewModel: Graph3ViewModel? = null, darkTheme: Boolean = true) : View(context) {
    private val drawer = Drawer(Canvas())
    private val handler = Handler(Looper.getMainLooper())
    private var frameStart = SystemClock.elapsedRealtime()

    private val backgroundColorPoint = toPoint(background)
    private val canvasBackgroundColorPoint = backgroundColorPoint
    private val lineColorPoint = if (darkTheme) Point(1.0, 1.0, 1.0) else Point(0.0, 0.0, 0.0)

    private var scale = Point(1.0, 1.0, 1.0)
    private val correctionFactor = Point2D(-1.0, 1.0)

    private var initialized = false

    private var scaling = 0

    constructor(context: Context) : this(context, Paint().apply {color = Color.TRANSPARENT})

    private fun projection(fov: Double, aspect: Double, z_near: Double, z_far: Double): Matrix {
        return Matrix(
            doubleArrayOf(
                1.0 / (aspect * Math.tan(Math.toRadians(fov / 2.0))), 0.0, 0.0, 0.0,
                0.0, 1.0 / Math.tan(Math.toRadians(fov / 2.0)), 0.0, 0.0,
                0.0, 0.0, -(z_far + z_near) / (z_far - z_near), -2.0 * z_far * z_near / (z_far - z_near),
                0.0, 0.0, -1.0, 0.0
            )
        )
    }

    private fun model(point: Point4D): Matrix {
        return Matrix(
            doubleArrayOf(
                1.0, 0.0, 0.0, 0.0,
                0.0, 1.0, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                point.x, point.y, point.z, 1.0
            )
        )
    }

    private fun scale(point: Point): Matrix {
        return Matrix(
            doubleArrayOf(
                point.x, 0.0, 0.0, 0.0,
                0.0, point.y, 0.0, 0.0,
                0.0, 0.0, point.z, 0.0,
                0.0, 0.0, 0.0, 1.0
            )
        )
    }



    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentTime = SystemClock.elapsedRealtime()
            val elapsedTime = 0.001 * (currentTime - frameStart)
            frameStart = currentTime

            if (graphViewModel == null) return

            /*var numSpaces = graphViewModel.size / graphViewModel.minorSpace / Math.pow(10.0, graphViewModel.power10.toDouble())
            while (numSpaces.x >= 18.0) {
                when (graphViewModel.minorSpace.x.toInt()) {
                    1 -> graphViewModel.minorSpace *= 2.0
                    2 -> {
                        graphViewModel.minorSpace *= 2.5
                        graphViewModel.majorSpace *= 0.8
                    }
                    5 -> {
                        graphViewModel.minorSpace /= 5.0
                        graphViewModel.majorSpace *= 1.25
                        graphViewModel.power10++
                    }
                }
                numSpaces = graphViewModel.size / graphViewModel.minorSpace / Math.pow(10.0, graphViewModel.power10.toDouble())
            }
            while (numSpaces.x <= 7.0) {
                when (graphViewModel.minorSpace.x.toInt()) {
                    1 -> {
                        graphViewModel.minorSpace *= 5.0
                        graphViewModel.majorSpace *= 0.8
                        graphViewModel.power10--
                    }
                    2 -> graphViewModel.minorSpace /= 2.0
                    5 -> {
                        graphViewModel.minorSpace /= 2.5
                        graphViewModel.majorSpace *= 1.25
                    }
                }
                numSpaces = graphViewModel.size / graphViewModel.minorSpace / Math.pow(10.0, graphViewModel.power10.toDouble())
            }*/

            invalidate()
            handler.postDelayed(this, 0)
        }
    }

    init {
        setBackgroundColor(background.color)

        handler.post(updateRunnable)
    }

    override fun onDraw(canvas: Canvas) {
        if (graphViewModel == null) return

        super.onDraw(canvas)

        /*canvas.clipRect(0f, 0f, width.toFloat(), height.toFloat())

        drawer.canvas = canvas

        scale = graphViewModel.size / graphViewModel.screenSpace * 2.0 * correctionFactor*/
    }

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var lastTouchPoint = Point2D()

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        if (graphViewModel == null) return

        graphViewModel.screenSpace = Point2D(w.toDouble(), h.toDouble())
        if (graphViewModel.size == Point(10.5, 10.5, 10.5) || graphViewModel.size.x <= 0.1) {
            graphViewModel.size = Point(w.toDouble(), w.toDouble(), h.toDouble()) / 100.0
        }
    }

    private var scaleFactor = 1.0f
    private val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            lastTouchPoint = Point2D(-detector.focusX.toDouble(), detector.focusY.toDouble())
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (graphViewModel == null) return false

            val scale = detector.scaleFactor

            scaleFactor *= scale
            scaleFactor = scaleFactor.coerceIn(0.5f, 3.0f)

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

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (graphViewModel == null) return false

                if (scaling == 1) {
                    scaling = 0
                    lastTouchX = event.x
                    lastTouchY = event.y
                }

                val dx = (event.x - lastTouchX) / width * (graphViewModel.size.x * 2.0)
                val dy = (event.y - lastTouchY) / height * (graphViewModel.size.y * 2.0)

                //TODO rotate

                lastTouchX = event.x
                lastTouchY = event.y

                invalidate()
            }
        }
        return true
    }
}
