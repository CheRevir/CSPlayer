package com.cere.csplayer.control

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 * Created by CheRevir on 2019/10/24
 */
abstract class PlayCallback {
    val callback: Callback
    private val mMainHelder: MainHelder

    companion object {
        private const val ON_PLAY = 0x1230
        private const val ON_DATA = 0x1231
        private const val ON_DURATION = 0x1232
        private const val ON_CURRENT_DURATON = 0x1233
        private const val ON_ACTION = 0x1234
    }

    init {
        callback = Callback()
        mMainHelder = MainHelder(Looper.getMainLooper())
    }

    abstract fun onPlay(isPlay: Boolean)
    abstract fun onData(data: String)
    abstract fun onDuration(duration: Int)
    abstract fun onCurrentDuration(duration: Int)
    abstract fun onAction(action: String, bundle: Bundle?)

    private inner class MainHelder(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                ON_PLAY -> onPlay(msg.obj as Boolean)
                ON_DATA -> onData(msg.obj.toString())
                ON_DURATION -> onDuration(msg.arg1)
                ON_CURRENT_DURATON -> onCurrentDuration(msg.arg1)
                ON_ACTION -> onAction(msg.obj.toString(), msg.data)
            }
        }
    }

    inner class Callback : IPlayCallback.Stub() {
        override fun setPlay(isPlay: Boolean) {
            mMainHelder.sendMessage(mMainHelder.obtainMessage(ON_PLAY, isPlay))
        }

        override fun setData(data: String) {
            mMainHelder.sendMessage(mMainHelder.obtainMessage(ON_DATA, data))
        }

        override fun setDuration(duration: Int) {
            mMainHelder.sendMessage(mMainHelder.obtainMessage(ON_DURATION, duration, 0))
        }

        override fun setCurrentDuration(duration: Int) {
            mMainHelder.sendMessage(mMainHelder.obtainMessage(ON_CURRENT_DURATON, duration, 0))
        }

        override fun sendAction(action: String, bundle: Bundle?) {
            val message = Message.obtain()
            message.data = bundle
            message.obj = action
            message.what = ON_ACTION
            mMainHelder.sendMessage(message)
        }
    }
}