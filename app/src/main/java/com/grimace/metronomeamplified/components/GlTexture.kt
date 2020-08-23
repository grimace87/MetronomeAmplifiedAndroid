package com.grimace.metronomeamplified.components

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import java.io.IOException

abstract class GlTexture {

    var textureHandle: Int = 0
        private set

    abstract fun getBitmap(context: Context): Bitmap

    fun activate() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
    }

    fun loadTextureReturningError(context: Context): String? {

        // Read texture asset into Bitmap
        val textureBitmap: Bitmap = try {
            getBitmap(context)
        } catch (e: IOException) {
            return e.message
        }

        // Load bitmap into a texture object
        textureHandle = loadTextureBitmap(textureBitmap)

        // Return null indicating success (no error)
        return null
    }

    private fun loadTextureBitmap(bitmap: Bitmap): Int {
        val textures = intArrayOf(0)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return textures[0]
    }
}
