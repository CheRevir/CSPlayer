package com.cere.csplayer.control

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 * Created by CheRevir on 2020/9/5
 */
open class PlayCallback {
    val callback = Callback()
    private val handler = MainHandler(Looper.getMainLooper())

    companion object {
        private const val ON_PLAY = 0x1230
        private const val ON_DATA = 0x1231
        private const val ON_DURATION = 0x1232
        private const val ON_CURRENT_DURATON = 0x1233
        private const val ON_ACTION = 0x1234
    }

    open fun onPlay(isPlay: Boolean) {}
    open fun onData(data: String) {}
    open fun onDuration(duration: Long) {}
    open fun onCurrentDuration(duration: Long) {}
    open fun onAction(action: String, bundle: Bundle?) {}

    private inner class MainHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                ON_PLAY -> onPlay(msg.obj as Boolean)
                ON_DATA -> onData(msg.obj.toString())
                ON_DURATION -> onDuration((msg.obj as Long).toLong())
                ON_CURRENT_DURATON -> onCurrentDuration((msg.obj as Long).toLong())
                ON_ACTION -> onAction(msg.obj.toString(), msg.data)
            }
        }
    }

    inner class Callback : IPlayCallback.Stub() {
        override fun setPlay(isPlay: Boolean) {
            handler.sendMessage(handler.obtainMessage(ON_PLAY, isPlay))
        }

        override fun setData(data: String) {
            handler.sendMessage(handler.obtainMessage(ON_DATA, data))
        }

        override fun setDuration(duration: Int) {
            handler.sendMessage(handler.obtainMessage(ON_DURATION, duration))
        }

        override fun setCurrentDuration(duration: Int) {
            handler.sendMessage(handler.obtainMessage(ON_CURRENT_DURATON, duration))
        }

        override fun sendAction(action: String, bundle: Bundle?) {
            val message = Message.obtain()
            message.data = bundle
            message.obj = action
            message.what = ON_ACTION
            handler.sendMessage(message)
        }
    }
}