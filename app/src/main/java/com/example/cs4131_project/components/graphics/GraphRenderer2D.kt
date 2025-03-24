package com.example.cs4131_project.components.graphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
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

    private val backgroundColorPoint = toPoint(background)
    private val canvasBackgroundColorPoint = Point(1.0, 1.0, 1.0)
    private val lineColorPoint = Point(0.0, 0.0, 0.0)

    private val minorSpace = Point2D(1.0, 1.0)
    private val majorSpace = Point2D(5.0, 5.0)
    private val minBound = Point2D(-0.5, -0.5)
    private val maxBound = Point2D(20.5, 20.5)

    private val paintA = toPoint(Paint().apply {color = Color.RED})
    private val paintB = toPoint(Paint().apply {color = Color.BLUE})
    private val startPointA = Point(9.0, 9.0, 28.0)
    private val startPointB = Point(9.0, 9.0, 28.0001)
    private val pointsA: ArrayList<Point> = arrayListOf()
    private val pointsB: ArrayList<Point> = arrayListOf()
    private val numPoints = 300

    constructor(context: Context) : this(context, Paint().apply {color = Color.TRANSPARENT})

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

    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentTime = SystemClock.elapsedRealtime()
            val elapsedTime = 0.001 * (currentTime - frameStart)
            frameStart = currentTime

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

            invalidate()
            handler.postDelayed(this, 0)
        }
    }

    init {
        setBackgroundColor(Color.WHITE)

        handler.post(updateRunnable)

        pointsA.add(startPointA)
        pointsB.add(startPointB)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawer.canvas = canvas

        gridDrawer.draw(minorSpace, majorSpace, minBound, maxBound, lineColorPoint, canvasBackgroundColorPoint)

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
}