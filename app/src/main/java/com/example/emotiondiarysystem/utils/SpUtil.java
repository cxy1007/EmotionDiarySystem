package com.example.emotiondiarysystem.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类
 */
public final class SpUtil {

    private static final String DEFAULT_SP_NAME = "emotion_diary_sp";

    private SpUtil() {
        // 工具类不允许外部实例化
    }

    /**
     * 保存String类型数据
     *
     * @param context 上下文
     * @param key 键
     * @param value 值
     */
    public static void putString(Context context, String key, String value) {
        getEditor(context).putString(key, value).apply();
    }

    /**
     * 读取String类型数据
     *
     * @param context 上下文
     * @param key 键
     * @param defaultValue 默认值
     * @return 读取结果
     */
    public static String getString(Context context, String key, String defaultValue) {
        return getSp(context).getString(key, defaultValue);
    }

    /**
     * 保存int类型数据
     *
     * @param context 上下文
     * @param key 键
     * @param value 值
     */
    public static void putInt(Context context, String key, int value) {
        getEditor(context).putInt(key, value).apply();
    }

    /**
     * 读取int类型数据
     *
     * @param context 上下文
     * @param key 键
     * @param defaultValue 默认值
     * @return 读取结果
     */
    public static int getInt(Context context, String key, int defaultValue) {
        return getSp(context).getInt(key, defaultValue);
    }

    /**
     * 保存float类型数据
     *
     * @param context 上下文
     * @param key 键
     * @param value 值
     */
    public static void putFloat(Context context, String key, float value) {
        getEditor(context).putFloat(key, value).apply();
    }

    /**
     * 读取float类型数据
     *
     * @param context 上下文
     * @param key 键
     * @param defaultValue 默认值
     * @return 读取结果
     */
    public static float getFloat(Context context, String key, float defaultValue) {
        return getSp(context).getFloat(key, defaultValue);
    }

    /**
     * 保存boolean类型数据
     *
     * @param context 上下文
     * @param key 键
     * @param value 值
     */
    public static void putBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).apply();
    }

    /**
     * 读取boolean类型数据
     *
     * @param context 上下文
     * @param key 键
     * @param defaultValue 默认值
     * @return 读取结果
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getSp(context).getBoolean(key, defaultValue);
    }

    /**
     * 保存long类型数据
     *
     * @param context 上下文
     * @param key 键
     * @param value 值
     */
    public static void putLong(Context context, String key, long value) {
        getEditor(context).putLong(key, value).apply();
    }

    /**
     * 读取long类型数据
     *
     * @param context 上下文
     * @param key 键
     * @param defaultValue 默认值
     * @return 读取结果
     */
    public static long getLong(Context context, String key, long defaultValue) {
        return getSp(context).getLong(key, defaultValue);
    }

    /**
     * 删除指定键的数据
     *
     * @param context 上下文
     * @param key 键
     */
    public static void remove(Context context, String key) {
        getEditor(context).remove(key).apply();
    }

    /**
     * 清空全部数据
     *
     * @param context 上下文
     */
    public static void clear(Context context) {
        getEditor(context).clear().apply();
    }

    /**
     * 判断是否包含指定键
     *
     * @param context 上下文
     * @param key 键
     * @return true表示包含
     */
    public static boolean contains(Context context, String key) {
        return getSp(context).contains(key);
    }

    private static SharedPreferences getSp(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSp(context).edit();
    }
}

