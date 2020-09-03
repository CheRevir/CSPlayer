package com.cere.csplayer.glide

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
class MetaDataGlidModelLoader : ModelLoader<String, ByteBuffer> {
    class Factory : ModelLoaderFactory<String, ByteBuffer> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, ByteBuffer> {
            return MetaDataGlidModelLoader()
        }

        override fun teardown() {}
    }

    override fun buildLoadData(s: String, width: Int, height: Int, options: Options): LoadData<ByteBuffer> {
        return LoadData(ObjectKey(s), MetaDataFetcher(s))
    }

    override fun handles(s: String): Boolean {
        return true
    }
}