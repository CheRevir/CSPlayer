package com.cere.csplayer.content

import android.content.Context
import android.os.Handler
import com.cere.csplayer.R
import com.cere.csplayer.activity.MainActivity
import com.cere.csplayer.data.SQLite
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play
import com.cere.csplayer.util.DialogUtils
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.litesuits.orm.db.assit.WhereBuilder

/**
 * Created by CheRevir on 2019/10/22
 */
class MusicManager(context: Context) : MusicScanner.OnScanDoneListener, PlayBuilder.OnBuilderDoneListener {
    private val mContext: Context = context
    private val mHandler: Handler
    private val mOnLoadDoneListener: OnLoadDoneListener
    var musics: ArrayList<Music>? = null
        private set
    var plays: ArrayList<Play>? = null
        private set

    init {
        mOnLoadDoneListener = context as MainActivity
        mHandler = Handler(context.mainLooper)
    }

    fun load() {
        val permissions = arrayOf(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.SYSTEM_ALERT_WINDOW)
        if (XXPermissions.isHasPermission(mContext, *permissions)) {
            musics = SQLite.getLiteOrm(mContext).query(Music::class.java)
            if (musics!!.isNotEmpty()) {
                plays = SQLite.getLiteOrm(mContext).query(Play::class.java)
                mOnLoadDoneListener.onMusicLoadDone(musics!!)
                if (plays!!.isNotEmpty()) {
                    mOnLoadDoneListener.onPlayLoadDone(plays!!)
                } else {
                    reBuildPlays()
                }
            } else {
                reScanMusics()
            }
        } else {
            XXPermissions.with(mContext as MainActivity).constantRequest().permission(*permissions).request(object : OnPermission {
                override fun hasPermission(granted: List<String>, isAll: Boolean) {
                    if (isAll) {
                        load()
                    }
                }

                override fun noPermission(denied: List<String>, quick: Boolean) {
                    DialogUtils.getAlertDialog(mContext, "提示", "没有权限将无法扫描音乐文件。", R.mipmap.ic_launcher)
                            .setPositiveButton("去设置打开") { _, _ -> XXPermissions.gotoPermissionSettings(mContext) }.show()
                }
            })
        }
    }

    override fun onScanDone(list: ArrayList<Music>) {
        if (list.isNotEmpty()) {
            musics = list
            mOnLoadDoneListener.onMusicLoadDone(list)
            reBuildPlays()
            mHandler.post {
                SQLite.getLiteOrm(mContext).delete(Music::class.java)
                SQLite.getLiteOrm(mContext).save(musics)
            }
        } else {
            mOnLoadDoneListener.onMusicLoadDone(list)
            DialogUtils.getAlertDialog(mContext, "提示", "没有扫描到音乐文件。", R.mipmap.ic_launcher).setPositiveButton("确定", null).show()
        }
    }

    override fun onBuildDone(list: ArrayList<Play>) {
        plays = list
        mOnLoadDoneListener.onPlayLoadDone(list)
        mHandler.post {
            SQLite.getLiteOrm(mContext).delete(Play::class.java)
            SQLite.getLiteOrm(mContext).save(list)
        }
    }

    fun reScanMusics() {
        MusicScanner(mContext, this).execute()
    }

    fun reBuildPlays() {
        val play = Play()
        val data = (mContext as MainActivity).data
        if (plays != null && data!!.isNotEmpty()) {
            play.position = getPosition(data)
        } else {
            play.position = 0
        }
        PlayBuilder(mContext, this, play).execute(musics)
    }

    fun deleteMusic(path: String) {
        val music = Music()
        music.path = path
        musics!!.remove(music)
        mHandler.post {
            val builder = WhereBuilder(Music::class.java)
            builder.equals("path", path)
            SQLite.getLiteOrm(mContext).delete(builder)
        }
    }

    fun deletePlay(position: Int) {
        val play = Play()
        play.position = position
        plays!!.removeAt(position)
        mHandler.post {
            val builder = WhereBuilder(Play::class.java)
            builder.equals("position", position)
            SQLite.getLiteOrm(mContext).delete(builder)
        }
    }

    fun getPosition(path: String): Int {
        val music = Music()
        music.path = path
        return musics!!.indexOf(music)
    }

    fun getPosition(position: Int): Int {
        val play = Play()
        play.position = position
        return plays!!.indexOf(play)
    }

    fun getMusic(path: String): Music {
        return musics!![getPosition(path)]
    }

    fun getPlay(position: Int): Play {
        return plays!![position]
    }

    interface OnLoadDoneListener {
        fun onMusicLoadDone(list: ArrayList<Music>)
        fun onPlayLoadDone(list: ArrayList<Play>)
    }
}