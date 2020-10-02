package com.cere.csplayer

import android.app.Application
import android.content.Context

/**
 * Created by CheRevir on 2020/9/5
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, PlayService::class.java))
        } else {
            startService(Intent(this, PlayService::class.java))
        }*/
    }

    companion object {
        lateinit var context: Context
            private set
    }
}