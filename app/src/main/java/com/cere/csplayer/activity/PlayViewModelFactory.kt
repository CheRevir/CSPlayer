package com.cere.csplayer.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cere.csplayer.content.MusicManager
import com.cere.csplayer.control.PlayControl

/**
 * Created by CheRevir on 20-10-11
 */
class PlayViewModelFactory(private val control: PlayControl, private val manager: MusicManager) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayViewModel::class.java)) {
            return PlayViewModel(control, manager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}