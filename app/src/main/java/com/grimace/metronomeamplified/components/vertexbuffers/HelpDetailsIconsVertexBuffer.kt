package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.RectF
import android.opengl.GLES20
import com.grimace.metronomeamplified.components.*
import java.nio.FloatBuffer

class HelpDetailsIconsVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override val subBufferVertexIndices: IntArray = intArrayOf(0, VERTICES_PER_QUAD * 2)

    override val regionsOfInterest: Array<RectF> = arrayOf(RectF(), RectF())

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {

        val displayMetrics = context.resources.displayMetrics
        val marginDips = 16.0f
        val marginUnitsW: Float = 2.0f * (marginDips * displayMetrics.density) / width.toFloat()
        val marginUnitsH: Float = 2.0f * (marginDips * displayMetrics.density) / height.toFloat()

        val aspect = width.toFloat() / height.toFloat()
        val iconHeightUnits = 0.25f
        val iconWidthUnits = iconHeightUnits * 0.5f / aspect

        val w1 = -1.0f + marginUnitsW
        val w2 = w1 + iconWidthUnits
        val w4 = 1.0f - marginUnitsW
        val w3 = w4 - iconWidthUnits

        val h1 = -0.5f * iconHeightUnits
        val h2 = 0.5f * iconHeightUnits

        val data = FloatArray(FLOATS_PER_QUAD * 2)

        data.putSquare(0                  , w1, h1, w2, h2, 0.875f, 0.5f, 1.0f, 1.0f)
        data.putSquare(1 * FLOATS_PER_QUAD, w3, h1, w4, h2, 1.0f, 0.5f, 0.875f, 1.0f)

        // Note the normal y direction for RectF is positive downward, so swap top and bottom here
        regionsOfInterest[0].set(w1, h1, w2, h2)
        regionsOfInterest[1].set(w3, h1, w4, h2)

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
