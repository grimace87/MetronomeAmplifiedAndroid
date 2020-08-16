package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import com.grimace.metronomeamplified.components.FLOATS_PER_QUAD
import com.grimace.metronomeamplified.components.GlVertexBuffer
import java.nio.FloatBuffer

class MainScreenBackgroundVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {
        val data = FloatArray(FLOATS_PER_QUAD)
        data.putSquare(0, -1f, -1f, 1f, 1f, 0f, 0f, 1f, 1f)
        return data.toFloatBuffer()
    }
}
