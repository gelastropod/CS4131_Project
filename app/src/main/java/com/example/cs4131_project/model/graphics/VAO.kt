package com.example.cs4131_project.model.graphics

import android.opengl.GLES30

class VAO {
    fun enableAttributePointer(location: Int, color: Int, vbo: VBO) {
        GLES30.glVertexAttribPointer(location, 3, GLES30.GL_FLOAT, false, 6 * 4, vbo.vertexBuffer)
        GLES30.glEnableVertexAttribArray(location)

        GLES30.glVertexAttribPointer(color, 3, GLES30.GL_FLOAT, false, 6 * 4, vbo.vertexBuffer.position(3))
        GLES30.glEnableVertexAttribArray(color)
    }

    fun draw(location: Int, color: Int, ebo: EBO, mode: Int) {
        GLES30.glEnableVertexAttribArray(location)
        GLES30.glEnableVertexAttribArray(color)
        GLES30.glDrawElements(mode, ebo.indices.size, GLES30.GL_UNSIGNED_SHORT, ebo.indexBuffer)
        GLES30.glDisableVertexAttribArray(location)
        GLES30.glDisableVertexAttribArray(color)
    }
}