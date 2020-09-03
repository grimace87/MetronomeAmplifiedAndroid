package com.grimace.metronomeamplified.caches

import com.grimace.metronomeamplified.components.GlFramebuffer

class FramebufferCache {

    private val framebuffers = HashMap<Class<out GlFramebuffer>, GlFramebuffer>()

    operator fun get(index: Class<out GlFramebuffer>): GlFramebuffer? {
        return framebuffers[index]
    }

    fun clearFramebuffers() {
        framebuffers.forEach { mapEntry ->
            mapEntry.value.release()
        }
        framebuffers.clear()
    }

    fun requireFramebuffers(framebufferClasses: List<Class<out GlFramebuffer>>, width: Int, height: Int) {
        framebufferClasses.forEach { framebufferClass ->
            val framebuffer = framebufferClass.newInstance()
            framebuffer.generate(surfaceWidth = width, surfaceHeight = height)
            framebuffers[framebufferClass] = framebuffer
        }
    }
}