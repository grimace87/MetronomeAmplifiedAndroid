package com.grimace.metronomeamplified.components.textures

import android.content.Context
import android.graphics.Bitmap
import com.grimace.metronomeamplified.components.GlTexture
import com.grimace.metronomeamplified.extensions.openAsBitmap

class SampleScreenTexture : GlTexture() {
    override fun getBitmap(context: Context): Bitmap {
        return context.assets.openAsBitmap("sample_screenshot.png")
    }
}
