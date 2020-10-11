package com.cere.csplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cere.csplayer.activity.MainActivity
import com.cere.csplayer.control.PlayControlled
import com.cere.csplayer.data.FileData

/**
 * Created by CheRevir on 2020/9/5
 */
class PlayService : Service() {
    private var callback: PlayControlled.Callback? = null
    private lateinit var builder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()

        builder = NotificationCompat.Builder(this, "music")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("music", "CSPlayer", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(false)
        } else {
            builder.setShowWhen(true)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pending =
            PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pending)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        val controlled = Controlled()
        callback = controlled.callback
        return controlled.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e("TAG", "PlayService -> onUnbind: $intent")
        return super.onUnbind(intent)
    }

    private inner class Controlled : PlayControlled() {
        override fun onPlay() {
            super.onPlay()
            callback.setPlay(true)
        }

        override fun onPause() {
            super.onPause()
            callback.setPlay(false)
        }

        override fun onData(data: FileData) {
            super.onData(data)
            callback.setData(data.id)
        }
    }
}