package com.example.emotiondiarysystem.ui.diary;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

        // 绑定保存按钮点击事件
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiary();
            }
        });
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
            // 获取当前用户ID
            int userId = SpUtil.getInt(this, "userId", 1);
            
            // 保存到数据库
            long result = dbHelper.addDiary(
                    userId,
                    content,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    "一般" // 可以根据实际情况设置
            );

            if (result > 0) {
                Toast.makeText(this, "日记保存成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "日记保存失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "保存失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}