package com.example.cs4131_project.model.utility

data class Matrix(val data: DoubleArray) {
    operator fun plus(other: Matrix): Matrix {
        return Matrix(doubleArrayOf(
            data[0] + other.data[0], data[1] + other.data[1], data[2] + other.data[2], data[3] + other.data[3],
            data[4] + other.data[4], data[5] + other.data[5], data[6] + other.data[6], data[7] + other.data[7],
            data[8] + other.data[8], data[9] + other.data[9], data[10] + other.data[10], data[11] + other.data[11],
            data[12] + other.data[12], data[13] + other.data[13], data[14] + other.data[14], data[15] + other.data[15]
        ))
    }

    operator fun unaryMinus(): Matrix {
        return Matrix(doubleArrayOf(
            -data[0], -data[1], -data[2], -data[3],
            -data[4], -data[5], -data[6], -data[7],
            -data[8], -data[9], -data[10], -data[11],
            -data[12], -data[13], -data[14], -data[15]
        ))
    }

    operator fun minus(other: Matrix): Matrix {
        return this + -other
    }

    operator fun times(scalar: Double): Matrix {
        return Matrix(doubleArrayOf(
            data[0] * scalar, data[1] * scalar, data[2] * scalar, data[3] * scalar,
            data[4] * scalar, data[5] * scalar, data[6] * scalar, data[7] * scalar,
            data[8] * scalar, data[9] * scalar, data[10] * scalar, data[11] * scalar,
            data[12] * scalar, data[13] * scalar, data[14] * scalar, data[15] * scalar
        ))
    }

    operator fun div(scalar: Double): Matrix {
        return this * (1f / scalar)
    }

    operator fun times(other: Point4D): Point4D {
        return Point4D(
            data[0] * other.x + data[1] * other.y + data[2] * other.z + data[3] * other.w,
            data[4] * other.x + data[5] * other.y + data[6] * other.z + data[7] * other.w,
            data[8] * other.x + data[9] * other.y + data[10] * other.z + data[11] * other.w,
            data[12] * other.x + data[13] * other.y + data[14] * other.z + data[15] * other.w
        )
    }

    operator fun times(other: Matrix): Matrix {
        val result = DoubleArray(16)
        for (row in 0..3) {
            for (col in 0..3) {
                var sum = 0.0
                for (i in 0..3) {
                    sum += this.data[row * 4 + i] * other.data[i * 4 + col]
                }
                result[row * 4 + col] = sum
            }
        }
        return Matrix(result)
    }

    override fun toString(): String {
        return "(${data[0]}, ${data[1]}, ${data[2]}, ${data[3]},\n" +
                "${data[4]}, ${data[5]}, ${data[6]}, ${data[7]},\n" +
                "${data[8]}, ${data[9]}, ${data[10]}, ${data[11]},\n" +
                "${data[12]}, ${data[13]}, ${data[14]}, ${data[15]})"
    }
}