package com.grimace.metronomeamplified.traits

import com.grimace.metronomeamplified.components.GlVertexBuffer

interface SceneStackManager {
    fun pushScene(scene: GlScene)
    fun popTopScene()
    fun<T : GlVertexBuffer> checkForPointOfInterest(vboClass: Class<T>, normalisedX: Float, normalisedY: Float): Int
    fun<T : GlVertexBuffer> getVertexBuffer(vboClass: Class<T>): GlVertexBuffer?
}