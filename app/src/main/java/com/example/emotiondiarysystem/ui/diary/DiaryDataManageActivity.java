package com.example.emotiondiarysystem.ui.diary;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.example.emotiondiarysystem.R;

public class DiaryDataManageActivity extends AppCompatActivity {

    private Button btnClearCache;
    private Button btnResetDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_data_manage);

        // 返回
        findViewById(R.id.topBar).setOnClickListener(v -> finish());

        btnClearCache = findViewById(R.id.btn_clear_cache);
        btnResetDb = findViewById(R.id.btn_reset_database);

        btnClearCache.setOnClickListener(v -> {
            Toast.makeText(this, "清空分析缓存成功", Toast.LENGTH_SHORT).show();
        });

        btnResetDb.setOnClickListener(v -> {
            Toast.makeText(this, "数据库已重置", Toast.LENGTH_SHORT).show();
        });
    }
}