package com.jeromefranco.firebaseaiandroidsampleapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri

fun Uri.toBitmap(context: Context): Bitmap? {
    return try {
        val source = ImageDecoder.createSource(context.contentResolver, this)
        ImageDecoder.decodeBitmap(source)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}