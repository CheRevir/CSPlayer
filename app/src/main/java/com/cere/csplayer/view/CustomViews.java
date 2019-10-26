package com.cere.csplayer.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.ColorInt;

/**
 * Created by CheRevir on 2019/10/26
 */
public class CustomViews {
    public static Bitmap getOldToNewBitmap(Bitmap bitmap, @ColorInt int oldColor, @ColorInt int newColor) {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                if (bitmap.getPixel(x, y) == oldColor) {
                    newBitmap.setPixel(x, y, newColor);
                } else {
                    newBitmap.setPixel(x, y, bitmap.getPixel(x, y));
                }
            }
        }
        bitmap.recycle();
        return newBitmap;
    }

    public static Bitmap getPrev(@ColorInt int color) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        Canvas canvas = new Canvas(bitmap);
        Path path = new Path();
        path.moveTo(15, 50);
        path.lineTo(50, 25);
        path.lineTo(50, 75);
        path.moveTo(50, 50);
        path.lineTo(85, 25);
        path.lineTo(85, 75);
        path.close();
        canvas.drawPath(path, paint);
        return bitmap;
    }

    public static Bitmap getNext(@ColorInt int color) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        Canvas canvas = new Canvas(bitmap);
        Path path = new Path();
        path.moveTo(15, 25);
        path.lineTo(15, 75);
        path.lineTo(50, 50);
        path.moveTo(50, 25);
        path.lineTo(50, 75);
        path.lineTo(85, 50);
        path.close();
        canvas.drawPath(path, paint);
        return bitmap;
    }

    public static Bitmap getPause(@ColorInt int color) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        Canvas canvas = new Canvas(bitmap);
        Path path = new Path();
        path.moveTo(15, 10);
        path.lineTo(15, 90);
        path.lineTo(85, 50);
        path.close();
        canvas.drawPath(path, paint);
        return bitmap;
    }

    public static Bitmap getPlay(@ColorInt int color) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(15, 10, 40, 90, paint);
        canvas.drawRect(60, 10, 85, 90, paint);
        return bitmap;
    }

    public static Bitmap getClose(@ColorInt int color) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawLine(15, 10, 85, 90, paint);
        canvas.drawLine(15, 90, 85, 10, paint);
        return bitmap;
    }
}
