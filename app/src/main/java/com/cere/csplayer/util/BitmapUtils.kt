package com.cere.csplayer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

/**
 * Created by CheRevir on 2019/10/25
 */
object BitmapUtils {
    @JvmStatic
    fun getBitmapBlur(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
        val outBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val rs = RenderScript.create(context)
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, bitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outBitmap)
        blurScript.setRadius(radius)
        blurScript.setInput(tmpIn)
        blurScript.forEach(tmpOut)
        tmpOut.copyTo(outBitmap)
        bitmap.recycle()
        rs.destroy()
        return outBitmap
    }

    @JvmStatic
    fun getScaleBitmap(bitmap: Bitmap, scaleWidget: Int, scaleHeight: Int): Bitmap {
        return ThumbnailUtils.extractThumbnail(bitmap, scaleWidget, scaleHeight)
    }

    fun getScaleBitmap(bitmap: Bitmap, scaleWidget: Int, scaleHeight: Int, keepProtion: Boolean): Bitmap {
        return if (keepProtion) {
            val width = bitmap.width
            val height = bitmap.height
            val w = scaleWidget.toFloat() / width
            val h = scaleHeight.toFloat() / height
            val matrix = Matrix()
            matrix.postScale(w, h)
            Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        } else {
            Bitmap.createScaledBitmap(bitmap, scaleWidget, scaleHeight, false)
        }
    }
}