package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import com.grimace.metronomeamplified.components.FLOATS_PER_QUAD
import com.grimace.metronomeamplified.components.GlVertexBuffer
import com.grimace.metronomeamplified.components.VERTICES_PER_QUAD
import java.nio.FloatBuffer

class MainScreenTranslucentOverlayVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override val subBufferVertexIndices: IntArray = intArrayOf(0, VERTICES_PER_QUAD * 18)

    override val regionsOfInterest: Array<RectF> = arrayOf()

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

        val data = FloatArray(FLOATS_PER_QUAD * 18)

        data.putSquare(0, w1, h1, w2, h2, 0.0f, 0.0f, 0.5f, 0.5f)
        data.putSquare(1 * FLOATS_PER_QUAD, w2, h1, w3, h2, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(2 * FLOATS_PER_QUAD, w3, h1, w4, h2, 0.5f, 0.0f, 0.0f, 0.5f)

        data.putSquare(3 * FLOATS_PER_QUAD, w7, h1, w8, h2, 0.0f, 0.0f, 0.5f, 0.5f)
        data.putSquare(4 * FLOATS_PER_QUAD, w8, h1, w9, h2, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(5 * FLOATS_PER_QUAD, w9, h1, w10, h2, 0.5f, 0.0f, 0.0f, 0.5f)

        data.putSquare(6 * FLOATS_PER_QUAD, w1, h2, w2, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(7 * FLOATS_PER_QUAD, w2, h3, w3, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(8 * FLOATS_PER_QUAD, w3, h2, w4, h4, 0.5f, 0.0f, 1.0f, 0.5f)

        data.putSquare(9 * FLOATS_PER_QUAD, w7, h2, w8, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(10 * FLOATS_PER_QUAD, w8, h3, w9, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(11 * FLOATS_PER_QUAD, w9, h2, w10, h4, 0.5f, 0.0f, 1.0f, 0.5f)

        data.putSquare(12 * FLOATS_PER_QUAD, w4, h3, w5, h4, 0.5f, 1.0f, 0.0f, 0.5f)
        data.putSquare(13 * FLOATS_PER_QUAD, w6, h3, w7, h4, 0.0f, 1.0f, 0.5f, 0.5f)

        data.putSquare(14 * FLOATS_PER_QUAD, w1, h4, w10, h5, 0.5f, 0.0f, 1.0f, 0.5f)

        data.putSquare(15 * FLOATS_PER_QUAD, w1, h5, w2, h6, 0.0f, 0.5f, 0.5f, 0.0f)
        data.putSquare(16 * FLOATS_PER_QUAD, w2, h5, w9, h6, 0.5f, 0.5f, 1.0f, 0.0f)
        data.putSquare(17 * FLOATS_PER_QUAD, w9, h5, w10, h6, 0.5f, 0.5f, 0.0f, 0.0f)

        return data.toFloatBuffer()
    }
}
