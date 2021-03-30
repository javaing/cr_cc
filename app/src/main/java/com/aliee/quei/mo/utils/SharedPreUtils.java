package com.aliee.quei.mo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.aliee.quei.mo.application.ReaderApplication;

/**
 * Created by newbiechen on 17-4-16.
 */

public class SharedPreUtils {
    private static final String SHARED_NAME = "IReader_pref";
    private static SharedPreUtils sInstance;
    private SharedPreferences sharedReadable;
    private SharedPreferences.Editor sharedWritable;

    private static final String SHARED_SHARE_COUNT = "share.count";
    private static final String SHARED_REWARD_TOTAL = "share.reward.total";
    private static final String SHARED_REWARD_TODAY = "share.reward.today";


    private static final String IS_FIRST_OPEN = "app.open";

    private SharedPreUtils() {
        sharedReadable = ReaderApplication.Companion.getInstance()
                .getSharedPreferences(SHARED_NAME, Context.MODE_MULTI_PROCESS);
        sharedWritable = sharedReadable.edit();
    }

    public static SharedPreUtils getInstance() {
        if (sInstance == null) {
            synchronized (SharedPreUtils.class) {
                if (sInstance == null) {
                    sInstance = new SharedPreUtils();
                }
            }
        }
        return sInstance;
    }

    public void putLong(String key, long value) {
        sharedWritable.putLong(key, value)
                .commit();
    }

    public long getLong(String key) {
        return sharedReadable.getLong(key, 0);
    }

    public String getString(String key) {
        return sharedReadable.getString(key, "");
    }

    public void clearAll() {
        sharedWritable.clear().commit();
    }

    public void putString(String key, String value) {
        sharedWritable.putString(key, value);
        sharedWritable.commit();
    }

    public void putInt(String key, int value) {
        sharedWritable.putInt(key, value);
        sharedWritable.commit();
    }

    public void putBoolean(String key, boolean value) {
        sharedWritable.putBoolean(key, value);
        sharedWritable.commit();
    }

    public int getInt(String key, int def) {
        return sharedReadable.getInt(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return sharedReadable.getBoolean(key, def);
    }

    public void setDailyShareCount(int shareDisplay) {
        sharedWritable.putInt(SHARED_SHARE_COUNT, shareDisplay).commit();
    }

    public int getDailyShareCount() {
        return sharedReadable.getInt(SHARED_SHARE_COUNT, 0);
    }

    public void setShareRewardTotal(int total) {
        sharedWritable.putInt(SHARED_REWARD_TOTAL, total).commit();
    }

    public int getShareRewardTotal() {
        return sharedReadable.getInt(SHARED_REWARD_TOTAL, 0);
    }

    public void setShareRewardToday(int total) {
        sharedWritable.putInt(SHARED_REWARD_TODAY, total).commit();
    }

    public int getShareRewardToday() {
        return sharedReadable.getInt(SHARED_REWARD_TODAY, 0);
    }

    public void setSaveFirstOpen(boolean b) {
        this.sharedWritable.putBoolean(IS_FIRST_OPEN, b);
    }

    public boolean getSaveFirstOpen() {
        return sharedReadable.getBoolean(IS_FIRST_OPEN, true);
    }
}
