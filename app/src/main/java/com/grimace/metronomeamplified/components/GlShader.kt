package com.grimace.metronomeamplified.components

import android.content.Context
import android.opengl.GLES20
import com.grimace.metronomeamplified.extensions.openAsString
import java.io.IOException

abstract class GlShader(
    private val vertexShaderAssetName: String,
    private val fragmentShaderAssetName: String) {

    var programHandle: Int = 0
        private set

    private var vertexShaderHandle: Int = 0
    private var fragmentShaderHandle: Int = 0

    fun compileReturningError(context: Context): String? {

        // Read shader assets into Strings
        val assetManager = context.assets
        val vertexSource: String
        val fragmentSource: String
        try {
            vertexSource = assetManager.openAsString(vertexShaderAssetName)
            fragmentSource = assetManager.openAsString(fragmentShaderAssetName)
        } catch (e: IOException) {
            return e.message
        }

        // Compile program from the sources
        try {
            vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource)
            fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
            programHandle = linkProgram(vertexShaderHandle, fragmentShaderHandle)
        } catch (e: IllegalArgumentException) {
            return e.message
        }

        // Return null indicating success (no error)
        return null
    }

    private fun compileShader(shaderType: Int, source: String): Int {
        val shader = GLES20.glCreateShader(shaderType)
        val status = intArrayOf(0)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            val log = GLES20.glGetShaderInfoLog(shader)
            throw IllegalArgumentException(log)
        }
        return shader
    }

    private fun linkProgram(vertexShader: Int, fragmentShader: Int): Int {
        val program = GLES20.glCreateProgram()
        val status = intArrayOf(0)
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            val log = GLES20.glGetProgramInfoLog(program)
            throw IllegalArgumentException(log)
        }
        return program
    }
}
