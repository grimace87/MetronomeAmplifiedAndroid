package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLES20
import android.view.Gravity
import com.grimace.metronomeamplified.components.*
import java.nio.FloatBuffer

class HelpHubTextsVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override val subBufferVertexIndices: IntArray = intArrayOf(0, 0, 0)

    override val regionsOfInterest: Array<RectF> = arrayOf(RectF(), RectF(), RectF(), RectF())

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
        labels.forEach { totalFloatCount += FLOATS_PER_QUAD * it.length }
        val vboData = FloatArray(totalFloatCount)

        var bufferIndex = 0
        val maxTextHeightPixels = 2.0f * marginDips * displayMetrics.density
        font.printTextIntoVbo(vboData, bufferIndex, labels[0], leftX, t1, rightX - leftX, t1 - t2, maxTextHeightPixels, screenSize, Gravity.START, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[0].length
        subBufferVertexIndices[1] = bufferIndex / FLOATS_PER_VERTEX
        font.printTextIntoVbo(vboData, bufferIndex, labels[1], leftX, t2, rightX - leftX, t2 - t3, maxTextHeightPixels, screenSize, Gravity.START, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[1].length
        font.printTextIntoVbo(vboData, bufferIndex, labels[2], leftX, t3, rightX - leftX, t3 - t4, maxTextHeightPixels, screenSize, Gravity.START, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[2].length
        font.printTextIntoVbo(vboData, bufferIndex, labels[3], leftX, t4, rightX - leftX, t4 - t5, maxTextHeightPixels, screenSize, Gravity.START, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[3].length
        font.printTextIntoVbo(vboData, bufferIndex, labels[4], leftX, t5, rightX - leftX, t5 - t6, maxTextHeightPixels, screenSize, Gravity.START, Gravity.CENTER_VERTICAL)
        bufferIndex += FLOATS_PER_QUAD * labels[4].length
        subBufferVertexIndices[2] = bufferIndex / FLOATS_PER_VERTEX

        // Note the normal y direction for RectF is positive downward, so swap top and bottom here
        regionsOfInterest[0].set(leftX, t3, rightX, t2)
        regionsOfInterest[1].set(leftX, t4, rightX, t3)
        regionsOfInterest[2].set(leftX, t5, rightX, t4)
        regionsOfInterest[3].set(leftX, t6, rightX, t5)

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
