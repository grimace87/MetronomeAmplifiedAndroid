package com.grimace.metronomeamplified.traits

import com.grimace.metronomeamplified.caches.FramebufferCache
import com.grimace.metronomeamplified.caches.ShaderCache
import com.grimace.metronomeamplified.caches.TextureCache
import com.grimace.metronomeamplified.caches.VertexBufferCache

interface GlRenderable {
    fun onResourcesAvailable(
        shaders: ShaderCache,
        textures: TextureCache,
        vertexBuffers: VertexBufferCache,
        framebuffers: FramebufferCache
    )
    fun drawScene(timeDeltaMillis: Double, stackManager: SceneStackManager)
}