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
                try {
                    fd.exists = true
                    fd.fd = context.contentResolver.openFileDescriptor(uri, "r")
                } catch (e: FileNotFoundException) {
                    fd.exists = false
                }
            }
            context is Activity -> {
                XXPermissions.with(context).permission(Permission.READ_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback(context) {
                        override fun hasPermission(granted: MutableList<String>, all: Boolean) {
                            fd = getFileData(context, uri)
                        }
                    })
            }
            else -> {
                PermissionUtils.instance.getXXPermissions()
                    ?.permission(Permission.READ_EXTERNAL_STORAGE)
                    ?.request(object :
                        OnPermissionCallback(PermissionUtils.instance.activity!!) {
                        override fun hasPermission(granted: MutableList<String>, all: Boolean) {
                            fd = getFileData(context, uri)
                        }
                    })
            }
        }
        return fd
    }
}