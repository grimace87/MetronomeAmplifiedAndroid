package com.grimace.metronomeamplified.caches

import com.grimace.metronomeamplified.sealed.GlVertexBuffer

class VertexBufferCache {

    private val vertexBuffers = HashMap<Class<out GlVertexBuffer>, GlVertexBuffer>()

    fun requireVertexBuffers(vertexBufferClasses: List<Class<out GlVertexBuffer>>, width: Int, height: Int) {
        vertexBufferClasses.forEach { bufferClass ->
            if (!vertexBuffers.containsKey(bufferClass)) {
                val vertexBuffer: GlVertexBuffer = bufferClass.newInstance()
                vertexBuffer.generateNewVertexBufferForSize(width, height)
                vertexBuffers[bufferClass] = vertexBuffer
            }
        }
    }

    fun invalidateAll() {
        vertexBuffers.forEach { (_, buffer) ->
            buffer.invalidate()
        }
    }

    fun regenerateVertexBuffers(vertexBufferClasses: List<Class<out GlVertexBuffer>>, width: Int, height: Int) {
        vertexBufferClasses.forEach { bufferClass ->
            vertexBuffers[bufferClass]?.updateVertexBufferForSize(width, height)
        }
    }

    operator fun get(index: Class<out GlVertexBuffer>): GlVertexBuffer? {
        return vertexBuffers[index]
    }
}