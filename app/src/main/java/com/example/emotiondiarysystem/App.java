package com.example.emotiondiarysystem;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.emotiondiarysystem.utils.SpUtil;

/**
 * 应用全局配置类
 * 在应用启动时加载主题和深色模式设置
 */
public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applySettings();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    /**
     * 应用启动时加载保存的主题和深色模式设置
     */
    private void applySettings() {
        // 应用启动时设置深色模式
        boolean isDarkMode = SpUtil.getBoolean(this, "darkMode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * 应用配置变更时重新加载设置（如切换深色模式后）
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applySettings();
    }

    public static App getInstance() {
        return instance;
    }
}
