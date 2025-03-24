package com.example.cs4131_project.components.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.cs4131_project.model.utility.Point2D

class Drawer(var canvas: Canvas) {
    companion object {
        fun toPaint(color: Int): Paint {
            return Paint().apply {
                this.color = color
                style = Paint.Style.FILL
            }
        }
    }

    fun getSize(): Point2D {
        return Point2D(canvas.width.toDouble(), canvas.height.toDouble())
    }

    // Doesn't work :c
    fun drawPoint(point: Point2D, color: Paint) {
        canvas.drawPoint(point.x.toFloat(), point.y.toFloat(), color)
    }

    fun drawLine(start: Point2D, end: Point2D, color: Paint) {
        canvas.drawLine(start.x.toFloat(), start.y.toFloat(), end.x.toFloat(), end.y.toFloat(), color)
    }

    fun drawLine(start: Point2D, end: Point2D, color: Int) {
        drawLine(start, end, toPaint(color))
    }
}