package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.PointF
import android.view.Gravity
import com.grimace.metronomeamplified.components.FLOATS_PER_QUAD
import com.grimace.metronomeamplified.components.GlVertexBuffer
import java.nio.FloatBuffer

class SettingsNavigatingTextsVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {

        // Guidelines from MainScreenBackgroundVertexBuffer
        val displayMetrics = context.resources.displayMetrics
        val marginDips = 16.0f
        val marginUnitsW: Float = 2.0f * (marginDips * displayMetrics.density) / width.toFloat()
        val marginUnitsH: Float = 2.0f * (marginDips * displayMetrics.density) / height.toFloat()
        val screenSize = PointF(displayMetrics.widthPixels.toFloat(), displayMetrics.heightPixels.toFloat())
        val leftX = -1.0f + marginUnitsW
        val rightX = 1.0f - marginUnitsW
        val h2 = 1.0f - marginUnitsH
        val h1 = h2 - 2.0f * marginUnitsH

        val font = getOrkneyFontDescription(context)
        val labels = arrayOf(
            "Navigating the App"
        )
        var totalFloatCount = 0
        labels.forEach { totalFloatCount += FLOATS_PER_QUAD * it.length }
        val vboData = FloatArray(totalFloatCount)

        var bufferIndex = 0
        val maxTextHeightPixels = 2.0f * marginDips * displayMetrics.density
        font.printTextIntoVboCentredInside(vboData, bufferIndex, labels[0], leftX, h2, rightX - leftX, h2 - h1, maxTextHeightPixels, screenSize, Gravity.START)
        bufferIndex += FLOATS_PER_QUAD * labels[0].length

        return vboData.toFloatBuffer()
    }
}
