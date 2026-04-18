package com.example.emotiondiarysystem.ui.diary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.db.DBHelper;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.google.android.material.textfield.TextInputEditText;

public class DiarySearchActivity extends AppCompatActivity {

    private TextInputEditText etSearch;
    private LinearLayout searchResultContainer;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diary_search);

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

        etSearch = findViewById(R.id.etSearch);
        searchResultContainer = findViewById(R.id.searchResultContainer);
        dbHelper = DBHelper.getInstance(this);

        findViewById(R.id.btnSearch).setOnClickListener(v -> searchDiary());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchDiary() {
        if (etSearch == null) {
            Toast.makeText(this, "页面加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        String keyword = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
        if (keyword.isEmpty()) {
            Toast.makeText(this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = SpUtil.getInt(this, "userId", 1);
        searchResultContainer.removeAllViews();

        try {
            // 搜索正常日记（is_deleted = 0）
            Cursor normalCursor = dbHelper.getReadableDatabase().query("diary", null,
                    "userId=? AND content LIKE ? AND is_deleted = 0",
                    new String[]{String.valueOf(userId), "%" + keyword + "%"},
                    null, null, "createTime DESC");

            // 搜索回收站日记（is_deleted = 1）
            Cursor recycleCursor = dbHelper.getReadableDatabase().query("diary", null,
                    "userId=? AND content LIKE ? AND is_deleted = 1",
                    new String[]{String.valueOf(userId), "%" + keyword + "%"},
                    null, null, "createTime DESC");

            boolean hasResults = false;

            // 显示正常日记
            if (normalCursor != null && normalCursor.moveToFirst()) {
                hasResults = true;
                do {
                    View itemView = getLayoutInflater().inflate(R.layout.item_diary, searchResultContainer, false);
                    TextView tvContent = itemView.findViewById(R.id.tvDiaryContent);
                    TextView tvTime = itemView.findViewById(R.id.tvDiaryTime);
                    TextView tvEmotion = itemView.findViewById(R.id.tvEmotionType);

                    int diaryId = normalCursor.getInt(normalCursor.getColumnIndexOrThrow("diaryId"));
                    String content = normalCursor.getString(normalCursor.getColumnIndexOrThrow("content"));
                    String time = normalCursor.getString(normalCursor.getColumnIndexOrThrow("createTime"));
                    String emotion = normalCursor.getString(normalCursor.getColumnIndexOrThrow("emotionType"));

                    tvContent.setText(content);
                    tvTime.setText(time);
                    tvEmotion.setText(emotion != null ? emotion : "一般");

                    final int finalDiaryId = diaryId;
                    itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(this, DiaryDetailActivity.class);
                        intent.putExtra("diaryId", finalDiaryId);
                        startActivity(intent);
                    });

                    searchResultContainer.addView(itemView);
                } while (normalCursor.moveToNext());
            }

            // 显示回收站日记
            if (recycleCursor != null && recycleCursor.moveToFirst()) {
                hasResults = true;
                do {
                    View itemView = getLayoutInflater().inflate(R.layout.item_diary, searchResultContainer, false);
                    TextView tvContent = itemView.findViewById(R.id.tvDiaryContent);
                    TextView tvTime = itemView.findViewById(R.id.tvDiaryTime);
                    TextView tvEmotion = itemView.findViewById(R.id.tvEmotionType);

                    int diaryId = recycleCursor.getInt(recycleCursor.getColumnIndexOrThrow("diaryId"));
                    String content = recycleCursor.getString(recycleCursor.getColumnIndexOrThrow("content"));
                    String time = recycleCursor.getString(recycleCursor.getColumnIndexOrThrow("createTime"));
                    String emotion = recycleCursor.getString(recycleCursor.getColumnIndexOrThrow("emotionType"));

                    // 特殊标注回收站日记
                    tvContent.setText(content + " [回收站]");
                    tvContent.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    tvTime.setText(time);
                    tvTime.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    tvEmotion.setText((emotion != null ? emotion : "一般") + " [回收站]");
                    tvEmotion.setTextColor(getResources().getColor(android.R.color.darker_gray));

                    itemView.setOnClickListener(v -> {
                        // 跳转到回收站页面
                        Intent intent = new Intent(this, DiaryRecycleActivity.class);
                        startActivity(intent);
                    });

                    searchResultContainer.addView(itemView);
                } while (recycleCursor.moveToNext());
            }

            if (!hasResults) {
                View emptyView = getLayoutInflater().inflate(R.layout.item_empty_diary, searchResultContainer, false);
                searchResultContainer.addView(emptyView);
            }

            // 关闭游标
            if (normalCursor != null) {
                normalCursor.close();
            }
            if (recycleCursor != null) {
                recycleCursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "搜索失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
