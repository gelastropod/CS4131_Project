package com.example.cs4131_project.model.utility

import android.graphics.Color
import android.graphics.Color.blue
import android.graphics.Color.green
import android.graphics.Color.red
import android.graphics.Paint
import androidx.compose.ui.graphics.Color as Color2

data class Point4D(val x: Double, val y: Double, val z: Double, val w: Double) {
    constructor(point: Point) : this(point.x, point.y, point.z, 1.0)
    constructor(values: FloatArray) : this(values[0].toDouble(), values[1].toDouble(), values[2].toDouble(), values[3].toDouble())

    operator fun plus(other: Point4D): Point4D {
        return Point4D(x + other.x, y + other.y, z + other.z, w + other.w)
    }

    operator fun unaryMinus(): Point4D {
        return Point4D(-x, -y, -z, -w)
    }

    operator fun minus(other: Point4D): Point4D {
        return this + -other
    }

    operator fun times(scalar: Double): Point4D {
        return Point4D(x * scalar, y * scalar, z * scalar, w * scalar)
    }

    operator fun div(scalar: Double): Point4D {
        return this * (1f / scalar)
    }

    operator fun div(other: Point4D): Point4D {
        return Point4D(x / other.x, y / other.y, z / other.z, w / other.w)
    }

    operator fun times(other: Point4D): Double {
        return x * other.x + y * other.y + z * other.z + w * other.w
    }

    override fun toString(): String {
        return "($x, $y, $z, $w)"
    }
}