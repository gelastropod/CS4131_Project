package com.example.cs4131_project.components.graphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point2D

class GraphRenderer2D(context: Context) : View(context) {
    private val paint = Paint().apply {color = Color.RED}
    private val points: ArrayList<Point> = arrayListOf()
    private val handler = Handler(Looper.getMainLooper())
    private var frameStart = SystemClock.elapsedRealtime()
    private var drawer = Drawer(Canvas())
    private val point = Point2D(100f, 100f)

    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentTime = SystemClock.elapsedRealtime()
            val elapsedTime = 0.001f * (currentTime - frameStart)
            frameStart = currentTime

            invalidate()
            handler.postDelayed(this, 8)
        }
    }

    init {
        handler.post(updateRunnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawer.canvas = canvas

        drawer.drawLine(point, point * 2f, paint)
    }
}