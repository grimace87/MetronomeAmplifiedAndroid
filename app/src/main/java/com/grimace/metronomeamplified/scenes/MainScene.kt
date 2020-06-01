package com.grimace.metronomeamplified.scenes

import android.opengl.GLES20
import com.grimace.metronomeamplified.caches.ShaderCache
import com.grimace.metronomeamplified.caches.TextureCache
import com.grimace.metronomeamplified.caches.VertexBufferCache
import com.grimace.metronomeamplified.sealed.*
import com.grimace.metronomeamplified.traits.GlScene

class MainScene : GlScene {

    override val requiredShaders: List<Class<out GlShader>>
        get() = listOf(AlphaTexture::class.java)

    override val requiredTextures: List<Class<out GlTexture>>
        get() = listOf(WoodenTexture::class.java, WhiteTranslucentShapesTexture::class.java)

    override val requiredVertexBuffers: List<Class<out GlVertexBuffer>>
        get() = listOf(MainScreenBackgroundVertexBuffer::class.java)

    private var shaderProgramHandle = 0
    private var backgroundTextureHandle = 0
    private var overlayTextureHandle = 0
    private var vertexBufferHandle = 0
    private var vertexAttrib = 0
    private var textureCoordAttrib = 0
    private var textureSampler = 0

    override fun onResourcesAvailable(
        shaders: ShaderCache,
        textures: TextureCache,
        vertexBuffers: VertexBufferCache
    ) {
        // Pre-fetch handles for resources
        shaderProgramHandle = shaders[AlphaTexture::class.java]?.programHandle ?: 0
        backgroundTextureHandle = textures[WoodenTexture::class.java]?.textureHandle ?: 0
        overlayTextureHandle = textures[WhiteTranslucentShapesTexture::class.java]?.textureHandle ?: 0
        vertexBufferHandle = vertexBuffers[MainScreenBackgroundVertexBuffer::class.java]?.vertexBufferHandle ?: 0

        // Get attributes and make sure they're enabled
        vertexAttrib = GLES20.glGetAttribLocation(shaderProgramHandle, "aPosition")
        textureCoordAttrib = GLES20.glGetAttribLocation(shaderProgramHandle, "aTextureCoord")
        GLES20.glEnableVertexAttribArray(vertexAttrib)
        GLES20.glEnableVertexAttribArray(textureCoordAttrib)

        // Get uniforms
        textureSampler = GLES20.glGetUniformLocation(shaderProgramHandle, "uTextureSampler")
    }

    override fun drawScene() {

        // Clear
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // Set program and active texture
        GLES20.glUseProgram(shaderProgramHandle)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(textureSampler, 0)

        // Load vertex array
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferHandle)
        GLES20.glVertexAttribPointer(vertexAttrib,
            3, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 0)
        GLES20.glVertexAttribPointer(textureCoordAttrib,
            2, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES)

        // Draw background vertices
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backgroundTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)

        // Draw overlay vertices
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, overlayTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 6, 108)

        // Unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }
}