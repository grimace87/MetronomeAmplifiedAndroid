package com.grimace.metronomeamplified.scenes

import android.opengl.GLES20
import com.grimace.metronomeamplified.caches.ShaderCache
import com.grimace.metronomeamplified.caches.TextureCache
import com.grimace.metronomeamplified.caches.VertexBufferCache
import com.grimace.metronomeamplified.components.*
import com.grimace.metronomeamplified.components.shaders.AlphaTextureShader
import com.grimace.metronomeamplified.components.shaders.FontShader
import com.grimace.metronomeamplified.components.textures.OrkneyTexture
import com.grimace.metronomeamplified.components.textures.WoodenBackgroundTexture
import com.grimace.metronomeamplified.components.vertexbuffers.MainScreenBackgroundVertexBuffer
import com.grimace.metronomeamplified.components.vertexbuffers.HelpHubTextsVertexBuffer
import com.grimace.metronomeamplified.traits.GlScene
import com.grimace.metronomeamplified.traits.SceneStackManager

class HelpHubScene : GlScene {

    override val requiredShaders: List<Class<out GlShader>>
        get() = listOf(AlphaTextureShader::class.java, FontShader::class.java)

    override val requiredTextures: List<Class<out GlTexture>>
        get() = listOf(WoodenBackgroundTexture::class.java, OrkneyTexture::class.java)

    override val requiredVertexBuffers: List<Class<out GlVertexBuffer>>
        get() = listOf(MainScreenBackgroundVertexBuffer::class.java, HelpHubTextsVertexBuffer::class.java)

    // Main shader program shader handles
    private var mainProgramHandle = 0
    private var mainProgramVertexAttrib = 0
    private var mainProgramTextureCoordAttrib = 0
    private var mainProgramTextureSampler = 0

    // Resources used with main shader
    private var backgroundTextureHandle = 0
    private var backgroundVertexBufferHandle = 0

    // Font shader program handles
    private var fontProgramHandle = 0
    private var fontProgramVertexAttrib = 0
    private var fontProgramTextureCoordAttrib = 0
    private var fontProgramTextureSampler = 0
    private var fontProgramPaintColor = 0

    // Resources used with font shader
    private var fontTextureHandle = 0
    private var textsVertexBufferHandle = 0

    override fun onResourcesAvailable(
        shaders: ShaderCache,
        textures: TextureCache,
        vertexBuffers: VertexBufferCache
    ) {
        // Pre-fetch handles for main shader and resources to be used with that shader
        mainProgramHandle = shaders[AlphaTextureShader::class.java]?.programHandle ?: 0
        backgroundTextureHandle = textures[WoodenBackgroundTexture::class.java]?.textureHandle ?: 0
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
        fontTextureHandle = textures[OrkneyTexture::class.java]?.textureHandle ?: 0
        textsVertexBufferHandle = vertexBuffers[HelpHubTextsVertexBuffer::class.java]?.vertexBufferHandle ?: 0

        // Get attributes for the font shader and make sure they're enabled
        fontProgramVertexAttrib = GLES20.glGetAttribLocation(fontProgramHandle, "aPosition")
        fontProgramTextureCoordAttrib = GLES20.glGetAttribLocation(fontProgramHandle, "aTextureCoord")
        GLES20.glEnableVertexAttribArray(fontProgramVertexAttrib)
        GLES20.glEnableVertexAttribArray(fontProgramTextureCoordAttrib)

        // Get uniforms for the font shader
        fontProgramTextureSampler = GLES20.glGetUniformLocation(fontProgramHandle, "uTextureSampler")
        fontProgramPaintColor = GLES20.glGetUniformLocation(fontProgramHandle, "uPaintColor")
    }

    override fun drawScene(timeDeltaMillis: Double) {

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

        // Set font program
        GLES20.glUseProgram(fontProgramHandle)
        GLES20.glUniform1i(fontProgramTextureSampler, 0)

        // Load vertex array
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textsVertexBufferHandle)
        GLES20.glVertexAttribPointer(fontProgramVertexAttrib,
            3, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 0)
        GLES20.glVertexAttribPointer(fontProgramTextureCoordAttrib,
            2, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES)

        // Draw first line of text in white
        GLES20.glUniform4f(fontProgramPaintColor, 1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fontTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 78)

        // Draw remaining lines in the sand (colour)
        GLES20.glUniform4f(fontProgramPaintColor, 0.96f, 0.87f, 0.70f, 1.0f)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fontTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 78, 486)

        // Unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    override fun onPointerDown(normalisedX: Float, normalisedY: Float, stackManager: SceneStackManager) {
        val vboRegion = stackManager.checkForPointOfInterest(HelpHubTextsVertexBuffer::class.java, normalisedX, normalisedY)
        when (vboRegion) {
            0 -> stackManager.pushScene(HelpNavigatingScene())
            else -> {}
        }
    }
}