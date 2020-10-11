package com.cere.csplayer.control

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference

/**
 * Created by CheRevir on 2020/9/5
 */
open class PlayCallback {
    private val handle: Handle by lazy { Handle(WeakReference(this)) }
    val callback = Callback(handle)

    open fun onPlay(isPlay: Boolean) {}
    open fun onData(id: Int) {}
    open fun onDuration(duration: Int) {}
    open fun onCurrentDuration(duration: Int) {}
    open fun onAction(action: String, bundle: Bundle?) {}

    class Handle(private val reference: WeakReference<PlayCallback>) :
        Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                ON_PLAY -> reference.get()?.onPlay(msg.arg1 == 1)
                ON_DATA -> reference.get()?.onData(msg.arg1)
                ON_DURATION -> reference.get()?.onDuration(msg.arg1)
                ON_CURRENT_DURATION -> reference.get()?.onCurrentDuration(msg.arg1)
                ON_ACTION -> reference.get()?.onAction(msg.obj.toString(), msg.data)
            }
        }
    }

    class Callback(private val handle: Handle) : IPlayCallback.Stub() {
        override fun setPlay(isPlay: Boolean) {
            handle.sendMessage(handle.obtainMessage(ON_PLAY, if (isPlay) 1 else 0, 0))
        }

        override fun setData(id: Int) {
            handle.sendMessage(handle.obtainMessage(ON_DATA, id, 0))
        }

        override fun setDuration(duration: Int) {
            handle.sendMessage(handle.obtainMessage(ON_DURATION, duration, 0))
        }

        override fun setCurrentDuration(duration: Int) {
            handle.sendMessage(handle.obtainMessage(ON_CURRENT_DURATION, duration, 0))
        }

        override fun sendAction(action: String?, bundle: Bundle?) {
            val message = Message.obtain()
            message.data = bundle
            message.obj = action ?: ""
            message.what = ON_ACTION
            handle.sendMessage(message)
        }
    }

    companion object {
        private const val ON_PLAY = 1
        private const val ON_DATA = 2
        private const val ON_DURATION = 3
        private const val ON_CURRENT_DURATION = 4
        private const val ON_ACTION = 5
    }
}
