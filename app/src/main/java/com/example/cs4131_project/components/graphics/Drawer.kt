package com.example.cs4131_project.components.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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

    fun drawQuad(point1: Point2D, point2: Point2D, point3: Point2D, point4: Point2D, color: Paint) {
        canvas.drawPath(
            Path().apply {
                moveTo(point1.x.toFloat(), point1.y.toFloat())
                lineTo(point2.x.toFloat(), point2.y.toFloat())
                lineTo(point3.x.toFloat(), point3.y.toFloat())
                lineTo(point4.x.toFloat(), point4.y.toFloat())
                lineTo(point1.x.toFloat(), point1.y.toFloat())
            }, color
        )
    }

    fun drawLines(points: ArrayList<Point2D>, color: Paint) {
        if (points.isEmpty()) return

        val lineVertices = FloatArray(points.size * 4 - 4)
        for (index in 1..<points.size) {
            lineVertices[index * 4 - 4] = points[index - 1].x.toFloat()
            lineVertices[index * 4 - 3] = points[index - 1].y.toFloat()
            lineVertices[index * 4 - 2] = points[index].x.toFloat()
            lineVertices[index * 4 - 1] = points[index].y.toFloat()
        }

        canvas.drawLines(lineVertices, color)
    }

    fun drawText(text: String, position: Point2D, color: Paint) {
        val textWidth = color.measureText(text)

        val fontMetrics = color.fontMetrics

        canvas.drawText(text, position.x.toFloat() - textWidth - 10.0f, position.y.toFloat() - fontMetrics.top - 5.0f, color)
    }
}