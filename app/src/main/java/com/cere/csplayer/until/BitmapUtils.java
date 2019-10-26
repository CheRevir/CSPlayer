package com.cere.csplayer.until;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.cere.csplayer.R;

/**
 * Created by CheRevir on 2019/10/25
 */
public class BitmapUtils {
    public static Bitmap getBitmapBlur(Context context, Bitmap bitmap, float radius) {
        if (bitmap == null) {
            bitmap = getScaleBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.clannad), 10, 10);
        }
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outBitmap);
        blurScript.setRadius(radius);
        blurScript.setInput(tmpIn);
        blurScript.forEach(tmpOut);
        tmpOut.copyTo(outBitmap);
        bitmap.recycle();
        rs.destroy();
        return outBitmap;
    }

    public static Bitmap getScaleBitmap(Bitmap bitmap, int scaleWidget, int scaleHeight) {
        if (bitmap != null) {
            return ThumbnailUtils.extractThumbnail(bitmap, scaleWidget, scaleHeight);
        }
        return null;
    }

    public static Bitmap getScaleBitmap(Bitmap bitmap, int scaleWidget, int scaleHeight, boolean keepProtion) {
        if (bitmap != null) {
            if (keepProtion) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float w = (float) scaleWidget / width;
                float h = (float) scaleHeight / height;
                Matrix matrix = new Matrix();
                matrix.postScale(w, h);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            } else {
                bitmap = Bitmap.createScaledBitmap(bitmap, scaleWidget, scaleHeight, false);
            }
        }
        return bitmap;
    }
}
