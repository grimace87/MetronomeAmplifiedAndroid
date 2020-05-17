package com.grimace.metronomeamplified.traits

import com.grimace.metronomeamplified.sealed.GlShader
import com.grimace.metronomeamplified.sealed.GlTexture
import com.grimace.metronomeamplified.sealed.GlVertexBuffer

interface UsesGlResources {
    val requiredShaders: List<Class<out GlShader>>
    val requiredTextures: List<Class<out GlTexture>>
    val requiredVertexBuffers: List<Class<out GlVertexBuffer>>
}