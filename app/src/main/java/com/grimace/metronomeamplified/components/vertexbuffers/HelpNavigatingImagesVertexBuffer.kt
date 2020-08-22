package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.RectF
import com.grimace.metronomeamplified.components.FLOATS_PER_QUAD
import com.grimace.metronomeamplified.components.GlVertexBuffer
import java.nio.FloatBuffer

class HelpNavigatingImagesVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override val regionsOfInterest: List<RectF> = listOf()

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {

        val imageAspect = 720.0f / 1280.0f

        val displayMetrics = context.resources.displayMetrics
        val marginDips = 16.0f
        val marginUnitsW: Float = 2.0f * (marginDips * displayMetrics.density) / width.toFloat()
        val marginUnitsH: Float = 2.0f * (marginDips * displayMetrics.density) / height.toFloat()
        val screenAspect = width.toFloat() / height.toFloat()

        val h1 = -0.125f - marginUnitsH
        val h2 = 1.0f - 5.0f * marginUnitsH
        val widthUnits = (h2 - h1) * imageAspect / screenAspect
        val w1 = -0.5f * widthUnits
        val w2 = 0.5f * widthUnits

        val data = FloatArray(FLOATS_PER_QUAD)
        data.putSquare(0, w1, h1, w2, h2, 0.0f, 1.0f, 1.0f, 0.0f)
        return data.toFloatBuffer()
    }
}
