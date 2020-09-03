package com.cere.csplayer.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorInt

/**
 * Created by CheRevir on 2019/10/26
 */
object CustomViews {
    @JvmStatic
    fun getOldToNewBitmap(bitmap: Bitmap, @ColorInt oldColor: Int, @ColorInt newColor: Int): Bitmap {
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                if (bitmap.getPixel(x, y) == oldColor) {
                    newBitmap.setPixel(x, y, newColor)
                } else {
                    newBitmap.setPixel(x, y, bitmap.getPixel(x, y))
                }
            }
        }
        bitmap.recycle()
        return newBitmap
    }

    @JvmStatic
    fun getPrev(@ColorInt color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color
        paint.style = Paint.Style.FILL
        val canvas = Canvas(bitmap)
        val path = Path()
        path.moveTo(15f, 50f)
        path.lineTo(50f, 25f)
        path.lineTo(50f, 75f)
        path.moveTo(50f, 50f)
        path.lineTo(85f, 25f)
        path.lineTo(85f, 75f)
        path.close()
        canvas.drawPath(path, paint)
        return bitmap
    }

    @JvmStatic
    fun getNext(@ColorInt color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color
        paint.style = Paint.Style.FILL
        val canvas = Canvas(bitmap)
        val path = Path()
        path.moveTo(15f, 25f)
        path.lineTo(15f, 75f)
        path.lineTo(50f, 50f)
        path.moveTo(50f, 25f)
        path.lineTo(50f, 75f)
        path.lineTo(85f, 50f)
        path.close()
        canvas.drawPath(path, paint)
        return bitmap
    }

    @JvmStatic
    fun getPause(@ColorInt color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color
        paint.style = Paint.Style.FILL
        val canvas = Canvas(bitmap)
        val path = Path()
        path.moveTo(15f, 10f)
        path.lineTo(15f, 90f)
        path.lineTo(85f, 50f)
        path.close()
        canvas.drawPath(path, paint)
        return bitmap
    }

    @JvmStatic
    fun getPlay(@ColorInt color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color
        paint.style = Paint.Style.FILL
        val canvas = Canvas(bitmap)
        canvas.drawRect(15f, 10f, 40f, 90f, paint)
        canvas.drawRect(60f, 10f, 85f, 90f, paint)
        return bitmap
    }

    @JvmStatic
    fun getClose(@ColorInt color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color
        paint.strokeWidth = 10f
        paint.style = Paint.Style.FILL
        val canvas = Canvas(bitmap)
        canvas.drawLine(15f, 10f, 85f, 90f, paint)
        canvas.drawLine(15f, 90f, 85f, 10f, paint)
        return bitmap
    }
}