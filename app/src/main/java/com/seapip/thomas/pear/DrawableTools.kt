package com.seapip.thomas.pear

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

object DrawableTools {
    fun convertToGrayscale(drawable: Drawable): Drawable {
        val matrix = ColorMatrix()
        matrix.setSaturation(0)

        val filter = ColorMatrixColorFilter(matrix)

        drawable.setColorFilter(filter)

        return drawable
    }

    fun convertToCircle(drawable: Drawable): Drawable {
        val bitmap = drawableToBitmap(drawable)
        val output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight())

        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(bitmap.getWidth() / 2,
                bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return BitmapDrawable(output)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return (drawable as BitmapDrawable).getBitmap()
        }

        var width = drawable.getIntrinsicWidth()
        width = if (width > 0) width else 1
        var height = drawable.getIntrinsicHeight()
        height = if (height > 0) height else 1

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)

        return bitmap
    }
}
