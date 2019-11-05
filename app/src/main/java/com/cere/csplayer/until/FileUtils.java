package com.cere.csplayer.until;

import java.io.File;

/**
 * Created by CheRevir on 2019/10/24
 */
public class FileUtils {
    public static boolean isExists(String path) {
        return new File(path).exists();
    }

    public static void delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
