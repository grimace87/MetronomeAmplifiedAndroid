package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import com.grimace.metronomeamplified.components.FLOATS_PER_QUAD
import com.grimace.metronomeamplified.components.GlVertexBuffer
import java.nio.FloatBuffer

class HelpDetailsOverlayVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {

        val displayMetrics = context.resources.displayMetrics
        val marginDips = 16.0f
        val marginUnitsW: Float = 2.0f * (marginDips * displayMetrics.density) / width.toFloat()
        val marginUnitsH: Float = 2.0f * (marginDips * displayMetrics.density) / height.toFloat()

        val w1 = -1.0f + marginUnitsW
        val w2 = w1 + marginUnitsW
        val w4 = 1.0f - marginUnitsW
        val w3 = w4 - marginUnitsW

        val h1 = -1.0f + marginUnitsH
        val h2 = h1 + marginUnitsH
        val h4 = 1.0f - 4.0f * marginUnitsH
        val h3 = h4 - marginUnitsH

        val data = FloatArray(FLOATS_PER_QUAD * 7)

        data.putSquare(0                  , w1, h1, w2, h2, 0.0f, 0.0f, 0.5f, 0.5f)
        data.putSquare(1 * FLOATS_PER_QUAD, w2, h1, w3, h2, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(2 * FLOATS_PER_QUAD, w3, h1, w4, h2, 0.5f, 0.0f, 0.0f, 0.5f)

        data.putSquare(3 * FLOATS_PER_QUAD, w1, h2, w4, h3, 0.5f, 0.0f, 1.0f, 0.5f)

        data.putSquare(4 * FLOATS_PER_QUAD, w1, h3, w2, h4, 0.0f, 0.5f, 0.5f, 0.0f)
        data.putSquare(5 * FLOATS_PER_QUAD, w2, h3, w3, h4, 0.5f, 0.5f, 1.0f, 0.0f)
        data.putSquare(6 * FLOATS_PER_QUAD, w3, h3, w4, h4, 0.5f, 0.5f, 0.0f, 0.0f)

        return data.toFloatBuffer()
    }
}
