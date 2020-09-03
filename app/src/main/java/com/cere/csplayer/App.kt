package com.cere.csplayer

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import com.cere.csplayer.service.PlayService

/**
 * Created by CheRevir on 2019/10/19
 */
class App : Application() {
    companion object {
        var context: Context? = null
            private set
    }

    private var mActivities: ArrayList<Activity> = ArrayList()

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        if ("com.cere.csplayer" == currentProcessName) {
            startService(Intent(this, PlayService::class.java))
            registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks())
        }
    }

    private inner class ActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            mActivities.add(activity)
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            mActivities.remove(activity)
            if (mActivities.size == 0) {
                stopService(Intent(this@App, PlayService::class.java))
            }
        }
    }

    private val currentProcessName: String
        get() {
            val pid = Process.myPid()
            var processName = ""
            val manager = applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (process in manager.runningAppProcesses) {
                if (process.pid == pid) {
                    processName = process.processName
                }
            }
            return processName
        }
}