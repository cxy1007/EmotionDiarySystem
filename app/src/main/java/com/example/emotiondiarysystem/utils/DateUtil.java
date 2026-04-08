package com.example.emotiondiarysystem.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 */
public final class DateUtil {

    /**
     * 默认时间格式：年-月-日 时:分:秒
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DateUtil() {
        // 工具类不允许外部实例化
    }

    /**
     * 获取当前时间（默认格式）
     *
     * @return 当前时间字符串
     */
    public static String getCurrentTime() {
        return formatTime(new Date(), DEFAULT_PATTERN);
    }

    /**
     * 获取当前时间（指定格式）
     *
     * @param pattern 时间格式
     * @return 当前时间字符串
     */
    public static String getCurrentTime(String pattern) {
        return formatTime(new Date(), pattern);
    }

    /**
     * 格式化时间戳
     *
     * @param timeMillis 时间戳（毫秒）
     * @param pattern 时间格式
     * @return 格式化后的时间字符串
     */
    public static String formatTime(long timeMillis, String pattern) {
        return formatTime(new Date(timeMillis), pattern);
    }

    /**
     * 格式化Date对象
     *
     * @param date 时间对象
     * @param pattern 时间格式
     * @return 格式化后的时间字符串
     */
    public static String formatTime(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        String targetPattern = (pattern == null || pattern.trim().isEmpty())
                ? DEFAULT_PATTERN
                : pattern;
        SimpleDateFormat format = new SimpleDateFormat(targetPattern, Locale.getDefault());
        return format.format(date);
    }
}

