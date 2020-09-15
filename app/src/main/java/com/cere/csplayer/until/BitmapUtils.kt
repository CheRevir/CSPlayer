package com.cere.csplayer.until

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

/**
 * Created by CheRevir on 2020/9/8
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
        //bitmap.recycle()
        rs.destroy()
        return outBitmap
    }

    @JvmStatic
    fun getBitmapScale(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return ThumbnailUtils.extractThumbnail(bitmap, width, height)
    }
}