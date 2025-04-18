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
import com.example.cs4131_project.model.graph.GraphViewModel
import com.example.cs4131_project.model.graphics.GridDrawer
import com.example.cs4131_project.model.utility.Matrix
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point.Companion.toPoint
import com.example.cs4131_project.model.utility.Point2D
import com.example.cs4131_project.model.utility.Point2D.Companion.point

class GraphRenderer2D(context: Context, background: Paint, private val graphViewModel: GraphViewModel? = null, darkTheme: Boolean = true) : View(context) {
    private val drawer = Drawer(Canvas())
    private val gridDrawer = GridDrawer(drawer)
    private val handler = Handler(Looper.getMainLooper())
    private var frameStart = SystemClock.elapsedRealtime()

    private val backgroundColorPoint = toPoint(background)
    private val canvasBackgroundColorPoint = backgroundColorPoint
    private val lineColorPoint = if (darkTheme) Point(1.0, 1.0, 1.0) else Point(0.0, 0.0, 0.0)

    private val centerPoint = Point2D(0.0, 0.0)
    private var scale = Point2D(1.0, 1.0)
    private val correctionFactor = Point2D(-1.0, 1.0)

    private var centering = false

    private var initialized = false

    private val dPoint = Point2D(0.0, 0.0)

    private var scaling = 0

    constructor(context: Context) : this(context, Paint().apply {color = Color.TRANSPARENT})

    fun recenter() {
        centering = true
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentTime = SystemClock.elapsedRealtime()
            val elapsedTime = 0.001 * (currentTime - frameStart)
            frameStart = currentTime

            if (graphViewModel == null) return

            if (centering) {
                graphViewModel.size += (graphViewModel.screenSpace / 100.0 - graphViewModel.size) * 0.3
                graphViewModel.viewPoint += (centerPoint - graphViewModel.viewPoint) * 0.3
                if (graphViewModel.power10 == 0 && graphViewModel.size.equals(graphViewModel.screenSpace / 100.0) && graphViewModel.viewPoint.equals(centerPoint)) {
                    centering = false
                }
            }

            var numSpaces = graphViewModel.size / graphViewModel.minorSpace / Math.pow(10.0, graphViewModel.power10.toDouble())
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
            }

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

        canvas.clipRect(0f, 0f, width.toFloat(), height.toFloat())

        drawer.canvas = canvas

        scale = graphViewModel.size / graphViewModel.screenSpace * 2.0 * correctionFactor
        gridDrawer.draw(graphViewModel.minorSpace * Math.pow(10.0, graphViewModel.power10.toDouble()), graphViewModel.majorSpace, graphViewModel.viewPoint - graphViewModel.size, graphViewModel.viewPoint + graphViewModel.size, lineColorPoint, canvasBackgroundColorPoint, graphViewModel.power10)

        for (equation in graphViewModel.equations) {
            equation.drawOnGrid(gridDrawer, graphViewModel.viewPoint, graphViewModel.size, backgroundColorPoint)
        }
    }

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var lastTouchPoint = Point2D()

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        if (graphViewModel == null) return

        graphViewModel.screenSpace = Point2D(w.toDouble(), h.toDouble())
        if (graphViewModel.size == Point2D(10.5, 10.5) || graphViewModel.size.x <= 0.1) {
            graphViewModel.size = Point2D(w.toDouble(), h.toDouble()) / 100.0
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

            val focusPoint = Point2D(-detector.focusX.toDouble(), detector.focusY.toDouble())
            graphViewModel.viewPoint += (focusPoint - lastTouchPoint) / graphViewModel.screenSpace * graphViewModel.size * 2.0
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

                dPoint.x = -dx
                dPoint.y = dy

                graphViewModel.viewPoint += dPoint

                lastTouchX = event.x
                lastTouchY = event.y

                invalidate()
            }
        }
        return true
    }
}
