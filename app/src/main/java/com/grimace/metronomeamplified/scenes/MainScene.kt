package com.grimace.metronomeamplified.scenes

import android.opengl.GLES20
import com.grimace.metronomeamplified.caches.ShaderCache
import com.grimace.metronomeamplified.caches.TextureCache
import com.grimace.metronomeamplified.caches.VertexBufferCache
import com.grimace.metronomeamplified.sealed.*
import com.grimace.metronomeamplified.traits.GlScene

class MainScene : GlScene {

    override val requiredShaders: List<Class<out GlShader>>
        get() = listOf(AlphaTexture::class.java, FontShader::class.java)

    override val requiredTextures: List<Class<out GlTexture>>
        get() = listOf(WoodenTexture::class.java, WhiteTranslucentShapesTexture::class.java, OrkneyFontTexture::class.java, IconsTexture::class.java)

    override val requiredVertexBuffers: List<Class<out GlVertexBuffer>>
        get() = listOf(MainScreenBackgroundVertexBuffer::class.java, RandomTextVertexBuffer::class.java)

    // Main shader program shader handles
    private var mainProgramHandle = 0
    private var mainProgramVertexAttrib = 0
    private var mainProgramTextureCoordAttrib = 0
    private var mainProgramTextureSampler = 0

    // Resources used with main shader
    private var backgroundTextureHandle = 0
    private var overlayTextureHandle = 0
    private var iconsTextureHandle = 0
    private var backgroundVertexBufferHandle = 0

    // Font shader program handles
    private var fontProgramHandle = 0
    private var fontProgramVertexAttrib = 0
    private var fontProgramTextureCoordAttrib = 0
    private var fontProgramTextureSampler = 0
    private var fontProgramPaintColor = 0

    // Resources used with font shader
    private var fontTextureHandle = 0
    private var fontVertexBufferHandle = 0

    override fun onResourcesAvailable(
        shaders: ShaderCache,
        textures: TextureCache,
        vertexBuffers: VertexBufferCache
    ) {
        // Pre-fetch handles for main shader and resources to be used with that shader
        mainProgramHandle = shaders[AlphaTexture::class.java]?.programHandle ?: 0
        backgroundTextureHandle = textures[WoodenTexture::class.java]?.textureHandle ?: 0
        overlayTextureHandle = textures[WhiteTranslucentShapesTexture::class.java]?.textureHandle ?: 0
        iconsTextureHandle = textures[IconsTexture::class.java]?.textureHandle ?: 0
        backgroundVertexBufferHandle = vertexBuffers[MainScreenBackgroundVertexBuffer::class.java]?.vertexBufferHandle ?: 0

        // Get attributes for the main shader and make sure they're enabled
        mainProgramVertexAttrib = GLES20.glGetAttribLocation(mainProgramHandle, "aPosition")
        mainProgramTextureCoordAttrib = GLES20.glGetAttribLocation(mainProgramHandle, "aTextureCoord")
        GLES20.glEnableVertexAttribArray(mainProgramVertexAttrib)
        GLES20.glEnableVertexAttribArray(mainProgramTextureCoordAttrib)

        // Get uniforms for the main shader
        mainProgramTextureSampler = GLES20.glGetUniformLocation(mainProgramHandle, "uTextureSampler")

        // Pre-fetch handles for font shader and resources to be used with that shader
        fontProgramHandle = shaders[FontShader::class.java]?.programHandle ?: 0
        fontTextureHandle = textures[OrkneyFontTexture::class.java]?.textureHandle ?: 0
        fontVertexBufferHandle = vertexBuffers[RandomTextVertexBuffer::class.java]?.vertexBufferHandle ?: 0

        // Get attributes for the font shader and make sure they're enabled
        fontProgramVertexAttrib = GLES20.glGetAttribLocation(fontProgramHandle, "aPosition")
        fontProgramTextureCoordAttrib = GLES20.glGetAttribLocation(fontProgramHandle, "aTextureCoord")
        GLES20.glEnableVertexAttribArray(fontProgramVertexAttrib)
        GLES20.glEnableVertexAttribArray(fontProgramTextureCoordAttrib)

        // Get uniforms for the font shader
        fontProgramTextureSampler = GLES20.glGetUniformLocation(fontProgramHandle, "uTextureSampler")
        fontProgramPaintColor = GLES20.glGetUniformLocation(fontProgramHandle, "uPaintColor")
    }

    override fun drawScene() {

        // Clear
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // Set main program and active texture
        GLES20.glUseProgram(mainProgramHandle)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(mainProgramTextureSampler, 0)

        // Load vertex array
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, backgroundVertexBufferHandle)
        GLES20.glVertexAttribPointer(mainProgramVertexAttrib,
            3, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 0)
        GLES20.glVertexAttribPointer(mainProgramTextureCoordAttrib,
            2, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES)

        // Draw background vertices
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backgroundTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)

        // Draw overlay vertices
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, overlayTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 6, 108)

        // Draw icon vertices
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iconsTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 114, 24)

        // Set font program
        GLES20.glUseProgram(fontProgramHandle)
        GLES20.glUniform1i(fontProgramTextureSampler, 0)
        GLES20.glUniform4f(fontProgramPaintColor, 0.94f, 0.89f, 0.68f, 1.0f)

        // Load vertex array
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, fontVertexBufferHandle)
        GLES20.glVertexAttribPointer(fontProgramVertexAttrib,
            3, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 0)
        GLES20.glVertexAttribPointer(fontProgramTextureCoordAttrib,
            2, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES)

        // Draw font vertices
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fontTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 72)

        // Unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }
}