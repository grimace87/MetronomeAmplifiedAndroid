package com.grimace.metronomeamplified.extensions

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder

@Throws(IOException::class)
fun AssetManager.openAsString(assetName: String): String {
    val stream = open(assetName)
    val stringBuilder = StringBuilder()
    val bufferedReader = BufferedReader(InputStreamReader(stream))
    var line: String? = bufferedReader.readLine()
    while (line != null) {
        stringBuilder.appendln(line)
        line = bufferedReader.readLine()
    }
    return stringBuilder.toString()
}

@Throws(IOException::class)
fun AssetManager.openAsBitmap(assetName: String): Bitmap {
    val stream = open(assetName)
    val bitmap: Bitmap? = BitmapFactory.decodeStream(stream)
    return bitmap ?: throw IOException("Could not decode to Bitmap: $assetName")
}

fun Context.openDrawableBitmap(@DrawableRes drawableResource: Int): Bitmap {
    val drawable = ResourcesCompat.getDrawable(resources, drawableResource, null)
    return drawable?.toBitmap() ?: throw IOException("")
}
