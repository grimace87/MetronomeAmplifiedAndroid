package com.grimace.metronomeamplified.traits

interface GlScene : UsesGlResources, GlRenderable {
    fun onPointerDown(normalisedX: Float, normalisedY: Float, stackManager: SceneStackManager)
}
