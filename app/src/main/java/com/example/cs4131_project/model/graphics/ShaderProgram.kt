package com.example.cs4131_project.model.graphics

import android.content.Context
import android.opengl.GLES30
import android.util.Log

class ShaderProgram(
    val context: Context,
    val fragmentSource: String,
    val vertexSource: String
) {
    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES30.glCreateShader(type)
        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)

        // Check for compilation errors
        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == GLES30.GL_FALSE) {
            val infoLog = GLES30.glGetShaderInfoLog(shader)
            Log.e("ShaderProgram", "Shader compilation failed: $infoLog")
        }
        return shader
    }

    private fun createShader(): Int {
        val fragmentInputStream = context.resources.openRawResource(context.resources.getIdentifier(fragmentSource, "raw", context.packageName))
        val fragmentShaderCode = fragmentInputStream.bufferedReader().use { it.readText() }

        val vertexInputStream = context.resources.openRawResource(context.resources.getIdentifier(vertexSource, "raw", context.packageName))
        val vertexShaderCode = vertexInputStream.bufferedReader().use {it.readText()}

        val program = GLES30.glCreateProgram()

        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        GLES30.glAttachShader(program, vertexShader)
        GLES30.glAttachShader(program, fragmentShader)

        GLES30.glLinkProgram(program)

        // Check for linking errors
        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == GLES30.GL_FALSE) {
            val infoLog = GLES30.glGetProgramInfoLog(program)
            Log.e("ShaderProgram", "Program linking failed: $infoLog")
        }

        return program
    }

    var ID: Int = 0

    init {
        ID = createShader()
    }

    fun bind() {
        GLES30.glUseProgram(ID)
    }

    fun getAttribute(name: String): Int {
        return GLES30.glGetAttribLocation(ID, name)
    }

    fun getUniform(name: String): Int {
        return GLES30.glGetUniformLocation(ID, name)
    }
}