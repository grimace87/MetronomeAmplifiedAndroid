package com.grimace.metronomeamplified.scenes

import android.opengl.GLES20
import android.opengl.Matrix
import com.grimace.metronomeamplified.caches.FramebufferCache
import com.grimace.metronomeamplified.caches.ShaderCache
import com.grimace.metronomeamplified.caches.TextureCache
import com.grimace.metronomeamplified.caches.VertexBufferCache
import com.grimace.metronomeamplified.components.GlFramebuffer
import com.grimace.metronomeamplified.components.GlShader
import com.grimace.metronomeamplified.components.GlTexture
import com.grimace.metronomeamplified.components.GlVertexBuffer
import com.grimace.metronomeamplified.components.shaders.AlphaTextureTransformShader
import com.grimace.metronomeamplified.components.shaders.FontTransformShader
import com.grimace.metronomeamplified.components.textures.*
import com.grimace.metronomeamplified.components.vertexbuffers.*
import com.grimace.metronomeamplified.traits.GlScene
import com.grimace.metronomeamplified.traits.SceneStackManager

class HelpNavigatingScene : GlScene {

    override val requiredShaders: List<Class<out GlShader>>
        get() = listOf(AlphaTextureTransformShader::class.java, FontTransformShader::class.java)

    override val requiredTextures: List<Class<out GlTexture>>
        get() = listOf(WoodenBackgroundTexture::class.java, OrkneyTexture::class.java, WhiteTranslucentShapesTexture::class.java, IconsTexture::class.java, SampleScreenTexture::class.java)

    override val requiredVertexBuffers: List<Class<out GlVertexBuffer>>
        get() = listOf(BackgroundVertexBuffer::class.java, HelpDetailsOverlayVertexBuffer::class.java, HelpDetailsIconsVertexBuffer::class.java, HelpNavigatingTextsVertexBuffer::class.java, HelpNavigatingImagesVertexBuffer::class.java)

    override val requiredFramebuffers: List<Class<out GlFramebuffer>>
        get() = listOf()

    private lateinit var mainShader: AlphaTextureTransformShader
    private lateinit var fontShader: FontTransformShader
    private lateinit var backgroundTexture: GlTexture
    private lateinit var overlayTexture: GlTexture
    private lateinit var iconsTexture: GlTexture
    private lateinit var imagesTexture: GlTexture
    private lateinit var fontTexture: GlTexture
    private lateinit var backgroundVbo: GlVertexBuffer
    private lateinit var iconsVbo: GlVertexBuffer
    private lateinit var overlayVbo: GlVertexBuffer
    private lateinit var imagesVbo: GlVertexBuffer
    private lateinit var textsVbo: GlVertexBuffer

    // Animation state
    private var isAnimating = false
    private var focusCard = 0
    private var animateToTheRight = true
    private var animationProgress = 0.0f
    private val identityMatrix = FloatArray(16).apply { Matrix.setIdentityM(this, 0) }
    private val transformLeftMatrix = FloatArray(16)
    private val transformRightMatrix = FloatArray(16)

    override fun onResourcesAvailable(
        shaders: ShaderCache,
        textures: TextureCache,
        vertexBuffers: VertexBufferCache,
        framebuffers: FramebufferCache
    ) {
        mainShader = shaders[AlphaTextureTransformShader::class.java] as AlphaTextureTransformShader
        fontShader = shaders[FontTransformShader::class.java]!! as FontTransformShader
        backgroundTexture = textures[WoodenBackgroundTexture::class.java]!!
        overlayTexture = textures[WhiteTranslucentShapesTexture::class.java]!!
        iconsTexture = textures[IconsTexture::class.java]!!
        imagesTexture = textures[SampleScreenTexture::class.java]!!
        fontTexture = textures[OrkneyTexture::class.java]!!
        backgroundVbo = vertexBuffers[BackgroundVertexBuffer::class.java]!!
        iconsVbo = vertexBuffers[HelpDetailsIconsVertexBuffer::class.java]!!
        overlayVbo = vertexBuffers[HelpDetailsOverlayVertexBuffer::class.java]!!
        imagesVbo = vertexBuffers[HelpNavigatingImagesVertexBuffer::class.java]!!
        textsVbo = vertexBuffers[HelpNavigatingTextsVertexBuffer::class.java]!!
    }

    override fun drawScene(timeDeltaMillis: Double, stackManager: SceneStackManager) {

        // Clear
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // Update matrices
        updateMatrices(timeDeltaMillis)

        // Set main program and active texture
        mainShader.activate()
        mainShader.setTransformationMatrix(identityMatrix, 0)

        // Draw background vertices
        backgroundVbo.activate(mainShader)
        backgroundTexture.activate()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, backgroundVbo.subBufferVertexIndices[0], backgroundVbo.verticesInSubBuffer(0))

        // Draw the overlay, either for one motionless slide or for each animating slide
        if (!isAnimating) {

            // Overlay
            overlayVbo.activate(mainShader)
            overlayTexture.activate()
            mainShader.setTransformationMatrix(transformLeftMatrix, 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, overlayVbo.subBufferVertexIndices[0], overlayVbo.verticesInSubBuffer(0))

            // Image
            imagesVbo.activate(mainShader)
            imagesTexture.activate()
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, imagesVbo.subBufferVertexIndices[0], imagesVbo.verticesInSubBuffer(0))
        } else {

            // Overlay
            overlayVbo.activate(mainShader)
            overlayTexture.activate()
            mainShader.setTransformationMatrix(transformLeftMatrix, 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, overlayVbo.subBufferVertexIndices[0], overlayVbo.verticesInSubBuffer(0))
            mainShader.setTransformationMatrix(transformRightMatrix, 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, overlayVbo.subBufferVertexIndices[0], overlayVbo.verticesInSubBuffer(0))

            // Image
            imagesVbo.activate(mainShader)
            imagesTexture.activate()
            mainShader.setTransformationMatrix(transformLeftMatrix, 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, imagesVbo.subBufferVertexIndices[0], imagesVbo.verticesInSubBuffer(0))
            mainShader.setTransformationMatrix(transformRightMatrix, 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, imagesVbo.subBufferVertexIndices[0], imagesVbo.verticesInSubBuffer(0))
        }

        // Draw icons, no transform applied
        mainShader.setTransformationMatrix(identityMatrix, 0)
        iconsVbo.activate(mainShader)
        iconsTexture.activate()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, iconsVbo.subBufferVertexIndices[0], iconsVbo.verticesInSubBuffer(0))

        // Set font program
        fontShader.activate()
        fontShader.setTransformationMatrix(identityMatrix, 0)

        // Draw heading in white
        textsVbo.activate(fontShader)
        fontShader.setPaintColour(1.0f, 1.0f, 1.0f, 1.0f)
        fontTexture.activate()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, textsVbo.subBufferVertexIndices[0], textsVbo.verticesInSubBuffer(0))

        // Transform the context text
        fontShader.setPaintColour(0.0f, 0.0f, 0.0f, 1.0f)
        if (!isAnimating) {
            fontShader.setTransformationMatrix(transformLeftMatrix, 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, textsVbo.subBufferVertexIndices[focusCard + 1], textsVbo.verticesInSubBuffer(focusCard + 1))
        } else {
            val leftSlide = when (animateToTheRight) {
                true -> focusCard
                false -> focusCard - 1
            }
            fontShader.setTransformationMatrix(transformLeftMatrix, 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, textsVbo.subBufferVertexIndices[leftSlide + 1], textsVbo.verticesInSubBuffer(leftSlide + 1))
            fontShader.setTransformationMatrix(transformRightMatrix, 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, textsVbo.subBufferVertexIndices[leftSlide + 2], textsVbo.verticesInSubBuffer(leftSlide + 2))
        }

        // Unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    private fun moveToNext() {
        if (isAnimating || focusCard >= 7) {
            return
        }
        focusCard++
        isAnimating = true
        animateToTheRight = false
        animationProgress = 0.0f
    }

    private fun moveToPrevious() {
        if (isAnimating || focusCard == 0) {
            return
        }
        focusCard--
        isAnimating = true
        animateToTheRight = true
        animationProgress = 0.0f
    }

    private fun updateMatrices(timeDeltaMillis: Double) {
        val animationDuration = 0.3f
        Matrix.setIdentityM(transformLeftMatrix, 0)
        if (isAnimating) {
            Matrix.setIdentityM(transformRightMatrix, 0)
            animationProgress += (0.001 * timeDeltaMillis / animationDuration).toFloat()
            if (animationProgress >= 1.0f) {
                animationProgress = 1.0f
                isAnimating = false
                return
            }
            val scaleOut = 0.66666667f + animationDuration / (9.0f * animationProgress + 3.0f * animationDuration)
            val scaleIn = 0.66666667f + animationDuration / (9.0f * (1.0f - animationProgress) + 3.0f * animationDuration)

            if (animateToTheRight) {
                Matrix.scaleM(transformLeftMatrix, 0, scaleIn, scaleIn, 1.0f)
                Matrix.scaleM(transformRightMatrix, 0, scaleOut, scaleOut, 1.0f)
                Matrix.translateM(transformLeftMatrix, 0, 2.0f * (-1.0f + animationProgress) / scaleIn, 0.0f, 0.0f)
                Matrix.translateM(transformRightMatrix, 0, 2.0f * animationProgress / scaleOut, 0.0f, 0.0f)
            } else {
                Matrix.scaleM(transformLeftMatrix, 0, scaleOut, scaleOut, 1.0f)
                Matrix.scaleM(transformRightMatrix, 0, scaleIn, scaleIn, 1.0f)
                Matrix.translateM(transformLeftMatrix, 0, -2.0f * animationProgress / scaleOut, 0.0f, 0.0f)
                Matrix.translateM(transformRightMatrix, 0, 2.0f * (1.0f - animationProgress) / scaleIn, 0.0f, 0.0f)
            }
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