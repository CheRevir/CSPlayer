package com.cere.csplayer.glide

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.cere.csplayer.util.BitmapUtils
import java.security.MessageDigest

/**
 * Created by CheRevir on 2019/10/27
 */
class BlurTransformation(private val context: Context) : BitmapTransformation() {
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return BitmapUtils.getBitmapBlur(context, BitmapUtils.getScaleBitmap(toTransform, 10, 10), 1.0f)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID.toByteArray(CHARSET))
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is BlurTransformation
    }

    companion object {
        private const val ID = "com.cere.csplayer.glide.BlurTransformation"
    }
}