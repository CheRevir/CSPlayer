package com.cere.csplayer.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cere.csplayer.adapter.AlbumArtAdapter

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val adapter = MutableLiveData(AlbumArtAdapter(application))
    val isFirst = MutableLiveData(true)
}