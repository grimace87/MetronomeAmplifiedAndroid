package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.RectF
import android.opengl.GLES20
import com.grimace.metronomeamplified.components.*
import java.nio.FloatBuffer

class MainScreenBackgroundVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override val subBufferVertexIndices: IntArray = intArrayOf(0, VERTICES_PER_QUAD)

    override val regionsOfInterest: Array<RectF> = arrayOf()

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {
        val data = FloatArray(FLOATS_PER_QUAD)
        data.putSquare(0, -1f, -1f, 1f, 1f, 0f, 0f, 1f, 1f)
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
