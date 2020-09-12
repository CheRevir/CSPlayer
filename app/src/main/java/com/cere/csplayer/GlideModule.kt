package com.cere.csplayer

import android.content.Context
import android.os.ParcelFileDescriptor
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.cere.csplayer.glide.MetaDataGlidModelLoader
import java.nio.ByteBuffer

/**
 * Created by CheRevir on 2020/9/12
 */
@GlideModule()
class GlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.append(
            ParcelFileDescriptor::class.java,
            ByteBuffer::class.java,
            MetaDataGlidModelLoader.Factory()
        )
    }
}