package com.cere.csplayer.activity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cere.csplayer.Constants
import com.cere.csplayer.content.MusicManager
import com.cere.csplayer.control.PlayCallback
import com.cere.csplayer.control.PlayControl
import com.cere.csplayer.data.AppDatabase
import com.cere.csplayer.data.SharePre
import com.cere.csplayer.service.PlayService

/**
 * Created by CheRevir on 2020/9/5
 */
class PlayViewModel(application: Application) : AndroidViewModel(application),
    PlayControl.OnServiceConnectionListener {
    val control = PlayControl(application, PlayService::class.java, this)
    val musics = AppDatabase.instance.getMusicDao().queryAll()
    val plays = AppDatabase.instance.getPlayDao().queryAll()

    val manager = MusicManager(application)
    private var isFirst = true
    private var isScan = false

    val id = MutableLiveData<Int>()
    val isPlay = MutableLiveData<Boolean>()
    val title = MutableLiveData<String>()
    val artist = MutableLiveData<String>()
    val album = MutableLiveData<String>()
    val duration = MutableLiveData<Int>()
    val current = MutableLiveData<Int>()
    val star = MutableLiveData(0f)
    val position = MutableLiveData(SharePre.getInt(Constants.MUSIC_POSITION, 0))

    init {
        musics.observeForever {
            manager.musics = it
            scan()
        }
        plays.observeForever {
            manager.plays = it
            scan()
        }
    }

    private fun scan() {
        if (isFirst) {
            if (isScan) {
                isFirst = false
                isScan = false
                manager.scan()
            } else {
                isScan = true
            }
        }
    }

    override fun onServiceConnected() {
        control.registerCallback(Callback().callback)
        Log.e("TAG", "PlayViewModel -> onServiceConnected: ")
    }

    override fun onServiceDisconnected() {
        Log.e("TAG", "PlayViewModel -> onServiceDisconnected: ")
    }

    private inner class Callback : PlayCallback() {
        override fun onPlay(isPlay: Boolean) {
            super.onPlay(isPlay)
            this@PlayViewModel.isPlay.value = isPlay
        }
    }
}