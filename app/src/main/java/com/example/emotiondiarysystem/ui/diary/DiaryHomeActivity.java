package com.example.emotiondiarysystem.ui.diary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.db.DBHelper;
import com.example.emotiondiarysystem.utils.SpUtil;

public class DiaryHomeActivity extends AppCompatActivity {

    private LinearLayout diaryListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diary_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        diaryListContainer = findViewById(R.id.diaryListContainer);

        // 绑定添加日记按钮点击事件
        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DiaryHomeActivity.this, DiaryEditActivity.class));
            }
        });

        // 加载日记列表
        loadDiaryList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新日记列表
        loadDiaryList();
    }

    private void loadDiaryList() {
        // 清空列表
        if (diaryListContainer != null) {
            diaryListContainer.removeAllViews();
        }

        // 获取当前用户ID
        int userId = SpUtil.getInt(this, "userId", 1);

        // 查询日记列表
        DBHelper dbHelper = DBHelper.getInstance(this);
        Cursor cursor = null;
        try {
            cursor = dbHelper.getAllDiaries(userId);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // 创建日记项
                    View diaryItem = getLayoutInflater().inflate(R.layout.item_diary, null);
                    TextView tvContent = diaryItem.findViewById(R.id.tvDiaryContent);
                    TextView tvTime = diaryItem.findViewById(R.id.tvDiaryTime);
                    TextView tvEmotion = diaryItem.findViewById(R.id.tvEmotionType);

                    // 设置数据
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    String time = cursor.getString(cursor.getColumnIndex("createTime"));
                    String emotion = cursor.getString(cursor.getColumnIndex("emotionType"));

                    tvContent.setText(content);
                    tvTime.setText(time);
                    tvEmotion.setText(emotion);

                    // 添加到容器
                    if (diaryListContainer != null) {
                        diaryListContainer.addView(diaryItem);
                    }
                } while (cursor.moveToNext());
            } else {
                // 显示空状态
                View emptyView = getLayoutInflater().inflate(R.layout.item_empty_diary, null);
                if (diaryListContainer != null) {
                    diaryListContainer.addView(emptyView);
                }
            }
        } catch (Exception e) {
            // 显示错误状态
            View errorView = getLayoutInflater().inflate(R.layout.item_empty_diary, null);
            TextView tvEmpty = errorView.findViewById(R.id.tvEmpty);
            tvEmpty.setText("加载失败，请重试");
            if (diaryListContainer != null) {
                diaryListContainer.addView(errorView);
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                }
            }
        }
    }

}
