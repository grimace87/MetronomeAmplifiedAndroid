package com.grimace.metronomeamplified.utils

import android.content.Context
import com.grimace.metronomeamplified.extensions.openAsString
import java.io.BufferedReader
import java.io.IOException
import java.io.StringReader
import java.security.InvalidParameterException

private const val FONT_TEXTURE_GLYPH_COUNT = 128
private const val FONT_TEXTURE_SIZE = 512.0f

class Font private constructor(
    private val baseHeight: Float,
    private val lineHeight: Float,
    private val glyphs: Array<Glyph?>) {

    private class Glyph(
        val textureS: Float,
        val textureT: Float,
        val offsetX: Float,
        val offsetY: Float,
        val width: Float,
        val height: Float,
        val advanceX: Float
    )

    companion object {

        @Throws(IOException::class)
        fun fromAsset(context: Context, fontDescriptionAsset: String): Font {
            val fileContents = context.assets.openAsString(fontDescriptionAsset)
            return decodeGlyphData(fileContents)
        }

        private fun decodeGlyphData(data: String): Font {

            // Create the buffer
            val glyphSet = Array<Glyph?>(FONT_TEXTURE_GLYPH_COUNT) { null }

            // Create a stream around this resource
            val stream = BufferedReader(StringReader(data))

            // Search for "base=XX" and "lineHeight=XX" components before "chars count=XX"
            val numberTerminators = charArrayOf(' ', '\n', '\r')
            val keyBase = "base="
            val keyLineHeight = "lineHeight="
            val keyCharCount = "count="
            var valBase = 0
            var valLineHeight = 0
            while (true) {

                // Read each component of the format "key=xx", return early if stream is consumed
                val nextLine = stream.readLine() ?: throw InvalidParameterException(
                    "Asset stream consumed before basic parameters were found")

                val basePos = nextLine.indexOf(keyBase)
                if (basePos != -1) {
                    val lineRemaining = nextLine.substring(basePos + keyBase.length)
                    val endPos = lineRemaining.indexOfAny(numberTerminators)
                    valBase = lineRemaining.substring(0, endPos).toInt()
                }

                val lineHeightPos = nextLine.indexOf(keyLineHeight)
                if (lineHeightPos != -1) {
                    val lineRemaining = nextLine.substring(lineHeightPos + keyLineHeight.length)
                    val endPos = lineRemaining.indexOfAny(numberTerminators)
                    valLineHeight = lineRemaining.substring(0, endPos).toInt()
                }

                val charCountPos = nextLine.indexOf(keyCharCount)
                if (charCountPos != -1) {
                    break
                }
            }

            // Parse each character line
            val keyNewChar = "char"
            val keyId = "id="
            val keyX = "x="
            val keyY = "y="
            val keyWidth = "width="
            val keyHeight = "height="
            val keyOffsetX = "xoffset="
            val keyOffsetY = "yoffset="
            val keyAdvance = "xadvance="
            while (true) {

                // Read each component of the format "key=xx"
                val nextLine = stream.readLine() ?: break

                // Read lines that begin with "char"
                if (nextLine.startsWith(keyNewChar).not()) {
                    continue
                }

                // Initialise properties
                var valId = -1
                var valTextureS = 0.0f
                var valTextureT = 0.0f
                var valOffsetX = 0.0f
                var valOffsetY = 0.0f
                var valWidth = 0.0f
                var valHeight = 0.0f
                var valAdvance = 0.0f

                // Read ID
                var keyPosition = nextLine.indexOf(keyId)
                if (keyPosition != -1) {
                    val lineRemaining = nextLine.substring(keyPosition + keyId.length)
                    val endPos = lineRemaining.indexOfAny(numberTerminators)
                    valId = lineRemaining.substring(0, endPos).toInt()
                }

                // Check we've made space for this ID, skip it otherwise
                if (valId < 0  || valId >= FONT_TEXTURE_GLYPH_COUNT) {
                    continue
                }

                // Read texture S
                keyPosition = nextLine.indexOf(keyX)
                if (keyPosition != -1) {
                    val lineRemaining = nextLine.substring(keyPosition + keyX.length)
                    val endPos = lineRemaining.indexOfAny(numberTerminators)
                    valTextureS = lineRemaining.substring(0, endPos).toFloat()
                }

                // Read texture T
                keyPosition = nextLine.indexOf(keyY)
                if (keyPosition != -1) {
                    val lineRemaining = nextLine.substring(keyPosition + keyY.length)
                    val endPos = lineRemaining.indexOfAny(numberTerminators)
                    valTextureT = lineRemaining.substring(0, endPos).toFloat()
                }

                // Read offset X
                keyPosition = nextLine.indexOf(keyOffsetX)
                if (keyPosition != -1) {
                    val lineRemaining = nextLine.substring(keyPosition + keyOffsetX.length)
                    val endPos = lineRemaining.indexOfAny(numberTerminators)
                    valOffsetX = lineRemaining.substring(0, endPos).toFloat()
                }

                // Read offsetY
                keyPosition = nextLine.indexOf(keyOffsetY)
                if (keyPosition != -1) {
                    val lineRemaining = nextLine.substring(keyPosition + keyOffsetY.length)
                    val endPos = lineRemaining.indexOfAny(numberTerminators)
                    valOffsetY = lineRemaining.substring(0, endPos).toFloat()
                }

                // Read width
                keyPosition = nextLine.indexOf(keyWidth)
                if (keyPosition != -1) {
                    val lineRemaining = nextLine.substring(keyPosition + keyWidth.length)
                    val endPos = lineRemaining.indexOfAny(numberTerminators)
                    valWidth = lineRemaining.substring(0, endPos).toFloat()
                }

                // Read height
                keyPosition = nextLine.indexOf(keyHeight)
                if (keyPosition != -1) {
                    val lineRemaining = nextLine.substring(keyPosition + keyHeight.length)
                    val endPos = lineRemaining.indexOfAny(numberTerminators)
                    valHeight = lineRemaining.substring(0, endPos).toFloat()
                }

                // Read advance X
                keyPosition = nextLine.indexOf(keyAdvance)
                if (keyPosition != -1) {
                    val lineRemaining = nextLine.substring(keyPosition + keyAdvance.length)
                    val endPos = lineRemaining.indexOfAny(numberTerminators)
                    valAdvance = lineRemaining.substring(0, endPos).toFloat()
                }

                // Save the character properties
                if (valId in 0 until FONT_TEXTURE_GLYPH_COUNT) {
                    glyphSet[valId] = Glyph(
                        textureS = valTextureS,
                        textureT = valTextureT,
                        offsetX = valOffsetX,
                        offsetY = valOffsetY,
                        width = valWidth,
                        height = valHeight,
                        advanceX = valAdvance
                    )
                }
            }

            // Return set
            return Font(valBase.toFloat(), valLineHeight.toFloat(), glyphSet)
        }
    }

    /**
     * Generate VBO data to render supplied text, writing into the provided float array.
     * Required space in the float array, starting at the startIndex offset, is 30 floats per
     * character.
     */
    fun printIntoVbo(
        vboData: FloatArray,
        startIndex: Int,
        textToRender: String,
        left: Float,
        top: Float,
        boxWidth: Float,
        boxHeight: Float,
        lines: Float,
        scale: Float) {

        // Will use 6 vertices per character and 5 floats per vertex
        val floatsPerCharacter = 30

        // Find scaling factor
        val lineHeightUnits: Float = boxHeight / lines
        val unitsPerPixel: Float = scale * lineHeightUnits / this.lineHeight

        // Start building the buffer
        var charsRendered = 0
        var penX: Float = left
        var penY: Float = top - this.baseHeight * unitsPerPixel
        for (c: Char in textToRender) {

            val glyph = this.glyphs[c.toInt()] ?: continue

            val xMin = penX + glyph.offsetX * unitsPerPixel
            val xMax = xMin + glyph.width * unitsPerPixel
            val yMax = penY + (this.baseHeight - glyph.offsetY) * unitsPerPixel
            val yMin = yMax - glyph.height * unitsPerPixel

            val sMin = glyph.textureS / FONT_TEXTURE_SIZE
            val sMax = sMin + glyph.width / FONT_TEXTURE_SIZE
            val tMin = glyph.textureT / FONT_TEXTURE_SIZE
            val tMax = tMin + glyph.height / FONT_TEXTURE_SIZE

            val quadData = floatArrayOf(
                xMin, yMax, 0.0f, sMin, tMin,
                xMin, yMin, 0.0f, sMin, tMax,
                xMax, yMin, 0.0f, sMax, tMax,
                xMax, yMin, 0.0f, sMax, tMax,
                xMax, yMax, 0.0f, sMax, tMin,
                xMin, yMax, 0.0f, sMin, tMin
            )
            quadData.copyInto(vboData, startIndex + charsRendered * floatsPerCharacter)

            penX += glyph.advanceX * unitsPerPixel
            if ((penX + this.lineHeight * unitsPerPixel) > boxWidth) {
                penX = left
                penY -= this.lineHeight * unitsPerPixel
            }
            charsRendered++
        }
    }
}