package com.example.cs4131_project.model.utility

import android.graphics.Color
import android.graphics.Color.blue
import android.graphics.Color.green
import android.graphics.Color.red
import android.graphics.Paint

data class Point(val x: Double, val y: Double, val z: Double) {
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

    operator fun times(other: Point): Double {
        return x * other.x + y * other.y + z * other.z
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }

    companion object {
        fun toPoint(paint: Paint): Point {
            val color = paint.color
            return Point(red(color) / 255.0, green(color) / 255.0, blue(color) / 255.0)
        }
    }

    fun toPaint() : Paint {
        return Paint().apply {
            color = Color.rgb(x.toFloat(), y.toFloat(), z.toFloat())
            style = Paint.Style.FILL
        }
    }
}