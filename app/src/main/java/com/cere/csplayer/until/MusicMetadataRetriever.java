package com.cere.csplayer.until;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

/**
 * Created by CheRevir on 2019/10/22
 */
public class MusicMetadataRetriever {
    private static MusicMetadataRetriever mInstance = new MusicMetadataRetriever();

    public static MusicMetadataRetriever getInstance() {
        return mInstance;
    }

    public Bitmap getAlbumArt(String path) {
        byte[] bytes = getBytes(path);
        if (bytes != null) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    public byte[] getBytes(String path) {
        return getMetadataRetriever(path).getEmbeddedPicture();
    }

    public MediaMetadataRetriever getMetadataRetriever(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        return retriever;
    }
}
