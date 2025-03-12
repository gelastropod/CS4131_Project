package com.example.cs4131_project.components.graphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View

class GraphRenderer2D(context: Context) : View(context) {
    private val paint = Paint().apply {color = Color.RED}
    private var x = 0f
    private var y = 0f
    private var z = 0f
    private val points: ArrayList<Point>
    private val handler = Handler(Looper.getMainLooper())
    private var frameStart = SystemClock.elapsedRealtime()

    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentTime = SystemClock.elapsedRealtime()
            val elapsedTime = 0.001f * (currentTime - frameStart)
            frameStart = currentTime
            y += 1000f * elapsedTime
            if (y > height) y = 0f
            invalidate()
            handler.postDelayed(this, 8)
        }
    }

    init {
        handler.post(updateRunnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawCircle(x, y, 50f, paint)
    }
}