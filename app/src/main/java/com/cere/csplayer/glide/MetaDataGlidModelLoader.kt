package com.cere.csplayer.glide

import android.os.ParcelFileDescriptor
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import java.nio.ByteBuffer

/**
 * Created by CheRevir on 2019/10/27
 */
class MetaDataGlidModelLoader : ModelLoader<ParcelFileDescriptor, ByteBuffer> {
    class Factory : ModelLoaderFactory<ParcelFileDescriptor, ByteBuffer> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<ParcelFileDescriptor, ByteBuffer> {
            return MetaDataGlidModelLoader()
        }

        override fun teardown() {}
    }

    /*override fun buildLoadData(s: String, width: Int, height: Int, options: Options): LoadData<ByteBuffer> {
        return LoadData(ObjectKey(s), MetaDataFetcher(s))
    }*/

    override fun buildLoadData(
        model: ParcelFileDescriptor,
        width: Int,
        height: Int,
        options: Options
    ): LoadData<ByteBuffer>? {
        return LoadData(ObjectKey(model), MetaDataFetcher(model))
    }

    override fun handles(model: ParcelFileDescriptor): Boolean {
        return true
    }
}