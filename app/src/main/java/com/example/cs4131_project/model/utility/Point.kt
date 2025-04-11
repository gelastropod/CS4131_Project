package com.example.cs4131_project.model.utility

import android.graphics.Color
import android.graphics.Color.blue
import android.graphics.Color.green
import android.graphics.Color.red
import android.graphics.Paint
import kotlin.math.sqrt
import androidx.compose.ui.graphics.Color as Color2

data class Point(val x: Double, val y: Double, val z: Double) {
    constructor(point: Point4D): this(point.x, point.y, point.z)

    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y, z + other.z)
    }

    operator fun unaryMinus(): Point {
        return Point(-x, -y, -z)
    }

    operator fun minus(other: Point): Point {
        return this + -other
    }

    operator fun times(scalar: Double): Point {
        return Point(x * scalar, y * scalar, z * scalar)
    }

    operator fun div(scalar: Double): Point {
        return this * (1f / scalar)
    }

    operator fun div(other: Point): Point {
        return Point(x / other.x, y / other.y, z / other.z)
    }

    operator fun times(other: Point): Double {
        return x * other.x + y * other.y + z * other.z
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }

    fun normalize(): Point {
        val length = sqrt(this * this)
        return this / length
    }

    fun cross(other: Point): Point {
        return Point(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )
    }

    companion object {
        fun toPoint(paint: Paint): Point {
            val color = paint.color
            return Point(red(color) / 255.0, green(color) / 255.0, blue(color) / 255.0)
        }
    }

    fun toColor(): Color2 {
        return Color2(x.toFloat(), y.toFloat(), z.toFloat(), 1f)
    }

    fun toPaint() : Paint {
        return Paint().apply {
            color = Color.rgb(x.toFloat(), y.toFloat(), z.toFloat())
            style = Paint.Style.FILL
        }
    }

    fun toLinePaint(stroke: Float) : Paint {
        return toPaint().apply{
            strokeWidth = stroke
            style = Paint.Style.STROKE
        }
    }

    fun toTextPaint(textSize: Float) : Paint {
        return toPaint().apply {
            isAntiAlias = true
            this.textSize = textSize
            isAntiAlias = true
        }
    }
}