package com.cere.csplayer.control

import android.os.Bundle
import android.os.RemoteCallbackList
import android.os.RemoteException
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play

/**
 * Created by CheRevir on 2019/10/24
 */
abstract class PlayControlled {
    val binder: Binder
    val callback: Callback
    private val mCallbackList: RemoteCallbackList<IPlayCallback>

    init {
        binder = Binder()
        callback = Callback()
        mCallbackList = RemoteCallbackList()
    }

    abstract fun onPlay()
    abstract fun onPause()
    abstract fun onPrevious()
    abstract fun onNext()
    abstract fun seekTo(progress: Long)
    abstract fun setData(data: String)
    abstract fun setList(list: List<Music>)
    abstract fun setPlayList(list: List<Play>)
    abstract fun setRepeatMode(repeatMode: Int)
    abstract fun setShuffleModeEnabled(shuffleMode: Boolean)
    abstract fun onAction(action: String, bundle: Bundle?)
    private fun registerCallback(callback: IPlayCallback) {
        mCallbackList.register(callback)
    }

    private fun unregisterCallback(callback: IPlayCallback) {
        mCallbackList.unregister(callback)
    }

    inner class Callback {
        fun setPlay(isPlay: Boolean) {
            val num = mCallbackList.beginBroadcast()
            for (n in 0 until num) {
                try {
                    mCallbackList.getBroadcastItem(n).setPlay(isPlay)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            mCallbackList.finishBroadcast()
        }

        fun setData(data: String) {
            val num = mCallbackList.beginBroadcast()
            for (n in 0 until num) {
                try {
                    mCallbackList.getBroadcastItem(n).setData(data)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            mCallbackList.finishBroadcast()
        }

        fun setDuration(duration: Int) {
            val num = mCallbackList.beginBroadcast()
            for (n in 0 until num) {
                try {
                    mCallbackList.getBroadcastItem(n).setDuration(duration)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            mCallbackList.finishBroadcast()
        }

        fun setCurrentDuration(duration: Int) {
            val num = mCallbackList.beginBroadcast()
            for (n in 0 until num) {
                try {
                    mCallbackList.getBroadcastItem(n).setCurrentDuration(duration)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            mCallbackList.finishBroadcast()
        }

        fun sendAction(action: String, bundle: Bundle?) {
            val num = mCallbackList.beginBroadcast()
            for (n in 0 until num) {
                try {
                    mCallbackList.getBroadcastItem(n).sendAction(action, bundle)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            mCallbackList.finishBroadcast()
        }
    }

    inner class Binder : IPlayControl.Stub() {
        override fun onPlay() {
            this@PlayControlled.onPlay()
        }

        override fun onPause() {
            this@PlayControlled.onPause()
        }

        override fun onPrevious() {
            this@PlayControlled.onPrevious()
        }

        override fun onNext() {
            this@PlayControlled.onNext()
        }

        override fun seekTo(progress: Long) {
            this@PlayControlled.seekTo(progress)
        }

        override fun setData(data: String) {
            this@PlayControlled.setData(data)
        }

        override fun setList(list: List<Music>) {
            this@PlayControlled.setList(list)
        }

        override fun setPlayList(list: List<Play>) {
            this@PlayControlled.setPlayList(list)
        }

        override fun setRepeatMode(repeatMode: Int) {
            this@PlayControlled.setRepeatMode(repeatMode)
        }

        override fun setShuffleModeEnabled(shuffleMode: Boolean) {
            this@PlayControlled.setShuffleModeEnabled(shuffleMode)
        }

        override fun sendAction(action: String, bundle: Bundle?) {
            onAction(action, bundle)
        }

        override fun registerCallback(callback: IPlayCallback) {
            this@PlayControlled.registerCallback(callback)
        }

        override fun unregisterCallback(callback: IPlayCallback) {
            this@PlayControlled.unregisterCallback(callback)
        }
    }
}