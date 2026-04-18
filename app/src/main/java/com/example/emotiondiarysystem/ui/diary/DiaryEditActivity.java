package com.example.emotiondiarysystem.ui.diary;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.db.DBHelper;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.databinding.ActivityDiaryEditBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryEditActivity extends AppCompatActivity {

    private ActivityDiaryEditBinding binding;
    private DBHelper dbHelper;
    private int diaryId = -1;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDiaryEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = DBHelper.getInstance(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        diaryId = getIntent().getIntExtra("diaryId", -1);
        isEdit = getIntent().getBooleanExtra("isEdit", false);

        if (isEdit && diaryId != -1) {
            binding.toolbar.setTitle("编辑日记");
            loadDiaryContent();
        } else {
            binding.toolbar.setTitle("写日记");
        }

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiary();
            }
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

    private void loadDiaryContent() {
        if (diaryId == -1) {
            return;
        }

        Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().query("diary", null,
                    "diaryId=?", new String[]{String.valueOf(diaryId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                binding.etContent.setText(content);
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载日记内容失败", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void saveDiary() {
        if (binding == null || binding.etContent == null) {
            Toast.makeText(this, "页面加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = binding.etContent.getText() != null ? binding.etContent.getText().toString().trim() : "";
        if (content.isEmpty()) {
            Toast.makeText(this, "日记内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int userId = SpUtil.getInt(this, "userId", 1);

            if (isEdit && diaryId != -1) {
                int result = dbHelper.updateDiary(diaryId, content, "一般");
                if (result > 0) {
                    Toast.makeText(this, "日记更新成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "日记更新失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                long result = dbHelper.addDiary(
                        userId,
                        content,
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                        "一般"
                );

                if (result > 0) {
                    Toast.makeText(this, "日记保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "日记保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "保存失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
