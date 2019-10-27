package com.cere.csplayer.glide;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.cere.csplayer.until.BitmapUtils;

import java.security.MessageDigest;

/**
 * Created by CheRevir on 2019/10/27
 */
public class BlurTransformation extends BitmapTransformation {
    private static final String ID = "com.cere.csplayer.glide.BlurTransformation";
    private Context mContext;

    public BlurTransformation(Context context) {
        mContext = context;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return BitmapUtils.getBitmapBlur(mContext, BitmapUtils.getScaleBitmap(toTransform, 10, 10), 1.0f);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID.getBytes(CHARSET));
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof BlurTransformation;
    }
}
