package com.example.cs4131_project.model.utility

data class Point2D(val x: Float, val y: Float) {
    companion object {
        fun point(point: Point): Point2D {
            return Point2D(point.x, point.y)
        }
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

    operator fun times(scalar: Float): Point2D {
        return Point2D(x * scalar, y * scalar)
    }

    operator fun div(scalar: Float): Point2D {
        return this * (1f / scalar)
    }

    operator fun times(other: Point2D): Float {
        return x * other.x + y * other.y
    }
}