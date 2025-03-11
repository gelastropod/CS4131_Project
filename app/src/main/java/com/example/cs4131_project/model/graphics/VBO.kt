package com.example.cs4131_project.model.graphics

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VBO(
    val vertices: FloatArray
) {
    val vertexBuffer: FloatBuffer

    init {
        val byteBuffer = ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuffer.asFloatBuffer().apply {
            put(vertices)
            position(0)
        }
    }
}