package com.example.emotiondiarysystem.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 全局弹窗工具类
 */
public final class ToastUtil {

    private static Toast sToast;

    private ToastUtil() {
        // 工具类不允许外部实例化
    }

    /**
     * 显示短时提示
     *
     * @param context 上下文
     * @param message 提示内容
     */
    public static void showShort(Context context, String message) {
        show(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 显示长时提示
     *
     * @param context 上下文
     * @param message 提示内容
     */
    public static void showLong(Context context, String message) {
        show(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 显示提示（内部统一方法）
     *
     * @param context 上下文
     * @param message 提示内容
     * @param duration 显示时长
     */
    private static void show(Context context, String message, int duration) {
        if (context == null || message == null || message.trim().isEmpty()) {
            return;
        }
        Context appContext = context.getApplicationContext();
        if (sToast == null) {
            sToast = Toast.makeText(appContext, message, duration);
        } else {
            sToast.setText(message);
            sToast.setDuration(duration);
        }
        sToast.show();
    }
}
