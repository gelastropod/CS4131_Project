package com.example.cs4131_project.model.utility

data class Matrix(val data: DoubleArray) {
    operator fun plus(other: Matrix): Matrix {
        return Matrix(doubleArrayOf(
            data[0] + other.data[0], data[1] + other.data[1], data[2] + other.data[2],
            data[3] + other.data[3], data[4] + other.data[4], data[5] + other.data[5],
            data[6] + other.data[6], data[7] + other.data[7], data[8] + other.data[8]
        ))
    }

    operator fun unaryMinus(): Matrix {
        return Matrix(doubleArrayOf(
            -data[0], -data[1], -data[2],
            -data[3], -data[4], -data[5],
            -data[6], -data[7], -data[8]
        ))
    }

    operator fun minus(other: Matrix): Matrix {
        return this + -other
    }

    operator fun times(scalar: Double): Matrix {
        return Matrix(doubleArrayOf(
            data[0] * scalar, data[1] * scalar, data[2] * scalar,
            data[3] * scalar, data[4] * scalar, data[5] * scalar,
            data[6] * scalar, data[7] * scalar, data[8] * scalar
        ))
    }

    operator fun div(scalar: Double): Matrix {
        return this * (1f / scalar)
    }

    operator fun times(other: Point): Point {
        return Point(
            data[0] * other.x + data[1] * other.y + data[2] * other.z,
            data[3] * other.x + data[4] * other.y + data[5] * other.z,
            data[6] * other.x + data[7] * other.y + data[8] * other.z
        )
    }

    operator fun times(other: Matrix): Matrix {
        return Matrix(doubleArrayOf(
            data[0] * other.data[0] + data[1] * other.data[3] + data[2] * other.data[6], data[0] * other.data[1] + data[1] * other.data[4] + data[2] * other.data[7], data[0] * other.data[2] + data[1] * other.data[5] + data[2] * other.data[8],
            data[3] * other.data[0] + data[4] * other.data[3] + data[5] * other.data[6], data[3] * other.data[1] + data[4] * other.data[4] + data[5] * other.data[7], data[3] * other.data[2] + data[4] * other.data[5] + data[5] * other.data[8],
            data[6] * other.data[0] + data[7] * other.data[3] + data[8] * other.data[6], data[6] * other.data[1] + data[7] * other.data[4] + data[8] * other.data[7], data[6] * other.data[2] + data[7] * other.data[5] + data[8] * other.data[8]
        ))
    }

    override fun toString(): String {
        return "(${data[0]}, ${data[1]}, ${data[2]}," +
                "${data[3]}, ${data[4]}, ${data[5]}," +
                "${data[6]}, ${data[7]}, ${data[8]})"
    }
}