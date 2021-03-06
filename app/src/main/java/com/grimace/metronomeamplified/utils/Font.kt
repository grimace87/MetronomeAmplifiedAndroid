package com.grimace.metronomeamplified.utils

import android.content.Context
import android.graphics.PointF
import android.view.Gravity
import com.grimace.metronomeamplified.components.FLOATS_PER_QUAD
import com.grimace.metronomeamplified.extensions.openAsString
import java.io.BufferedReader
import java.io.IOException
import java.io.StringReader
import java.security.InvalidParameterException
import kotlin.math.min

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
    fun printTextIntoVbo(
        vboData: FloatArray,
        startIndex: Int,
        textToRender: String,
        left: Float,
        top: Float,
        boxWidth: Float,
        boxHeight: Float,
        maxHeightPixels: Float,
        screenSize: PointF,
        horizontalGravity: Int,
        verticalGravity: Int) {

        // Find scaling factors
        val pixelsPerUnitWidth = screenSize.x / 2.0f
        val pixelsPerUnitHeight = screenSize.y / 2.0f

        // Get available area and print line height in pixels
        val targetWidthPixels = pixelsPerUnitWidth * boxWidth
        val targetHeightPixels = pixelsPerUnitHeight * boxHeight
        val lineHeightPixels = min(targetHeightPixels, maxHeightPixels)
        val screenPixelsPerFontPixel = lineHeightPixels / this.lineHeight

        // Do an initial pass to determine how many lines need to be rendered, and how many
        // characters will be on each of those lines
        val charactersPerLine = ArrayList<Int>()
        val pixelWidthOfLine = ArrayList<Float>()
        var pixelsAcrossThisLine = 0.0f
        var currentWordBegunAt = 0
        var pixelsIntoThisWord = 0.0f
        var charsForThisLine = 0
        textToRender.forEachIndexed { index, c ->
            this.glyphs[c.toInt()]?.let { glyph ->
                val advance = glyph.advanceX * screenPixelsPerFontPixel
                pixelsAcrossThisLine += advance
                pixelsIntoThisWord += advance
                charsForThisLine++
                if (c == ' ') {
                    currentWordBegunAt = index + 1
                    pixelsIntoThisWord = 0.0f
                } else if (pixelsAcrossThisLine > targetWidthPixels) {
                    if (index - currentWordBegunAt + 1 == charsForThisLine) {
                        charactersPerLine.add(index - currentWordBegunAt)
                        currentWordBegunAt = index
                        charsForThisLine = 1
                        pixelWidthOfLine.add(pixelsAcrossThisLine - advance)
                        pixelsAcrossThisLine = advance
                        pixelsIntoThisWord = advance
                    } else {
                        val charactersForNextLine = index + 1 - currentWordBegunAt
                        charactersPerLine.add(charsForThisLine - charactersForNextLine)
                        charsForThisLine = charactersForNextLine
                        pixelWidthOfLine.add(pixelsAcrossThisLine - pixelsIntoThisWord)
                        pixelsAcrossThisLine = pixelsIntoThisWord
                    }
                }
            }
        }
        if (charsForThisLine > 0) {
            charactersPerLine.add(charsForThisLine)
            pixelWidthOfLine.add(pixelsAcrossThisLine)
        }

        // Set side margin, horizontal margin depends on supplied gravity
        val totalTextHeightPixels = charactersPerLine.size.toFloat() * lineHeightPixels
        val marginYPixels: Float = when (verticalGravity) {
            Gravity.START -> targetHeightPixels - totalTextHeightPixels
            Gravity.END -> 0.0f
            else -> 0.5f * (targetHeightPixels - totalTextHeightPixels)
        }

        // Start building the buffer
        var charsRendered = 0
        val widthUnitsPerFontPixel = screenPixelsPerFontPixel / pixelsPerUnitWidth
        val heightUnitsPerFontPixel = screenPixelsPerFontPixel / pixelsPerUnitHeight
        var penY: Float = top - boxHeight + marginYPixels / pixelsPerUnitHeight + (charactersPerLine.size - 1).toFloat() * lineHeightPixels / pixelsPerUnitHeight
        var textIndex = 0
        charactersPerLine.forEachIndexed { index, charsOnLine ->
            val lineWidthPixels = pixelWidthOfLine[index]
            val marginXPixels: Float = when (horizontalGravity) {
                Gravity.START -> 0.0f
                Gravity.END -> targetWidthPixels - lineWidthPixels
                else -> 0.5f * (targetWidthPixels - lineWidthPixels)
            }
            var penX: Float = left + marginXPixels / pixelsPerUnitWidth
            for (i in 0.until(charsOnLine)) {
                val char = textToRender[textIndex]
                textIndex++
                val glyph = this.glyphs[char.toInt()] ?: continue

                val xMin = penX + glyph.offsetX * widthUnitsPerFontPixel
                val xMax = xMin + glyph.width * widthUnitsPerFontPixel
                val yMax = penY + (this.baseHeight - glyph.offsetY) * heightUnitsPerFontPixel
                val yMin = yMax - glyph.height * heightUnitsPerFontPixel

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
                quadData.copyInto(vboData, startIndex + charsRendered * FLOATS_PER_QUAD)
                penX += glyph.advanceX * widthUnitsPerFontPixel
                charsRendered++
            }
            penY -= lineHeightPixels / pixelsPerUnitHeight
        }
    }
}