package com.example.emotiondiarysystem.ui.audio;

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
import com.example.emotiondiarysystem.bean.AudioNote;
import com.example.emotiondiarysystem.db.DBHelper;
import com.example.emotiondiarysystem.databinding.ActivityAudioInputBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioInputActivity extends AppCompatActivity {

    private ActivityAudioInputBinding binding;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAudioInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = DBHelper.getInstance(this);

        // 绑定按钮点击事件
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAudioNote();
            }
        });
    }

    private void saveAudioNote() {
        if (binding == null) {
            Toast.makeText(this, "页面加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        // 模拟数据，实际项目中应该从录音和转文字获取
        AudioNote audioNote = new AudioNote();
        audioNote.setUserId(1); // 假设当前用户ID为1
        audioNote.setAudioPath("/path/to/audio.mp3");
        audioNote.setTranscript("这是一段录音转文字的内容");
        audioNote.setDuration(120); // 2分钟
        audioNote.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        audioNote.setEmotionTag("开心");

        SQLiteDatabase db = null;
        try {
            // 保存到数据库
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", audioNote.getUserId());
            values.put("audio_path", audioNote.getAudioPath());
            values.put("transcript", audioNote.getTranscript());
            values.put("duration", audioNote.getDuration());
            values.put("create_time", audioNote.getCreateTime());
            values.put("emotion_tag", audioNote.getEmotionTag());

            long result = db.insert("audio_note", null, values);

            if (result > 0) {
                Toast.makeText(this, "录音记录保存成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "录音记录保存失败", Toast.LENGTH_SHORT).show();
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
