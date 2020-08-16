package com.grimace.metronomeamplified.components.vertexbuffers

import android.content.Context
import android.graphics.PointF
import com.grimace.metronomeamplified.components.GlVertexBuffer
import java.nio.FloatBuffer

class MainScreenBackgroundVertexBuffer : GlVertexBuffer() {

    override val isWindowSizeDependent: Boolean = true

    override fun generateVerticesForSize(context: Context, width: Int, height: Int): FloatBuffer {

        val displayMetrics = context.resources.displayMetrics
        val marginDips = 16.0f
        val marginUnitsW: Float = 2.0f * (marginDips * displayMetrics.density) / width.toFloat()
        val marginUnitsH: Float = 2.0f * (marginDips * displayMetrics.density) / height.toFloat()
        val screenSize = PointF(displayMetrics.widthPixels.toFloat(), displayMetrics.heightPixels.toFloat())

        val w1 = -1.0f + marginUnitsW
        val w2 = w1 + marginUnitsW
        val w4 = -1.0f + (2.0f - 2.0f * marginUnitsW) / 3.0f
        val w3 = w4 - marginUnitsW
        val w5 = w4 + marginUnitsW
        val w7 = 1.0f - (2.0f - 2.0f * marginUnitsW) / 3.0f
        val w6 = w7 - marginUnitsW
        val w8 = w7 + marginUnitsW
        val w10 = 1.0f - marginUnitsW
        val w9 = w10 - marginUnitsW

        val h1 = -1.0f + marginUnitsH
        val h2 = h1 + marginUnitsH
        val h4 = -1.0f + (2.0f - marginUnitsH) / 4.0f
        val h3 = h4 - marginUnitsH
        val h6 = 0.0f - marginUnitsH
        val h5 = h6 - marginUnitsH

        val hIcon1Left = -1.0f
        val hIcon2Left = -0.5f
        val hIcon3Left = 0.0f
        val hIcon4Left = 0.5f
        val hIcon4Right = 1.0f

        val hIconBottom = 0.7f
        val hIconLabelBottom = 0.5f
        val hIconTop = 1.0f
        val hLowerIconsLabelTop = h2 + 0.25f * (h3 - h2)

        val data = FloatArray(30 * 25)
        data.putSquare(0, -1f, -1f, 1f, 1f, 0f, 0f, 1f, 1f)

        data.putSquare(30, w1, h1, w2, h2, 0.0f, 0.0f, 0.5f, 0.5f)
        data.putSquare(60, w2, h1, w3, h2, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(90, w3, h1, w4, h2, 0.5f, 0.0f, 0.0f, 0.5f)

        data.putSquare(120, w7, h1, w8, h2, 0.0f, 0.0f, 0.5f, 0.5f)
        data.putSquare(150, w8, h1, w9, h2, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(180, w9, h1, w10, h2, 0.5f, 0.0f, 0.0f, 0.5f)

        data.putSquare(210, w1, h2, w2, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(240, w2, h3, w3, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(270, w3, h2, w4, h4, 0.5f, 0.0f, 1.0f, 0.5f)

        data.putSquare(300, w7, h2, w8, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(330, w8, h3, w9, h4, 0.5f, 0.0f, 1.0f, 0.5f)
        data.putSquare(360, w9, h2, w10, h4, 0.5f, 0.0f, 1.0f, 0.5f)

        data.putSquare(390, w4, h3, w5, h4, 0.5f, 1.0f, 0.0f, 0.5f)
        data.putSquare(420, w6, h3, w7, h4, 0.0f, 1.0f, 0.5f, 0.5f)

        data.putSquare(450, w1, h4, w10, h5, 0.5f, 0.0f, 1.0f, 0.5f)

        data.putSquare(480, w1, h5, w2, h6, 0.0f, 0.5f, 0.5f, 0.0f)
        data.putSquare(510, w2, h5, w9, h6, 0.5f, 0.5f, 1.0f, 0.0f)
        data.putSquare(540, w9, h5, w10, h6, 0.5f, 0.5f, 0.0f, 0.0f)

        data.putSquareCentredInside(570, hIcon1Left, hIconBottom, hIcon2Left, hIconTop, 0.0f, 0.5f, 0.25f, 0.0f, screenSize)
        data.putSquareCentredInside(600, hIcon2Left, hIconBottom, hIcon3Left, hIconTop, 0.25f, 0.5f, 0.5f, 0.0f, screenSize)
        data.putSquareCentredInside(630, hIcon3Left, hIconBottom, hIcon4Left, hIconTop, 0.5f, 0.5f, 0.75f, 0.0f, screenSize)
        data.putSquareCentredInside(660, hIcon4Left, hIconBottom, hIcon4Right, hIconTop, 0.75f, 0.5f, 1.0f, 0.0f, screenSize)
        data.putSquareCentredInside(690, w2, hLowerIconsLabelTop, w3, h3, 0.0f, 1.0f, 0.25f, 0.5f, screenSize)
        data.putSquareCentredInside(720, w8, hLowerIconsLabelTop, w9, h3, 0.25f, 1.0f, 0.5f, 0.5f, screenSize)

        return data.toFloatBuffer()
    }
}
