package com.grimace.metronomeamplified.components.textures

import android.content.Context
import android.graphics.Bitmap
import com.grimace.metronomeamplified.components.GlTexture
import com.grimace.metronomeamplified.extensions.openAsBitmap

class WoodenBackgroundTexture : GlTexture() {
    override fun getBitmap(context: Context): Bitmap {
        return context.assets.openAsBitmap("wood_bg_texture.jpg")
    }
}
