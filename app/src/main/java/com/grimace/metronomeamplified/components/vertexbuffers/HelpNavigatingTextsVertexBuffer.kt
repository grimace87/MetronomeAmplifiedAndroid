package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLES20
import android.view.Gravity
import com.grimace.metronomeamplified.components.*
import java.nio.FloatBuffer

class HelpNavigatingTextsVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override val subBufferVertexIndices: IntArray = intArrayOf(0, 0, 0)

    override val regionsOfInterest: Array<RectF> = arrayOf()

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {

        // Guidelines from MainScreenBackgroundVertexBuffer
        val displayMetrics = context.resources.displayMetrics
        val marginDips = 16.0f
        val marginUnitsW: Float = 2.0f * (marginDips * displayMetrics.density) / width.toFloat()
        val marginUnitsH: Float = 2.0f * (marginDips * displayMetrics.density) / height.toFloat()
        val screenSize = PointF(displayMetrics.widthPixels.toFloat(), displayMetrics.heightPixels.toFloat())
        val w1 = -1.0f + marginUnitsW
        val w2 = w1 + marginUnitsW
        val w4 = 1.0f - marginUnitsW
        val w3 = w4 - marginUnitsW
        val h1 = -1.0f + 2.0f * marginUnitsH
        val h2 = -0.125f - marginUnitsH
        val h4 = 1.0f - marginUnitsH
        val h3 = h4 - 2.0f * marginUnitsH

        val font = getOrkneyFontDescription(context)
        val labels = arrayOf(
            "Navigating the App",
            "The pattern of percussive beats you'll play along with are displayed here. The time signature is shown, along with the timing of each note, in case you're familiar with musical notation. A song consists of one or more of these sections, each with its own note pattern, and therefore can be very simple or very complex."
        )
        var totalFloatCount = 0
        labels.forEach { totalFloatCount += FLOATS_PER_QUAD * it.length }
        val vboData = FloatArray(totalFloatCount)

        var bufferIndex = 0
        val headingTextHeight = 2.0f * marginDips * displayMetrics.density
        val bodyTextHeight = 1.5f * marginDips * displayMetrics.density
        font.printTextIntoVbo(vboData, bufferIndex, labels[0], w1, h4, w4 - w1, h4 - h3, headingTextHeight, screenSize, Gravity.START, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[0].length
        subBufferVertexIndices[1] = bufferIndex / FLOATS_PER_VERTEX
        font.printTextIntoVbo(vboData, bufferIndex, labels[1], w2, h2, w3 - w2, h2 - h1, bodyTextHeight, screenSize, Gravity.START, Gravity.START)
        bufferIndex += FLOATS_PER_QUAD * labels[1].length
        subBufferVertexIndices[2] = bufferIndex / FLOATS_PER_VERTEX

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
