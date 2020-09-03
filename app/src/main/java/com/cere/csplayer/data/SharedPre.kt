package com.cere.csplayer.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by CheRevir on 2019/10/22
 */
object SharedPre {
    private const val NAME = "share_preference"

    @Suppress("DEPRECATION")
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS)
    }

    @SuppressLint("ApplySharedPref")
    @JvmStatic
    fun putInt(context: Context, key: String?, value: Int) {
        getSharedPreferences(context).edit().putInt(key, value).commit()
    }

    @JvmStatic
    fun getInt(context: Context, key: String?, defValue: Int): Int {
        return getSharedPreferences(context).getInt(key, defValue)
    }

    @SuppressLint("ApplySharedPref")
    fun putString(context: Context, key: String?, value: String?) {
        getSharedPreferences(context).edit().putString(key, value).commit()
    }

    fun getString(context: Context, key: String?, defValue: String?): String? {
        return getSharedPreferences(context).getString(key, defValue)
    }

    @SuppressLint("ApplySharedPref")
    @JvmStatic
    fun putBoolean(context: Context, key: String?, value: Boolean) {
        getSharedPreferences(context).edit().putBoolean(key, value).commit()
    }

    @JvmStatic
    fun getBoolean(context: Context, key: String?, defValue: Boolean): Boolean {
        return getSharedPreferences(context).getBoolean(key, defValue)
    }
}