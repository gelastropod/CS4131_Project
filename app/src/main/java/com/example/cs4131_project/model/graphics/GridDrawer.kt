package com.example.cs4131_project.model.graphics

import android.graphics.Color
import android.util.Log
import com.example.cs4131_project.components.graphics.Drawer
import com.example.cs4131_project.model.utility.Point
import com.example.cs4131_project.model.utility.Point2D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.math.sign

class GridDrawer(var drawer: Drawer) {
    companion object {
        private val correctionFactor = Point2D(1.0, -1.0)
    }

    private fun properMod(a: Double, b: Double) : Double {
        if (Math.abs(a % b) <= 1e-6)
            return b
        return ((a % b) + b) % b
    }

    private fun round(x: Double, dp: Int, power10: Int): String {
        if (power10 in -3..-1)
            return String.format("%.${1 - power10}f", x)
        if (power10 in 0..3)
            return String.format("%.${dp}f", x)
        val newX = x / Math.pow(10.0, power10.toDouble())
        return String.format("%.${dp}fe$power10", newX)
    }

    fun draw(minorSpace: Point2D, majorSpace: Point2D, minBound: Point2D, maxBound: Point2D, color: Point, background: Point, power10: Int) {
        val startX = minBound.x - properMod(minBound.x, minorSpace.x) + minorSpace.x
        val startY = minBound.y - properMod(minBound.y, minorSpace.y) + minorSpace.y
        val startIndexX = startX / minorSpace.x
        val startIndexY = startY / minorSpace.y
        val numSpaceX = ((maxBound.x - startX) / minorSpace.x).toInt()
        val numSpaceY = ((maxBound.y - startY) / minorSpace.y).toInt()
        val offsetX = startX - minBound.x
        val offsetY = startY - minBound.y
        val size = drawer.getSize()
        val scale = size / (maxBound - minBound)
        val stupidConstant = size * Point2D(0.0, 1.0)

        val minorLineColor = color * 0.1 + background * 0.9
        val majorLineColor = color * 0.25 + background * 0.75

        for (index in 0..numSpaceX) {
            val currentX = (offsetX + index * minorSpace.x) * scale.x
            if (Math.abs(minBound.x + currentX) <= 1e-6) continue
            val tickMark = ((startX + index * minorSpace.x) / minorSpace.x).roundToInt()
            val isMajor = tickMark % majorSpace.x.roundToInt() == 0
            val colorUsed = if (isMajor) majorLineColor else minorLineColor
            drawer.drawLine(Point2D(currentX, 0.0), Point2D(currentX, size.y), colorUsed.toPaint())
        }

        for (index in 0..numSpaceY) {
            val currentY = (offsetY + index * minorSpace.y) * scale.y
            if (Math.abs(minBound.y + currentY) <= 1e-6) continue
            val tickMark = ((startY + index * minorSpace.y) / minorSpace.y).roundToInt()
            val isMajor = tickMark % majorSpace.y.roundToInt() == 0
            val colorUsed = if (isMajor) majorLineColor else minorLineColor
            drawer.drawLine(stupidConstant - Point2D(0.0, currentY), stupidConstant - Point2D(-size.x, currentY), colorUsed.toPaint())
        }

        if (minBound.x.sign != maxBound.x.sign) {
            drawer.drawLine(Point2D(-minBound.x * scale.x, 0.0), Point2D(-minBound.x * scale.x, size.y), color.toLinePaint(3f))
        }

        if (minBound.y.sign != maxBound.y.sign) {
            drawer.drawLine(Point2D(0.0, size.y + minBound.y * scale.y), Point2D(size.x, size.y + minBound.y * scale.y), color.toLinePaint(3f))
        }

        for (index in 0..(numSpaceX + 2)) {
            val currentX = (offsetX + index * minorSpace.x) * scale.x
            val tickMark = ((startX + index * minorSpace.x) / minorSpace.x).roundToInt()
            val isMajor = tickMark % majorSpace.x.roundToInt() == 0
            if (minBound.y.sign != maxBound.y.sign && isMajor && tickMark != 0) {
                drawer.drawText(round(startX + index * minorSpace.x, 1, power10 + 1), Point2D(currentX, size.y + minBound.y * scale.y), color.toTextPaint(40f))
            }
        }

        for (index in 0..(numSpaceY + 2)) {
            val currentY = (offsetY + index * minorSpace.y) * scale.y
            val tickMark = ((startY + index * minorSpace.y) / minorSpace.y).roundToInt()
            val isMajor = tickMark % majorSpace.y.roundToInt() == 0
            if (minBound.x.sign != maxBound.x.sign && isMajor && tickMark != 0) {
                drawer.drawText(round(startY + index * minorSpace.y, 1, power10 + 1), size - Point2D(size.x + minBound.x * scale.x, currentY), color.toTextPaint(40f))
            }
        }

        if (minBound.x.sign != maxBound.x.sign && minBound.y.sign != maxBound.y.sign) {
            drawer.drawText("0",  stupidConstant - minBound * scale * correctionFactor, color.toTextPaint(40f))
        }
    }

    private fun convertToUV(minBound: Point2D, maxBound: Point2D, point: Point2D): Point2D {
        return (point - minBound) / (maxBound - minBound)
    }

    fun drawGraph(minBound: Point2D, maxBound: Point2D, color: Point, background: Point, precision: Int, equation: (Double) -> Double?) {
        val size = drawer.getSize()
        val stupidConstant = size * Point2D(0.0, 1.0)

        val points = arrayListOf<Point2D>()

        for (value in 1..precision) {
            val prevX = minBound.x + ((value - 1).toDouble() / precision) * (maxBound.x - minBound.x)
            val crntX = minBound.x + (value.toDouble() / precision) * (maxBound.x - minBound.x)
            val prevY = equation(prevX)
            val crntY = equation(crntX)
            if (prevY == null || crntY == null) continue
            if ((prevY < minBound.y && crntY < minBound.y) || (prevY > maxBound.y && crntY > maxBound.y)) continue
            val prev = stupidConstant + convertToUV(
                minBound,
                maxBound,
                Point2D(prevX, prevY)
            ) * size * correctionFactor
            val crnt = stupidConstant + convertToUV(
                minBound,
                maxBound,
                Point2D(crntX, crntY)
            ) * size * correctionFactor
            points.add(prev)
            points.add(crnt)
        }

        drawer.drawLines(points, color.toLinePaint(2f))
    }
}