package com.cere.csplayer.activity

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cere.csplayer.content.MusicManager
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play

/**
 * Created by CheRevir on 2020/9/3
 */
class MainViewModel : ViewModel(), MusicManager.OnLoadDoneListener {
    private lateinit var context: Context
    private lateinit var musicManager: MusicManager

    private val musics: MutableLiveData<List<Music>>
    private val plays: MutableLiveData<List<Play>>

    init {
        musics = MutableLiveData<List<Music>>()
        plays = MutableLiveData<List<Play>>()
    }

    override fun onMusicLoadDone(list: ArrayList<Music>) {
        musics.value = list
    }

    override fun onPlayLoadDone(list: ArrayList<Play>) {
        plays.value = list
    }
}