package com.cere.csplayer.util

import android.app.AlertDialog
import android.content.Context
import androidx.annotation.DrawableRes

/**
 * Created by CheRevir on 2019/10/24
 */
object DialogUtils {
    @JvmStatic
    fun getAlertDialog(context: Context, title: String, message: String, @DrawableRes icon: Int): AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(icon)
        return builder
    }
}