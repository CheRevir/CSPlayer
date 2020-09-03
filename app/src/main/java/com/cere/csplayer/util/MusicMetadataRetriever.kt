package com.cere.csplayer.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

/**
 * Created by CheRevir on 2019/10/22
 */
class MusicMetadataRetriever private constructor() {
    companion object {
        val instance: MusicMetadataRetriever by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { MusicMetadataRetriever() }
    }

    fun getAlbumArt(path: String): Bitmap? {
        val bytes = getBytes(path)
        return if (bytes != null) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else null
    }

    fun getBytes(path: String): ByteArray? {
        return getMetadataRetriever(path).embeddedPicture
    }

    fun getMetadataRetriever(path: String): MediaMetadataRetriever {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever
    }
}