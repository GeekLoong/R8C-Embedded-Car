package net.kuisec.r8c.Utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jinsn
 * @date 2022/10/13 19:27
 */
public class TimeUtil {
    public static long getMsTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date(System.currentTimeMillis());
        return Long.parseLong(simpleDateFormat.format(date));
    }

    public static String getLifeTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}
