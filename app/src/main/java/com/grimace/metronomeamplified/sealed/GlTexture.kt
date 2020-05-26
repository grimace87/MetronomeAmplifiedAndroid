package com.grimace.metronomeamplified.sealed

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import com.grimace.metronomeamplified.extensions.openAsBitmap
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.sqrt

sealed class GlTexture() {

    var textureHandle: Int = 0
        private set

    abstract fun getBitmap(context: Context): Bitmap

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

/**
 * Opens from file
 */
class WoodenTexture : GlTexture() {
    override fun getBitmap(context: Context): Bitmap {
        return context.assets.openAsBitmap("wood_bg_texture.jpg")
    }
}

/**
 * Generates bitmap dependent on device density
 */
class WhiteTranslucentShapesTexture : GlTexture() {
    override fun getBitmap(context: Context): Bitmap {

        // Determine sizes and create the data array
        val cornerRadiusDips = 16.0f
        val alphaLevel = 0x80.toByte()
        val cornerRadiusPixels = (cornerRadiusDips * context.resources.displayMetrics.density).toInt()
        val rowStrideBytes = 4 * 2 * cornerRadiusPixels
        val sectionOffsetBytes = 4 * cornerRadiusPixels
        val textureData = ByteArray(rowStrideBytes * cornerRadiusPixels) { 0xFF.toByte() }

        // Generate the left section (rounded corner)
        for (j in 0.until(cornerRadiusPixels)) {
            var index = j * rowStrideBytes
            val transparentPixels = (cornerRadiusPixels - sqrt(max(0.0, 2.0 * j * cornerRadiusPixels - j * j))).toInt()
            for (i in 0.until(cornerRadiusPixels)) {
                val pixelAlpha = if (i <= transparentPixels) 0 else alphaLevel
                textureData[index + 3] = pixelAlpha
                index += 4
            }
        }

        // Generate the right section (solid colour)
        for (j in 0.until(cornerRadiusPixels)) {
            var index = j * rowStrideBytes + sectionOffsetBytes
            for (i in 0.until(cornerRadiusPixels)) {
                textureData[index + 3] = alphaLevel
                index += 4
            }
        }

        // Put data into a buffer
        val buffer = ByteBuffer.allocateDirect(textureData.size)
        buffer.put(textureData)
        buffer.position(0)

        // Create the Android Bitmap with this data
        val bitmap = Bitmap.createBitmap(cornerRadiusPixels * 2, cornerRadiusPixels, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }
}
