package com.grimace.metronomeamplified.caches

import android.content.res.Resources
import com.grimace.metronomeamplified.sealed.GlVertexBuffer

class VertexBufferCache {

    private val vertexBuffers = HashMap<Class<out GlVertexBuffer>, GlVertexBuffer>()

    fun requireVertexBuffers(vertexBufferClasses: List<Class<out GlVertexBuffer>>, resources: Resources, width: Int, height: Int) {
        vertexBufferClasses.forEach { bufferClass ->
            if (!vertexBuffers.containsKey(bufferClass)) {
                val vertexBuffer: GlVertexBuffer = bufferClass.newInstance()
                vertexBuffer.generateNewVertexBufferForSize(resources, width, height)
                vertexBuffers[bufferClass] = vertexBuffer
            }
        }
    }

    fun invalidateAll() {
        vertexBuffers.forEach { (_, buffer) ->
            buffer.invalidate()
        }
    }

    fun regenerateVertexBuffers(vertexBufferClasses: List<Class<out GlVertexBuffer>>, resources: Resources, width: Int, height: Int) {
        vertexBufferClasses.forEach { bufferClass ->
            vertexBuffers[bufferClass]?.updateVertexBufferForSize(resources, width, height)
        }
    }

    operator fun get(index: Class<out GlVertexBuffer>): GlVertexBuffer? {
        return vertexBuffers[index]
    }
}