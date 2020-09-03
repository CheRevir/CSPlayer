package com.cere.csplayer.control

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play

/**
 * Created by CheRevir on 2019/10/24
 */
class PlayControl(private val mContext: Context, private val mClass: Class<*>) : ServiceConnection {
    private var mIPlayControl: IPlayControl? = null
    private lateinit var mOnConnectedListener: OnConnectedListener
    var isConnected = false
        private set

    fun connect(onConnectedListener: OnConnectedListener) {
        val intent = Intent(mContext, mClass)
        mContext.bindService(intent, this, Service.BIND_AUTO_CREATE)
        mOnConnectedListener = onConnectedListener
    }

    fun disconnect() {
        if (isConnected) {
            mContext.unbindService(this)
            isConnected = false
            mOnConnectedListener.onConnected(isConnected)
        }
    }

    fun play() {
        mIPlayControl!!.onPlay()
    }

    fun pause() {
        mIPlayControl!!.onPause()
    }

    fun previous() {
        mIPlayControl!!.onPrevious()
    }

    operator fun next() {
        mIPlayControl!!.onNext()
    }

    fun seekTo(progress: Long) {
        mIPlayControl!!.seekTo(progress)
    }

    fun setData(data: String) {
        mIPlayControl!!.setData(data)
    }

    fun setList(list: List<Music>) {
        mIPlayControl!!.setList(list)
    }

    fun setPlayList(list: List<Play>) {
        mIPlayControl!!.setPlayList(list)
    }

    fun setRepeatMode(repeatMode: Int) {
        mIPlayControl!!.setRepeatMode(repeatMode)
    }

    fun setShuffleModeEnabled(shuffleMode: Boolean) {
        mIPlayControl!!.setShuffleModeEnabled(shuffleMode)
    }

    fun sendAction(action: String, bundle: Bundle?) {
        mIPlayControl!!.sendAction(action, bundle)
    }

    fun registerCallback(callback: IPlayCallback) {
        mIPlayControl!!.registerCallback(callback)
    }

    fun unregisterCallback(callback: IPlayCallback) {
        mIPlayControl!!.unregisterCallback(callback)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        mIPlayControl = IPlayControl.Stub.asInterface(service)
        isConnected = true
        mOnConnectedListener.onConnected(isConnected)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        isConnected = false
        mIPlayControl = null
        mOnConnectedListener.onConnected(isConnected)
    }

    interface OnConnectedListener {
        fun onConnected(isConnected: Boolean)
    }
}