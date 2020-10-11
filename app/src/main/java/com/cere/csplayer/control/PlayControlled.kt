package com.cere.csplayer.control

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.RemoteCallbackList
import com.cere.csplayer.data.FileData
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play
import java.lang.ref.WeakReference

/**
 * Created by CheRevir on 2020/9/5
 */
open class PlayControlled {
    private val remoteCallbackList = RemoteCallbackList<IPlayCallback>()
    private val handle: Handle by lazy { Handle(WeakReference(this)) }
    val binder = Binder(handle)
    val callback = Callback()

    open fun onPlay() {}
    open fun onPause() {}
    open fun onPrevious() {}
    open fun onNext() {}
    open fun onSeekTo(progress: Int) {}
    open fun onData(data: FileData) {}
    open fun onMusicList(list: List<Music>) {}
    open fun onPlayList(list: List<Play>) {}
    open fun onRepeatMode(repeatMode: Int) {}
    open fun onShuffleMode(shuffleMode: Boolean) {}
    open fun onAction(action: String, bundle: Bundle?) {}

    class Handle(private val reference: WeakReference<PlayControlled>) :
        Handler(Looper.getMainLooper()) {
        @Suppress("UNCHECKED_CAST")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                ON_PLAY -> reference.get()?.onPlay()
                ON_PAUSE -> reference.get()?.onPause()
                ON_PREVIOUS -> reference.get()?.onPrevious()
                ON_NEXT -> reference.get()?.onNext()
                ON_SEEK_TO -> reference.get()?.onSeekTo(msg.arg1)
                ON_DATA -> reference.get()?.onData(msg.obj as FileData)
                ON_MUSIC_LIST -> reference.get()?.onMusicList(msg.obj as List<Music>)
                ON_PLAY_LIST -> reference.get()?.onPlayList(msg.obj as List<Play>)
                ON_REPEAT_MODE -> reference.get()?.onRepeatMode(msg.arg1)
                ON_SHUFFLE_MODE -> reference.get()?.onShuffleMode(msg.arg1 == 1)
                ON_SEND_ACTION -> reference.get()?.onAction(msg.obj.toString(), msg.data)
                ON_REGISTER_CALLBACK -> reference.get()?.remoteCallbackList?.register(msg.obj as IPlayCallback)
                ON_UNREGISTER_CALLBACK -> reference.get()?.remoteCallbackList?.unregister(msg.obj as IPlayCallback)
            }
        }
    }

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

    class Binder(private val handle: Handle) : IPlayControl.Stub() {
        override fun play() {
            handle.sendEmptyMessage(ON_PLAY)
        }

        override fun pause() {
            handle.sendEmptyMessage(ON_PAUSE)
        }

        override fun previous() {
            handle.sendEmptyMessage(ON_PREVIOUS)
        }

        override fun next() {
            handle.sendEmptyMessage(ON_NEXT)
        }

        override fun seekTo(progress: Int) {
            handle.sendMessage(handle.obtainMessage(ON_SEEK_TO, progress, 0))
        }

        override fun setData(data: FileData?) {
            data?.let {
                handle.sendMessage(handle.obtainMessage(ON_DATA, it))
            }
        }

        override fun setMusicList(list: MutableList<Music>?) {
            list?.let {
                handle.sendMessage(handle.obtainMessage(ON_MUSIC_LIST, it))
            }
        }

        override fun setPlayList(list: MutableList<Play>?) {
            list?.let {
                handle.sendMessage(handle.obtainMessage(ON_PLAY_LIST, it))
            }
        }

        override fun setRepeatMode(repeatMode: Int) {
            handle.sendMessage(handle.obtainMessage(ON_REPEAT_MODE, repeatMode, 0))
        }

        override fun setShuffleMode(shuffleMode: Boolean) {
            handle.sendMessage(handle.obtainMessage(ON_SHUFFLE_MODE, if (shuffleMode) 1 else 0, 0))
        }

        override fun sendAction(action: String?, bundle: Bundle?) {
            action?.let {
                val message = Message()
                message.what = ON_SEND_ACTION
                message.obj = it
                message.data = bundle
                handle.sendMessage(message)
            }
        }

        override fun registerCallback(callback: IPlayCallback?) {
            callback?.let {
                handle.sendMessage(handle.obtainMessage(ON_REGISTER_CALLBACK, it))
            }
        }

        override fun unregisterCallback(callback: IPlayCallback?) {
            callback?.let {
                handle.sendMessage(handle.obtainMessage(ON_UNREGISTER_CALLBACK, it))
            }
        }

    }

    companion object {
        const val ON_PLAY = 1
        const val ON_PAUSE = 2
        const val ON_PREVIOUS = 3
        const val ON_NEXT = 4
        const val ON_SEEK_TO = 5
        const val ON_DATA = 6
        const val ON_MUSIC_LIST = 7
        const val ON_PLAY_LIST = 8
        const val ON_REPEAT_MODE = 9
        const val ON_SHUFFLE_MODE = 10
        const val ON_SEND_ACTION = 11
        const val ON_REGISTER_CALLBACK = 12
        const val ON_UNREGISTER_CALLBACK = 13
    }
}
