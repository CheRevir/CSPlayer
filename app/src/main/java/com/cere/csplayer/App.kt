package com.cere.csplayer

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.util.Log
import com.cere.csplayer.control.PlayControl
import com.cere.csplayer.service.PlayService

/**
 * Created by CheRevir on 2020/9/5
 */
class App : Application(), PlayControl.OnConnectionListener {
    override fun onCreate() {
        super.onCreate()
        context = this

        if (this.packageName == getCurrentProcessName()) {
            control = PlayControl(this, PlayService::class.java)
            control.addListener(this)
            control.connect()
        }
    }

    override fun onConnectChange(isConnected: Boolean) {
        if (isConnected) {
            Log.e("TAG", "App -> onServiceConnected: ")
        } else {
            Log.e("TAG", "App -> onServiceDisconnected: ")
            control.connect()
        }
    }

    private fun getCurrentProcessName(): String {
        var process = ""
        val uid = Process.myUid()
        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (i in manager.runningAppProcesses) {
            if (uid == i.uid) {
                process = i.processName
            }
        }
        return process
    }

    companion object {
        lateinit var context: Context
            private set
        lateinit var control: PlayControl
            private set
    }
}