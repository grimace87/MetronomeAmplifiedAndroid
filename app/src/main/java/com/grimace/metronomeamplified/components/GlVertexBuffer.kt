package com.grimace.metronomeamplified.components

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLES20
import com.grimace.metronomeamplified.utils.Font
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.abs

const val FLOAT_SIZE_BYTES = 4
const val FLOATS_PER_QUAD = 30
const val FLOATS_PER_VERTEX = 5
const val VERTICES_PER_QUAD = 6

abstract class GlVertexBuffer {

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
    abstract val subBufferVertexIndices: IntArray
    abstract val regionsOfInterest: Array<RectF>

    abstract fun activate(shader: GlShader)

    fun regionOfInterestAt(xNormalised: Float, yNormalised: Float): Int {
        regionsOfInterest.forEachIndexed { index, region ->
            if (region.contains(xNormalised, yNormalised)) {
                return index
            }
        }
        return -1
    }

    fun verticesInSubBuffer(index: Int): Int {
        return (subBufferVertexIndices[index + 1] - subBufferVertexIndices[index])
    }

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






