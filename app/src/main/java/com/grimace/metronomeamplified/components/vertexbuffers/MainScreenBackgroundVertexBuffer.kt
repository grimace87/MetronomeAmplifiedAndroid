package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.RectF
import com.grimace.metronomeamplified.components.FLOATS_PER_QUAD
import com.grimace.metronomeamplified.components.GlVertexBuffer
import com.grimace.metronomeamplified.components.VERTICES_PER_QUAD
import java.nio.FloatBuffer

class MainScreenBackgroundVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override val subBufferVertexIndices: IntArray = intArrayOf(0, VERTICES_PER_QUAD)

    override val regionsOfInterest: Array<RectF> = arrayOf()

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {
        val data = FloatArray(FLOATS_PER_QUAD)
        data.putSquare(0, -1f, -1f, 1f, 1f, 0f, 0f, 1f, 1f)
        return data.toFloatBuffer()
    }
}
