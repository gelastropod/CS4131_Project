package com.example.cs4131_project.model.utility

data class Point(val x: Float, val y: Float, val z: Float) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y, z + other.z)
    }

    operator fun unaryMinus(): Point {
        return Point(-x, -y, -z)
    }

    operator fun minus(other: Point): Point {
        return this + -other
    }

    operator fun times(scalar: Float): Point {
        return Point(x * scalar, y * scalar, z * scalar)
    }

    operator fun div(scalar: Float): Point {
        return this * (1f / scalar)
    }

    operator fun times(other: Point): Float {
        return x * other.x + y * other.y + z * other.z
    }
}