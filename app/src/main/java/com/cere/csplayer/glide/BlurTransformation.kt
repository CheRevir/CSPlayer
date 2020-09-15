package com.cere.csplayer.glide

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.cere.csplayer.until.BitmapUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.security.MessageDigest

/**
 * Created by CheRevir on 2020/9/15
 */
class BlurTransformation(private val context: Context) : BitmapTransformation() {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID.toByteArray(CHARSET))
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return BitmapUtils.getBitmapBlur(
            context,
            BitmapUtils.getBitmapScale(toTransform, 10, 10),
            1.0f
        )
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