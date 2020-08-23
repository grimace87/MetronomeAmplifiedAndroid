package com.grimace.metronomeamplified.components.shaders

import android.opengl.GLES20
import com.grimace.metronomeamplified.components.GlShader

class FontTransformShader : GlShader("font_transform_shader.vert", "font_shader.frag") {

    override val attribs: IntArray = intArrayOf(0, 0)

    private var textureUniform = GLES20.GL_NONE
    private var transformMatrixUniform = GLES20.GL_NONE
    private var paintColourUniform = GLES20.GL_NONE

    override fun prepare() {
        attribs[0] = GLES20.glGetAttribLocation(programHandle, "aPosition")
        attribs[1] = GLES20.glGetAttribLocation(programHandle, "aTextureCoord")
        GLES20.glEnableVertexAttribArray(attribs[0])
        GLES20.glEnableVertexAttribArray(attribs[1])
        textureUniform = GLES20.glGetUniformLocation(programHandle, "uTextureSampler")
        transformMatrixUniform = GLES20.glGetUniformLocation(programHandle, "uTransform")
        paintColourUniform = GLES20.glGetUniformLocation(programHandle, "uPaintColor")
    }

    override fun activate() {
        GLES20.glUseProgram(programHandle)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(textureUniform, 0)
    }

    fun setTransformationMatrix(matrix: FloatArray, offset: Int) {
        GLES20.glUniformMatrix4fv(transformMatrixUniform, 1, false, matrix, offset)
    }

    fun setPaintColour(r: Float, g: Float, b: Float, a: Float) {
        GLES20.glUniform4f(paintColourUniform, r, g, b, a)
    }
}
