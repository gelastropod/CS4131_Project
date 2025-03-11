package com.example.cs4131_project.model.graphics

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.nio.ShortBuffer

class EBO(
    val indices: ShortArray
) {
    val indexBuffer: ShortBuffer

    init {
        val byteBuffer = ByteBuffer.allocateDirect(indices.size * 2).order(ByteOrder.nativeOrder())
        indexBuffer = byteBuffer.asShortBuffer().apply {
            put(indices)
            position(0)
        }
    }
}