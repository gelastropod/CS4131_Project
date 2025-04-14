package com.example.cs4131_project.components.graphics.openGL

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.cs4131_project.components.graphics.Drawer
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.graphics.openGL.Graph3DRenderer
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point2D
import com.example.cs4131_project.model.utility.Point4D
import kotlin.math.abs

class GraphGLSurfaceView(context: Context, background: Paint, val graphViewModel: Graph3ViewModel, darkTheme: Boolean, var zoomScene: Boolean, var centering: Boolean = true, var quality: Int = 1) : GLSurfaceView(context) {
    private val renderer: Graph3DRenderer
    private val drawer = Drawer(Canvas())
    private var modelMatrix = FloatArray(16)
    private var correctionFactor = Point2D(1.0, -1.0)
    private val lineColorPoint = if (darkTheme) Point(1.0, 1.0, 1.0) else Point(0.0, 0.0, 0.0)
    var updated = false
    private var immutableScale = 1f

    init {
        setEGLContextClientVersion(2)

        renderer = Graph3DRenderer(background, graphViewModel, darkTheme, quality)

        setRenderer(renderer)

        renderMode = RENDERMODE_CONTINUOUSLY
    }

    private val handler = Handler(Looper.getMainLooper())

    fun setQualityValue(quality: Int) {
        if (renderer.initialized && renderer.graphSurface.quality.toInt() != quality) {
            Log.e("rege", "🐥")
            renderer.graphSurface.quality = quality.toFloat()
            renderer.graphSurface.generateSurfaceMesh(renderer.innerScale)
        }
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            //Log.e("AAA", "${graphViewModel.size} $azimuth $elevation $scale ${renderer.innerScale} ${graphViewModel.power10}")

            if (centering) {
                graphViewModel.size += (Point(10.5, 10.5, 10.5) - graphViewModel.size) * 0.3
                azimuth *= 0.7f
                elevation *= 0.7f
                scale += (1f - scale) * 0.3f
                renderer.innerScale += (1f - renderer.innerScale) * 0.3f
                immutableScale += (1f - immutableScale) * 0.3f
                if (renderer.initialized) {
                    renderer.graphGridlines.scale = renderer.innerScale
                    renderer.graphSurface.scale = immutableScale
                }
                if (graphViewModel.power10 == -1 && graphViewModel.size.equals(Point(10.5, 10.5, 10.5)) && abs(renderer.innerScale - 1f) < 0.01f && abs(azimuth) < 0.01f && abs(elevation) < 0.01f && abs(scale - 1f) < 0.01f && abs(immutableScale - 1f) < 0.01f) {
                    centering = false
                    updated = true
                }
            }

            var numSpaces = graphViewModel.size / graphViewModel.minorSpace / Math.pow(10.0, graphViewModel.power10.toDouble())
            while (numSpaces.x >= 50.0) {
                updated = true
                when (graphViewModel.minorSpace.x.toInt()) {
                    1 -> {
                        graphViewModel.minorSpace *= 2.0
                        renderer.innerScale /= 4f
                        if (renderer.initialized) {
                            renderer.graphGridlines.scale = renderer.innerScale
                        }
                    }
                    2 -> {
                        graphViewModel.minorSpace *= 2.5
                        graphViewModel.majorSpace *= 0.8
                        renderer.innerScale /= 2.5f * 2.5f
                        if (renderer.initialized) {
                            renderer.graphGridlines.scale = renderer.innerScale
                        }
                    }
                    5 -> {
                        graphViewModel.minorSpace /= 5.0
                        graphViewModel.majorSpace *= 1.25
                        graphViewModel.power10++
                        renderer.innerScale /= 4f
                        if (renderer.initialized) {
                            renderer.graphGridlines.scale = renderer.innerScale
                        }
                    }
                }
                numSpaces = graphViewModel.size / graphViewModel.minorSpace / Math.pow(10.0, graphViewModel.power10.toDouble())
            }
            while (numSpaces.x <= 20.0) {
                updated = true
                when (graphViewModel.minorSpace.x.toInt()) {
                    1 -> {
                        graphViewModel.minorSpace *= 5.0
                        graphViewModel.majorSpace *= 0.8
                        graphViewModel.power10--
                        renderer.innerScale *= 4f
                        if (renderer.initialized) {
                            renderer.graphGridlines.scale = renderer.innerScale
                        }
                    }
                    2 -> {
                        graphViewModel.minorSpace /= 2.0
                        renderer.innerScale *= 4f
                        if (renderer.initialized) {
                            renderer.graphGridlines.scale = renderer.innerScale
                        }
                    }
                    5 -> {
                        graphViewModel.minorSpace /= 2.5
                        graphViewModel.majorSpace *= 1.25
                        renderer.innerScale *= 2.5f * 2.5f
                        if (renderer.initialized) {
                            renderer.graphGridlines.scale = renderer.innerScale
                        }
                    }
                }
                numSpaces = graphViewModel.size / graphViewModel.minorSpace / Math.pow(10.0, graphViewModel.power10.toDouble())
            }

            if (updated && renderer.initialized) {
                //renderer.generateLabels()
                renderer.graphGridlines.generateGrid()
                renderer.graphSurface.generateSurfaceMesh(renderer.innerScale)
                updated = false
            }

            if (renderer.initialized) {
                renderer.graphGridlines.scale = renderer.innerScale
                renderer.graphSurface.scale = immutableScale
            }

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
                if (zoomScene) {
                    scale *= detector.scaleFactor
                    scale = scale.coerceIn(0.01f, 100.0f)
                }
                else if ((renderer.innerScale in 0.1f..10f) ||
                    (renderer.innerScale < 0.1f && detector.scaleFactor <= 1f) ||
                    (renderer.innerScale > 10f && detector.scaleFactor >= 1f)) {
                    Log.e("rege", "🍷")
                    renderer.innerScale *= detector.scaleFactor
                    renderer.graphGridlines.scale = renderer.innerScale
                    immutableScale *= detector.scaleFactor
                    graphViewModel.size *= detector.scaleFactor.toDouble()
                }

                val focusPoint = Point2D(detector.focusX.toDouble(), detector.focusY.toDouble())
                val dPoint = (focusPoint - lastTouchPoint) * 0.5
                azimuth += dPoint.x.toFloat()
                elevation += dPoint.y.toFloat()
                lastTouchPoint = focusPoint

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
}