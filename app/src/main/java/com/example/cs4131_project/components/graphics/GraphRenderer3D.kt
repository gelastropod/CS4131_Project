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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

    private var crntazi = 0.0
    private var crntelev = 0.0

    constructor(context: Context) : this(context, Paint().apply {color = Color.TRANSPARENT})

    private val verts = arrayListOf(
        // Front face (Red)
        Point(-1.0, -1.0,  1.0), Point(1.0, 0.0, 0.0),  // Bottom left, red
        Point( 1.0, -1.0,  1.0), Point(1.0, 0.0, 0.0),  // Bottom right, red
        Point( 1.0,  1.0,  1.0), Point(1.0, 0.0, 0.0),  // Top right, red
        Point(-1.0,  1.0,  1.0), Point(1.0, 0.0, 0.0),  // Top left, red

        // Back face (Cyan)
        Point(-1.0, -1.0, -1.0), Point(0.0, 1.0, 1.0),  // Bottom left, cyan
        Point( 1.0, -1.0, -1.0), Point(0.0, 1.0, 1.0),  // Bottom right, cyan
        Point( 1.0,  1.0, -1.0), Point(0.0, 1.0, 1.0),  // Top right, cyan
        Point(-1.0,  1.0, -1.0), Point(0.0, 1.0, 1.0),  // Top left, cyan

        // Top face (Green)
        Point(-1.0,  1.0,  1.0), Point(0.0, 1.0, 0.0),  // Front left, green
        Point( 1.0,  1.0,  1.0), Point(0.0, 1.0, 0.0),  // Front right, green
        Point( 1.0,  1.0, -1.0), Point(0.0, 1.0, 0.0),  // Back right, green
        Point(-1.0,  1.0, -1.0), Point(0.0, 1.0, 0.0),  // Back left, green

        // Bottom face (Magenta)
        Point(-1.0, -1.0,  1.0), Point(1.0, 0.0, 1.0),  // Front left, magenta
        Point( 1.0, -1.0,  1.0), Point(1.0, 0.0, 1.0),  // Front right, magenta
        Point( 1.0, -1.0, -1.0), Point(1.0, 0.0, 1.0),  // Back right, magenta
        Point(-1.0, -1.0, -1.0), Point(1.0, 0.0, 1.0),  // Back left, magenta

        // Left face (Blue)
        Point(-1.0, -1.0,  1.0), Point(0.0, 0.0, 1.0),  // Front bottom, blue
        Point(-1.0,  1.0,  1.0), Point(0.0, 0.0, 1.0),  // Front top, blue
        Point(-1.0,  1.0, -1.0), Point(0.0, 0.0, 1.0),  // Back top, blue
        Point(-1.0, -1.0, -1.0), Point(0.0, 0.0, 1.0),  // Back bottom, blue

        // Right face (Yellow)
        Point( 1.0, -1.0,  1.0), Point(1.0, 1.0, 0.0),  // Front bottom, yellow
        Point( 1.0,  1.0,  1.0), Point(1.0, 1.0, 0.0),  // Front top, yellow
        Point( 1.0,  1.0, -1.0), Point(1.0, 1.0, 0.0),  // Back top, yellow
        Point( 1.0, -1.0, -1.0), Point(1.0, 1.0, 0.0)   // Back bottom, yellow
    )

    private val indices = arrayListOf(
        0, 1, 2, 3,
        4, 5, 6, 7,
        8, 9, 10, 11,
        12, 13, 14, 15,
        16, 17, 18, 19,
        20, 21, 22, 23
    )

    private fun projection(fov: Double, aspect: Double, z_near: Double, z_far: Double): Matrix {
        return Matrix(
            doubleArrayOf(
                1.0 / (aspect * Math.tan(fov / 2.0)), 0.0, 0.0, 0.0,
                0.0, 1.0 / Math.tan(fov / 2.0), 0.0, 0.0,
                0.0, 0.0, -(z_far + z_near) / (z_far - z_near), -2.0 * z_far * z_near / (z_far - z_near),
                0.0, 0.0, -1.0, 1.0
            )
        )
    }

    private fun model(point: Point): Matrix {
        return Matrix(
            doubleArrayOf(
                1.0, 0.0, 0.0, -point.x,
                0.0, 1.0, 0.0, -point.y,
                0.0, 0.0, 1.0, -point.z,
                0.0, 0.0, 0.0, 1.0
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

    fun rotation(azimuth: Double, elevation: Double): Matrix {
        val dirX = cos(elevation) * sin(azimuth)
        val dirY = sin(elevation)
        val dirZ = cos(elevation) * cos(azimuth)

        val forward = Point(dirX, dirY, dirZ).normalize()
        val worldUp = Point(0.0, 1.0, 0.0)

        val right = worldUp.cross(forward).normalize()
        val up = forward.cross(right)

        return Matrix(doubleArrayOf(
            right.x, up.x, forward.x, 0.0,
            right.y, up.y, forward.y, 0.0,
            right.z, up.z, forward.z, 0.0,
            0.0, 0.0, 0.0, 1.0
        ))
    }

    private fun view(azimuth: Double, elevation: Double, scale: Point, translate: Point, fov: Double, aspect: Double, z_near: Double, z_far: Double): Matrix {
        //Log.e("3D projection", projection(fov, aspect, z_near, z_far).toString())
        //Log.e("3D model", model(translate).toString())
        //Log.e("3D scale", scale(scale).toString())
        //Log.e("3D rotation", rotation(azimuth, elevation).toString())
        return projection(fov, aspect, z_near, z_far) * model(translate) * scale(scale) * rotation(azimuth, elevation)
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentTime = SystemClock.elapsedRealtime()
            val elapsedTime = 0.001 * (currentTime - frameStart)
            frameStart = currentTime

            crntazi += elapsedTime * PI / 180.0 * 20.0 * Math.E
            crntelev += elapsedTime * PI / 180.0 * 10.0 * PI

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

        canvas.clipRect(0f, 0f, width.toFloat(), height.toFloat())

        drawer.canvas = canvas

        val mvp = view(crntazi, crntelev, Point(1.0, 1.0, 1.0), Point(0.0, 0.0, -5.0), PI / 3.0, graphViewModel.screenSpace.x / graphViewModel.screenSpace.y, 0.1, 100.0)
        //Log.e("3D", mvp.toString())

        val projectedVerts = arrayListOf<Point2D>()
        val colours = arrayListOf<Point>()

        for (index in 0..<verts.size step 2) {
            val point = verts[index]
            val clip = mvp * Point4D(point)
            val ndc = Point(clip) / clip.w
            val screen = (Point2D(ndc) * -correctionFactor + 1.0) * 0.5 * graphViewModel.screenSpace
            projectedVerts.add(screen)
            colours.add(verts[index + 1])
            //Log.e("3D", screen.toString())
        }

        for (index in 0..<indices.size step 4) {
            val point1 = projectedVerts[indices[index]]
            val point2 = projectedVerts[indices[index + 1]]
            val point3 = projectedVerts[indices[index + 2]]
            val point4 = projectedVerts[indices[index + 3]]
            val color = colours[indices[index]]
            drawer.drawQuad(point1, point2, point3, point4, color.toPaint())
        }

        //Log.e("3D Screen", graphViewModel.screenSpace.toString())
        //Log.e("3D Paint", lineColorPoint.toString())

        //drawer.drawQuad(projectedVerts[0], projectedVerts[1], projectedVerts[2], projectedVerts[3], lineColorPoint.toPaint())

        //scale = graphViewModel.size / graphViewModel.screenSpace * 2.0 * correctionFactor
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
