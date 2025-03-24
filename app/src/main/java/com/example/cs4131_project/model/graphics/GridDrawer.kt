package com.example.cs4131_project.model.graphics

import android.graphics.Color
import android.util.Log
import com.example.cs4131_project.components.graphics.Drawer
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point2D
import kotlin.math.sign

class GridDrawer(var drawer: Drawer) {
    private fun properMod(a: Double, b: Double) : Double {
        return ((a % b) + b) % b
    }

    fun draw(minorSpace: Point2D, majorSpace: Point2D, minBound: Point2D, maxBound: Point2D, color: Point, background: Point) {
        val startX = minBound.x - (properMod(minBound.x, minorSpace.x)) + minorSpace.x
        val startY = minBound.y - (properMod(minBound.y, minorSpace.y)) + minorSpace.y
        val startIndexX = startX / minorSpace.x
        val startIndexY = startY / minorSpace.y
        val numSpaceX = ((maxBound.x - startX) / minorSpace.x).toInt()
        val numSpaceY = ((maxBound.y - startY) / minorSpace.y).toInt()
        val offsetX = startX - minBound.x
        val offsetY = startY - minBound.y
        val size = drawer.getSize()
        val scale = size / (maxBound - minBound)

        val minorLineColor = color * 0.1 + background * 0.9
        val majorLineColor = color * 0.25 + background * 0.75

        for (index in 0..numSpaceX) {
            val currentX = (offsetX + index * minorSpace.x) * scale.x
            if (Math.abs(minBound.x + currentX) <= 1e-6) continue
            val colorUsed = if (index % majorSpace.x.toInt() == startIndexX.toInt()) majorLineColor else minorLineColor
            drawer.drawLine(Point2D(currentX, 0.0), Point2D(currentX, size.y), colorUsed.toPaint())
        }

        for (index in 0..numSpaceY) {
            val currentY = (offsetY + index * minorSpace.y) * scale.y
            if (Math.abs(minBound.y + currentY) <= 1e-6) continue
            val colorUsed = if (index % majorSpace.y.toInt() == startIndexY.toInt()) majorLineColor else minorLineColor
            drawer.drawLine(Point2D(0.0, size.y - currentY), Point2D(size.x, size.y - currentY), colorUsed.toPaint())
        }

        if (minBound.x.sign != maxBound.x.sign) {
            drawer.drawLine(Point2D(-minBound.x * scale.x, 0.0), Point2D(-minBound.x * scale.x, size.y), color.toPaint())
        }

        if (minBound.y.sign != maxBound.y.sign) {
            drawer.drawLine(Point2D(0.0, size.y + minBound.y * scale.y), Point2D(size.x, size.y + minBound.y * scale.y), color.toPaint())
        }
    }
}