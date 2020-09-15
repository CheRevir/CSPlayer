package com.cere.csplayer.control

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import com.cere.csplayer.data.FileData
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play

/**
 * Created by CheRevir on 2020/9/5
 */
class PlayControl(
    context: Context,
    service: Class<*>,
    private val onServiceConnectionListener: OnServiceConnectionListener
) : ServiceConnection {
    private var iPlayControl: IPlayControl? = null
    var isConnecting = false
        private set

    init {
        context.bindService(Intent(context, service), this, Context.BIND_AUTO_CREATE)
    }

    fun play() {
        iPlayControl!!.play()
    }

    fun pause() {
        iPlayControl!!.pause()
    }

    fun previous() {
        iPlayControl!!.previous()
    }

    fun next() {
        iPlayControl!!.next()
    }

    fun seekTo(progress: Int) {
        iPlayControl!!.seekTo(progress)
    }

    fun setData(data: FileData) {
        iPlayControl!!.setData(data)
    }

    fun setMusicList(list: List<Music>) {
        iPlayControl!!.setMusicList(list)
    }

    fun setPlayList(list: List<Play>) {
        iPlayControl!!.setPlayList(list)
    }

    fun setRepeatMode(repeatMode: Int) {
        iPlayControl!!.setRepeatMode(repeatMode)
    }

    fun setShuffleMode(shuffleMode: Boolean) {
        iPlayControl!!.setShuffleMode(shuffleMode)
    }

    fun sendAction(action: String, bundle: Bundle?) {
        iPlayControl!!.sendAction(action, bundle)
    }

    fun registerCallback(callback: IPlayCallback) {
        iPlayControl!!.registerCallback(callback)
    }

    fun unregisterCallback(callback: IPlayCallback) {
        iPlayControl!!.unregisterCallback(callback)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        iPlayControl = IPlayControl.Stub.asInterface(service)
        isConnecting = true
        onServiceConnectionListener.onServiceConnected()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        isConnecting = false
        onServiceConnectionListener.onServiceDisconnected()
    }

    interface OnServiceConnectionListener {
        fun onServiceConnected()
        fun onServiceDisconnected()
    }
}