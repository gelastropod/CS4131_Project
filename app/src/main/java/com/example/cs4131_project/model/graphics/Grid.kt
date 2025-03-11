package com.example.cs4131_project.model.graphics

import android.content.Context
import android.graphics.Shader
import android.opengl.GLES30
import android.util.Log
import com.example.cs4131_project.model.utility.Point

class Grid(
    val context: Context,
    var x: Float,
    var y: Float,
    val width: Int,
    val height: Int,
    val spaceWidth: Float,
    val spaceHeight: Float,
    val color: FloatArray,
) {
    val vertices: FloatArray
    val indices: ShortArray
    val shaderProgram: ShaderProgram
    val vao: VAO
    val vbo: VBO
    val ebo: EBO
    var positionHandle: Int = 0
    var colorHandle: Int = 0

    fun <T> concat(a: ArrayList<T>, b: ArrayList<T>) {
        a.addAll(b)
    }

    fun concatFloat(a: ArrayList<Float>, b: FloatArray) {
        for (value in b)
            a.add(value)
    }

    init {
        x %= spaceWidth
        y %= spaceHeight
        x += spaceWidth
        y += spaceHeight
        x %= spaceWidth
        y %= spaceHeight
        x--
        y--

        val verticesArrayList = arrayListOf<Float>()

        var currentX = x
        while (currentX < 1f) {
            concat(verticesArrayList, arrayListOf(currentX, -1f, 0f))
            concatFloat(verticesArrayList, color)
            concat(verticesArrayList, arrayListOf(currentX, 1f, 0f))
            concatFloat(verticesArrayList, color)

            currentX += spaceWidth
        }

        var currentY = y
        while (currentY < 1f) {
            concat(verticesArrayList, arrayListOf(-1f, currentY, 0f))
            concatFloat(verticesArrayList, color)
            concat(verticesArrayList, arrayListOf(1f, currentY, 0f))
            concatFloat(verticesArrayList, color)

            currentY += spaceHeight
        }

        vertices = verticesArrayList.toFloatArray()

        val indicesArrayList = arrayListOf<Short>()

        for (index in 0..<verticesArrayList.size) {
            indicesArrayList.add(index.toShort())
        }

        indices = indicesArrayList.toShortArray()

        shaderProgram = ShaderProgram(context, "fshader", "vshader")

        shaderProgram.bind()

        // Get attribute and uniform locations
        positionHandle = shaderProgram.getAttribute("vPosition")
        colorHandle = shaderProgram.getAttribute("vColor")

        Log.d("positions", "$colorHandle")

        // Create objects
        vao = VAO()
        vbo = VBO(vertices)
        ebo = EBO(indices)

        vao.enableAttributePointer(positionHandle, colorHandle, vbo)
    }

    fun draw() {
        shaderProgram.bind()

        vao.draw(positionHandle, colorHandle, ebo, GLES30.GL_LINES)
    }
}