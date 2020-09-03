package com.cere.csplayer.content

import android.content.Context
import android.os.AsyncTask
import com.cere.csplayer.Constants
import com.cere.csplayer.data.SharedPre.getBoolean
import com.cere.csplayer.data.SharedPre.putInt
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play
import java.util.*

/**
 * Created by CheRevir on 2019/10/22
 */
class PlayBuilder(private val mContext: Context, private val mOnBuilderDoneListener: OnBuilderDoneListener, private val mPlay: Play) : AsyncTask<ArrayList<Music>, Void, ArrayList<Play>>() {
    override fun doInBackground(vararg arrayLists: ArrayList<Music>): ArrayList<Play> {
        val list = ArrayList<Play>()
        for (n in arrayLists[0].indices) {
            val play = Play()
            play.position = n
            list.add(play)
        }
        if (getBoolean(mContext, Constants.MODE_SHUFFLE, false)) {
            list.shuffle()
            list[list.indexOf(mPlay)].position = list[0].position
            list[0].position = mPlay.position
            putInt(mContext, Constants.PLAY_INDEX, 0)
        } else {
            putInt(mContext, Constants.PLAY_INDEX, list.indexOf(mPlay))
        }
        return list
    }

    override fun onPostExecute(plays: ArrayList<Play>) {
        super.onPostExecute(plays)
        mOnBuilderDoneListener.onBuildDone(plays)
    }

    interface OnBuilderDoneListener {
        fun onBuildDone(list: ArrayList<Play>)
    }
}