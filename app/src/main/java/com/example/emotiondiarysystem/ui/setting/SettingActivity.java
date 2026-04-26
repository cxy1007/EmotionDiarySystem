package com.example.emotiondiarysystem.ui.setting;

import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private LinearLayout itemFontSize;
    private TextView tvFontSizeValue;
    private SwitchMaterial switchDarkMode;
    private SwitchMaterial switchDailyReminder;
    private LinearLayout itemChangePassword;
    private LinearLayout itemAbout;
    private LinearLayout itemTheme;
    private TextView tvThemeValue;
    private LinearLayout itemRecycle;
    private TextView tvRecycleValue;
    private LinearLayout topBar;
    private View divider;
    private LinearLayout settingContainer;

    private String[] fontSizes = {"小", "中", "大"};
    private int currentFontSizeIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadSettings();
        applyFullTheme();
        setListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        itemFontSize = findViewById(R.id.item_font_size);
        tvFontSizeValue = findViewById(R.id.tv_font_size_value);
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchDailyReminder = findViewById(R.id.switch_daily_reminder);
        itemChangePassword = findViewById(R.id.item_change_password);
        itemAbout = findViewById(R.id.item_about);
        itemTheme = findViewById(R.id.item_theme);
        tvThemeValue = findViewById(R.id.tv_theme_value);
        itemRecycle = findViewById(R.id.item_recycle);
        tvRecycleValue = findViewById(R.id.tv_recycle_value);
        settingContainer = findViewById(R.id.setting_container);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);
    }

    private void loadSettings() {
        currentFontSizeIndex = SpUtil.getInt(this, "fontSizeIndex", 1);
        tvFontSizeValue.setText(fontSizes[currentFontSizeIndex]);

        boolean isDarkMode = SpUtil.getBoolean(this, "darkMode", false);
        switchDarkMode.setChecked(isDarkMode);

        boolean hasReminder = SpUtil.getBoolean(this, "dailyReminder", false);
        switchDailyReminder.setChecked(hasReminder);

        String theme = SpUtil.getString(this, "appTheme", "default");
        tvThemeValue.setText(ThemeColorUtil.getThemeName(theme));
    }

    private void setListeners() {
        btnBack.setOnClickListener(this);
        itemFontSize.setOnClickListener(this);
        itemAbout.setOnClickListener(this);
        if (itemChangePassword != null) {
            itemChangePassword.setOnClickListener(this);
        }

        if (itemTheme != null) {
            itemTheme.setOnClickListener(this);
        }

        if (itemRecycle != null) {
            itemRecycle.setOnClickListener(this);
        }

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SpUtil.putBoolean(this, "darkMode", isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                ToastUtil.showShort(this, "深色模式已开启");
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                ToastUtil.showShort(this, "浅色模式已开启");
            }
            applyFullTheme();
        });

        switchDailyReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SpUtil.putBoolean(this, "dailyReminder", isChecked);
            if (isChecked) {
                ToastUtil.showShort(this, "每日提醒已开启");
            } else {
                ToastUtil.showShort(this, "每日提醒已关闭");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次显示页面时更新主题（从主题切换页返回时）
        applyFullTheme();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            finish();
        } else if (v.getId() == R.id.item_font_size) {
            showFontSizeDialog();
        } else if (v.getId() == R.id.item_about) {
            showAboutDialog();
        } else if (v.getId() == R.id.item_change_password) {
            startActivity(new android.content.Intent(this, ChangePasswordActivity.class));
        } else if (v.getId() == R.id.item_theme) {
            startActivity(new android.content.Intent(this, ThemeSwitchActivity.class));
        } else if (v.getId() == R.id.item_recycle) {
            startActivity(new android.content.Intent(this, com.example.emotiondiarysystem.ui.diary.DiaryRecycleActivity.class));
        }
    }

    /**
     * 应用完整主题到页面所有元素
     */
    private void applyFullTheme() {
        ThemeColorUtil.ThemeColors colors = getCurrentColors();
        if (colors == null) {
            colors = ThemeColorUtil.getCurrentTheme(this);
        }
        boolean isDark = ThemeColorUtil.isDarkMode(this);

        // 应用到主背景
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            mainView.setBackgroundColor(colors.background);
        }

        // 应用到顶部导航栏
        if (topBar != null) {
            topBar.setBackgroundColor(colors.surface);
        }

        // 应用到分隔线
        if (divider != null) {
            divider.setBackgroundColor(colors.divider);
        }

        // 应用到设置项容器
        if (settingContainer != null) {
            settingContainer.setBackgroundColor(colors.surface);
        }

        // 应用到字体大小值
        tvFontSizeValue.setTextColor(colors.textSecondary);

        // 应用到主题名称
        tvThemeValue.setTextColor(colors.textSecondary);

        // 应用到回收站文本
        if (tvRecycleValue != null) {
            tvRecycleValue.setTextColor(colors.textSecondary);
        }

        // 应用到返回按钮颜色
        updateImageViewTint(btnBack, colors.iconTint);

        // 应用到分隔线
        updateDividers();

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }

    private void updateImageViewTint(View view, int tintColor) {
        if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            iv.setColorFilter(tintColor);
        }
    }

    private void updateDividers() {
        View divider1 = findViewById(R.id.divider);
        if (divider1 != null) {
            divider1.setBackgroundColor(getCurrentColors().divider);
        }
    }

    /**
     * 显示字体大小选择对话框
     */
    private void showFontSizeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("选择字体大小")
                .setSingleChoiceItems(fontSizes, currentFontSizeIndex, (dialog, which) -> {
                    currentFontSizeIndex = which;
                    tvFontSizeValue.setText(fontSizes[which]);
                    SpUtil.putInt(this, "fontSizeIndex", which);
                    ToastUtil.showShort(this, "字体大小已设置为：" + fontSizes[which]);
                    dialog.dismiss();
                    // 字体大小改变后刷新页面
                    recreate();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 显示关于我们对话框
     */
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("关于我们")
                .setMessage(
                        "智能情感日记 v1.0.0\n\n" +
                        "本项目是一个基于文本情感分析的个性化智能日记系统，通过自然语言处理技术分析用户日记内容的情感倾向，提供情绪可视化、个性化推荐与心理关怀建议。\n\n" +
                        "功能特点：\n" +
                        "• 日记撰写与管理\n" +
                        "• 情感智能分析\n" +
                        "• 情绪趋势统计\n" +
                        "• 个性化主题设置"
                )
                .setPositiveButton("知道了", null)
                .show();
    }
}
