package com.grimace.metronomeamplified.extensions

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
        stringBuilder.append(line)
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
