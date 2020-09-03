package com.grimace.metronomeamplified.components

import android.opengl.GLES20
import android.util.Log

abstract class GlFramebuffer {

    var mFramebuffer: Int = GLES20.GL_NONE
        private set

    var mColorTexture: Int = GLES20.GL_NONE
        private set

    var mDepthRenderbuffer: Int = GLES20.GL_NONE
        private set

    var mHasColorAttachment: Boolean = false
    var mHasDepthAttachment: Boolean = false

    abstract fun generate (surfaceWidth: Int, surfaceHeight: Int)

    fun activate() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer)
    }

    fun deactivate() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    fun release() {
        if (mHasColorAttachment) {
            GLES20.glDeleteTextures(1, intArrayOf(mColorTexture), 0)
        }
        if (mHasDepthAttachment) {
            GLES20.glDeleteRenderbuffers(1, intArrayOf(mDepthRenderbuffer), 0)
        }
        GLES20.glDeleteFramebuffers(1, intArrayOf(mFramebuffer), 0)
    }

    protected fun makeRgbForSize(width: Int, height: Int) {

        val framebuffer: Int = intArrayOf(0).run {
            GLES20.glGenFramebuffers(1, this, 0)
            this[0]
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer)

        val texture: Int = intArrayOf(0).run {
            GLES20.glGenTextures(1, this, 0)
            this[0]
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, width, height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture, 0)

        val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
        if (status == GLES20.GL_FRAMEBUFFER_COMPLETE) {
            mFramebuffer = framebuffer
            mColorTexture = texture
            mHasColorAttachment = true
            mHasDepthAttachment = false
        } else {
            Log.e("GlFramebuffer", "Framebuffer not complete, status returned $status")
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }
}