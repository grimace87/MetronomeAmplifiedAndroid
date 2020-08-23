package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.RectF
import android.opengl.GLES20
import com.grimace.metronomeamplified.components.*
import java.nio.FloatBuffer

class HelpNavigatingImagesVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override val subBufferVertexIndices: IntArray = intArrayOf(0, VERTICES_PER_QUAD)

    override val regionsOfInterest: Array<RectF> = arrayOf()

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
