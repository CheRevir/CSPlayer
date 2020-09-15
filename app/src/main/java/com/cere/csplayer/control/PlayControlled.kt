package com.cere.csplayer.control

import android.net.Uri
import android.os.Bundle
import android.os.RemoteCallbackList
import com.cere.csplayer.data.FileData
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play

/**
 * Created by CheRevir on 2020/9/5
 */
abstract class PlayControlled {
    private val remoteCallbackList = RemoteCallbackList<IPlayCallback>()
    val binder = Binder()
    val callback = Callback()

    abstract fun onPlay()
    abstract fun onPause()
    abstract fun onPrevious()
    abstract fun onNext()
    abstract fun onSeekTo(progress: Int)
    abstract fun onData(data: FileData)
    abstract fun onMusicList(list: List<Music>)
    abstract fun onPlayList(list: List<Play>)
    abstract fun onRepeatMode(repeatMode: Int)
    abstract fun onShuffleMode(shuffleMode: Boolean)
    abstract fun onAction(action: String, bundle: Bundle?)

    inner class Callback {
        fun setPlay(isPlay: Boolean) {
            val num = remoteCallbackList.beginBroadcast()
            for (i in 0 until num) {
                remoteCallbackList.getBroadcastItem(i).setPlay(isPlay)
            }
            remoteCallbackList.finishBroadcast()
        }

        fun setData(id: Int) {
            val num = remoteCallbackList.beginBroadcast()
            for (i in 0 until num) {
                remoteCallbackList.getBroadcastItem(i).setData(id)
            }
            remoteCallbackList.finishBroadcast()
        }

        fun setDuration(duration: Int) {
            val num = remoteCallbackList.beginBroadcast()
            for (i in 0 until num) {
                remoteCallbackList.getBroadcastItem(i).setDuration(duration)
            }
            remoteCallbackList.finishBroadcast()
        }

        fun setCurrentDuration(duration: Int) {
            val num = remoteCallbackList.beginBroadcast()
            for (i in 0 until num) {
                remoteCallbackList.getBroadcastItem(i).setCurrentDuration(duration)
            }
            remoteCallbackList.finishBroadcast()
        }

        fun sendAction(action: String, bundle: Bundle?) {
            val num = remoteCallbackList.beginBroadcast()
            for (i in 0 until num) {
                remoteCallbackList.getBroadcastItem(i).sendAction(action, bundle)
            }
            remoteCallbackList.finishBroadcast()
        }
    }

    inner class Binder : IPlayControl.Stub() {
        override fun play() {
            this@PlayControlled.onPlay()
        }

        override fun pause() {
            this@PlayControlled.onPause()
        }

        override fun previous() {
            this@PlayControlled.onPrevious()
        }

        override fun next() {
            this@PlayControlled.onNext()
        }

        override fun seekTo(progress: Int) {
            this@PlayControlled.onSeekTo(progress)
        }

        override fun setData(data: FileData) {
            this@PlayControlled.onData(data)
        }

        override fun setMusicList(list: MutableList<Music>) {
            this@PlayControlled.onMusicList(list)
        }

        override fun setPlayList(list: MutableList<Play>) {
            this@PlayControlled.onPlayList(list)
        }

        override fun setRepeatMode(repeatMode: Int) {
            this@PlayControlled.onRepeatMode(repeatMode)
        }

        override fun setShuffleMode(shuffleMode: Boolean) {
            this@PlayControlled.onShuffleMode(shuffleMode)
        }

        override fun sendAction(action: String, bundle: Bundle?) {
            this@PlayControlled.onAction(action, bundle)
        }

        override fun registerCallback(callback: IPlayCallback) {
            this@PlayControlled.remoteCallbackList.register(callback)
        }

        override fun unregisterCallback(callback: IPlayCallback) {
            this@PlayControlled.remoteCallbackList.unregister(callback)
        }
    }
}