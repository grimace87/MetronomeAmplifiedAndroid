package com.grimace.metronomeamplified.components.shaders

import android.opengl.GLES20
import com.grimace.metronomeamplified.components.GlShader

class SlimeTransitionShader : GlShader("slime_transition.vert", "slime_transition.frag") {

    override val attribs: IntArray = intArrayOf(0, 0)

    private var mainTextureUniform = GLES20.GL_NONE
    private var normalMapTextureUniform = GLES20.GL_NONE
    private var progressUniform = GLES20.GL_NONE

    override fun prepare() {
        attribs[0] = GLES20.glGetAttribLocation(programHandle, "aPosition")
        attribs[1] = GLES20.glGetAttribLocation(programHandle, "aTextureCoord")
        GLES20.glEnableVertexAttribArray(attribs[0])
        GLES20.glEnableVertexAttribArray(attribs[1])
        mainTextureUniform = GLES20.glGetUniformLocation(programHandle, "uTextureSampler")
        normalMapTextureUniform = GLES20.glGetUniformLocation(programHandle, "uNormalMapSampler")
        progressUniform = GLES20.glGetUniformLocation(programHandle, "uProgress")
    }

    override fun activate() {
        GLES20.glUseProgram(programHandle)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(mainTextureUniform, 0)
        GLES20.glUniform1i(normalMapTextureUniform, 1)
    }

    fun setProgress(progress: Float) {
        GLES20.glUniform1f(progressUniform, progress)
    }
}
