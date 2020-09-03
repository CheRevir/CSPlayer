package com.cere.csplayer.util

import java.io.File

/**
 * Created by CheRevir on 2019/10/24
 */
object FileUtils {

    @JvmStatic
    fun isExists(path: String): Boolean {
        return File(path).exists()
    }

    @JvmStatic
    fun delete(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

}