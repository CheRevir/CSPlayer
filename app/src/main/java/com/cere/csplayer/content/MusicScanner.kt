package com.cere.csplayer.content

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.cere.csplayer.entity.Music

/**
 * Created by CheRevir on 2020/9/7
 */
class MusicScanner(private val context: Context, private val listener: OnMusicScanDoneListener) :
    AsyncTask<Void, Void, List<Music>>() {
    @Suppress("DEPRECATION")
    @SuppressLint("Recycle")
    override fun doInBackground(vararg params: Void?): List<Music> {
        val list = ArrayList<Music>()
        val parentKey =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Audio.Media.RELATIVE_PATH
            else MediaStore.Audio.Media.DATA
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            parentKey
        )
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Audio.Media._ID
        )?.let { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val duration =
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                var parent =
                    cursor.getString(cursor.getColumnIndex(parentKey))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    parent = parent.substring(0, parent.length - 1)
                } else {
                    parent = parent.replace(
                        Environment.getExternalStorageDirectory().absolutePath + "/",
                        ""
                    )
                    parent = parent.substring(0, parent.lastIndexOf("/"))
                }
                list.add(Music(id, title, artist, album, duration, 0f, parent))
            }
            cursor.close()
        }
        return list
    }

    override fun onPostExecute(result: List<Music>) {
        super.onPostExecute(result)
        listener.onMusicScanDone(result)
    }

    interface OnMusicScanDoneListener {
        fun onMusicScanDone(list: List<Music>)
    }
}