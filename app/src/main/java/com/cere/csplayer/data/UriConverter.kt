package com.cere.csplayer.data

import android.net.Uri
import androidx.room.TypeConverter

/**
 * Created by CheRevir on 2020/9/7
 */
class UriConverter {
    @TypeConverter
    fun revertUri(path: String) = Uri.parse(path)

    @TypeConverter
    fun convertString(uri: Uri) = uri.toString()
}