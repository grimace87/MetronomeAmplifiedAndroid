package com.grimace.metronomeamplified.view

import android.annotation.SuppressLint
import android.app.Activity
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.grimace.metronomeamplified.renderer.MainRenderer

@SuppressLint("ViewConstructor")
class MainSurfaceView(activity: Activity) : GLSurfaceView(activity) {

    private val renderer: MainRenderer
    private val locationArray = intArrayOf(0, 0)

    init {
        setEGLContextClientVersion(2)
        renderer = MainRenderer(activity)
        setRenderer(renderer)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                getLocationInWindow(locationArray)
                queueEvent { renderer.onPointerDown(event.x, event.y - locationArray[1]) }
            }
        }
        return super.onTouchEvent(event)
    }

    fun tryHandleDeviceBack(): Boolean {
        val stackSize = renderer.stackSize()
        val willEnd = stackSize < 2
        if (!willEnd) {
            queueEvent { renderer.popTopScene() }
        }
        return !willEnd
    }
}
