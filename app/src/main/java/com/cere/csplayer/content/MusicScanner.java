package com.cere.csplayer.content;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.cere.csplayer.entity.Music;
import com.cere.csplayer.view.DialogProgress;

import java.util.ArrayList;

/**
 * Created by CheRevir on 2019/10/22
 */
public class MusicScanner extends AsyncTask<Void, Void, ArrayList<Music>> {
    private Context mContext;
    private OnScanDoneListener mOnScanDoneListener;
    private DialogProgress mDialogProgress;

    public MusicScanner(Context context, OnScanDoneListener onScanDoneListener) {
        this.mContext = context;
        this.mOnScanDoneListener = onScanDoneListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialogProgress = new DialogProgress(mContext);
        this.mDialogProgress.setMessage("正在扫描，请稍等...");
        this.mDialogProgress.setCancelable(false);
        this.mDialogProgress.show();
    }

    @Override
    protected ArrayList<Music> doInBackground(Void... voids) {
        ArrayList<Music> list = new ArrayList<>();
        String[] projection = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Audio.Media.TITLE);
        while (cursor.moveToNext()) {
            Music music = new Music();
            music.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            music.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            music.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            music.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            music.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            list.add(music);
        }
        cursor.close();
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<Music> list) {
        super.onPostExecute(list);
        mOnScanDoneListener.onScanDone(list);
        mDialogProgress.dismiss();
    }

    public interface OnScanDoneListener {
        void onScanDone(ArrayList<Music> list);
    }
}
