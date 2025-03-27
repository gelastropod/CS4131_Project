package com.example.cs4131_project.model.utility

data class Point2D(var x: Double, var y: Double) {
    companion object {
        fun point(point: Point): Point2D {
            return Point2D(point.x, point.z)
        }
    }

    constructor() : this(0.0, 0.0)

    private fun isEqual(x: Double, y: Double): Boolean {
        return Math.abs(x - y) < 0.01
    }

    operator fun plus(other: Point2D): Point2D {
        return Point2D(x + other.x, y + other.y)
    }

    operator fun unaryMinus(): Point2D {
        return Point2D(-x, -y)
    }

    operator fun minus(other: Point2D): Point2D {
        return this + -other
    }

    operator fun times(scalar: Double): Point2D {
        return Point2D(x * scalar, y * scalar)
    }

    operator fun div(scalar: Double): Point2D {
        return this * (1f / scalar)
    }

    operator fun div(other: Point2D): Point2D {
        return Point2D(x / other.x, y / other.y)
    }

    operator fun times(other: Point2D): Point2D {
        return Point2D(x * other.x, y * other.y)
    }

    fun equals(other: Point2D): Boolean {
        return isEqual(x, other.x) && isEqual(y, other.y)
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}