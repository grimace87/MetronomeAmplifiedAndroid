package com.grimace.metronomeamplified.caches

import android.content.Context
import com.grimace.metronomeamplified.components.GlShader
import java.lang.RuntimeException

class ShaderCache {

    private val shaders = HashMap<Class<out GlShader>, GlShader>()

    fun requireShaders(context: Context, shaderClasses: List<Class<out GlShader>>) {
        shaderClasses.forEach { shaderClass ->
            if (!shaders.containsKey(shaderClass)) {
                val shader: GlShader = shaderClass.newInstance()
                val compileResult = shader.compileReturningError(context)
                if (compileResult != null) {
                    throw RuntimeException(compileResult)
                }
                shader.prepare()
                shaders[shaderClass] = shader
            }
        }
    }

    operator fun get(index: Class<out GlShader>): GlShader? {
        return shaders[index]
    }
}