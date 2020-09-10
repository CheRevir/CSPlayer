package com.cere.csplayer.until

import android.app.Activity
import com.cere.csplayer.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import kotlin.LazyThreadSafetyMode.SYNCHRONIZED

/**
 * Created by CheRevir on 2020/9/9
 */
class PermissionUtils {
    var activity: Activity? = null

    companion object {
        val instance: PermissionUtils by lazy(mode = SYNCHRONIZED) {
            PermissionUtils()
        }
    }

    fun request(permission: String): Boolean {
        var result = false
        activity?.let {
            XXPermissions.with(it).permission(permission)
                .request(object : OnPermissionCallback(it) {
                    override fun hasPermission(granted: MutableList<String>, all: Boolean) {
                        result = true
                    }
                })
        }
        return result
    }

    fun getXXPermissions(): XXPermissions? {
        activity?.let {
            return XXPermissions.with(it)
        }
        return null
    }
}