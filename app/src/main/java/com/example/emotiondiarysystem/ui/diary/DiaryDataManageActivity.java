package com.example.emotiondiarysystem.ui.diary;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.db.DBHelper;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;

public class DiaryDataManageActivity extends BaseActivity {

    private Button btnClearCache;
    private Button btnResetDb;
    private ImageView btnBack;
    private TextView tvTitle;
    private LinearLayout topBar;
    private View divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_data_manage);

        initViews();
        setListeners();
        applyFullTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFullTheme();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_title);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);
        btnClearCache = findViewById(R.id.btn_clear_cache);
        btnResetDb = findViewById(R.id.btn_reset_database);
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnClearCache.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("清空分析缓存")
                    .setMessage("确定要清空情感分析缓存数据吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        DBHelper dbHelper = DBHelper.getInstance(this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("emotion", null, null);
                        db.close();
                        Toast.makeText(this, "清空分析缓存成功", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        btnResetDb.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("重置数据库")
                    .setMessage("确定要重置整个数据库吗？所有日记和情感数据都将被删除，此操作不可恢复！")
                    .setPositiveButton("确定重置", (dialog, which) -> {
                        boolean deleted = deleteDatabase("emotion_diary.db");
                        if (deleted) {
                            SpUtil.clear(this);
                            Toast.makeText(this, "数据库已重置，请重新注册账号", Toast.LENGTH_LONG).show();
                            finishAffinity();
                        } else {
                            Toast.makeText(this, "重置失败", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    private void applyFullTheme() {
        ThemeColorUtil.ThemeColors colors = getCurrentColors();
        if (colors == null) colors = ThemeColorUtil.getCurrentTheme(this);
        boolean isDark = ThemeColorUtil.isDarkMode(this);

        View main = findViewById(R.id.main);
        if (main != null) main.setBackgroundColor(colors.background);

        if (topBar != null) topBar.setBackgroundColor(colors.surface);
        if (divider != null) divider.setBackgroundColor(colors.divider);

        btnBack.setColorFilter(colors.iconTint);
        tvTitle.setTextColor(colors.textPrimary);

        applyButtonStyle(btnClearCache, colors);
        applyButtonStyle(btnResetDb, colors);

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }

    private void applyButtonStyle(Button button, ThemeColorUtil.ThemeColors colors) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(8f);
        drawable.setColor(colors.buttonBackground);
        button.setBackground(drawable);
        button.setTextColor(colors.buttonText);
    }
}