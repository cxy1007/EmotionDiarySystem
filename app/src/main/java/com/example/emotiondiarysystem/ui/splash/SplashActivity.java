package com.example.emotiondiarysystem.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.ui.diary.DiaryHomeActivity;
import com.example.emotiondiarysystem.ui.login.LoginActivity;
import com.example.emotiondiarysystem.utils.SpUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 延迟2秒跳转
        new Handler().postDelayed(() -> {
            // 适配你原有的静态SpUtil写法
            int userId = SpUtil.getInt(SplashActivity.this, "userId", -1);

            if (userId == -1) {
                // 未登录 → 跳登录页
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                // 已登录 → 跳主页
                startActivity(new Intent(this, DiaryHomeActivity.class));
            }
            finish();
        }, 2000);
    }
}