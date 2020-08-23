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

    // Textures
    private var backgroundTextureHandle = 0
    private var fontTextureHandle = 0

    override fun onResourcesAvailable(
        shaders: ShaderCache,
        textures: TextureCache,
        vertexBuffers: VertexBufferCache
    ) {
        // Pre-fetch handles for textures
        backgroundTextureHandle = textures[WoodenBackgroundTexture::class.java]?.textureHandle ?: 0
        fontTextureHandle = textures[OrkneyTexture::class.java]?.textureHandle ?: 0
    }

    override fun drawScene(timeDeltaMillis: Double, stackManager: SceneStackManager) {

        // Clear
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // Get shaders and VBOs
        val mainShader = stackManager.getShader(AlphaTextureShader::class.java) ?: return
        val fontShader = stackManager.getShader(FontShader::class.java) as? FontShader ?: return
        val backgroundVbo = stackManager.getVertexBuffer(MainScreenBackgroundVertexBuffer::class.java) ?: return
        val textsVbo = stackManager.getVertexBuffer(HelpHubTextsVertexBuffer::class.java) ?: return

        // Set main program and active texture
        mainShader.activate()

        // Draw background vertices
        backgroundVbo.activate(mainShader)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backgroundTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, backgroundVbo.subBufferVertexIndices[0], backgroundVbo.verticesInSubBuffer(0))

        // Set font program
        fontShader.activate()

        // Draw first line of text in white
        textsVbo.activate(fontShader)
        fontShader.setPaintColour(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fontTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, textsVbo.subBufferVertexIndices[0], textsVbo.verticesInSubBuffer(0))

        // Draw remaining lines in the sand (colour)
        fontShader.setPaintColour(0.96f, 0.87f, 0.70f, 1.0f)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fontTextureHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, textsVbo.subBufferVertexIndices[1], textsVbo.verticesInSubBuffer(1))

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