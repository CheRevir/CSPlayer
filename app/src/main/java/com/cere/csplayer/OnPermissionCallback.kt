package com.cere.csplayer

import com.hjq.permissions.OnPermission

/**
 * Created by CheRevir on 2019/10/26
 */
abstract class OnPermissionCallback : OnPermission {
    override fun hasPermission(granted: List<String>, isAll: Boolean) {
        hasPermission(isAll)
    }

    override fun noPermission(denied: List<String>, quick: Boolean) {}
    abstract fun hasPermission(isAll: Boolean)
}