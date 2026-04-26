package com.example.emotiondiarysystem.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.ColorUtils;

/**
 * 主题颜色工具类
 * 提供不同主题的配色方案，支持日间/深色双模式，动态应用到UI
 */
public class ThemeColorUtil {

    // 主题颜色定义
    public static class ThemeColors {
        // 主色调
        public final int primary;
        public final int primaryText;
        public final int secondary;

        // 背景和表面
        public final int background;
        public final int surface;
        public final int surfaceVariant;

        // 文字颜色
        public final int textPrimary;
        public final int textSecondary;
        public final int textOnPrimary;

        // 分隔线和边框
        public final int border;
        public final int divider;

        // 图标
        public final int iconTint;

        // 按钮
        public final int buttonBackground;
        public final int buttonText;

        public ThemeColors(int primary, int primaryText, int secondary,
                          int background, int surface, int surfaceVariant,
                          int textPrimary, int textSecondary, int textOnPrimary,
                          int border, int divider, int iconTint,
                          int buttonBackground, int buttonText) {
            this.primary = primary;
            this.primaryText = primaryText;
            this.secondary = secondary;
            this.background = background;
            this.surface = surface;
            this.surfaceVariant = surfaceVariant;
            this.textPrimary = textPrimary;
            this.textSecondary = textSecondary;
            this.textOnPrimary = textOnPrimary;
            this.border = border;
            this.divider = divider;
            this.iconTint = iconTint;
            this.buttonBackground = buttonBackground;
            this.buttonText = buttonText;
        }
    }

    // ============================================================
    // 日间主题配色
    // ============================================================

    // 默认主题 - 深灰经典风（日间）
    private static final ThemeColors THEME_DEFAULT_LIGHT = new ThemeColors(
            0xFF2D3748, 0xFFFFFFFF, 0xFF4A5568,
            0xFFFFFFFF, 0xFFF7FAFC, 0xFFEDF2F7,
            0xFF2D3748, 0xFF718096, 0xFFFFFFFF,
            0xFFE2E8F0, 0xFFE2E8F0, 0xFF2D3748,
            0xFF2D3748, 0xFFFFFFFF
    );

    // 粉色主题 - 少女风（日间）
    private static final ThemeColors THEME_PINK_LIGHT = new ThemeColors(
            0xFFE879A9, 0xFFFFFFFF, 0xFFF687B3,
            0xFFFFFAF0, 0xFFFFF5F7, 0xFFFED7E2,
            0xFF702459, 0xFFB83280, 0xFFFFFFFF,
            0xFFFED7E2, 0xFFFED7E2, 0xFFE879A9,
            0xFFE879A9, 0xFFFFFFFF
    );

    // 蓝色主题 - 天空风（日间）
    private static final ThemeColors THEME_BLUE_LIGHT = new ThemeColors(
            0xFF3182CE, 0xFFFFFFFF, 0xFF4299E1,
            0xFFF0F7FF, 0xFFEBF8FF, 0xFFBEE3F8,
            0xFF2A4365, 0xFF2B6CB0, 0xFFFFFFFF,
            0xFFBEE3F8, 0xFFBEE3F8, 0xFF3182CE,
            0xFF3182CE, 0xFFFFFFFF
    );

    // 绿色主题 - 自然风（日间）
    private static final ThemeColors THEME_GREEN_LIGHT = new ThemeColors(
            0xFF38A169, 0xFFFFFFFF, 0xFF48BB78,
            0xFFF0FFF4, 0xFFC6F6D5, 0xFFC6F6D5,
            0xFF22543D, 0xFF276749, 0xFFFFFFFF,
            0xFFC6F6D5, 0xFFC6F6D5, 0xFF38A169,
            0xFF38A169, 0xFFFFFFFF
    );

    // ============================================================
    // 深色主题配色
    // ============================================================

    // 默认主题 - 深灰经典风（深色）
    private static final ThemeColors THEME_DEFAULT_DARK = new ThemeColors(
            0xFF4A5568, 0xFFFFFFFF, 0xFF718096,
            0xFF1A1F2E, 0xFF1D2230, 0xFF252C3A,
            0xFFE6EAF3, 0xFFA7AEC4, 0xFF1D2230,
            0xFF2D3548, 0xFF2D3548, 0xFFA7AEC4,
            0xFF4A5568, 0xFFFFFFFF
    );

    // 粉色主题 - 少女风（深色）
    private static final ThemeColors THEME_PINK_DARK = new ThemeColors(
            0xFFF687B3, 0xFF1D2230, 0xFFFDAFC3,
            0xFF1A1018, 0xFF1D1520, 0xFF2A1D2E,
            0xFFFED7E2, 0xFFF687B3, 0xFF1D2230,
            0xFF3D253A, 0xFF3D253A, 0xFFF687B3,
            0xFFF687B3, 0xFFFFFFFF
    );

    // 蓝色主题 - 天空风（深色）
    private static final ThemeColors THEME_BLUE_DARK = new ThemeColors(
            0xFF4299E1, 0xFF1D2230, 0xFF63B3ED,
            0xFF0F1A2E, 0xFF152238, 0xFF1E3050,
            0xFFBEE3F8, 0xFF4299E1, 0xFF1D2230,
            0xFF1E3050, 0xFF1E3050, 0xFF4299E1,
            0xFF4299E1, 0xFFFFFFFF
    );

    // 绿色主题 - 自然风（深色）
    private static final ThemeColors THEME_GREEN_DARK = new ThemeColors(
            0xFF48BB78, 0xFF1D2230, 0xFF68D391,
            0xFF0F1E14, 0xFF152618, 0xFF1E3020,
            0xFFC6F6D5, 0xFF48BB78, 0xFF1D2230,
            0xFF1E3020, 0xFF1E3020, 0xFF48BB78,
            0xFF48BB78, 0xFFFFFFFF
    );

    /**
     * 检测当前是否为深色模式
     * @param context
     * @return true=深色模式，false=日间模式
     */
    public static boolean isDarkMode(Context context) {
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            return true;
        } else if (nightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            return false;
        }
        // MODE_NIGHT_FOLLOW_SYSTEM 或未设置，通过配置检测
        int currentTheme = context.getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return currentTheme == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * 获取当前主题颜色（根据日间/深色模式自动选择配色）
     */
    public static ThemeColors getCurrentTheme(Context context) {
        String theme = SpUtil.getString(context, "appTheme", "default");
        boolean dark = isDarkMode(context);
        switch (theme) {
            case "pink":
                return dark ? THEME_PINK_DARK : THEME_PINK_LIGHT;
            case "blue":
                return dark ? THEME_BLUE_DARK : THEME_BLUE_LIGHT;
            case "green":
                return dark ? THEME_GREEN_DARK : THEME_GREEN_LIGHT;
            default:
                return dark ? THEME_DEFAULT_DARK : THEME_DEFAULT_LIGHT;
        }
    }

    /**
     * 获取字体大小缩放因子
     * @param context
     * @return 缩放因子：小=0.85, 中=1.0, 大=1.15
     */
    public static float getFontScale(Context context) {
        int fontSizeIndex = SpUtil.getInt(context, "fontSizeIndex", 1);
        switch (fontSizeIndex) {
            case 0:
                return 0.85f;
            case 2:
                return 1.15f;
            default:
                return 1.0f;
        }
    }

    /**
     * 获取字体大小名称
     */
    public static String getFontSizeName(int index) {
        switch (index) {
            case 0:
                return "小";
            case 2:
                return "大";
            default:
                return "中";
        }
    }

    /**
     * 获取主题名称
     */
    public static String getThemeName(String theme) {
        switch (theme) {
            case "pink":
                return "粉色少女风";
            case "blue":
                return "蓝色天空风";
            case "green":
                return "绿色自然风";
            default:
                return "经典深灰风";
        }
    }

    // ============================================================
    // 通用递归主题应用方法（供 Activity / Fragment 共用）
    // ============================================================

    /**
     * 对整个视图树应用深色主题
     * 替换硬编码的浅色背景为深色模式对应颜色
     */
    public static void applyDarkModeRecursive(View root, ThemeColors colors, boolean isDark) {
        if (root == null || !isDark) return;

        applyViewBgDark(root, colors);

        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                applyDarkModeRecursive(vg.getChildAt(i), colors, isDark);
            }
        }
    }

    /**
     * 为单个视图应用深色背景（硬编码浅色背景 -> 深色对应色）
     */
    public static void applyViewBgDark(View view, ThemeColors colors) {
        if (view == null) return;
        android.graphics.drawable.Drawable bg = view.getBackground();
        if (!(bg instanceof android.graphics.drawable.ColorDrawable)) return;

        int bgColor = ((android.graphics.drawable.ColorDrawable) bg).getColor();

        if (isLightColor(bgColor, 0xFFFFFFFF) || isLightColor(bgColor, 0xFFF7F8FC) || isLightColor(bgColor, 0xFFF7FAFC)) {
            view.setBackgroundColor(colors.surface);
        } else if (isLightColor(bgColor, 0xFFEDF2F7) || isLightColor(bgColor, 0xFFF9FAFB)) {
            view.setBackgroundColor(colors.surfaceVariant);
        } else if (isLightColor(bgColor, 0xFFFFF5F7) || isLightColor(bgColor, 0xFFFED7E2)) {
            view.setBackgroundColor(colors.surfaceVariant);
        } else if (isLightColor(bgColor, 0xFFEBF8FF) || isLightColor(bgColor, 0xFFBEE3F8) || isLightColor(bgColor, 0xFFF0F7FF)) {
            view.setBackgroundColor(colors.surfaceVariant);
        } else if (isLightColor(bgColor, 0xFFC6F6D5) || isLightColor(bgColor, 0xFFF0FFF4)) {
            view.setBackgroundColor(colors.surfaceVariant);
        } else if (isLightColor(bgColor, 0xFFFEF2F2) || isLightColor(bgColor, 0xFFFED7D7)) {
            view.setBackgroundColor(0xFF2D1520);
        }
    }

    /**
     * 判断颜色是否与参考色相似（在 30 偏差范围内）
     */
    private static boolean isLightColor(int color1, int color2) {
        return Math.abs(((color1 >> 16) & 0xFF) - ((color2 >> 16) & 0xFF)) < 30 &&
               Math.abs(((color1 >> 8) & 0xFF) - ((color2 >> 8) & 0xFF)) < 30 &&
               Math.abs((color1 & 0xFF) - (color2 & 0xFF)) < 30;
    }

    /**
     * 修改 CardView 的背景色
     */
    public static void applyCardViewBgDark(androidx.cardview.widget.CardView cardView, ThemeColors colors, boolean isDark) {
        if (cardView == null || !isDark) return;
        cardView.setCardBackgroundColor(colors.surface);
    }
}
