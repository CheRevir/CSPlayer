package com.cere.csplayer.glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import java.nio.ByteBuffer;

/**
 * Created by CheRevir on 2019/10/27
 */
public class MetaDataGlidModelLoader implements ModelLoader<String, ByteBuffer> {
    public static class Factory implements ModelLoaderFactory<String, ByteBuffer> {
        @NonNull
        @Override
        public ModelLoader<String, ByteBuffer> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new MetaDataGlidModelLoader();
        }

        @Override
        public void teardown() {

        }
    }

    @Nullable
    @Override
    public LoadData<ByteBuffer> buildLoadData(@NonNull String s, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(s),new MetaDataFetcher(s));
    }

    @Override
    public boolean handles(@NonNull String s) {
        return true;
    }
}
