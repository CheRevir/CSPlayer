package com.cere.csplayer.content

import android.content.Context
import com.cere.csplayer.Constants
import com.cere.csplayer.OnPermissionCallback
import com.cere.csplayer.data.AppDatabase
import com.cere.csplayer.data.SharePre
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play
import com.cere.csplayer.until.PermissionUtils
import com.cere.csplayer.until.Utils
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by CheRevir on 2020/9/9
 */
class MusicManager(private val context: Context) :
    MusicScanner.OnMusicScanDoneListener {
    lateinit var musics: List<Music>
    lateinit var plays: List<Play>

    override fun onMusicScanDone(list: List<Music>) {
        if (list.isNotEmpty()) {
            Observable.create<List<Music>> { ob ->
                ob.onNext(Utils.deepCopy(musics))
            }.subscribeOn(Schedulers.newThread())
                .subscribe {
                    if (it.isEmpty()) {
                        GlobalScope.launch {
                            AppDatabase.instance.getMusicDao().insert(list)
                            AppDatabase.instance.getPlayDao().deleteAll()
                            AppDatabase.instance.getPlayDao().insert(
                                musicsToPlays(
                                    list,
                                    Play(list[0].id)
                                )
                            )
                        }
                    } else {
                        GlobalScope.launch {
                            val removeMusics = listCompare(list, it)
                            val removePlays = ArrayList<Play>(removeMusics.size)
                            removeMusics.forEach { music ->
                                removePlays.add(plays[plays.indexOf(Play(music.id))])
                            }
                            if (removeMusics.isNotEmpty()) AppDatabase.instance.getMusicDao()
                                .delete(removeMusics)
                            if (removePlays.isNotEmpty()) AppDatabase.instance.getPlayDao()
                                .delete(removePlays)
                        }
                        GlobalScope.launch {
                            val addMusics = listCompare(it, list)
                            val addPlays = ArrayList<Play>(addMusics.size)
                            addMusics.forEach { music ->
                                addPlays.add(Play(music.id))
                            }
                            if (addMusics.isNotEmpty()) AppDatabase.instance.getMusicDao()
                                .insert(addMusics)
                            if (addPlays.isNotEmpty()) AppDatabase.instance.getPlayDao()
                                .insert(addPlays)
                        }
                    }
                }
        }
    }

    suspend fun buildPlays(position: Int) {
        val list = musicsToPlays(musics, Play(position))
        AppDatabase.instance.getPlayDao().deleteAll()
        AppDatabase.instance.getPlayDao().insert(list)
    }

    private fun musicsToPlays(list: List<Music>, play: Play): List<Play> {
        val plays = ArrayList<Play>(list.size)
        list.forEach {
            plays.add(Play(it.id))
        }
        if (SharePre.getBoolean(Constants.MODE_SHUFFLE, false)) {
            plays.shuffle()
            plays[plays.indexOf(play)] = plays[0]
            plays[0] = play
        }
        return plays
    }

    private fun listCompare(list1: List<Music>, list2: List<Music>): List<Music> {
        val list = ArrayList<Music>()
        val map = HashMap<Int, Int>(list1.size + list2.size)
        list1.forEach {
            map[it.id] = 1
        }
        list2.forEach {
            if (map[it.id] != 1) {
                list.add(it)
            }
        }
        return list
    }

    @Suppress("DEPRECATION")
    fun scan() {
        if (XXPermissions.hasPermission(context, Permission.READ_EXTERNAL_STORAGE)) {
            MusicScanner(context, this).execute()
        } else {
            PermissionUtils.instance.getXXPermissions()
                ?.permission(Permission.READ_EXTERNAL_STORAGE)
                ?.request(
                    object : OnPermissionCallback(context) {
                        override fun hasPermission(granted: MutableList<String>, all: Boolean) {
                            scan()
                        }
                    })
        }
    }
}