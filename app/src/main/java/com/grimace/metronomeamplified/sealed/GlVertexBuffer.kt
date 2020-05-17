package com.grimace.metronomeamplified.sealed

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

const val FLOAT_SIZE_BYTES = 4

sealed class GlVertexBuffer {

    var vertexBufferHandle: Int = GLES20.GL_NONE
        private set

    private var isValid = false

    protected abstract fun generateVerticesForSize(width: Int, height: Int): FloatBuffer

    fun invalidate() {
        isValid = false
    }

    fun generateNewVertexBufferForSize(width: Int, height: Int) {

        // Don't regenerate needlessly
        if (isValid) {
            return
        }

        // Create a buffer object
        vertexBufferHandle = createBuffer()

        // Get vertex data for this screen size if possible, and load into the buffer
        updateVertexBufferForSize(width, height)
    }

    fun updateVertexBufferForSize(width: Int, height: Int) {

        // Don't regenerate needlessly, or if the size is invalid
        if (isValid || width == 0 || height == 0 || vertexBufferHandle == GLES20.GL_NONE) {
            return
        }

        // Generate the data and upload it into the buffer object
        val vertexData = generateVerticesForSize(width, height)
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
}

class FullBackgroundBuffer : GlVertexBuffer() {
    override fun generateVerticesForSize(width: Int, height: Int): FloatBuffer {
        return floatArrayOf(
            -1f, -1f, 0f, 0f, 0f,
            1f, -1f, 0f, 1f, 0f,
            1f, 1f, 0f, 1f, 1f,
            1f, 1f, 0f, 1f, 1f,
            -1f, 1f, 0f, 0f, 1f,
            -1f, -1f, 0f, 0f, 0f
        ).toFloatBuffer()
    }
}
