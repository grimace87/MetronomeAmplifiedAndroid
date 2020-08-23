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

    override val subBufferVertexIndices: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

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
            "The pattern of percussive beats you'll play along with are displayed here. The time signature is shown, along with the timing of each note, in case you're familiar with musical notation. A song consists of one or more of these sections, each with its own note pattern, and therefore can be very simple or very complex.",
            "Playback is controlled with the buttons at the bottom. Fast-forward and rewind have a use only with songs that have multiple sections. Pausing will halt the current position in the current section, while stopping will reset the playback position to the beginning.",
            "A section of a song has a default tempo, although the tempo it is being played at can vary. Tapping the beat-per-minute count will reset to the default tempo, while the slider allows free manual adjustments.",
            "Training modes exist to alter the tempo automatically while you play, or set a limit to how long you'd like to play. Pressing the One-touch Tempo Lift button will begin tempo control, and this feature can be customised through the settings screen.",
            "The timer will allow you to set a timespan before the metronome stops playing.",
            "There is more than one set of sounds that the metronome can play - they can be loaded by pressing the Tone button.",
            "The song and its sections can be fully customised by tapping the Song button.",
            "Various settings are accessible by tapping the Settings button. These settings control the Tempo Lift behaviour, as well as visual cues to coincide with the sounds you here."
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
        font.printTextIntoVbo(vboData, bufferIndex, labels[2], w2, h2, w3 - w2, h2 - h1, bodyTextHeight, screenSize, Gravity.START, Gravity.START)
        bufferIndex += FLOATS_PER_QUAD * labels[2].length
        subBufferVertexIndices[3] = bufferIndex / FLOATS_PER_VERTEX
        font.printTextIntoVbo(vboData, bufferIndex, labels[3], w2, h2, w3 - w2, h2 - h1, bodyTextHeight, screenSize, Gravity.START, Gravity.START)
        bufferIndex += FLOATS_PER_QUAD * labels[3].length
        subBufferVertexIndices[4] = bufferIndex / FLOATS_PER_VERTEX
        font.printTextIntoVbo(vboData, bufferIndex, labels[4], w2, h2, w3 - w2, h2 - h1, bodyTextHeight, screenSize, Gravity.START, Gravity.START)
        bufferIndex += FLOATS_PER_QUAD * labels[4].length
        subBufferVertexIndices[5] = bufferIndex / FLOATS_PER_VERTEX
        font.printTextIntoVbo(vboData, bufferIndex, labels[5], w2, h2, w3 - w2, h2 - h1, bodyTextHeight, screenSize, Gravity.START, Gravity.START)
        bufferIndex += FLOATS_PER_QUAD * labels[5].length
        subBufferVertexIndices[6] = bufferIndex / FLOATS_PER_VERTEX
        font.printTextIntoVbo(vboData, bufferIndex, labels[6], w2, h2, w3 - w2, h2 - h1, bodyTextHeight, screenSize, Gravity.START, Gravity.START)
        bufferIndex += FLOATS_PER_QUAD * labels[6].length
        subBufferVertexIndices[7] = bufferIndex / FLOATS_PER_VERTEX
        font.printTextIntoVbo(vboData, bufferIndex, labels[7], w2, h2, w3 - w2, h2 - h1, bodyTextHeight, screenSize, Gravity.START, Gravity.START)
        bufferIndex += FLOATS_PER_QUAD * labels[7].length
        subBufferVertexIndices[8] = bufferIndex / FLOATS_PER_VERTEX
        font.printTextIntoVbo(vboData, bufferIndex, labels[8], w2, h2, w3 - w2, h2 - h1, bodyTextHeight, screenSize, Gravity.START, Gravity.START)
        bufferIndex += FLOATS_PER_QUAD * labels[8].length
        subBufferVertexIndices[9] = bufferIndex / FLOATS_PER_VERTEX

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
