package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLES20
import android.view.Gravity
import com.grimace.metronomeamplified.components.*
import java.nio.FloatBuffer

class MainScreenIconLabelsVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override val subBufferVertexIndices: IntArray = intArrayOf(0, 0)

    override val regionsOfInterest: Array<RectF> = arrayOf()

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {

        // Guidelines from MainScreenBackgroundVertexBuffer
        val displayMetrics = context.resources.displayMetrics
        val marginDips = 16.0f
        val marginUnitsW: Float = 2.0f * (marginDips * displayMetrics.density) / width.toFloat()
        val marginUnitsH: Float = 2.0f * (marginDips * displayMetrics.density) / height.toFloat()
        val screenSize = PointF(displayMetrics.widthPixels.toFloat(), displayMetrics.heightPixels.toFloat())
        val w2 = -1.0f + 2.0f * marginUnitsW
        val w3 = -1.0f + (2.0f - 2.0f * marginUnitsW) / 3.0f - marginUnitsW
        val w8 = 1.0f - (2.0f - 2.0f * marginUnitsW) / 3.0f + marginUnitsW
        val w9 = 1.0f - 2.0f * marginUnitsW
        val hIcon1Left = -1.0f
        val hIcon2Left = -0.5f
        val hIcon3Left = 0.0f
        val hIcon4Left = 0.5f
        val hIcon4Right = 1.0f
        val hIconBottom = 0.7f
        val hIconLabelBottom = 0.5f
        val h2 = -1.0f + 2.0f * marginUnitsH
        val h3 = -1.0f + (2.0f - marginUnitsH) / 4.0f - marginUnitsH
        val hLowerIconsLabelTop = h2 + 0.25f * (h3 - h2)

        val font = getOrkneyFontDescription(context)
        val labels = arrayOf(
            "TONE",
            "SONG",
            "HELP",
            "SETTINGS",
            "TIMER",
            "LIFT"
        )
        var totalFloatCount = 0
        labels.forEach { totalFloatCount += FLOATS_PER_QUAD * it.length }
        val vboData = FloatArray(totalFloatCount)

        var bufferIndex = 0
        val maxTextHeightPixels = 24.0f * displayMetrics.density
        font.printTextIntoVbo(vboData, bufferIndex, labels[0], hIcon1Left, hIconBottom, hIcon2Left - hIcon1Left, hIconBottom - hIconLabelBottom, maxTextHeightPixels, screenSize, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[0].length
        font.printTextIntoVbo(vboData, bufferIndex, labels[1], hIcon2Left, hIconBottom, hIcon3Left - hIcon2Left, hIconBottom - hIconLabelBottom, maxTextHeightPixels, screenSize, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[1].length
        font.printTextIntoVbo(vboData, bufferIndex, labels[2], hIcon3Left, hIconBottom, hIcon4Left - hIcon3Left, hIconBottom - hIconLabelBottom, maxTextHeightPixels, screenSize, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[2].length
        font.printTextIntoVbo(vboData, bufferIndex, labels[3], hIcon4Left, hIconBottom, hIcon4Right - hIcon4Left, hIconBottom - hIconLabelBottom, maxTextHeightPixels, screenSize, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[3].length
        font.printTextIntoVbo(vboData, bufferIndex, labels[4], w2, hLowerIconsLabelTop, w3 - w2, hLowerIconsLabelTop - h2, maxTextHeightPixels, screenSize, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[4].length
        font.printTextIntoVbo(vboData, bufferIndex, labels[5], w8, hLowerIconsLabelTop, w9 - w8, hLowerIconsLabelTop - h2, maxTextHeightPixels, screenSize, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[5].length
        subBufferVertexIndices[1] = bufferIndex / FLOATS_PER_VERTEX

        return vboData.toFloatBuffer()
    }

    override fun activate(shader: GlShader) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferHandle)
        GLES20.glVertexAttribPointer(shader.attribs[0],
            3, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 0)
        GLES20.glVertexAttribPointer(shader.attribs[1],
            2, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES
        )
    }
}
