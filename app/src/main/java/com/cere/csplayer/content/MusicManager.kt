package com.cere.csplayer.content

import android.util.Log
import com.cere.csplayer.Constants
import com.cere.csplayer.OnPermissionCallback
import com.cere.csplayer.activity.PlayViewModel
import com.cere.csplayer.data.AppDatabase
import com.cere.csplayer.data.SharePre
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play
import com.cere.csplayer.until.PermissionUtils
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Created by CheRevir on 2020/9/9
 */
class MusicManager(private val viewModel: PlayViewModel) :
    MusicScanner.OnMusicScanDoneListener {
    private var isFirst = true

    init {
        GlobalScope.launch {
            viewModel.musics.postValue(AppDatabase.instance.getMusicDao().query())
            viewModel.plays.postValue(AppDatabase.instance.getPlayDao().query())
            if (isFirst) {
                isFirst = false
                scan()
            }
        }
    }

    override fun onMusicScanDone(list: List<Music>) {
        viewModel.musics.value?.let {
            if (list.isNotEmpty()) {
                Observable.create<List<Music>> {
                    it.onNext(deepCopy(viewModel.musics.value!!))
                }.subscribeOn(Schedulers.newThread())
                    .subscribe {
                        val remove = listCompare(list, it)
                        GlobalScope.launch {
                            remove.forEach {
                                Log.e(
                                    "TAG",
                                    "MusicManager -> onMusicScanDone: Remove: $it"
                                )
                            }
                            if (remove.isNotEmpty()) AppDatabase.instance.getMusicDao()
                                .delete(remove)
                        }
                        val add = listCompare(it, list)
                        GlobalScope.launch {
                            add.forEach {
                                Log.e(
                                    "TAG",
                                    "MusicManager -> onMusicScanDone: Add: $it"
                                )
                            }
                            if (add.isNotEmpty()) AppDatabase.instance.getMusicDao().insert(add)
                        }
                    }

                list.toObservable().subscribeOn(Schedulers.newThread()).toList()
                    .subscribe(Consumer {
                        viewModel.musics.postValue(it)
                        viewModel.plays.value?.let { plays ->
                            if (plays.isEmpty()) {
                                GlobalScope.launch {
                                    val l = musicsToPlays(it, Play(0))
                                    viewModel.plays.postValue(l)
                                    AppDatabase.instance.getPlayDao().deleteAll()
                                    AppDatabase.instance.getPlayDao().insert(l)
                                }
                            } else {

                            }
                        }
                    })
            }
        }
    }

    fun musicsToPlays(list: List<Music>, play: Play): List<Play> {
        val plays = ArrayList<Play>(list.size)
        for (i in list.indices) {
            plays.add(Play(i))
        }
        if (SharePre.getBoolean(Constants.MODE_SHUFFLE, false)) {
            plays.shuffled()
            plays[plays.indexOf(play)].position == plays[0].position
            plays[0].position = play.position
            SharePre.putInt(Constants.MUSIC_POSITION, 0)
        } else {
            SharePre.putInt(Constants.MUSIC_POSITION, plays.indexOf(play))
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
        if (XXPermissions.hasPermission(
                viewModel.getApplication(),
                Permission.READ_EXTERNAL_STORAGE
            )
        ) {
            MusicScanner(viewModel.getApplication(), this).execute()
        } else {
            PermissionUtils.instance.getXXPermissions()
                ?.permission(Permission.READ_EXTERNAL_STORAGE)
                ?.request(object : OnPermissionCallback(PermissionUtils.instance.activity!!) {
                    override fun hasPermission(granted: MutableList<String>, all: Boolean) {
                        scan()
                    }
                })
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> deepCopy(src: List<T>): List<T> {
        val byteOut = ByteArrayOutputStream()
        val out = ObjectOutputStream(byteOut)
        out.writeObject(src)

        val byteIn = ByteArrayInputStream(byteOut.toByteArray())
        val oIn = ObjectInputStream(byteIn)
        return oIn.readObject() as List<T>
    }
}