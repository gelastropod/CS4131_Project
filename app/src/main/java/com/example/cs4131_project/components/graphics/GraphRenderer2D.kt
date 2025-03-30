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
import com.example.cs4131_project.components.graphics.Drawer.Companion.toPaint
import com.example.cs4131_project.model.graph.Equation
import com.example.cs4131_project.model.graphics.GridDrawer
import com.example.cs4131_project.model.utility.Matrix
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point.Companion.toPoint
import com.example.cs4131_project.model.utility.Point2D
import com.example.cs4131_project.model.utility.Point2D.Companion.point

class GraphRenderer2D(context: Context, background: Paint) : View(context) {
    private val drawer = Drawer(Canvas())
    private val gridDrawer = GridDrawer(drawer)
    private val handler = Handler(Looper.getMainLooper())
    private var frameStart = SystemClock.elapsedRealtime()

    private var screenSpace = Point2D(width.toDouble(), height.toDouble())

    private val backgroundColorPoint = toPoint(background)
    private val canvasBackgroundColorPoint = Point(1.0, 1.0, 1.0)
    private val lineColorPoint = Point(0.0, 0.0, 0.0)

    private var power10 = 0
    private var minorSpace = Point2D(1.0, 1.0)
    private var majorSpace = Point2D(5.0, 5.0)
    private var viewPoint = Point2D(0.0, 0.0)
    private val centerPoint = Point2D(0.0, 0.0)
    private var size = Point2D(10.5, 10.5)
    private var scale = Point2D(1.0, 1.0)
    private val correctionFactor = Point2D(-1.0, 1.0)

    private var centering = false

    private var initialized = false

    private val dPoint = Point2D(0.0, 0.0)

    private var scaling = 0

    private val equation1 = Equation(
        {x ->
            x
        },
        Point(1.0, 0.5, 0.5)
    )
    private val equation2 = Equation(
        {x ->
            x * x
        },
        Point(0.5, 0.5, 1.0)
    )

    private val equations = arrayListOf(equation1, equation2)

    /*
    private val paintA = toPoint(Paint().apply {color = Color.RED})
    private val paintB = toPoint(Paint().apply {color = Color.BLUE})
    private val startPointA = Point(9.0, 9.0, 28.0)
    private val startPointB = Point(9.0, 9.0, 28.0001)
    private val pointsA: ArrayList<Point> = arrayListOf()
    private val pointsB: ArrayList<Point> = arrayListOf()
    private val numPoints = 300
    */

    constructor(context: Context) : this(context, Paint().apply {color = Color.TRANSPARENT})

    /*
    private val sigma = 10f
    private val rho = 28f
    private val beta = 8f/3f
    private fun lorenz(point: Point): Point {
        return Point(
            sigma * (point.y - point.x),
            point.x * (rho - point.z) - point.y,
            point.x * point.y - beta * point.z
        )
    }
    */

    fun recenter() {
        centering = true
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentTime = SystemClock.elapsedRealtime()
            val elapsedTime = 0.001 * (currentTime - frameStart)
            frameStart = currentTime

            if (centering) {
                size += (screenSpace / 100.0 - size) * 0.3
                viewPoint += (centerPoint - viewPoint) * 0.3
                if (power10 == 0 && size.equals(screenSpace / 100.0) && viewPoint.equals(centerPoint)) {
                    centering = false
                }
            }

            var numSpaces = size / minorSpace / Math.pow(10.0, power10.toDouble())
            while (numSpaces.x >= 18.0) {
                when (minorSpace.x.toInt()) {
                    1 -> minorSpace *= 2.0
                    2 -> {
                        minorSpace *= 2.5
                        majorSpace *= 0.8
                    }
                    5 -> {
                        minorSpace /= 5.0
                        majorSpace *= 1.25
                        power10++
                    }
                }
                numSpaces = size / minorSpace / Math.pow(10.0, power10.toDouble())
            }
            while (numSpaces.x <= 7.0) {
                when (minorSpace.x.toInt()) {
                    1 -> {
                        minorSpace *= 5.0
                        majorSpace *= 0.8
                        power10--
                    }
                    2 -> minorSpace /= 2.0
                    5 -> {
                        minorSpace /= 2.5
                        majorSpace *= 1.25
                    }
                }
                numSpaces = size / minorSpace / Math.pow(10.0, power10.toDouble())
            }

            /*
            if (pointsA.size == numPoints)
                pointsA.removeAt(0)

            val previousPointA = pointsA[pointsA.size - 1]
            val dPointA = lorenz(previousPointA)
            val newPointA = previousPointA + dPointA * elapsedTime
            pointsA.add(newPointA)

            if (pointsB.size == numPoints)
                pointsB.removeAt(0)

            val previousPointB = pointsB[pointsB.size - 1]
            val dPointB = lorenz(previousPointB)
            val newPointB = previousPointB + dPointB * elapsedTime
            pointsB.add(newPointB)
            */

            invalidate()
            handler.postDelayed(this, 0)
        }
    }

    init {
        screenSpace = Point2D(width.toDouble(), height.toDouble())

        setBackgroundColor(Color.WHITE)

        handler.post(updateRunnable)

        //pointsA.add(startPointA)
        //pointsB.add(startPointB)
    }

    override fun onDraw(canvas: Canvas) {
        if (!drawer.getSize().equals(screenSpace)) {
            screenSpace = drawer.getSize()
            size = screenSpace / 100.0
        }

        super.onDraw(canvas)

        canvas.clipRect(0f, 0f, width.toFloat(), height.toFloat())

        drawer.canvas = canvas

        scale = size / screenSpace * 2.0 * correctionFactor
        gridDrawer.draw(minorSpace * Math.pow(10.0, power10.toDouble()), majorSpace, viewPoint - size, viewPoint + size, lineColorPoint, canvasBackgroundColorPoint, power10)

       for (equation in equations) {
            equation.drawOnGrid(gridDrawer, viewPoint, size, backgroundColorPoint)
        }

        /*
        for (index in 1..<pointsA.size) {
            val indexFraction = index.toFloat() / pointsA.size.toFloat()
            val lineColorPoint = paintA * indexFraction + backgroundColorPoint * (1f - indexFraction)
            drawer.drawLine(point(pointsA[index - 1]) * 10f, point(pointsA[index]) * 10f, lineColorPoint.toPaint())
        }

        for (index in 1..<pointsB.size) {
            val indexFraction = index.toFloat() / pointsB.size.toFloat()
            val lineColorPoint = paintB * indexFraction + backgroundColorPoint * (1f - indexFraction)
            drawer.drawLine(point(pointsB[index - 1]) * 10f, point(pointsB[index]) * 10f, lineColorPoint.toPaint())
        }
        */
    }

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var lastTouchPoint = Point2D()

    private var scaleFactor = 1.0f
    private val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            lastTouchPoint = Point2D(-detector.focusX.toDouble(), detector.focusY.toDouble())
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scale = detector.scaleFactor

            scaleFactor *= scale
            scaleFactor = scaleFactor.coerceIn(0.5f, 3.0f)

            val focusPoint = Point2D(-detector.focusX.toDouble(), detector.focusY.toDouble())
            viewPoint += (focusPoint - lastTouchPoint) / screenSpace * size * 2.0
            lastTouchPoint = focusPoint

            size /= scale.toDouble()

            var numSpaces = size / minorSpace / Math.pow(10.0, power10.toDouble())
            while (numSpaces.x >= 18.0) {
                when (minorSpace.x.toInt()) {
                    1 -> minorSpace *= 2.0
                    2 -> {
                        minorSpace *= 2.5
                        majorSpace *= 0.8
                    }
                    5 -> {
                        minorSpace /= 5.0
                        majorSpace *= 1.25
                        power10++
                    }
                }
                numSpaces = size / minorSpace / Math.pow(10.0, power10.toDouble())
            }
            while (numSpaces.x <= 7.0) {
                when (minorSpace.x.toInt()) {
                    1 -> {
                        minorSpace *= 5.0
                        majorSpace *= 0.8
                        power10--
                    }
                    2 -> minorSpace /= 2.0
                    5 -> {
                        minorSpace /= 2.5
                        majorSpace *= 1.25
                    }
                }
                numSpaces = size / minorSpace / Math.pow(10.0, power10.toDouble())
            }

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
                if (scaling == 1) {
                    scaling = 0
                    lastTouchX = event.x
                    lastTouchY = event.y
                }

                val dx = (event.x - lastTouchX) / width * (size.x * 2.0)
                val dy = (event.y - lastTouchY) / height * (size.y * 2.0)

                dPoint.x = -dx
                dPoint.y = dy

                viewPoint += dPoint

                lastTouchX = event.x
                lastTouchY = event.y

                invalidate()
            }
        }
        return true
    }
}
