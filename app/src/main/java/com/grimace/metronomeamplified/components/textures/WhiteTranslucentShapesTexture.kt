package com.grimace.metronomeamplified.components.textures

import android.content.Context
import android.graphics.Bitmap
import com.grimace.metronomeamplified.components.GlTexture
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.sqrt

class WhiteTranslucentShapesTexture : GlTexture() {
    override fun getBitmap(context: Context): Bitmap {

        // Determine sizes and create the data array
        val cornerRadiusDips = 16.0f
        val alphaLevel = 0x80.toByte()
        val cornerRadiusPixels = (cornerRadiusDips * context.resources.displayMetrics.density).toInt()
        val rowStrideBytes = 4 * 2 * cornerRadiusPixels
        val sectionOffsetBytes = 4 * cornerRadiusPixels
        val textureData = ByteArray(2 * rowStrideBytes * cornerRadiusPixels) { 0xFF.toByte() }

        // Generate the top-left section (outer corner)
        for (j in 0.until(cornerRadiusPixels)) {
            var index = j * rowStrideBytes
            val transparentPixels = (cornerRadiusPixels - sqrt(max(0.0, 2.0 * j * cornerRadiusPixels - j * j))).toInt()
            for (i in 0.until(cornerRadiusPixels)) {
                val pixelAlpha = if (i <= transparentPixels) 0 else alphaLevel
                textureData[index + 3] = pixelAlpha
                index += 4
            }
        }

        // Generate the top-right section (solid colour)
        for (j in 0.until(cornerRadiusPixels)) {
            var index = j * rowStrideBytes + sectionOffsetBytes
            for (i in 0.until(cornerRadiusPixels)) {
                textureData[index + 3] = alphaLevel
                index += 4
            }
        }

        // Generate the bottom-left section (inner corner)
        for (j in 0.until(cornerRadiusPixels)) {
            var index = (cornerRadiusPixels + j) * rowStrideBytes
            val transparentPixels = sqrt(max(0.0, 2.0 * j * cornerRadiusPixels - j * j)).toInt()
            for (i in 0.until(cornerRadiusPixels)) {
                val pixelAlpha = if (i <= transparentPixels) 0 else alphaLevel
                textureData[index + 3] = pixelAlpha
                index += 4
            }
        }

        // Generate the right section (fully transparent)
        for (j in 0.until(cornerRadiusPixels)) {
            var index = (cornerRadiusPixels + j) * rowStrideBytes + sectionOffsetBytes
            for (i in 0.until(cornerRadiusPixels)) {
                textureData[index + 3] = 0
                index += 4
            }
        }

        // Put data into a buffer
        val buffer = ByteBuffer.allocateDirect(textureData.size)
        buffer.put(textureData)
        buffer.position(0)

        // Create the Android Bitmap with this data
        val bitmap = Bitmap.createBitmap(cornerRadiusPixels * 2, cornerRadiusPixels * 2, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }
}
