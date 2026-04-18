package com.example.emotiondiarysystem.ui.diary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.db.DBHelper;
import com.example.emotiondiarysystem.utils.SpUtil;

public class DiaryDetailActivity extends AppCompatActivity {

    private TextView tvContent, tvCreateTime, tvEmotionType;
    private int diaryId;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diary_detail);

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

        tvContent = findViewById(R.id.tvContent);
        tvCreateTime = findViewById(R.id.tvCreateTime);
        tvEmotionType = findViewById(R.id.tvEmotionType);

        dbHelper = DBHelper.getInstance(this);

        diaryId = getIntent().getIntExtra("diaryId", -1);
        if (diaryId == -1) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDiaryDetail();

        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            Intent intent = new Intent(this, DiaryEditActivity.class);
            intent.putExtra("diaryId", diaryId);
            intent.putExtra("isEdit", true);
            startActivity(intent);
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> showDeleteDialog());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDiaryDetail();
    }

    private void loadDiaryDetail() {
        int userId = SpUtil.getInt(this, "userId", 1);
        Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().query("diary", null,
                    "diaryId=?", new String[]{String.valueOf(diaryId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String createTime = cursor.getString(cursor.getColumnIndexOrThrow("createTime"));
                String emotionType = cursor.getString(cursor.getColumnIndexOrThrow("emotionType"));

                tvContent.setText(content);
                tvCreateTime.setText(createTime);
                tvEmotionType.setText(emotionType != null ? emotionType : "一般");
            } else {
                Toast.makeText(this, "日记不存在", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("删除日记")
                .setMessage("确定要删除这篇日记吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    int result = dbHelper.deleteDiary(diaryId);
                    if (result > 0) {
                        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
