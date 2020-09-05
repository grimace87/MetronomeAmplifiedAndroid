package com.grimace.metronomeamplified

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.grimace.metronomeamplified.state.AppState
import com.grimace.metronomeamplified.state.Song
import com.grimace.metronomeamplified.traits.AudioInterface
import com.grimace.metronomeamplified.view.MainSurfaceView
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity(), AudioInterface {

    companion object {
        init {
            System.loadLibrary("nativeaudio")
        }
    }

    private external fun nativeInitialiseAudio(assetManager: AssetManager)
    private external fun nativeReleaseAudio()
    private external fun nativeStartAudio()
    private external fun nativeStopAudio()
    private external fun nativeSetSong(data: ByteBuffer)

    private lateinit var surfaceView: MainSurfaceView
    private var mAppState = AppState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surfaceView = MainSurfaceView(this)
        setContentView(surfaceView)

        val newSong = Song.newDefault()
        mAppState.loadSong(newSong)
        nativeInitialiseAudio(assets)
        nativeSetSong(newSong.asByteBuffer())
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeReleaseAudio()
    }

    override fun onBackPressed() {
        if (!surfaceView.tryHandleDeviceBack()) {
            super.onBackPressed()
        }
    }

    override fun startAudio() {
        nativeStartAudio()
    }

    override fun stopAudio() {
        nativeStopAudio()
    }
}
