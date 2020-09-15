package com.cere.csplayer

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions

/**
 * Created by CheRevir on 2020/9/5
 */
abstract class OnPermissionCallback(private val context: Context) : OnPermission {

    abstract override fun hasPermission(granted: MutableList<String>, all: Boolean)

    override fun noPermission(denied: MutableList<String>, never: Boolean) {
        AlertDialog.Builder(context).setIcon(R.mipmap.ic_launcher)
            .setTitle(context.getString(R.string.tip))
            .setMessage(context.getString(R.string.no_permission))
            .setPositiveButton(context.getString(R.string.setting)) { _, _ ->
                XXPermissions.startPermissionActivity(context, denied)
            }.show()
    }
}