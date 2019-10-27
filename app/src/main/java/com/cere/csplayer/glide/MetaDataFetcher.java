package com.cere.csplayer.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.cere.csplayer.until.MusicMetadataRetriever;

import java.nio.ByteBuffer;

/**
 * Created by CheRevir on 2019/10/27
 */
public class MetaDataFetcher implements DataFetcher<ByteBuffer> {
    private String path;

    public MetaDataFetcher(String path) {
        this.path = path;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super ByteBuffer> callback) {
        callback.onDataReady(ByteBuffer.wrap(MusicMetadataRetriever.getInstance().getBytes(path)));
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class<ByteBuffer> getDataClass() {
        return ByteBuffer.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
