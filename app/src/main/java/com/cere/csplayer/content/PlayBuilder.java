package com.cere.csplayer.content;

import android.content.Context;
import android.os.AsyncTask;

import com.cere.csplayer.Constants;
import com.cere.csplayer.data.SharedPre;
import com.cere.csplayer.entity.Music;
import com.cere.csplayer.entity.Play;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by CheRevir on 2019/10/22
 */
public class PlayBuilder extends AsyncTask<ArrayList<Music>, Void, ArrayList<Play>> {
    private Context mContext;
    private OnBuilderDoneListener mOnBuilderDoneListener;
    private Play mPlay;

    public PlayBuilder(Context context, OnBuilderDoneListener onBuilderDoneListener, Play play) {
        this.mContext = context;
        this.mOnBuilderDoneListener = onBuilderDoneListener;
        this.mPlay = play;
    }

    @Override
    protected ArrayList<Play> doInBackground(ArrayList<Music>... arrayLists) {
        ArrayList<Play> list = new ArrayList<>();
        for (int n = 0; n < arrayLists[0].size(); n++) {
            Play play = new Play();
            play.setPosition(n);
            list.add(play);
        }
        if (SharedPre.getBoolean(mContext, Constants.MODE_SHUFFLE, false)) {
            Collections.shuffle(list);
            list.get(list.indexOf(mPlay)).setPosition(list.get(0).getPosition());
            list.get(0).setPosition(mPlay.getPosition());
            SharedPre.putInt(mContext, Constants.PLAY_INDEX, 0);
        } else {
            SharedPre.putInt(mContext, Constants.PLAY_INDEX, list.indexOf(mPlay));
        }
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<Play> plays) {
        super.onPostExecute(plays);
        mOnBuilderDoneListener.onBuildDone(plays);
    }

    public interface OnBuilderDoneListener {
        void onBuildDone(ArrayList<Play> list);
    }
}
