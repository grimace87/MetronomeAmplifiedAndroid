package com.grimace.metronomeamplified.caches

import android.content.res.Resources
import com.grimace.metronomeamplified.sealed.GlVertexBuffer

class VertexBufferCache {

    private val vertexBuffers = HashMap<Class<out GlVertexBuffer>, GlVertexBuffer>()

    fun requireVertexBuffers(vertexBufferClasses: List<Class<out GlVertexBuffer>>, resources: Resources, width: Int, height: Int) {
        vertexBufferClasses.forEach { bufferClass ->
            if (!vertexBuffers.containsKey(bufferClass)) {
                val vertexBuffer: GlVertexBuffer = bufferClass.newInstance()
                vertexBuffer.generateNewVertexBuffer(resources, width, height)
                vertexBuffers[bufferClass] = vertexBuffer
            } else {
                val vertexBuffer = vertexBuffers[bufferClass]
                vertexBuffer?.updateIfNeeded(resources, width, height)
            }
        }
    }

    fun invalidateSizeDependentBuffers() {
        vertexBuffers.forEach { (_, buffer) ->
            if (buffer.isWindowSizeDependent) {
                buffer.invalidate()
            }
        }
    }

    fun regenerateVertexBuffers(vertexBufferClasses: List<Class<out GlVertexBuffer>>, resources: Resources, width: Int, height: Int) {
        vertexBufferClasses.forEach { bufferClass ->
            vertexBuffers[bufferClass]?.updateIfNeeded(resources, width, height)
        }
    }

    operator fun get(index: Class<out GlVertexBuffer>): GlVertexBuffer? {
        return vertexBuffers[index]
    }
}