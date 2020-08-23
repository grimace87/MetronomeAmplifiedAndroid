package com.grimace.metronomeamplified.scenes

import android.opengl.GLES20
import android.opengl.Matrix
import com.grimace.metronomeamplified.caches.ShaderCache
import com.grimace.metronomeamplified.caches.TextureCache
import com.grimace.metronomeamplified.caches.VertexBufferCache
import com.grimace.metronomeamplified.components.FLOAT_SIZE_BYTES
import com.grimace.metronomeamplified.components.GlShader
import com.grimace.metronomeamplified.components.GlTexture
import com.grimace.metronomeamplified.components.GlVertexBuffer
import com.grimace.metronomeamplified.components.shaders.AlphaTextureTransformShader
import com.grimace.metronomeamplified.components.shaders.FontTransformShader
import com.grimace.metronomeamplified.components.textures.*
import com.grimace.metronomeamplified.components.vertexbuffers.*
import com.grimace.metronomeamplified.traits.GlScene
import com.grimace.metronomeamplified.traits.SceneStackManager
import kotlin.math.max

class HelpNavigatingScene : GlScene {

    override val requiredShaders: List<Class<out GlShader>>
        get() = listOf(AlphaTextureTransformShader::class.java, FontTransformShader::class.java)

    override val requiredTextures: List<Class<out GlTexture>>
        get() = listOf(WoodenBackgroundTexture::class.java, OrkneyTexture::class.java, WhiteTranslucentShapesTexture::class.java, IconsTexture::class.java, SampleScreenTexture::class.java)

    override val requiredVertexBuffers: List<Class<out GlVertexBuffer>>
        get() = listOf(MainScreenBackgroundVertexBuffer::class.java, HelpDetailsOverlayVertexBuffer::class.java, HelpDetailsIconsVertexBuffer::class.java, HelpNavigatingTextsVertexBuffer::class.java, HelpNavigatingImagesVertexBuffer::class.java)

    // Animation state
    private var isAnimating = false
    private var focusCard = 0
    private var animateToTheRight = true
    private var animationProgress = 0.0f
    private val identityMatrix = FloatArray(16).apply { Matrix.setIdentityM(this, 0) }
    private val transformMatrix = FloatArray(16)

    // Main shader program shader handles
    private var mainTransformProgramHandle = 0
    private var mainProgramVertexAttrib = 0
    private var mainProgramTextureCoordAttrib = 0
    private var mainProgramTextureSampler = 0
    private var mainProgramTransformUniform = 0

    // Resources used with main shader
    private var backgroundTextureHandle = 0
    private var backgroundVertexBufferHandle = 0
    private var overlayTextureHandle = 0
    private var overlayVertexBufferHandle = 0
    private var iconsTextureHandle = 0
    private var iconsVertexBufferHandle = 0
    private var sampleImageTextureHandle = 0
    private var sampleImageVertexBufferHandle = 0

    // Font shader program handles
    private var fontTransformProgramHandle = 0
    private var fontProgramVertexAttrib = 0
    private var fontProgramTextureCoordAttrib = 0
    private var fontProgramTextureSampler = 0
    private var fontProgramPaintColor = 0
    private var fontProgramTransformUniform = 0

    // Resources used with font shader
    private var fontTextureHandle = 0
    private var textsVertexBufferHandle = 0

    override fun onResourcesAvailable(
        shaders: ShaderCache,
        textures: TextureCache,
        vertexBuffers: VertexBufferCache
    ) {
        // Pre-fetch handles for main shader and resources to be used with that shader
        mainTransformProgramHandle = shaders[AlphaTextureTransformShader::class.java]?.programHandle ?: 0
        backgroundTextureHandle = textures[WoodenBackgroundTexture::class.java]?.textureHandle ?: 0
        backgroundVertexBufferHandle = vertexBuffers[MainScreenBackgroundVertexBuffer::class.java]?.vertexBufferHandle ?: 0
        overlayTextureHandle = textures[WhiteTranslucentShapesTexture::class.java]?.textureHandle ?: 0
        overlayVertexBufferHandle = vertexBuffers[HelpDetailsOverlayVertexBuffer::class.java]?.vertexBufferHandle ?: 0
        iconsTextureHandle = textures[IconsTexture::class.java]?.textureHandle ?: 0
        iconsVertexBufferHandle = vertexBuffers[HelpDetailsIconsVertexBuffer::class.java]?.vertexBufferHandle ?: 0
        sampleImageTextureHandle = textures[SampleScreenTexture::class.java]?.textureHandle ?: 0
        sampleImageVertexBufferHandle = vertexBuffers[HelpNavigatingImagesVertexBuffer::class.java]?.vertexBufferHandle ?: 0

        // Get attributes for the main shader and make sure they're enabled
        mainProgramVertexAttrib = GLES20.glGetAttribLocation(mainTransformProgramHandle, "aPosition")
        mainProgramTextureCoordAttrib = GLES20.glGetAttribLocation(mainTransformProgramHandle, "aTextureCoord")
        GLES20.glEnableVertexAttribArray(mainProgramVertexAttrib)
        GLES20.glEnableVertexAttribArray(mainProgramTextureCoordAttrib)

        // Get uniforms for the main shader
        mainProgramTextureSampler = GLES20.glGetUniformLocation(mainTransformProgramHandle, "uTextureSampler")
        mainProgramTransformUniform = GLES20.glGetUniformLocation(mainTransformProgramHandle, "uTransform")

        // Pre-fetch handles for font shader and resources to be used with that shader
        fontTransformProgramHandle = shaders[FontTransformShader::class.java]?.programHandle ?: 0
        fontTextureHandle = textures[OrkneyTexture::class.java]?.textureHandle ?: 0
        textsVertexBufferHandle = vertexBuffers[HelpNavigatingTextsVertexBuffer::class.java]?.vertexBufferHandle ?: 0

        // Get attributes for the font shader and make sure they're enabled
        fontProgramVertexAttrib = GLES20.glGetAttribLocation(fontTransformProgramHandle, "aPosition")
        fontProgramTextureCoordAttrib = GLES20.glGetAttribLocation(fontTransformProgramHandle, "aTextureCoord")
        GLES20.glEnableVertexAttribArray(fontProgramVertexAttrib)
        GLES20.glEnableVertexAttribArray(fontProgramTextureCoordAttrib)

        // Get uniforms for the font shader
        fontProgramTextureSampler = GLES20.glGetUniformLocation(fontTransformProgramHandle, "uTextureSampler")
        fontProgramPaintColor = GLES20.glGetUniformLocation(fontTransformProgramHandle, "uPaintColor")
        fontProgramTransformUniform = GLES20.glGetUniformLocation(fontTransformProgramHandle, "uTransform")
    }

    override fun drawScene(timeDeltaMillis: Double, stackManager: SceneStackManager) {

        // Clear
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // Get VBOs
        val backgroundVbo: GlVertexBuffer = stackManager.getVertexBuffer(MainScreenBackgroundVertexBuffer::class.java) ?: return
        val iconsVbo: GlVertexBuffer = stackManager.getVertexBuffer(HelpDetailsIconsVertexBuffer::class.java) ?: return
        val overlayVbo: GlVertexBuffer = stackManager.getVertexBuffer(HelpDetailsOverlayVertexBuffer::class.java) ?: return
        val imagesVbo: GlVertexBuffer = stackManager.getVertexBuffer(HelpNavigatingImagesVertexBuffer::class.java) ?: return
        val textsVbo: GlVertexBuffer = stackManager.getVertexBuffer(HelpNavigatingTextsVertexBuffer::class.java) ?: return

        // Update matrices
        updateMatrices(timeDeltaMillis)

        // Set main program and active texture
        GLES20.glUseProgram(mainTransformProgramHandle)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(mainProgramTextureSampler, 0)
        GLES20.glUniformMatrix4fv(mainProgramTransformUniform, 1, false, identityMatrix, 0)

        // Load vertex array
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, backgroundVertexBufferHandle)
        GLES20.glVertexAttribPointer(mainProgramVertexAttrib,
            3, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 0)
        GLES20.glVertexAttribPointer(mainProgramTextureCoordAttrib,
            2, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES
        )

        // Draw background vertices
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backgroundTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, backgroundVbo.subBufferVertexIndices[0], backgroundVbo.verticesInSubBuffer(0))

        // Transform the overlay and image slide
        GLES20.glUniformMatrix4fv(mainProgramTransformUniform, 1, false, transformMatrix, 0)

        // Load vertex array
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, overlayVertexBufferHandle)
        GLES20.glVertexAttribPointer(mainProgramVertexAttrib,
            3, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 0)
        GLES20.glVertexAttribPointer(mainProgramTextureCoordAttrib,
            2, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES
        )

        // Draw background vertices
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, overlayTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, overlayVbo.subBufferVertexIndices[0], overlayVbo.verticesInSubBuffer(0))

        // Load vertex array
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, sampleImageVertexBufferHandle)
        GLES20.glVertexAttribPointer(mainProgramVertexAttrib,
            3, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 0)
        GLES20.glVertexAttribPointer(mainProgramTextureCoordAttrib,
            2, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES
        )

        // Draw background vertices
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sampleImageTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, imagesVbo.subBufferVertexIndices[0], imagesVbo.verticesInSubBuffer(0))

        // Don't transform the icons
        GLES20.glUniformMatrix4fv(mainProgramTransformUniform, 1, false, identityMatrix, 0)

        // Load vertex array
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, iconsVertexBufferHandle)
        GLES20.glVertexAttribPointer(mainProgramVertexAttrib,
            3, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 0)
        GLES20.glVertexAttribPointer(mainProgramTextureCoordAttrib,
            2, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES
        )

        // Draw background vertices
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iconsTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, iconsVbo.subBufferVertexIndices[0], iconsVbo.verticesInSubBuffer(0))

        // Set font program
        GLES20.glUseProgram(fontTransformProgramHandle)
        GLES20.glUniform1i(fontProgramTextureSampler, 0)
        GLES20.glUniformMatrix4fv(fontProgramTransformUniform, 1, false, identityMatrix, 0)

        // Load vertex array
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textsVertexBufferHandle)
        GLES20.glVertexAttribPointer(fontProgramVertexAttrib,
            3, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 0)
        GLES20.glVertexAttribPointer(fontProgramTextureCoordAttrib,
            2, GLES20.GL_FLOAT, false,
            5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES
        )

        // Draw heading in white
        GLES20.glUniform4f(fontProgramPaintColor, 1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fontTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, textsVbo.subBufferVertexIndices[0], textsVbo.verticesInSubBuffer(0))

        // Transform the context text
        GLES20.glUniformMatrix4fv(fontProgramTransformUniform, 1, false, transformMatrix, 0)

        // Draw text body in black
        GLES20.glUniform4f(fontProgramPaintColor, 0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fontTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, textsVbo.subBufferVertexIndices[1], textsVbo.verticesInSubBuffer(1))

        // Unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    private fun moveToNext() {
        if (isAnimating || focusCard > 2) {
            return
        }
        focusCard++
        isAnimating = true
        animateToTheRight = true
        animationProgress = 0.0f
    }

    private fun moveToPrevious() {
        if (isAnimating || focusCard == 0) {
            return
        }
        focusCard--
        isAnimating = true
        animateToTheRight = false
        animationProgress = 0.0f
    }

    private fun updateMatrices(timeDeltaMillis: Double) {
        Matrix.setIdentityM(transformMatrix, 0)
        if (isAnimating) {
            animationProgress += (timeDeltaMillis / 2000.0).toFloat()
            if (animationProgress >= 1.0f) {
                animationProgress = 1.0f
                isAnimating = false
            }
            val scale = max(animationProgress, 1.0f - animationProgress)
            Matrix.scaleM(transformMatrix, 0, scale, scale, 1.0f)
        }
    }

    override fun onPointerDown(normalisedX: Float, normalisedY: Float, stackManager: SceneStackManager) {
        val vboRegion = stackManager.checkForPointOfInterest(HelpDetailsIconsVertexBuffer::class.java, normalisedX, normalisedY)
        when (vboRegion) {
            0 -> moveToPrevious()
            1 -> moveToNext()
            else -> {}
        }
    }
}