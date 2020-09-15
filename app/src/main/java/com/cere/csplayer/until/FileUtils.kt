package com.cere.csplayer.until

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.cere.csplayer.OnPermissionCallback
import com.cere.csplayer.data.FileData
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.io.FileNotFoundException

/**
 * Created by CheRevir on 2020/9/5
 */
object FileUtils {
    @Suppress("DEPRECATION")
    @JvmStatic
    fun getFileData(context: Context, uri: Uri): FileData {
        var fd = FileData()
        when {
            XXPermissions.hasPermission(context, Permission.READ_EXTERNAL_STORAGE) -> {
                fd = getFD(context, uri)
            }
            context is Activity -> {
                XXPermissions.with(context).permission(Permission.READ_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback(context) {
                        override fun hasPermission(granted: MutableList<String>, all: Boolean) {
                            fd = getFD(context, uri)
                        }
                    })
            }
            else -> {
                PermissionUtils.instance.getXXPermissions()
                    ?.permission(Permission.READ_EXTERNAL_STORAGE)
                    ?.request(object :
                        OnPermissionCallback(PermissionUtils.instance.activity!!) {
                        override fun hasPermission(granted: MutableList<String>, all: Boolean) {
                            fd = getFD(context, uri)
                        }
                    })
            }
        }
        return fd
    }

    private fun getFD(context: Context, uri: Uri): FileData {
        val s = uri.toString()
        val id = Integer.parseInt(s.substring(s.lastIndexOf("/") + 1))
        return try {
            val fd = context.contentResolver.openFileDescriptor(uri, "r")
            FileData(id, true, fd)
        } catch (e: FileNotFoundException) {
            FileData(id, false)
        }
    }
}