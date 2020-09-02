package com.grimace.metronomeamplified

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.grimace.metronomeamplified.traits.AudioInterface
import com.grimace.metronomeamplified.view.MainSurfaceView

class MainActivity : AppCompatActivity(), AudioInterface {

    companion object {
        init {
            System.loadLibrary("nativeaudio")
        }
    }

    private external fun nativeStartAudio()
    private external fun nativeStopAudio()

    private lateinit var surfaceView: MainSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surfaceView = MainSurfaceView(this)
        setContentView(surfaceView)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudio()
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
