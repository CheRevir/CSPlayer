package com.cere.csplayer.until

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Created by CheRevir on 2020/9/11
 */
object Utils {
    @JvmStatic
    fun timeToString(time: Int): String {
        var second = time / 1000
        var minute = second / 60
        val hour = minute / 60
        second %= 60
        minute %= 60
        return if (hour > 0) {
            String.format("%2d:%02d:%02d", hour, minute, second)
        } else {
            String.format("%02d:%02d", minute, second)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <T> deepCopy(src: List<T>): List<T> {
        val byteOut = ByteArrayOutputStream()
        val out = ObjectOutputStream(byteOut)
        out.writeObject(src)

        val byteIn = ByteArrayInputStream(byteOut.toByteArray())
        val oIn = ObjectInputStream(byteIn)
        return oIn.readObject() as List<T>
    }
}