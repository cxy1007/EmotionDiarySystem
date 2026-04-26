package com.example.emotiondiarysystem.ui.base;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emotiondiarysystem.utils.ThemeColorUtil;

/**
 * 所有Activity的基类
 * 自动应用主题颜色和字体大小，支持日间/深色双模式
 */
public class BaseActivity extends AppCompatActivity {

    private ThemeColorUtil.ThemeColors currentColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentColors = ThemeColorUtil.getCurrentTheme(this);
        applyStatusBarAdaptation();
        applyFontSizeToRoot();
    }

    /**
     * 应用状态栏适配，确保内容不被状态栏遮挡
     */
    private void applyStatusBarAdaptation() {
        // 设置状态栏透明
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        // 让布局延伸到状态栏
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentColors = ThemeColorUtil.getCurrentTheme(this);
        applyThemeToAllViews(getWindow().getDecorView());
        applyFontSizeToRoot();
    }

    private void applyThemeToAllViews(View view) {
        if (view == null) return;

        applyViewTheme(view);

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                applyThemeToAllViews(vg.getChildAt(i));
            }
        }
    }

    private void applyViewTheme(View view) {
        if (view instanceof TextView) {
            applyTextColor((TextView) view);
        } else if (view instanceof Button) {
            applyButtonStyle((Button) view);
        }

        // 动态修改背景色（针对布局容器和卡片）
        applyBackgroundColor(view);

        // 修改图标 tint
        applyIconTint(view);
    }

    /**
     * 根据浅色背景色值判断是否为浅色背景视图，动态替换为深色模式对应色
     */
    private void applyBackgroundColor(View view) {
        int bgColor;
        if (view.getBackground() instanceof android.graphics.drawable.ColorDrawable) {
            bgColor = ((android.graphics.drawable.ColorDrawable) view.getBackground()).getColor();
        } else {
            return;
        }

        // 白色系背景 -> surface
        if (isColorSimilar(bgColor, 0xFFFFFFFF) || isColorSimilar(bgColor, 0xFFF7F8FC) || isColorSimilar(bgColor, 0xFFF7FAFC)) {
            view.setBackgroundColor(currentColors.surface);
        }
        // 浅灰系背景 -> surfaceVariant
        else if (isColorSimilar(bgColor, 0xFFEDF2F7) || isColorSimilar(bgColor, 0xFFF9FAFB)) {
            view.setBackgroundColor(currentColors.surfaceVariant);
        }
        // 浅粉色背景
        else if (isColorSimilar(bgColor, 0xFFFFF5F7) || isColorSimilar(bgColor, 0xFFFED7E2)) {
            view.setBackgroundColor(currentColors.surfaceVariant);
        }
        // 浅蓝色背景
        else if (isColorSimilar(bgColor, 0xFFEBF8FF) || isColorSimilar(bgColor, 0xFFBEE3F8) || isColorSimilar(bgColor, 0xFFF0F7FF)) {
            view.setBackgroundColor(currentColors.surfaceVariant);
        }
        // 浅绿色背景
        else if (isColorSimilar(bgColor, 0xFFC6F6D5) || isColorSimilar(bgColor, 0xFFF0FFF4)) {
            view.setBackgroundColor(currentColors.surfaceVariant);
        }
        // 顶部栏白色背景
        else if (isColorSimilar(bgColor, 0xFFFFFFFF)) {
            view.setBackgroundColor(currentColors.surface);
        }
    }

    /**
     * 修改 ImageView 图标颜色
     */
    private void applyIconTint(View view) {
        if (!(view instanceof android.widget.ImageView)) return;
        android.widget.ImageView iv = (android.widget.ImageView) view;
        if (iv.getDrawable() == null) return;
        // 跳过带透明度的图标（保留逻辑位置，后续可扩展）
        // 通用图标颜色修正（避免白色图标不可见）
        if (currentColors.surface == 0xFF1D2230 || currentColors.surface == 0xFF161A24) {
            // 深色模式下让部分图标更亮
        }
    }

    private void applyTextColor(TextView tv) {
        int currentColor = tv.getCurrentTextColor();
        if (isColorSimilar(currentColor, 0xFF2D3748) ||
            isColorSimilar(currentColor, 0xFF4A5568) ||
            isColorSimilar(currentColor, 0xFF702459) ||
            isColorSimilar(currentColor, 0xFF2A4365) ||
            isColorSimilar(currentColor, 0xFF22543D)) {
            tv.setTextColor(currentColors.textPrimary);
        } else if (isColorSimilar(currentColor, 0xFF718096) ||
                   isColorSimilar(currentColor, 0xFF888888) ||
                   isColorSimilar(currentColor, 0xFFB83280) ||
                   isColorSimilar(currentColor, 0xFF2B6CB0) ||
                   isColorSimilar(currentColor, 0xFF276749)) {
            tv.setTextColor(currentColors.textSecondary);
        }
    }

    private void applyButtonStyle(Button button) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(8f);
        drawable.setColor(currentColors.buttonBackground);
        button.setBackground(drawable);
        button.setTextColor(currentColors.buttonText);
    }

    private boolean isColorSimilar(int color1, int color2) {
        return Math.abs(((color1 >> 16) & 0xFF) - ((color2 >> 16) & 0xFF)) < 30 &&
               Math.abs(((color1 >> 8) & 0xFF) - ((color2 >> 8) & 0xFF)) < 30 &&
               Math.abs((color1 & 0xFF) - ((color2) & 0xFF)) < 30;
    }

    private void applyFontSizeToRoot() {
        float scale = ThemeColorUtil.getFontScale(this);
        if (scale != 1.0f) {
            applyFontSizeRecursive(getWindow().getDecorView(), scale);
        }
    }

    private void applyFontSizeRecursive(View view, float scale) {
        if (view == null) return;
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            float currentSize = tv.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
            if (currentSize > 0 && currentSize < 50) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentSize * scale);
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                applyFontSizeRecursive(vg.getChildAt(i), scale);
            }
        }
    }

    protected ThemeColorUtil.ThemeColors getCurrentColors() {
        return currentColors;
    }
}
