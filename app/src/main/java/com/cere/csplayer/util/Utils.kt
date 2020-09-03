package com.cere.csplayer.util

/**
 * Created by CheRevir on 2019/10/24
 */
object Utils {
    @JvmStatic
    fun timeToString(time: Long): String {
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
}