package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLES20
import com.grimace.metronomeamplified.components.*
import java.nio.FloatBuffer

class MainScreenIconsVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override val subBufferVertexIndices: IntArray = intArrayOf(0, VERTICES_PER_QUAD * 10)

    override val regionsOfInterest: Array<RectF> = arrayOf(
        RectF(), RectF(), RectF(), RectF(), RectF(), RectF(), RectF(), RectF(), RectF(), RectF())

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
        val w7 = 1.0f - (2.0f - 2.0f * marginUnitsW) / 3.0f
        val w8 = w7 + marginUnitsW
        val w10 = 1.0f - marginUnitsW
        val w9 = w10 - marginUnitsW

        val h1 = -1.0f + marginUnitsH
        val h2 = h1 + marginUnitsH
        val h4 = -1.0f + (2.0f - marginUnitsH) / 4.0f
        val h3 = h4 - marginUnitsH

        val hIcon1Left = -1.0f
        val hIcon2Left = -0.5f
        val hIcon3Left = 0.0f
        val hIcon4Left = 0.5f
        val hIcon4Right = 1.0f

        val hIconBottom = 0.7f
        val hIconLabelBottom = 0.5f
        val hIconTop = 1.0f
        val hLowerIconsLabelTop = h2 + 0.25f * (h3 - h2)

        val playLeft = w4 + (w7 - w4) / 3.0f - marginUnitsW
        val playRight = w7 - (w7 - w4) / 3.0f + marginUnitsW
        val playBottom = h1 + 0.5f * (h4 - h1) - 2.0f * marginUnitsH

        val data = FloatArray(FLOATS_PER_QUAD * 10)

        data.putSquareCentredInside(0, hIcon1Left, hIconBottom, hIcon2Left, hIconTop, 0.0f, 0.5f, 0.25f, 0.0f, screenSize)
        data.putSquareCentredInside(1 * FLOATS_PER_QUAD, hIcon2Left, hIconBottom, hIcon3Left, hIconTop, 0.25f, 0.5f, 0.5f, 0.0f, screenSize)
        data.putSquareCentredInside(2 * FLOATS_PER_QUAD, hIcon3Left, hIconBottom, hIcon4Left, hIconTop, 0.5f, 0.5f, 0.75f, 0.0f, screenSize)
        data.putSquareCentredInside(3 * FLOATS_PER_QUAD, hIcon4Left, hIconBottom, hIcon4Right, hIconTop, 0.75f, 0.5f, 1.0f, 0.0f, screenSize)

        data.putSquareCentredInside(4 * FLOATS_PER_QUAD, w2, hLowerIconsLabelTop, w3, h3, 0.0f, 1.0f, 0.25f, 0.5f, screenSize)
        data.putSquareCentredInside(5 * FLOATS_PER_QUAD, w8, hLowerIconsLabelTop, w9, h3, 0.25f, 1.0f, 0.5f, 0.5f, screenSize)

        data.putSquareCentredInside(6 * FLOATS_PER_QUAD, w4, playBottom, playLeft, h4, 0.75f, 1.0f, 0.625f, 0.75f, screenSize)
        data.putSquareCentredInside(7 * FLOATS_PER_QUAD, playLeft, playBottom, playRight, h4, 0.5f, 0.75f, 0.625f, 0.5f, screenSize)
        data.putSquareCentredInside(8 * FLOATS_PER_QUAD, playRight, playBottom, w7, h4, 0.625f, 1.0f, 0.75f, 0.75f, screenSize)
        data.putSquareCentredInside(9 * FLOATS_PER_QUAD, playLeft, h1, playRight, playBottom, 0.625f, 0.75f, 0.75f, 0.5f, screenSize)

        // Note the normal y direction for RectF is positive downward, so swap top and bottom here
        regionsOfInterest[0].set(hIcon1Left, hIconLabelBottom, hIcon2Left, hIconTop)
        regionsOfInterest[1].set(hIcon2Left, hIconLabelBottom, hIcon3Left, hIconTop)
        regionsOfInterest[2].set(hIcon3Left, hIconLabelBottom, hIcon4Left, hIconTop)
        regionsOfInterest[3].set(hIcon4Left, hIconLabelBottom, hIcon4Right, hIconTop)
        regionsOfInterest[4].set(w2, h2, w3, h3)
        regionsOfInterest[5].set(w8, h2, w9, h3)
        regionsOfInterest[6].set(w4, playBottom, playLeft, h4)
        regionsOfInterest[7].set(playLeft, playBottom, playRight, h4)
        regionsOfInterest[8].set(playRight, playBottom, w7, h4)
        regionsOfInterest[9].set(playLeft, h1, playRight, playBottom)

        return data.toFloatBuffer()
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
