package com.cere.csplayer.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cere.csplayer.Constants
import com.cere.csplayer.content.MusicManager
import com.cere.csplayer.control.PlayCallback
import com.cere.csplayer.control.PlayControl
import com.cere.csplayer.data.AppDatabase
import com.cere.csplayer.data.SharePre
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by CheRevir on 2020/9/5
 */
class PlayViewModel(val control: PlayControl, val manager: MusicManager) :
    ViewModel(),
    PlayControl.OnConnectionListener {
    val musics =
        AppDatabase.instance.getMusicDao().queryAll(SharePre.getInt(Constants.LIST_ORDER, 1))
    val plays = AppDatabase.instance.getPlayDao().queryAll()

    private var isFirst = true
    private var isScan = false
    private var isLoad = false
    var isReload = false

    val id = MutableLiveData<Int>()
    val isPlay = MutableLiveData(false)
    val title = MutableLiveData<String>()
    val artist = MutableLiveData<String>()
    val album = MutableLiveData<String>()
    val duration = MutableLiveData<Int>()
    val current = MutableLiveData<Int>()
    val star = MutableLiveData<Float>()
    val position = MutableLiveData<Int>()
    val number = MutableLiveData<String>()

    val repeat = MutableLiveData(SharePre.getInt(Constants.MODE_REPEAT, 0))
    val shuffle = MutableLiveData(SharePre.getBoolean(Constants.MODE_SHUFFLE, false))

    init {
        control.addListener(this)
        musics.observeForever {
            manager.musics = it
            scan()
        }
        plays.observeForever {
            manager.plays = it
            scan()
            if (isReload) {
                isReload = false
                load()
            }
        }
    }

    override fun onConnectChange(isConnected: Boolean) {
        if (isConnected) {
            control.registerCallback(Callback().callback)
            Log.e("TAG", "PlayViewModel -> onServiceConnected: ")
            load()
        } else {
            Log.e("TAG", "PlayViewModel -> onServiceDisconnected: ")
            isFirst = true
            isScan = false
            isLoad = false
        }
    }

    private fun scan() {
        if (isFirst) {
            if (isScan) {
                isFirst = false
                isScan = false
                manager.scan()
                load()
            } else {
                isScan = true
            }
        }
    }

    private fun load() {
        if (isLoad) {
            val position = SharePre.getInt(Constants.MUSIC_POSITION, 0)
            if (plays.value!!.size > position) {
                this.position.value = position
                val id = plays.value!![position].id
                this.id.value = id
            } else {
                this.position.value = 0
            }
        } else {
            isLoad = true
        }
    }

    fun previous() {
        if (plays.value!!.isNotEmpty()) {
            var index = position.value!!
            if (index > 0) index--
            else {
                index = plays.value!!.size - 1
            }
            position.value = index
        }
    }

    fun next() {
        if (plays.value!!.isNotEmpty()) {
            var index = position.value!!
            if (index <= plays.value!!.size - 2) index++
            else {
                index = 0
            }
            position.value = index
        }
    }

    private inner class Callback : PlayCallback() {
        override fun onPlay(isPlay: Boolean) {
            super.onPlay(isPlay)
            this@PlayViewModel.isPlay.value = isPlay
        }

        override fun onData(id: Int) {
            super.onData(id)
            GlobalScope.launch {
                val music = AppDatabase.instance.getMusicDao().query(id)
                music?.let {
                    title.postValue(it.title)
                    artist.postValue(it.artist)
                    album.postValue(it.album)
                    star.postValue(it.star)
                    number.postValue("${position.value!! + 1}/${plays.value?.size}")
                }
            }
            this@PlayViewModel.id.value = id
            SharePre.putInt(Constants.MUSIC_POSITION, position.value!!)
        }
    }

    override fun onCleared() {
        super.onCleared()
        control.removeListener(this)
    }
}