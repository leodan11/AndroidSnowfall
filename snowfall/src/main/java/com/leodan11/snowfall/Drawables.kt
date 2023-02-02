package com.leodan11.snowfall

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable


internal fun Drawable.toBitmap(): Bitmap {
    return when (this) {
        is BitmapDrawable -> bitmap
        is VectorDrawable -> toBitmap()
        else -> throw IllegalArgumentException("Unsupported drawable type")
    }
}

internal fun VectorDrawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap

}