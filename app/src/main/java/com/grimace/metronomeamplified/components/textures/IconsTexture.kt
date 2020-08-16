package com.grimace.metronomeamplified.components.textures

import android.content.Context
import android.graphics.Bitmap
import com.grimace.metronomeamplified.R
import com.grimace.metronomeamplified.components.GlTexture
import com.grimace.metronomeamplified.extensions.openDrawableBitmap

class IconsTexture : GlTexture() {
    override fun getBitmap(context: Context): Bitmap {
        return context.openDrawableBitmap(R.drawable.icons)
    }
}
