package com.cere.csplayer.until;

import java.io.File;

/**
 * Created by CheRevir on 2019/10/24
 */
public class FileUtils {
    public static boolean exists(String path) {
        return new File(path).exists();
    }
}
