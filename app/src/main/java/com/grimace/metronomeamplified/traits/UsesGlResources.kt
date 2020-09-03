package com.grimace.metronomeamplified.traits

import com.grimace.metronomeamplified.components.GlFramebuffer
import com.grimace.metronomeamplified.components.GlShader
import com.grimace.metronomeamplified.components.GlTexture
import com.grimace.metronomeamplified.components.GlVertexBuffer

interface UsesGlResources {
    val requiredShaders: List<Class<out GlShader>>
    val requiredTextures: List<Class<out GlTexture>>
    val requiredVertexBuffers: List<Class<out GlVertexBuffer>>
    val requiredFramebuffers: List<Class<out GlFramebuffer>>
}