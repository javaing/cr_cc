package com.aliee.quei.mo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.aliee.quei.mo.application.ReaderApplication;

/**
 * SharedPreferences工具类
 *
 * @auther var_rain.
 * @date 2018/5/31.
 */
public class SharedUtils {

    /*声明数据储存器*/
    private static SharedPreferences preferences;
    /*声明数据储存编辑器*/
    private static SharedPreferences.Editor editor;

    /**
     * 获取编辑器
     *
     * @return 返回一个SharedPreferences.Editor对象
     */
    private static SharedPreferences.Editor getEditor() {
        if (editor == null) {
            return SharedUtils.getPreferences().edit();
        } else {
            return SharedUtils.editor;
        }
    }

    /**
     * 获取数据储存器
     *
     * @return 返回一个SharedPreferences对象
     */
    private static SharedPreferences getPreferences() {
        if (preferences == null) {
            return ReaderApplication.instance.getSharedPreferences("app_data", Context.MODE_PRIVATE);
        } else {
            return SharedUtils.preferences;
        }
    }

    /**
     * 写入字符串数据
     *
     * @param key   键
     * @param value 值
     */
    public static void putString(String key, String value) {
        SharedUtils.getEditor().putString(key, value).apply();
    }

    /**
     * 写入boolean数据
     *
     * @param key   键
     * @param value 值
     */
    public static void putBoolean(String key, boolean value) {
        SharedUtils.getEditor().putBoolean(key, value).apply();
    }

    /**
     * 写入整型数据
     *
     * @param key   键
     * @param value 值
     */
    public static void putInt(String key, int value) {
        SharedUtils.getEditor().putInt(key, value).apply();
    }

    /**
     * 写入长整型数据
     *
     * @param key   键
     * @param value 值
     */
    public static void putLong(String key, long value) {
        SharedUtils.getEditor().putLong(key, value).apply();
    }

    /**
     * 写入浮点类型数据
     *
     * @param key   键
     * @param value 值
     */
    public static void putFloat(String key, float value) {
        SharedUtils.getEditor().putFloat(key, value).apply();
    }

    /**
     * 取出字符串数据
     *
     * @param key 键
     * @param def 默认值
     * @return 如果有数据则返回对应数据, 没有则返回默认值
     */
    public static String getString(String key, String def) {
        return SharedUtils.getPreferences().getString(key, def);
    }

    /**
     * 取出Boolean型数据
     *
     * @param key 键
     * @param def 默认值
     * @return 如果有数据则返回对应数据, 没有则返回默认值
     */
    public static boolean getBoolean(String key, boolean def) {
        return SharedUtils.getPreferences().getBoolean(key, def);
    }

    /**
     * 取出整型数据
     *
     * @param key 键
     * @param def 默认值
     * @return 如果有数据则返回对应数据, 没有则返回默认值
     */
    public static int getInt(String key, int def) {
        return SharedUtils.getPreferences().getInt(key, def);
    }

    /**
     * 取出长整型数据
     *
     * @param key 键
     * @param def 默认值
     * @return 如果有数据则返回对应数据, 没有则返回默认值
     */
    public static long getLong(String key, long def) {
        return SharedUtils.getPreferences().getLong(key, def);
    }

    /**
     * 取出浮点型数据
     *
     * @param key 键
     * @param def 默认值
     * @return 如果有数据则返回对应数据, 没有则返回默认值
     */
    public static float getFloat(String key, float def) {
        return SharedUtils.getPreferences().getFloat(key, def);
    }

    /**
     * 删除指定键的数据
     *
     * @param key 键
     */
    public static void remove(String key) {
        SharedUtils.getEditor().remove(key).apply();
    }
}
