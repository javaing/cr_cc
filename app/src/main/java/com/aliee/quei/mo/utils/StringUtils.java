package com.aliee.quei.mo.utils;

import android.support.annotation.StringRes;

import com.aliee.quei.mo.application.ReaderApplication;

import java.util.Locale;

/**
 * Created by newbiechen on 17-4-22.
 * 对文字操作的工具类
 */

public class StringUtils {

    public static String getString(@StringRes int id){
        return ReaderApplication.Companion.getInstance().getResources().getString(id);
    }

    public static String getString(@StringRes int id, Object... formatArgs){
        return ReaderApplication.Companion.getInstance().getResources().getString(id,formatArgs);
    }

    public static String secToTime(int seconds) {
        int hour = seconds / 3600;
        int minute = (seconds - hour * 3600) / 60;
        int second = (seconds - hour * 3600 - minute * 60);

        StringBuffer sb = new StringBuffer();
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
        if (second == 0) {
            sb.append("<1秒");
        }
        return sb.toString();
    }

    public static String formatTime(int time){
        int totalSeconds = time / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }
}
