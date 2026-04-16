package com.example.emotiondiarysystem.ui.emotion;

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
import com.example.emotiondiarysystem.databinding.ActivityMoodCheckinBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MoodCheckinActivity extends AppCompatActivity {

    private ActivityMoodCheckinBinding binding;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMoodCheckinBinding.inflate(getLayoutInflater());
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
                saveMoodCheckin();
            }
        });
    }

    private void saveMoodCheckin() {
        if (binding == null || binding.etNote == null) {
            Toast.makeText(this, "页面加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        int moodScore = 5; // 假设默认分数为5
        String moodTag = "开心"; // 假设默认标签为开心
        String note = binding.etNote.getText() != null ? binding.etNote.getText().toString().trim() : "";

        SQLiteDatabase db = null;
        try {
            // 保存到数据库
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", 1); // 假设当前用户ID为1
            values.put("mood_score", moodScore);
            values.put("mood_tag", moodTag);
            values.put("note", note);
            values.put("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            long result = db.insert("mood_checkin", null, values);

            if (result > 0) {
                Toast.makeText(this, "心情打卡保存成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "心情打卡保存失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "保存失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
