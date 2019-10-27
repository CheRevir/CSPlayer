package com.cere.csplayer;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.cere.csplayer.glide.MetaDataGlidModelLoader;

import java.nio.ByteBuffer;

/**
 * Created by CheRevir on 2019/10/27
 */
@GlideModule(glideName = "GlideApp")
public class MyGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.replace(String.class, ByteBuffer.class, new MetaDataGlidModelLoader.Factory());
    }
}
