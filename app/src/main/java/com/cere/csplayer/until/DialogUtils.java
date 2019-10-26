package com.cere.csplayer.until;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.DrawableRes;

/**
 * Created by CheRevir on 2019/10/24
 */
public class DialogUtils {
    public static AlertDialog.Builder getAlertDialog(Context context, String title, String message, @DrawableRes int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(icon);
        return builder;
    }
}
