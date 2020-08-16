package com.grimace.metronomeamplified.renderer

import android.app.Activity
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.grimace.metronomeamplified.caches.ShaderCache
import com.grimace.metronomeamplified.caches.TextureCache
import com.grimace.metronomeamplified.caches.VertexBufferCache
import com.grimace.metronomeamplified.scenes.MainScene
import com.grimace.metronomeamplified.scenes.SettingsHubScene
import com.grimace.metronomeamplified.scenes.SettingsNavigatingScene
import com.grimace.metronomeamplified.traits.GlScene
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainRenderer(activity: Activity) : GLSurfaceView.Renderer {

    private var surfaceWidth = 0
    private var surfaceHeight = 0

    private val activity = WeakReference(activity)
    private val sceneStack = Stack<GlScene>()
    private val shaderCache = ShaderCache()
    private val textureCache = TextureCache()
    private val vertexBufferCache = VertexBufferCache()

    private fun pushNewScene(scene: GlScene) {
        val context: Context = activity.get() ?: return
        shaderCache.requireShaders(context, scene.requiredShaders)
        textureCache.requireTextures(context, scene.requiredTextures)
        vertexBufferCache.requireVertexBuffers(
            scene.requiredVertexBuffers,
            context,
            surfaceWidth,
            surfaceHeight)
        scene.onResourcesAvailable(shaderCache, textureCache, vertexBufferCache)
        sceneStack.push(scene)
    }

    fun popScene() {

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

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        pushNewScene(MainScene())
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        surfaceWidth = width
        surfaceHeight = height
        vertexBufferCache.invalidateSizeDependentBuffers()
        verifyTopmostScene()
    }

    override fun onDrawFrame(gl: GL10?) {
        if (sceneStack.empty()) {
            return
        }

        val currentScene = sceneStack.peek()
        currentScene.drawScene()
    }

    fun stackSize(): Int {
        return sceneStack.size
    }

    fun onPointerDown() {
        if (sceneStack.size == 1) {
            pushNewScene(SettingsHubScene())
        } else if (sceneStack.size == 2) {
            pushNewScene(SettingsNavigatingScene())
        }
    }
}