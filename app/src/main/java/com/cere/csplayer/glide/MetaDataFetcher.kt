package com.cere.csplayer.glide

import android.os.ParcelFileDescriptor
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.cere.csplayer.until.MetadataReceiverUtils
import java.nio.ByteBuffer

/**
 * Created by CheRevir on 2019/10/27
 */
class MetaDataFetcher(private val fd: ParcelFileDescriptor) : DataFetcher<ByteBuffer> {
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
        MetadataReceiverUtils().getBytes(fd)?.let {
            callback.onDataReady(ByteBuffer.wrap(it))
        } ?: run {
            callback.onDataReady(ByteBuffer.wrap(ByteArray(0)))
        }
    }

    override fun cleanup() {}
    override fun cancel() {}
    override fun getDataClass(): Class<ByteBuffer> {
        return ByteBuffer::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }
}