package com.example.cs4131_project.components.graphics

import android.graphics.Canvas
import android.graphics.Paint
import com.example.cs4131_project.model.utility.Point2D

class Drawer(var canvas: Canvas) {
    // Doesn't work :c
    fun drawPoint(point: Point2D, color: Paint) {
        canvas.drawPoint(point.x, point.y, color)
    }

    fun drawLine(start: Point2D, end: Point2D, color: Paint) {
        canvas.drawLine(start.x, start.y, end.x, end.y, color)
    }
}