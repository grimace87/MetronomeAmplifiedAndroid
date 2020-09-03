package com.grimace.metronomeamplified.scenes

import android.opengl.GLES20
import com.grimace.metronomeamplified.caches.FramebufferCache
import com.grimace.metronomeamplified.caches.ShaderCache
import com.grimace.metronomeamplified.caches.TextureCache
import com.grimace.metronomeamplified.caches.VertexBufferCache
import com.grimace.metronomeamplified.components.*
import com.grimace.metronomeamplified.components.framebuffers.MainOffscreenFramebuffer
import com.grimace.metronomeamplified.components.shaders.FontShader
import com.grimace.metronomeamplified.components.shaders.SlimeTransitionShader
import com.grimace.metronomeamplified.components.textures.BubbleNormalMapTexture
import com.grimace.metronomeamplified.components.textures.OrkneyTexture
import com.grimace.metronomeamplified.components.textures.WoodenBackgroundTexture
import com.grimace.metronomeamplified.components.vertexbuffers.BackgroundVertexBuffer
import com.grimace.metronomeamplified.components.vertexbuffers.HelpHubTextsVertexBuffer
import com.grimace.metronomeamplified.traits.GlScene
import com.grimace.metronomeamplified.traits.SceneStackManager

private const val SCREEN_TRANSITION_DURATION_SECONDS = 0.5

class TransitionScene : GlScene {

    override val requiredShaders: List<Class<out GlShader>>
        get() = listOf(SlimeTransitionShader::class.java)

    override val requiredTextures: List<Class<out GlTexture>>
        get() = listOf(BubbleNormalMapTexture::class.java)

    override val requiredVertexBuffers: List<Class<out GlVertexBuffer>>
        get() = listOf(BackgroundVertexBuffer::class.java)

    override val requiredFramebuffers: List<Class<out GlFramebuffer>>
        get() = listOf(MainOffscreenFramebuffer::class.java)

    private lateinit var slimeShader: SlimeTransitionShader
    private lateinit var normalMapTexture: GlTexture
    private lateinit var backgroundVbo: GlVertexBuffer
    private lateinit var offscreenFramebuffer: GlFramebuffer

    var mIsTransitioning: Boolean = false
        private set

    private var mTransitionProgress = 0.0f

    override fun onResourcesAvailable(
        shaders: ShaderCache,
        textures: TextureCache,
        vertexBuffers: VertexBufferCache,
        framebuffers: FramebufferCache
    ) {
        slimeShader = shaders[SlimeTransitionShader::class.java]!! as SlimeTransitionShader
        normalMapTexture = textures[BubbleNormalMapTexture::class.java]!!
        backgroundVbo = vertexBuffers[BackgroundVertexBuffer::class.java]!!
        offscreenFramebuffer = framebuffers[MainOffscreenFramebuffer::class.java]!!
    }

    /**
     * Will draw on top of another render - do not clear the framebuffer
     */
    override fun drawScene(timeDeltaMillis: Double, stackManager: SceneStackManager) {

        // Handle progress
        mTransitionProgress += (timeDeltaMillis * 0.001 / SCREEN_TRANSITION_DURATION_SECONDS).toFloat()
        if (mTransitionProgress > 1.0f) {
            mIsTransitioning = false
            mTransitionProgress = 1.0f
        }

        // Set main program and active textures
        slimeShader.activate()
        slimeShader.setProgress(mTransitionProgress)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, offscreenFramebuffer.mColorTexture)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, normalMapTexture.textureHandle)

        // Draw vertices
        backgroundVbo.activate(slimeShader)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, backgroundVbo.subBufferVertexIndices[0], backgroundVbo.verticesInSubBuffer(0))

        // Unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    override fun onPointerDown(normalisedX: Float, normalisedY: Float, stackManager: SceneStackManager) {}

    fun startTransitionWith(scene: GlScene, stackManager: SceneStackManager) {
        offscreenFramebuffer.activate()
        scene.drawScene(timeDeltaMillis = 0.0, stackManager = stackManager)
        offscreenFramebuffer.deactivate()
        mIsTransitioning = true
        mTransitionProgress = 0.0f
    }
}