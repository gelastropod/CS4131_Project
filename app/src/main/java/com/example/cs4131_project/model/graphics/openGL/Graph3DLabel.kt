package com.example.cs4131_project.model.graphics.openGL

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.cs4131_project.model.graph.Graph3ViewModel
import com.example.cs4131_project.model.utility.Point
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.opengl.GLUtils
import android.util.Log

class Graph3DLabel(
    graphViewModel: Graph3ViewModel,
    backgroundColorPoint: Point,
    darkTheme: Boolean,
    location: Point,
    vertical: Boolean,
    val id: Int,
    text: String,
    textSize: Float = 64f,
    scale: Float = 1.0f
) {
    private val lineColorPoint = if (darkTheme) Point(1.0, 1.0, 1.0) else Point(0.0, 0.0, 0.0)

    private fun createTextBitmap(text: String, textSize: Float): Bitmap {
        val paint = lineColorPoint.toTextPaint(textSize)

        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)

        val bitmapWidth = textBounds.width() + 20
        val bitmapHeight = textBounds.height() + 20

        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawText(text, 10f, textBounds.height().toFloat(), paint)

        return bitmap.copy(Bitmap.Config.ARGB_8888, false)
    }

    val bitmap = createTextBitmap(text, textSize)
    val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

    private val vertexData = if (vertical) {
        floatArrayOf(
            location.x.toFloat() - aspectRatio * 0.025f * scale, location.y.toFloat() + 0.025f * scale, location.z.toFloat(), 0f, 0f,
            location.x.toFloat() - aspectRatio * 0.025f * scale, location.y.toFloat() - 0.025f * scale, location.z.toFloat(), 0f, 1f,
            location.x.toFloat() + aspectRatio * 0.025f * scale, location.y.toFloat() - 0.025f * scale, location.z.toFloat(), 1f, 1f,
            location.x.toFloat() + aspectRatio * 0.025f * scale, location.y.toFloat() + 0.025f * scale, location.z.toFloat(), 1f, 0f
        )
    }
    else {
        floatArrayOf(
            location.x.toFloat() - aspectRatio * 0.025f * scale, location.y.toFloat(), location.z.toFloat() + 0.025f * scale, 0f, 1f,
            location.x.toFloat() - aspectRatio * 0.025f * scale, location.y.toFloat(), location.z.toFloat() - 0.025f * scale, 0f, 0f,
            location.x.toFloat() + aspectRatio * 0.025f * scale, location.y.toFloat(), location.z.toFloat() - 0.025f * scale, 1f, 0f,
            location.x.toFloat() + aspectRatio * 0.025f * scale, location.y.toFloat(), location.z.toFloat() + 0.025f * scale, 1f, 1f
        )
    }

    private val indexData = shortArrayOf(0, 1, 2, 0, 2, 3)

    private val vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(vertexData)
            position(0)
        }

    private val indexBuffer = ByteBuffer.allocateDirect(indexData.size * 2)
        .order(ByteOrder.nativeOrder()).asShortBuffer().apply {
            put(indexData)
            position(0)
        }

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 a_Position;
        attribute vec2 a_TexCoord;
        varying vec2 v_TexCoord;
        varying vec4 vWorldPos;
        void main() {
            gl_Position = uMVPMatrix * a_Position;
            v_TexCoord = a_TexCoord;
            vWorldPos = a_Position;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        uniform sampler2D u_Texture;
        varying vec2 v_TexCoord;
        varying vec4 vWorldPos;
        uniform float uScale;
        void main() {
            vec4 scaledPos = vWorldPos * uScale;  // Use a local variable
            if (abs(scaledPos.x) > 1.01 || abs(scaledPos.y) > 1.01 || abs(scaledPos.z) > 1.01) {
                discard;
            }
            vec4 fragColor = texture2D(u_Texture, v_TexCoord);
            if (fragColor.a < 0.01) discard;
            gl_FragColor = fragColor;
        }
    """

    private var initialized = false

    private var program: Int = 0
    private var textureId: Int = 0

    fun initialize() {
        if (initialized) return

        program = GLES20.glCreateProgram().also {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        textureId = id.also{loadTexture(bitmap, it)}

        initialized = true


        Log.e("AAA", "AAA")
    }

    fun draw(modelMatrix: FloatArray, scale: Float) {
        if (!initialized) return

        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        val texCoordHandle = GLES20.glGetAttribLocation(program, "a_TexCoord")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        val textureHandle = GLES20.glGetUniformLocation(program, "u_Texture")

        val scaleHandle = GLES20.glGetUniformLocation(program, "uScale")
        GLES20.glUniform1f(scaleHandle, scale * 0.1f)

        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 5 * 4, vertexBuffer)
        GLES20.glEnableVertexAttribArray(positionHandle)

        vertexBuffer.position(3)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, vertexBuffer)
        GLES20.glEnableVertexAttribArray(texCoordHandle)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0f, 0f, 0f, -5f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexData.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    private fun loadTexture(bitmap: Bitmap, id: Int) {
        //Log.e("AAA", id.toString())

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
    }

    private fun loadShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)
        return shader
    }
}
