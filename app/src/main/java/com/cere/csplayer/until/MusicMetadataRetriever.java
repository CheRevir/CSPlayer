package com.cere.csplayer.until;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by CheRevir on 2019/10/22
 */
public class MusicMetadataRetriever {
    private static MusicMetadataRetriever mInstance = new MusicMetadataRetriever();
    private MediaMetadataRetriever mRetriever;

    private MusicMetadataRetriever() {
        mRetriever = new MediaMetadataRetriever();
    }

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
        if (!path.isEmpty()) {
            File file = new File(path);
            FileInputStream input = null;
            if (file.exists()) {
                try {
                    input = new FileInputStream(file);
                    mRetriever.setDataSource(input.getFD());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return mRetriever.getEmbeddedPicture();
            }
        }
        return null;
    }
}
