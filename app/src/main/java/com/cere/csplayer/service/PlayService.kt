package com.cere.csplayer.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.palette.graphics.Palette
import com.cere.csplayer.Constants
import com.cere.csplayer.R
import com.cere.csplayer.activity.MainActivity
import com.cere.csplayer.control.PlayControlled
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play
import com.cere.csplayer.util.BitmapUtils
import com.cere.csplayer.util.MusicMetadataRetriever
import com.cere.csplayer.view.CustomViews
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.io.IOException

/**
 * Created by CheRevir on 2019/10/24
 */
class PlayService : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, Palette.PaletteAsyncListener, AudioManager.OnAudioFocusChangeListener {
    companion object {
        private const val NOTIFICATION_ACTION = "notification_aciton"
        private const val NOTIFICATION_ACTION_PLAY = "notification_action_play"
        private const val NOTIFICATION_ACTION_PRE = "notification_action_pre"
        private const val NOTIFICATION_ACTION_NEXT = "notification_action_next"
        private const val NOTIFICATION_ACTION_CLOSE = "notification_action_close"

        private const val MODE_REPEAT_NONE = 0
        private const val MODE_REPEAT_SINGLE = 1
        private const val MODE_REPEAT_ALL = 2
    }

    private lateinit var mPlayer: MediaPlayer
    private lateinit var mAudioManager: AudioManager
    private lateinit var mAudioFocusRequest: AudioFocusRequest
    private lateinit var mPlayControlled: PlayControlled
    private lateinit var mHandler: Handler
    private lateinit var mUpdateUI: UpdateUI
    private lateinit var mUpdateProgress: UpdateProgress
    private var mMusics: List<Music>? = null
    private var mPlays: List<Play>? = null
    private var index = 0
    private lateinit var mBroadcast: Broadcast
    private lateinit var mNotificationCompatBuilder: NotificationCompat.Builder
    private lateinit var mUpdateNotification: UpdateNotification
    private lateinit var mRemoteViews: RemoteViews
    private lateinit var mRemoteViewsBig: RemoteViews
    private lateinit var mHeadset: Headset
    private var isHeadsetPressState = false
    private var headsetPressCount = 0
    private var isPlaying = false
    private var isPause = false
    private var isShuffleMode = false
    private var repeatMode = 0

    override fun onCreate() {
        super.onCreate()
        mHandler = Handler(this.mainLooper)
        mUpdateUI = UpdateUI()
        mUpdateProgress = UpdateProgress()
        mHeadset = Headset()
        initMediaPlayer()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        mPlayControlled = Controlled()
        return mPlayControlled.binder
    }

    private fun initMediaPlayer() {
        mPlayer = MediaPlayer()
        mPlayer.setVolume(1.0f, 1.0f)
        mPlayer.setOnCompletionListener(this)
        mPlayer.setOnPreparedListener(this)
        mPlayer.setOnErrorListener(this)
        val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        mPlayer.setAudioAttributes(attributes)
        mAudioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAudioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(attributes)
                    .setOnAudioFocusChangeListener(this)
                    .setAcceptsDelayedFocusGain(true)
                    .build()
        }
        //ComponentName componentName = new ComponentName(this, HeadsetButtonReceiver.class);
        //mAudioManager.registerMediaButtonEventReceiver(componentName);
        initNotification()
    }

    private fun initNotification() {
        mBroadcast = Broadcast()
        val filter = IntentFilter()
        filter.addAction(NOTIFICATION_ACTION)
        filter.addAction(Constants.ACTION_HEADSET)
        filter.addAction(AudioManager.ACTION_HEADSET_PLUG)
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        this.registerReceiver(mBroadcast, filter)
        val managerCompat = NotificationManagerCompat.from(this)
        mNotificationCompatBuilder = NotificationCompat.Builder(this, "music")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("music", "CSPlayer", NotificationManager.IMPORTANCE_LOW)
            channel.enableLights(false)
            channel.description = "CSPlayer播放通知"
            channel.setShowBadge(true)
            channel.setBypassDnd(true)
            channel.enableVibration(false)
            channel.vibrationPattern = longArrayOf(0)
            channel.setSound(null, null)
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            managerCompat.createNotificationChannel(channel)
        } else {
            mNotificationCompatBuilder.setVibrate(longArrayOf(0))
            mNotificationCompatBuilder.setSound(null)
        }
        mNotificationCompatBuilder.setSmallIcon(R.mipmap.ic_launcher)
        mNotificationCompatBuilder.setOngoing(true)
        mNotificationCompatBuilder.setAutoCancel(false)
        mNotificationCompatBuilder.priority = NotificationCompat.PRIORITY_MAX
        mNotificationCompatBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        mNotificationCompatBuilder.setContentIntent(PendingIntent.getActivity(this, 100, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT))
        mUpdateNotification = UpdateNotification()
    }

    private fun initAtBinded() {
        if (XXPermissions.isHasPermission(this, Manifest.permission.WAKE_LOCK)) {
            mPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
        } else {
            mPlayControlled.callback.sendAction(Constants.PERMISSION_WAKE_LOCK, null)
        }
        initTelephonyManager()
    }

    private fun initTelephonyManager() {
        if (XXPermissions.isHasPermission(this, Permission.READ_PHONE_STATE)) {
            val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.listen(TelephonyListener(), PhoneStateListener.LISTEN_CALL_STATE)
        } else {
            mPlayControlled.callback.sendAction(Constants.PERMISSION_READ_PHONE_STATE, null)
        }
    }

    private inner class Controlled : PlayControlled() {
        override fun onPlay() {
            play()
        }

        override fun onPause() {
            pause(true)
        }

        override fun onPrevious() {
            previous()
        }

        override fun onNext() {
            next()
        }

        override fun seekTo(progress: Long) {
            mPlayer.seekTo(progress.toInt())
        }

        override fun setData(data: String) {
            val index = getPosition(getPosition(data))
            if (index != -1) {
                this@PlayService.index = index
                this@PlayService.setData(mPlays!![index].position)
            }
        }

        override fun setList(list: List<Music>) {
            mMusics = list
        }

        override fun setPlayList(list: List<Play>) {
            mPlays = list
        }

        override fun setRepeatMode(repeatMode: Int) {
            this@PlayService.repeatMode = repeatMode
        }

        override fun setShuffleModeEnabled(shuffleMode: Boolean) {
            isShuffleMode = shuffleMode
        }

        override fun onAction(action: String, bundle: Bundle?) {
            when (action) {
                Constants.PLAY_SERVICE_INIT -> initAtBinded()
                Constants.PERMISSION_WAKE_LOCK -> mPlayer.setWakeMode(this@PlayService, PowerManager.PARTIAL_WAKE_LOCK)
                Constants.PERMISSION_READ_PHONE_STATE -> initTelephonyManager()
            }
        }
    }

    private inner class Broadcast : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Constants.ACTION_HEADSET -> {
                    headsetPressCount++
                    if (!isHeadsetPressState) {
                        isHeadsetPressState = true
                        mHandler.postDelayed(mHeadset, 1000)
                    }
                }
                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> if (mPlayer.isPlaying) {
                    pause(true)
                }
                AudioManager.ACTION_HEADSET_PLUG -> if (intent.hasExtra("state") && intent.extras!!.getInt("state") == 1 && isPause) {
                    play()
                }
                NOTIFICATION_ACTION -> when (intent.extras!!.getString(NOTIFICATION_ACTION)) {
                    NOTIFICATION_ACTION_PLAY -> if (mPlayer.isPlaying) {
                        pause(true)
                    } else {
                        play()
                    }
                    NOTIFICATION_ACTION_PRE -> previous()
                    NOTIFICATION_ACTION_NEXT -> next()
                    NOTIFICATION_ACTION_CLOSE -> {
                        pause(false)
                        this@PlayService.stopForeground(true)
                    }
                }
            }
        }
    }

    private inner class Headset : Runnable {
        override fun run() {
            when (headsetPressCount) {
                1 -> if (mPlayer.isPlaying) {
                    pause(true)
                } else {
                    play()
                }
                2 -> next()
                else -> previous()
            }
            headsetPressCount = 0
            isHeadsetPressState = false
        }
    }

    private fun play() {
        if (!mPlayer.isPlaying && requestAudioFocus()) {
            mPlayer.start()
            isPlaying = true
            isPause = false
            mPlayControlled.callback.setPlay(mPlayer.isPlaying)
            mHandler.postDelayed(mUpdateProgress, 0)
            mHandler.post(mUpdateNotification)
        }
    }

    private fun pause(isNotify: Boolean) {
        if (mPlayer.isPlaying || isPlaying) {
            mPlayer.pause()
            isPlaying = false
            isPause = true
            mPlayControlled.callback.setPlay(mPlayer.isPlaying)
            mHandler.removeCallbacks(mUpdateProgress)
            if (isNotify) {
                mHandler.post(mUpdateNotification)
            }
            abandonAudioFocus()
        }
    }

    private fun previous() {
        index--
        if (index < 0) {
            index = mPlays!!.size - 1
        }
        setData(mPlays!![index].position)
    }

    private fun next() {
        index++
        if (index > mPlays!!.size - 1) {
            index = 0
        }
        setData(mPlays!![index].position)
    }

    private fun setData(position: Int) {
        mHandler.post {
            mPlayer.reset()
            try {
                mPlayer.setDataSource(mMusics!![position].path)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mPlayer.prepareAsync()
        }
    }

    override fun onCompletion(mp: MediaPlayer) {
        when (repeatMode) {
            MODE_REPEAT_NONE -> {
                index++
                if (index > mPlays!!.size - 1) {
                    pause(true)
                    index = mPlays!!.size - 1
                } else {
                    setData(mPlays!![index].position)
                }
            }
            MODE_REPEAT_SINGLE -> setData(mPlays!![index].position)
            MODE_REPEAT_ALL -> next()
        }
    }

    override fun onPrepared(mp: MediaPlayer) {
        if (isPlaying) {
            play()
        }
        mHandler.post(mUpdateUI)
        mHandler.post(mUpdateNotification)
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        return false
    }

    private inner class UpdateNotification : Runnable {
        override fun run() {
            mRemoteViews = RemoteViews(this@PlayService.packageName, R.layout.notification_play)
            mRemoteViewsBig = RemoteViews(this@PlayService.packageName, R.layout.notification_play_big)
            val intent = Intent(NOTIFICATION_ACTION)
            intent.setPackage(this@PlayService.packageName)
            intent.putExtra(NOTIFICATION_ACTION, NOTIFICATION_ACTION_PLAY)
            val intentPlay = PendingIntent.getBroadcast(this@PlayService, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            mRemoteViews.setOnClickPendingIntent(R.id.notification_play_play, intentPlay)
            mRemoteViewsBig.setOnClickPendingIntent(R.id.notification_play_big_play, intentPlay)
            intent.putExtra(NOTIFICATION_ACTION, NOTIFICATION_ACTION_PRE)
            val intentPre = PendingIntent.getBroadcast(this@PlayService, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            mRemoteViews.setOnClickPendingIntent(R.id.notification_play_prev, intentPre)
            mRemoteViewsBig.setOnClickPendingIntent(R.id.notification_play_big_prev, intentPre)
            intent.putExtra(NOTIFICATION_ACTION, NOTIFICATION_ACTION_NEXT)
            val intentNext = PendingIntent.getBroadcast(this@PlayService, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            mRemoteViews.setOnClickPendingIntent(R.id.notification_play_next, intentNext)
            mRemoteViewsBig.setOnClickPendingIntent(R.id.notification_play_big_next, intentNext)
            intent.putExtra(NOTIFICATION_ACTION, NOTIFICATION_ACTION_CLOSE)
            val intentClose = PendingIntent.getBroadcast(this@PlayService, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            mRemoteViews.setOnClickPendingIntent(R.id.notification_play_close, intentClose)
            mRemoteViewsBig.setOnClickPendingIntent(R.id.notification_play_big_close, intentClose)
            val music = mMusics!![mPlays!![index].position]
            mRemoteViews.setTextViewText(R.id.notification_play_title, music.title)
            mRemoteViewsBig.setTextViewText(R.id.notification_play_big_title, music.title)
            mRemoteViews.setTextViewText(R.id.notification_play_artist, music.artist)
            mRemoteViewsBig.setTextViewText(R.id.notification_play_big_artist, music.artist)
            mRemoteViews.setTextViewText(R.id.notification_play_album, music.album)
            mRemoteViewsBig.setTextViewText(R.id.notification_play_big_album, music.album)
            mRemoteViewsBig.setTextViewText(R.id.notification_play_big_number, "${index + 1}/${mPlays!!.size}")
            var bitmap: Bitmap? = null
            MusicMetadataRetriever.instance.getAlbumArt(music.path!!)?.let {
                bitmap = BitmapUtils.getScaleBitmap(it, 200, 200)
            }
            mRemoteViews.setImageViewBitmap(R.id.notification_play_album_art, bitmap)
            mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_album_art, bitmap)
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.clannad)
            }
            val palette = Palette.from(bitmap!!)
            palette.generate(this@PlayService)
        }
    }

    override fun onGenerated(palette: Palette?) {
        val color = palette!!.getLightMutedColor(Color.WHITE)
        if (mPlayer.isPlaying) {
            val play = CustomViews.getPlay(color)
            mRemoteViews.setImageViewBitmap(R.id.notification_play_play, play)
            mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_play, play)
        } else {
            val pause = CustomViews.getPause(color)
            mRemoteViews.setImageViewBitmap(R.id.notification_play_play, pause)
            mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_play, pause)
        }
        val prev = CustomViews.getPrev(color)
        mRemoteViews.setImageViewBitmap(R.id.notification_play_prev, prev)
        mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_prev, prev)
        val next = CustomViews.getNext(color)
        mRemoteViews.setImageViewBitmap(R.id.notification_play_next, next)
        mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_next, next)
        val close = CustomViews.getClose(color)
        mRemoteViews.setImageViewBitmap(R.id.notification_play_close, close)
        mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_close, close)
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        bitmap.setPixel(0, 0, palette.getDarkMutedColor(Color.DKGRAY))
        mRemoteViews.setImageViewBitmap(R.id.notification_play_background, bitmap)
        mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_background, bitmap)
        mRemoteViews.setTextColor(R.id.notification_play_title, palette.getLightVibrantColor(Color.WHITE))
        mRemoteViewsBig.setTextColor(R.id.notification_play_big_title, palette.getLightVibrantColor(Color.WHITE))
        mRemoteViews.setTextColor(R.id.notification_play_artist, palette.getLightVibrantColor(Color.WHITE))
        mRemoteViewsBig.setTextColor(R.id.notification_play_big_artist, palette.getVibrantColor(Color.WHITE))
        mRemoteViews.setTextColor(R.id.notification_play_album, palette.getLightMutedColor(Color.WHITE))
        mRemoteViewsBig.setTextColor(R.id.notification_play_big_album, palette.getLightMutedColor(Color.WHITE))
        mNotificationCompatBuilder.setCustomContentView(mRemoteViews)
        mNotificationCompatBuilder.setCustomBigContentView(mRemoteViewsBig)
        this@PlayService.startForeground(14, mNotificationCompatBuilder.build())
    }

    private inner class UpdateUI : Runnable {
        override fun run() {
            mPlayControlled.callback.setData(mMusics!![mPlays!![index].position].path!!)
            mPlayControlled.callback.setDuration(mPlayer.duration)
        }
    }

    private inner class UpdateProgress : Runnable {
        override fun run() {
            mPlayControlled.callback.setCurrentDuration(mPlayer.currentPosition)
            mHandler.postDelayed(mUpdateProgress, 1000)
        }
    }

    @Suppress("DEPRECATION")
    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAudioManager.requestAudioFocus(mAudioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    @Suppress("DEPRECATION")
    private fun abandonAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                mPlayer.setVolume(1.0f, 1.0f)
                if (!mPlayer.isPlaying) {
                    play()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> if (mPlayer.isPlaying) {
                pause(true)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (mPlayer.isPlaying) {
                pause(true)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mPlayer.setVolume(0.5f, 0.5f)
        }
    }

    private fun getPosition(data: String): Int {
        val music = Music()
        music.path = data
        return mMusics!!.indexOf(music)
    }

    private fun getPosition(position: Int): Int {
        val play = Play()
        play.position = position
        return mPlays!!.indexOf(play)
    }

    private inner class TelephonyListener : PhoneStateListener() {
        private var isPhone = false

        @SuppressLint("SwitchIntDef")
        override fun onCallStateChanged(state: Int, phoneNumber: String) {
            super.onCallStateChanged(state, phoneNumber)
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> if (mPlayer.isPlaying) {
                    pause(true)
                    isPhone = true
                }
                TelephonyManager.CALL_STATE_IDLE -> if (isPhone && !mPlayer.isPlaying) {
                    play()
                    isPhone = false
                }
            }
        }
    }
}