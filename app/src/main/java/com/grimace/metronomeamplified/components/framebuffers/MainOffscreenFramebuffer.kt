package com.grimace.metronomeamplified.components.framebuffers

import com.grimace.metronomeamplified.components.GlFramebuffer

class MainOffscreenFramebuffer : GlFramebuffer() {
    override fun generate(surfaceWidth: Int, surfaceHeight: Int) {
        makeRgbForSize(surfaceWidth, surfaceHeight)
    }
}
