package com.grimace.metronomeamplified.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.grimace.metronomeamplified.MainActivity
import com.grimace.metronomeamplified.caches.FramebufferCache
import com.grimace.metronomeamplified.caches.ShaderCache
import com.grimace.metronomeamplified.caches.TextureCache
import com.grimace.metronomeamplified.caches.VertexBufferCache
import com.grimace.metronomeamplified.components.GlFramebuffer
import com.grimace.metronomeamplified.components.GlShader
import com.grimace.metronomeamplified.components.GlTexture
import com.grimace.metronomeamplified.components.GlVertexBuffer
import com.grimace.metronomeamplified.scenes.MainScene
import com.grimace.metronomeamplified.scenes.TransitionScene
import com.grimace.metronomeamplified.traits.AudioInterface
import com.grimace.metronomeamplified.traits.GlScene
import com.grimace.metronomeamplified.traits.SceneStackManager
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainRenderer(activity: MainActivity) : GLSurfaceView.Renderer, SceneStackManager {

    private var surfaceWidth = 0
    private var surfaceHeight = 0
    private var lastTimeUpdate = 0L

    private val activity = WeakReference(activity)
    private val sceneStack = Stack<GlScene>()
    private val shaderCache = ShaderCache()
    private val textureCache = TextureCache()
    private val vertexBufferCache = VertexBufferCache()
    private val framebufferCache = FramebufferCache()

    private val mTransitionScene = TransitionScene()

    override fun pushScene(scene: GlScene) {
        beginTransition()
        lastTimeUpdate = System.currentTimeMillis()
        requireSceneResources(scene)
        sceneStack.push(scene)
    }

    override fun popTopScene() {

        // Check for popping from empty stack
        if (sceneStack.empty()) {
            throw RuntimeException("Attempting to pop scene off empty stack")
        }

        // Remove and discard top-most scene, finish now if nothing remains
        sceneStack.pop()
        if (sceneStack.isEmpty()) {
            activity.get()?.finish()
            return
        }

        // Regenerate any invalidated vertex buffers that are now needed
        verifyTopmostScene()
    }

    private fun verifyTopmostScene() {
        val context: Context = activity.get() ?: return
        if (sceneStack.isNotEmpty()) {
            val topScene = sceneStack.peek()
            vertexBufferCache.regenerateVertexBuffers(
                topScene.requiredVertexBuffers,
                context,
                surfaceWidth,
                surfaceHeight)
        }
    }

    private fun requireSceneResources(scene: GlScene) {
        val context: Context = activity.get() ?: return
        shaderCache.requireShaders(context, scene.requiredShaders)
        textureCache.requireTextures(context, scene.requiredTextures)
        vertexBufferCache.requireVertexBuffers(
            scene.requiredVertexBuffers,
            context,
            surfaceWidth,
            surfaceHeight)
        framebufferCache.requireFramebuffers(
            scene.requiredFramebuffers,
            surfaceWidth,
            surfaceHeight)
        scene.onResourcesAvailable(shaderCache, textureCache, vertexBufferCache, framebufferCache)
    }

    override fun getAudioController(): AudioInterface? {
        return activity.get()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        pushScene(MainScene())
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        surfaceWidth = width
        surfaceHeight = height

        vertexBufferCache.invalidateSizeDependentBuffers()
        verifyTopmostScene()

        framebufferCache.clearFramebuffers()
        requireSceneResources(mTransitionScene)
    }

    override fun onDrawFrame(gl: GL10?) {

        if (sceneStack.empty()) {
            return
        }

        val currentTime = System.currentTimeMillis()
        val timeDelta = currentTime - lastTimeUpdate
        lastTimeUpdate = currentTime
        val currentScene = sceneStack.peek()
        currentScene.drawScene(timeDeltaMillis = timeDelta.toDouble(), stackManager = this)

        if (mTransitionScene.mIsTransitioning) {
            mTransitionScene.drawScene(timeDeltaMillis = timeDelta.toDouble(), stackManager = this)
        }
    }

    fun stackSize(): Int {
        return sceneStack.size
    }

    override fun <T : GlVertexBuffer> checkForPointOfInterest(vboClass: Class<T>, normalisedX: Float, normalisedY: Float): Int {
        val vbo = vertexBufferCache[vboClass] ?: return -1
        return vbo.regionOfInterestAt(normalisedX, normalisedY)
    }

    override fun <T : GlVertexBuffer> getVertexBuffer(vboClass: Class<T>): GlVertexBuffer? {
        return vertexBufferCache[vboClass]
    }

    override fun <T : GlShader> getShader(shaderClass: Class<T>): GlShader? {
        return shaderCache[shaderClass]
    }

    override fun <T : GlTexture> getTexture(textureClass: Class<T>): GlTexture? {
        return textureCache[textureClass]
    }

    override fun <T : GlFramebuffer> getFramebuffer(framebufferClass: Class<T>): GlFramebuffer? {
        return framebufferCache[framebufferClass]
    }

    fun onPointerDown(x: Float, y: Float) {
        if (surfaceWidth == 0 || surfaceHeight == 0) {
            throw RuntimeException("Cannot process touch events before surface size is set")
        }

        val normalisedX = 2.0f * x / surfaceWidth.toFloat() - 1.0f
        val normalisedY = -2.0f * y / surfaceHeight.toFloat() + 1.0f
        if (sceneStack.isNotEmpty()) {
            val topScene = sceneStack.peek()
            topScene.onPointerDown(normalisedX, normalisedY, this)
        }
    }

    private fun beginTransition() {
        if (sceneStack.isEmpty()) {
            return
        }
        val scene = sceneStack.peek()
        mTransitionScene.startTransitionWith(scene = scene, stackManager = this)
    }
}