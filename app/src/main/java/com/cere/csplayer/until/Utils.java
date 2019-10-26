package com.cere.csplayer.until;

/**
 * Created by CheRevir on 2019/10/24
 */
public class Utils {
    public static String timeToString(long time) {
        long second = time / 1000;
        long minute = second / 60;
        long hour = minute / 60;
        second %= 60;
        minute %= 60;
        if (hour > 0) {
            return String.format("%2d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }
    }
}
