package com.grimace.metronomeamplified.scenes

import android.opengl.GLES20
import android.opengl.Matrix
import com.grimace.metronomeamplified.caches.ShaderCache
import com.grimace.metronomeamplified.caches.TextureCache
import com.grimace.metronomeamplified.caches.VertexBufferCache
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

    override fun onResourcesAvailable(
        shaders: ShaderCache,
        textures: TextureCache,
        vertexBuffers: VertexBufferCache
    ) {

    }

    override fun drawScene(timeDeltaMillis: Double, stackManager: SceneStackManager) {

        // Clear
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // Get shaders and VBOs
        val mainShader = stackManager.getShader(AlphaTextureTransformShader::class.java) as? AlphaTextureTransformShader ?: return
        val fontShader = stackManager.getShader(FontTransformShader::class.java) as? FontTransformShader ?: return
        val backgroundTexture = stackManager.getTexture(WoodenBackgroundTexture::class.java) ?: return
        val overlayTexture = stackManager.getTexture(WhiteTranslucentShapesTexture::class.java) ?: return
        val iconsTexture = stackManager.getTexture(IconsTexture::class.java) ?: return
        val imagesTexture = stackManager.getTexture(SampleScreenTexture::class.java) ?: return
        val fontTexture = stackManager.getTexture(OrkneyTexture::class.java) ?: return
        val backgroundVbo = stackManager.getVertexBuffer(MainScreenBackgroundVertexBuffer::class.java) ?: return
        val iconsVbo = stackManager.getVertexBuffer(HelpDetailsIconsVertexBuffer::class.java) ?: return
        val overlayVbo = stackManager.getVertexBuffer(HelpDetailsOverlayVertexBuffer::class.java) ?: return
        val imagesVbo = stackManager.getVertexBuffer(HelpNavigatingImagesVertexBuffer::class.java) ?: return
        val textsVbo = stackManager.getVertexBuffer(HelpNavigatingTextsVertexBuffer::class.java) ?: return

        // Update matrices
        updateMatrices(timeDeltaMillis)

        // Set main program and active texture
        mainShader.activate()
        mainShader.setTransformationMatrix(identityMatrix, 0)

        // Draw background vertices
        backgroundVbo.activate(mainShader)
        backgroundTexture.activate()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, backgroundVbo.subBufferVertexIndices[0], backgroundVbo.verticesInSubBuffer(0))

        // Transform the overlay and image slide
        mainShader.setTransformationMatrix(transformMatrix, 0)

        // Draw background vertices
        overlayVbo.activate(mainShader)
        overlayTexture.activate()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, overlayVbo.subBufferVertexIndices[0], overlayVbo.verticesInSubBuffer(0))

        // Draw background vertices
        imagesVbo.activate(mainShader)
        imagesTexture.activate()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, imagesVbo.subBufferVertexIndices[0], imagesVbo.verticesInSubBuffer(0))

        // Don't transform the icons
        mainShader.setTransformationMatrix(identityMatrix, 0)

        // Draw background vertices
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
        fontShader.setTransformationMatrix(transformMatrix, 0)

        // Draw text body in black
        fontShader.setPaintColour(0.0f, 0.0f, 0.0f, 1.0f)
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