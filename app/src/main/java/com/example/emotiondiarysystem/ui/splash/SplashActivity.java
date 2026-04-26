package com.example.emotiondiarysystem.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.ui.diary.DiaryHomeActivity;
import com.example.emotiondiarysystem.ui.login.LoginActivity;
import com.example.emotiondiarysystem.utils.SessionUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;

public class SplashActivity extends BaseActivity {

    private LinearLayout splashRoot;
    private TextView tvAppName;
    private TextView tvAppSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashRoot = findViewById(R.id.splash_root);
        tvAppName = findViewById(R.id.tv_app_name);
        tvAppSubtitle = findViewById(R.id.tv_app_subtitle);

        applyFullTheme();

        // 延迟2秒跳转
        new Handler().postDelayed(() -> {
            int userId = SessionUtil.ensureUserId(SplashActivity.this);

            if (userId == -1) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, DiaryHomeActivity.class));
            }
            finish();
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFullTheme();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void applyFullTheme() {
        ThemeColorUtil.ThemeColors colors = getCurrentColors();
        if (colors == null) colors = ThemeColorUtil.getCurrentTheme(this);
        boolean isDark = ThemeColorUtil.isDarkMode(this);

        if (splashRoot != null) splashRoot.setBackgroundColor(colors.background);
        if (tvAppName != null) tvAppName.setTextColor(colors.textPrimary);
        if (tvAppSubtitle != null) tvAppSubtitle.setTextColor(colors.textSecondary);

        // 递归处理所有子视图的浅色背景
        if (getWindow().getDecorView() != null) {
            ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
        }
    }
}