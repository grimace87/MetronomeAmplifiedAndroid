package com.grimace.metronomeamplified.sealed

import android.content.Context
import android.graphics.PointF
import android.opengl.GLES20
import com.grimace.metronomeamplified.utils.Font
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.abs

const val FLOAT_SIZE_BYTES = 4

sealed class GlVertexBuffer {

    protected companion object {

        private var orkneyInstance: Font? = null

        fun getOrkneyFontDescription(context: Context): Font {
            val font: Font = orkneyInstance ?: Font.fromAsset(context, "Orkney.fnt")
            orkneyInstance = font
            return font
        }
    }

    var vertexBufferHandle: Int = GLES20.GL_NONE
        private set

    private var isValid = false

    abstract val isWindowSizeDependent: Boolean

    protected abstract fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer

    fun invalidate() {
        isValid = false
    }

    fun generateNewVertexBuffer(context: Context, width: Int, height: Int) {

        // Don't regenerate needlessly
        if (isValid) {
            return
        }

        // Create a buffer object
        vertexBufferHandle = createBuffer()

        // Get vertex data for this screen size if possible, and load into the buffer
        updateIfNeeded(context, width, height)
    }

    fun updateIfNeeded(context: Context, width: Int, height: Int) {

        // Don't regenerate needlessly, or if the size is invalid
        if (isValid || width == 0 || height == 0 || vertexBufferHandle == GLES20.GL_NONE) {
            return
        }

        // Generate the data and upload it into the buffer object
        val vertexData = generateVerticesForSize(context, width, height)
        updateBuffer(vertexData)
        isValid = true
    }

    private fun createBuffer(): Int {
        val buffers = intArrayOf(0)
        GLES20.glGenBuffers(1, buffers, 0)
        return buffers[0]
    }

    private fun updateBuffer(bufferData: FloatBuffer) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferHandle)
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bufferData.capacity() * FLOAT_SIZE_BYTES, bufferData, GLES20.GL_STATIC_DRAW)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }

    protected fun FloatArray.toFloatBuffer(): FloatBuffer {
        val dataBuffer = ByteBuffer.allocateDirect(this.size * FLOAT_SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        dataBuffer.put(this)
            .position(0)
        return dataBuffer
    }

    protected fun FloatArray.putSquare(index: Int, x1: Float, y1: Float, x2: Float, y2: Float, s1: Float, t1: Float, s2: Float, t2: Float) {
        val squareFloats = floatArrayOf(
            x1, y1, 0.0f, s1, t1,
            x2, y1, 0.0f, s2, t1,
            x2, y2, 0.0f, s2, t2,
            x2, y2, 0.0f, s2, t2,
            x1, y2, 0.0f, s1, t2,
            x1, y1, 0.0f, s1, t1
        )
        squareFloats.copyInto(this, index)
    }

    protected fun FloatArray.putSquareCentredInside(index: Int, x1: Float, y1: Float, x2: Float, y2: Float, s1: Float, t1: Float, s2: Float, t2: Float, screenSize: PointF) {
        // Get units to pixels scaling factor, and use those to determine dimensions of requested rect in pixels
        val pixelsPerUnitWidth = screenSize.x / 2.0f
        val pixelsPerUnitHeight = screenSize.y / 2.0f
        val rectWidthPixels = abs(x2 - x1) * pixelsPerUnitWidth
        val rectHeightPixels = abs(y2 - y1) * pixelsPerUnitHeight

        // Figure out where the square lies in this rect (squareness is defined within pixel coordinates)
        if (rectWidthPixels > rectHeightPixels) {
            val direction = if (x1 > x2) -1.0f else 1.0f
            val widthMargin = direction * 0.5f * (rectWidthPixels - rectHeightPixels) / pixelsPerUnitWidth
            putSquare(index, x1 + widthMargin, y1, x2 - widthMargin, y2, s1, t1, s2, t2)
        } else {
            val direction = if (y1 > y2) -1.0f else 1.0f
            val heightMargin = direction * 0.5f * (rectHeightPixels - rectWidthPixels) / pixelsPerUnitHeight
            putSquare(index, x1, y1 + heightMargin, x2, y2 - heightMargin, s1, t1, s2, t2)
        }
    }
}

class MainScreenBackgroundVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {

        val displayMetrics = context.resources.displayMetrics
        val marginDips = 16.0f
        val marginUnitsW: Float = 2.0f * (marginDips * displayMetrics.density) / width.toFloat()
        val marginUnitsH: Float = 2.0f * (marginDips * displayMetrics.density) / height.toFloat()
        val screenSize = PointF(displayMetrics.widthPixels.toFloat(), displayMetrics.heightPixels.toFloat())

        val w1 = -1.0f + marginUnitsW
        val w2 = w1 + marginUnitsW
        val w4 = -1.0f + (2.0f - 2.0f * marginUnitsW) / 3.0f
        val w3 = w4 - marginUnitsW
        val w5 = w4 + marginUnitsW
        val w7 = 1.0f - (2.0f - 2.0f * marginUnitsW) / 3.0f
        val w6 = w7 - marginUnitsW
        val w8 = w7 + marginUnitsW
        val w10 = 1.0f - marginUnitsW
        val w9 = w10 - marginUnitsW

        val h1 = -1.0f + marginUnitsH
        val h2 = h1 + marginUnitsH
        val h4 = -1.0f + (2.0f - marginUnitsH) / 4.0f
        val h3 = h4 - marginUnitsH
        val h6 = 0.0f - marginUnitsH
        val h5 = h6 - marginUnitsH

        val hIcon1Left = -1.0f
        val hIcon2Left = -0.5f
        val hIcon3Left = 0.0f
        val hIcon4Left = 0.5f
        val hIcon4Right = 1.0f

        val hIconBottom = 0.7f
        val hIconLabelBottom = 0.5f
        val hIconTop = 1.0f
        val hLowerIconsLabelTop = h2 + 0.25f * (h3 - h2)

        val data = FloatArray(30 * 25)
        data.putSquare(0, -1f, -1f, 1f, 1f, 0f, 0f, 1f, 1f)

        data.putSquare(30, w1, h1, w2, h2, 0.0f, 0.0f, 0.5f, 0.5f)
        data.putSquare(60, w2, h1, w3, h2, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(90, w3, h1, w4, h2, 0.5f, 0.0f, 0.0f, 0.5f)

        data.putSquare(120, w7, h1, w8, h2, 0.0f, 0.0f, 0.5f, 0.5f)
        data.putSquare(150, w8, h1, w9, h2, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(180, w9, h1, w10, h2, 0.5f, 0.0f, 0.0f, 0.5f)

        data.putSquare(210, w1, h2, w2, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(240, w2, h3, w3, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(270, w3, h2, w4, h4, 0.5f, 0.0f, 1.0f, 0.5f)

        data.putSquare(300, w7, h2, w8, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(330, w8, h3, w9, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(360, w9, h2, w10, h4, 0.5f, 0.0f, 1.0f, 0.5f)

        data.putSquare(390, w4, h3, w5, h4, 0.5f, 1.0f, 0.0f, 0.5f)
        data.putSquare(420, w6, h3, w7, h4, 0.0f, 1.0f, 0.5f, 0.5f)

        data.putSquare(450, w1, h4, w10, h5, 0.5f, 0.0f, 1.0f, 0.5f)

        data.putSquare(480, w1, h5, w2, h6, 0.0f, 0.5f, 0.5f, 0.0f)
        data.putSquare(510, w2, h5, w9, h6, 0.5f, 0.5f, 1.0f, 0.0f)
        data.putSquare(540, w9, h5, w10, h6, 0.5f, 0.5f, 0.0f, 0.0f)

        data.putSquareCentredInside(570, hIcon1Left, hIconBottom, hIcon2Left, hIconTop, 0.0f, 0.5f, 0.25f, 0.0f, screenSize)
        data.putSquareCentredInside(600, hIcon2Left, hIconBottom, hIcon3Left, hIconTop, 0.25f, 0.5f, 0.5f, 0.0f, screenSize)
        data.putSquareCentredInside(630, hIcon3Left, hIconBottom, hIcon4Left, hIconTop, 0.5f, 0.5f, 0.75f, 0.0f, screenSize)
        data.putSquareCentredInside(660, hIcon4Left, hIconBottom, hIcon4Right, hIconTop, 0.75f, 0.5f, 1.0f, 0.0f, screenSize)
        data.putSquareCentredInside(690, w2, hLowerIconsLabelTop, w3, h3, 0.0f, 1.0f, 0.25f, 0.5f, screenSize)
        data.putSquareCentredInside(720, w8, hLowerIconsLabelTop, w9, h3, 0.25f, 1.0f, 0.5f, 0.5f, screenSize)

        return data.toFloatBuffer()
    }
}

class RandomTextVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {
        val font = getOrkneyFontDescription(context)
        val data = font.generateTextVbo("Hello world!", -0.8f, 0.2f, 1.6f, 0.4f, 2f, 1.0f)
        return data.toFloatBuffer()
    }
}
