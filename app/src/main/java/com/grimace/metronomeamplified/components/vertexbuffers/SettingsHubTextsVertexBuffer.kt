package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.PointF
import android.view.Gravity
import com.grimace.metronomeamplified.components.GlVertexBuffer
import java.nio.FloatBuffer

class SettingsHubTextsVertexBuffer : GlVertexBuffer() {

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
        val t1 = 1.0f - 2.0f * marginUnitsH
        val t2 = t1 - 2.0f * marginUnitsH
        val t3 = t2 - 2.0f * marginUnitsH
        val t4 = t3 - 2.0f * marginUnitsH
        val t5 = t4 - 2.0f * marginUnitsH
        val t6 = t5 - 2.0f * marginUnitsH

        val font = getOrkneyFontDescription(context)
        val labels = arrayOf(
            "Help Sections",
            "Navigating the App",
            "Controlling the Experience",
            "Crafting Your Song",
            "Managing Song Files"
        )
        var totalFloatCount = 0
        labels.forEach { totalFloatCount += 30 * it.length }
        val vboData = FloatArray(totalFloatCount)

        var bufferIndex = 0
        val maxTextHeightPixels = 2.0f * marginDips * displayMetrics.density
        font.printTextIntoVboCentredInside(vboData, bufferIndex, labels[0], leftX, t1, rightX - leftX, t1 - t2, maxTextHeightPixels, screenSize, Gravity.START)
        bufferIndex += 30 * labels[0].length
        font.printTextIntoVboCentredInside(vboData, bufferIndex, labels[1], leftX, t2, rightX - leftX, t2 - t3, maxTextHeightPixels, screenSize, Gravity.START)
        bufferIndex += 30 * labels[1].length
        font.printTextIntoVboCentredInside(vboData, bufferIndex, labels[2], leftX, t3, rightX - leftX, t3 - t4, maxTextHeightPixels, screenSize, Gravity.START)
        bufferIndex += 30 * labels[2].length
        font.printTextIntoVboCentredInside(vboData, bufferIndex, labels[3], leftX, t4, rightX - leftX, t4 - t5, maxTextHeightPixels, screenSize, Gravity.START)
        bufferIndex += 30 * labels[3].length
        font.printTextIntoVboCentredInside(vboData, bufferIndex, labels[4], leftX, t5, rightX - leftX, t5 - t6, maxTextHeightPixels, screenSize, Gravity.START)
        bufferIndex += 30 * labels[4].length

        return vboData.toFloatBuffer()
    }
}
