package com.grimace.metronomeamplified.caches

import android.content.Context
import com.grimace.metronomeamplified.sealed.GlTexture
import java.lang.RuntimeException

class TextureCache {

    private val textures = HashMap<Class<out GlTexture>, GlTexture>()

    fun requireTextures(context: Context, textureClasses: List<Class<out GlTexture>>) {
        textureClasses.forEach { textureClass ->
            if (!textures.containsKey(textureClass)) {
                val texture: GlTexture = textureClass.newInstance()
                val loadResult = texture.loadTextureReturningError(context)
                if (loadResult != null) {
                    throw RuntimeException(loadResult)
                }
                textures[textureClass] = texture
            }
        }
    }

    operator fun get(index: Class<out GlTexture>): GlTexture? {
        return textures[index]
    }
}