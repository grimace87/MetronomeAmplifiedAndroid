package com.grimace.metronomeamplified.traits

import com.grimace.metronomeamplified.components.GlFramebuffer
import com.grimace.metronomeamplified.components.GlShader
import com.grimace.metronomeamplified.components.GlTexture
import com.grimace.metronomeamplified.components.GlVertexBuffer

interface SceneStackManager {
    fun pushScene(scene: GlScene)
    fun popTopScene()
    fun<T : GlVertexBuffer> checkForPointOfInterest(vboClass: Class<T>, normalisedX: Float, normalisedY: Float): Int
    fun<T : GlVertexBuffer> getVertexBuffer(vboClass: Class<T>): GlVertexBuffer?
    fun<T : GlShader> getShader(shaderClass: Class<T>): GlShader?
    fun<T : GlTexture> getTexture(textureClass: Class<T>): GlTexture?
    fun<T : GlFramebuffer> getFramebuffer(framebufferClass: Class<T>): GlFramebuffer?
}