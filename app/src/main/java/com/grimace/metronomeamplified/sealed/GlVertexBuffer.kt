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

    protected abstract fun generateVerticesForSize(resources: Resources, width: Int, height: Int): FloatBuffer

    fun invalidate() {
        isValid = false
    }

    fun generateNewVertexBufferForSize(resources: Resources, width: Int, height: Int) {

        // Don't regenerate needlessly
        if (isValid) {
            return
        }

        // Create a buffer object
        vertexBufferHandle = createBuffer()

        // Get vertex data for this screen size if possible, and load into the buffer
        updateVertexBufferForSize(resources, width, height)
    }

    fun updateVertexBufferForSize(resources: Resources, width: Int, height: Int) {

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
    override fun generateVerticesForSize(resources: Resources, width: Int, height: Int): FloatBuffer {

        val marginDips = 16.0f
        val marginUnitsW: Float = 2.0f * (marginDips * resources.displayMetrics.density) / width.toFloat()
        val marginUnitsH: Float = 2.0f * (marginDips * resources.displayMetrics.density) / height.toFloat()

        val w1 = -1.0f + marginUnitsW
        val w2 = w1 + marginUnitsW
        val w4 = 1.0f - marginUnitsW
        val w3 = w4 - marginUnitsW

        val h1 = -1.0f + marginUnitsH
        val h2 = h1 + marginUnitsH
        val h4 = 1.0f - marginUnitsH
        val h3 = h4 - marginUnitsH

        val data = FloatArray(30 * 8)
        data.putSquare(0, -1f, -1f, 1f, 1f, 0f, 0f, 1f, 1f)
        data.putSquare(30, w1, h1, w2, h2, 0.0f, 0.0f, 0.5f, 1.0f)
        data.putSquare(60, w2, h1, w3, h2, 0.5f, 0.0f, 1.0f, 1.0f)
        data.putSquare(90, w3, h1, w4, h2, 0.5f, 0.0f, 0.0f, 1.0f)
        data.putSquare(120, w1, h2, w4, h3, 0.5f, 0.0f, 1.0f, 1.0f)
        data.putSquare(150, w1, h3, w2, h4, 0.0f, 1.0f, 0.5f, 0.0f)
        data.putSquare(180, w2, h3, w3, h4, 0.5f, 1.0f, 1.0f, 0.0f)
        data.putSquare(210, w3, h3, w4, h4, 0.5f, 1.0f, 0.0f, 0.0f)
        return data.toFloatBuffer()
    }
}
