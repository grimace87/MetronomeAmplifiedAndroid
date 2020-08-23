package com.grimace.metronomeamplified.components.shaders

import android.opengl.GLES20
import com.grimace.metronomeamplified.components.GlShader

class AlphaTextureShader : GlShader("alpha_texture.vert", "alpha_texture.frag") {

    override val attribs: IntArray = intArrayOf(0, 0)

    private var textureUniform = GLES20.GL_NONE

    override fun prepare() {
        attribs[0] = GLES20.glGetAttribLocation(programHandle, "aPosition")
        attribs[1] = GLES20.glGetAttribLocation(programHandle, "aTextureCoord")
        GLES20.glEnableVertexAttribArray(attribs[0])
        GLES20.glEnableVertexAttribArray(attribs[1])
        textureUniform = GLES20.glGetUniformLocation(programHandle, "uTextureSampler")
    }

    override fun activate() {
        GLES20.glUseProgram(programHandle)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(textureUniform, 0)
    }
}
