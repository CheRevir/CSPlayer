package com.cere.csplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cere.csplayer.activity.MainActivity
import com.cere.csplayer.control.PlayControlled
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play

/**
 * Created by CheRevir on 2020/9/5
 */
class PlayService : Service() {
    private lateinit var callback: PlayControlled.Callback
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

    private inner class Controlled : PlayControlled() {
        override fun onPlay() {
            TODO("Not yet implemented")
        }

        override fun onPause() {
            TODO("Not yet implemented")
        }

        override fun onPrevious() {
            TODO("Not yet implemented")
        }

        override fun onNext() {
            TODO("Not yet implemented")
        }

        override fun onSeekTo(progress: Int) {
            TODO("Not yet implemented")
        }

        override fun onData(data: Uri) {
            Log.e("TAG", "Controlled -> onData: ${Thread.currentThread().name}");
            Log.e("TAG", "Controlled -> onData: $data");
        }

        override fun onMusicList(list: List<Music>) {
            TODO("Not yet implemented")
        }

        override fun onPlayList(list: List<Play>) {
            TODO("Not yet implemented")
        }

        override fun onRepeatMode(repeatMode: Int) {
            TODO("Not yet implemented")
        }

        override fun onShuffleMode(shuffleMode: Boolean) {
            TODO("Not yet implemented")
        }

        override fun onAction(action: String, bundle: Bundle?) {
            TODO("Not yet implemented")
        }
    }
}