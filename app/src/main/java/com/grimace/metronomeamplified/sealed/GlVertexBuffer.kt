package com.grimace.metronomeamplified.sealed

import android.content.res.Resources
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

const val FLOAT_SIZE_BYTES = 4

sealed class GlVertexBuffer {

    var vertexBufferHandle: Int = GLES20.GL_NONE
        private set

    private var isValid = false

    abstract val isWindowSizeDependent: Boolean

    protected abstract fun generateVerticesForSize(resources: Resources, width: Int, height: Int): FloatBuffer

    fun invalidate() {
        isValid = false
    }

    fun generateNewVertexBuffer(resources: Resources, width: Int, height: Int) {

        // Don't regenerate needlessly
        if (isValid) {
            return
        }

        // Create a buffer object
        vertexBufferHandle = createBuffer()

        // Get vertex data for this screen size if possible, and load into the buffer
        updateIfNeeded(resources, width, height)
    }

    fun updateIfNeeded(resources: Resources, width: Int, height: Int) {

        // Don't regenerate needlessly, or if the size is invalid
        if (isValid || width == 0 || height == 0 || vertexBufferHandle == GLES20.GL_NONE) {
            return
        }

        // Generate the data and upload it into the buffer object
        val vertexData = generateVerticesForSize(resources, width, height)
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
}

class MainScreenBackgroundVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override fun generateVerticesForSize(resources: Resources, width: Int, height: Int): FloatBuffer {

        val marginDips = 16.0f
        val marginUnitsW: Float = 2.0f * (marginDips * resources.displayMetrics.density) / width.toFloat()
        val marginUnitsH: Float = 2.0f * (marginDips * resources.displayMetrics.density) / height.toFloat()

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

        val data = FloatArray(30 * 19)
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

        return data.toFloatBuffer()
    }
}
