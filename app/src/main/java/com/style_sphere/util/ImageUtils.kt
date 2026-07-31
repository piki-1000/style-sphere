package com.style_sphere.util

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

fun bitmapToBase64(bitmap: Bitmap, maxDimension: Int = 600, quality: Int = 50): String {
    val ratio = minOf(
        maxDimension.toFloat() / bitmap.width,
        maxDimension.toFloat() / bitmap.height,
        1f
    )
    val targetWidth = (bitmap.width * ratio).toInt().coerceAtLeast(1)
    val targetHeight = (bitmap.height * ratio).toInt().coerceAtLeast(1)

    val scaled = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

    val stream = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    val bytes = stream.toByteArray()

    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

fun base64ToBitmap(base64: String): Bitmap? {
    return try {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
        null
    }
}