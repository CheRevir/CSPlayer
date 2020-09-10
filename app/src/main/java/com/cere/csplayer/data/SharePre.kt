package com.cere.csplayer.data

import android.content.Context
import android.content.SharedPreferences
import com.cere.csplayer.App

/**
 * Created by CheRevir on 2020/9/5
 */
object SharePre {
    private const val NAME = "share_preference"

    private fun getSharePreference(): SharedPreferences {
        return App.context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun getString(key: String, defValue: String): String {
        return getSharePreference().getString(key, defValue)!!
    }

    @JvmStatic
    fun putString(key: String, value: String) {
        getSharePreference().edit().putString(key, value).apply()
    }

    @JvmStatic
    fun getInt(key: String, defValue: Int): Int {
        return getSharePreference().getInt(key, defValue)
    }

    @JvmStatic
    fun putInt(key: String, value: Int) {
        getSharePreference().edit().putInt(key, value).apply()
    }

    @JvmStatic
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return getSharePreference().getBoolean(key, defValue)
    }

    @JvmStatic
    fun putBoolean(key: String, value: Boolean) {
        getSharePreference().edit().putBoolean(key, value).apply()
    }
}