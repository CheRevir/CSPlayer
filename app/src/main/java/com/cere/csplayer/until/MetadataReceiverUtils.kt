package com.cere.csplayer.until

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.ParcelFileDescriptor

/**
 * Created by CheRevir on 2020/9/8
 */
class MetadataReceiverUtils {

    fun getBitmap(fd: ParcelFileDescriptor): Bitmap? {
        val bytes = getBytes(fd)
        bytes?.let {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        return null
    }

    private fun getBytes(fd: ParcelFileDescriptor): ByteArray? {
        return getReceiver(fd).embeddedPicture
    }

    private fun getReceiver(fd: ParcelFileDescriptor): MediaMetadataRetriever {
        val receiver = MediaMetadataRetriever()
        receiver.setDataSource(fd.fileDescriptor)
        return receiver
    }
}