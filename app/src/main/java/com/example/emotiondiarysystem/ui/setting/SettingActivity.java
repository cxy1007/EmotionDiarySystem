package com.example.emotiondiarysystem.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import com.example.emotiondiarysystem.ui.BaseActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.ui.diary.DiaryRecycleActivity;
import com.example.emotiondiarysystem.ui.diary.DiarySearchActivity;
import com.example.emotiondiarysystem.ui.login.ChangePasswordActivity;
import com.example.emotiondiarysystem.ui.reminder.ReminderSettingActivity;
import com.example.emotiondiarysystem.ui.login.LoginActivity;
import com.example.emotiondiarysystem.ui.theme.FontSizeActivity;
import com.example.emotiondiarysystem.ui.theme.ThemeSwitchActivity;
import com.example.emotiondiarysystem.utils.SpUtil;

public class SettingActivity extends BaseActivity {

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.tvTheme).setOnClickListener(v -> {
            startActivity(new Intent(this, ThemeSwitchActivity.class));
        });

        findViewById(R.id.tvFontSize).setOnClickListener(v -> {
            Toast.makeText(this, "字体大小设置", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.tvBgCustom).setOnClickListener(v -> {
            Toast.makeText(this, "背景自定义", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.tvListStyle).setOnClickListener(v -> {
            Toast.makeText(this, "列表样式设置", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.tvReminder).setOnClickListener(v -> {
            startActivity(new Intent(this, ReminderSettingActivity.class));
        });

        findViewById(R.id.tvChangePassword).setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        findViewById(R.id.tvDiarySearch).setOnClickListener(v -> {
            startActivity(new Intent(this, DiarySearchActivity.class));
        });

        findViewById(R.id.tvDiaryRecycle).setOnClickListener(v -> {
            startActivity(new Intent(this, DiaryRecycleActivity.class));
        });

        findViewById(R.id.tvLogout).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                .setTitle("确认退出")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 清除SharedPreferences数据
                    SpUtil.clear(this);
                    // 跳转到登录页并关闭所有之前的Activity
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
