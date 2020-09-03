package com.cere.csplayer.content

import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.provider.MediaStore
import com.cere.csplayer.entity.Music
import com.cere.csplayer.view.DialogProgress

/**
 * Created by CheRevir on 2019/10/22
 */
class MusicScanner(private val mContext: Context, private val mOnScanDoneListener: OnScanDoneListener) : AsyncTask<Void, Void, ArrayList<Music>>() {
    private lateinit var mDialogProgress: DialogProgress
    override fun onPreExecute() {
        super.onPreExecute()
        mDialogProgress = DialogProgress(mContext)
        mDialogProgress.setMessage("正在扫描，请稍等...")
        mDialogProgress.setCancelable(false)
        mDialogProgress.show()
    }

    @Suppress("DEPRECATION")
    override fun doInBackground(vararg voids: Void): ArrayList<Music> {
        val list = ArrayList<Music>()
        val projection = arrayOf(MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA)
        val cursor: Cursor? = mContext.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Audio.Media.TITLE)
        cursor?.let {
            while (cursor.moveToNext()) {
                val music = Music()
                music.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                music.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                music.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                music.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                music.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                list.add(music)
            }
            cursor.close()
        }
        return list
    }

    override fun onPostExecute(list: ArrayList<Music>) {
        super.onPostExecute(list)
        mOnScanDoneListener.onScanDone(list)
        mDialogProgress.dismiss()
    }

    interface OnScanDoneListener {
        fun onScanDone(list: ArrayList<Music>)
    }
}