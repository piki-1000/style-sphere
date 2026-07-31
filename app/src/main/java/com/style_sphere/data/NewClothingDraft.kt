package com.style_sphere.data

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object NewClothingDraft {
    var photoBitmap by mutableStateOf<Bitmap?>(null)

    fun clear() {
        photoBitmap = null
    }
}