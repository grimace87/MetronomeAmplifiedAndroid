package com.grimace.metronomeamplified

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.grimace.metronomeamplified.view.MainSurfaceView

class MainActivity : AppCompatActivity() {

    private lateinit var surfaceView: MainSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surfaceView = MainSurfaceView(this)
        setContentView(surfaceView)
    }
}
