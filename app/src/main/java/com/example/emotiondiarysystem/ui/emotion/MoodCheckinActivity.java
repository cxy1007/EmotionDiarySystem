package com.example.emotiondiarysystem.ui.emotion;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.emotiondiarysystem.databinding.ActivityMoodCheckinBinding;
import com.example.emotiondiarysystem.utils.SpUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MoodCheckinActivity extends AppCompatActivity {

    private ActivityMoodCheckinBinding binding;
    private DBHelper dbHelper;
    private Integer selectedMoodScore = null;
    private String selectedMoodTag = "";

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

        // 设置 Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 绑定心情等级按钮点击事件
        binding.btnMoodVeryBad.setOnClickListener(v -> selectMood(25, "很差"));
        binding.btnMoodBad.setOnClickListener(v -> selectMood(50, "较差"));
        binding.btnMoodNeutral.setOnClickListener(v -> selectMood(75, "一般"));
        binding.btnMoodGood.setOnClickListener(v -> selectMood(90, "良好"));
        binding.btnMoodVeryGood.setOnClickListener(v -> selectMood(100, "极好"));

        // 绑定保存按钮点击事件
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMoodCheckin();
            }
        });
    }

    private void selectMood(int score, String tag) {
        selectedMoodScore = score;
        selectedMoodTag = tag;
        
        // 重置所有按钮样式
        resetButtonStyles();
        
        // 高亮选中的按钮
        switch (score) {
            case 25:
                binding.btnMoodVeryBad.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                binding.btnMoodVeryBad.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case 50:
                binding.btnMoodBad.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                binding.btnMoodBad.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case 75:
                binding.btnMoodNeutral.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                binding.btnMoodNeutral.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case 90:
                binding.btnMoodGood.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                binding.btnMoodGood.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case 100:
                binding.btnMoodVeryGood.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                binding.btnMoodVeryGood.setTextColor(getResources().getColor(android.R.color.white));
                break;
        }
        
        // 更新选中状态提示
        binding.tvSelectedMood.setText("已选择：" + tag + " (" + score + "分)");
    }

    private void resetButtonStyles() {
        int[] buttonIds = {R.id.btnMoodVeryBad, R.id.btnMoodBad, R.id.btnMoodNeutral, R.id.btnMoodGood, R.id.btnMoodVeryGood};
        for (int id : buttonIds) {
            com.google.android.material.button.MaterialButton button = findViewById(id);
            button.setBackgroundTintList(null);
            button.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void saveMoodCheckin() {
        if (binding == null || binding.etNote == null) {
            Toast.makeText(this, "页面加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedMoodScore == null) {
            Toast.makeText(this, "请选择心情等级", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = binding.etNote.getText() != null ? binding.etNote.getText().toString().trim() : "";

        SQLiteDatabase db = null;
        try {
            // 保存到数据库
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            int userId = SpUtil.getInt(MoodCheckinActivity.this, "userId", 1);
            values.put("user_id", userId);
            values.put("mood_score", selectedMoodScore);
            values.put("mood_tag", selectedMoodTag);
            values.put("note", note);
            values.put("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            long result = db.insert("mood_checkin", null, values);

            if (result > 0) {
                Toast.makeText(this, "打卡成功", Toast.LENGTH_SHORT).show();
                // 清空选择，允许继续打卡
                resetSelection();
            } else {
                Toast.makeText(this, "打卡失败", Toast.LENGTH_SHORT).show();
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

    private void resetSelection() {
        selectedMoodScore = null;
        selectedMoodTag = "";
        resetButtonStyles();
        binding.tvSelectedMood.setText("请选择心情等级");
        binding.etNote.setText("");
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
