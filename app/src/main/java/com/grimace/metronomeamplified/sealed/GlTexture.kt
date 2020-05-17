package com.grimace.metronomeamplified.sealed

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import com.grimace.metronomeamplified.extensions.openAsBitmap
import java.io.IOException

sealed class GlTexture(private val textureAssetName: String) {

    var textureHandle: Int = 0
        private set

    fun loadTextureReturningError(context: Context): String? {

        // Read texture asset into Bitmap
        val assetManager = context.assets
        val textureBitmap: Bitmap
        try {
            textureBitmap = assetManager.openAsBitmap(textureAssetName)
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
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return textures[0]
    }
}

class WoodenTexture : GlTexture("wood_bg_texture.jpg")
