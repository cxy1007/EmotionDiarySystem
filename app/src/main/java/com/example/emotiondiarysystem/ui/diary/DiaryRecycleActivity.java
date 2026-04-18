package com.example.emotiondiarysystem.ui.diary;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

public class DiaryRecycleActivity extends AppCompatActivity {

    private LinearLayout recycleListContainer;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diary_recycle);

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

        recycleListContainer = findViewById(R.id.recycleListContainer);
        dbHelper = DBHelper.getInstance(this);

        findViewById(R.id.btnClearAll).setOnClickListener(v -> showClearAllDialog());

        loadRecycleList();
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
        loadRecycleList();
    }

    private void loadRecycleList() {
        if (recycleListContainer != null) {
            recycleListContainer.removeAllViews();
        }

        int userId = SpUtil.getInt(this, "userId", 1);
        android.database.Cursor cursor = null;
        try {
            cursor = dbHelper.getDeletedDiaries(userId);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    View itemView = getLayoutInflater().inflate(R.layout.item_diary, recycleListContainer, false);
                    TextView tvContent = itemView.findViewById(R.id.tvDiaryContent);
                    TextView tvTime = itemView.findViewById(R.id.tvDiaryTime);
                    TextView tvEmotion = itemView.findViewById(R.id.tvEmotionType);

                    int diaryId = cursor.getInt(cursor.getColumnIndex("diaryId"));
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    String time = cursor.getString(cursor.getColumnIndex("createTime"));
                    String emotion = cursor.getString(cursor.getColumnIndex("emotionType"));

                    tvContent.setText(content);
                    tvTime.setText(time);
                    tvEmotion.setText(emotion != null ? emotion : "一般");

                    final int finalDiaryId = diaryId;
                    itemView.setOnClickListener(v -> showItemDialog(finalDiaryId));

                    recycleListContainer.addView(itemView);
                } while (cursor.moveToNext());
            } else {
                View emptyView = getLayoutInflater().inflate(R.layout.item_empty_diary, recycleListContainer, false);
                recycleListContainer.addView(emptyView);
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void showItemDialog(final int diaryId) {
        new AlertDialog.Builder(this)
                .setTitle("操作")
                .setItems(new String[]{"恢复", "彻底删除"}, (dialog, which) -> {
                    if (which == 0) {
                        restoreDiary(diaryId);
                    } else {
                        showPermanentDeleteDialog(diaryId);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void restoreDiary(int diaryId) {
        int result = dbHelper.restoreDiary(diaryId);
        if (result > 0) {
            Toast.makeText(this, "日记已恢复", Toast.LENGTH_SHORT).show();
            loadRecycleList();
        } else {
            Toast.makeText(this, "恢复失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPermanentDeleteDialog(final int diaryId) {
        new AlertDialog.Builder(this)
                .setTitle("彻底删除")
                .setMessage("确定要彻底删除这篇日记吗？此操作不可恢复！")
                .setPositiveButton("彻底删除", (dialog, which) -> permanentlyDeleteDiary(diaryId))
                .setNegativeButton("取消", null)
                .show();
    }

    private void permanentlyDeleteDiary(int diaryId) {
        int result = dbHelper.permanentlyDeleteDiary(diaryId);
        if (result > 0) {
            Toast.makeText(this, "日记已彻底删除", Toast.LENGTH_SHORT).show();
            loadRecycleList();
        } else {
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showClearAllDialog() {
        new AlertDialog.Builder(this)
                .setTitle("清空回收站")
                .setMessage("确定要清空回收站吗？此操作不可恢复！")
                .setPositiveButton("清空", (dialog, which) -> clearAllRecycle())
                .setNegativeButton("取消", null)
                .show();
    }

    private void clearAllRecycle() {
        int userId = SpUtil.getInt(this, "userId", 1);
        android.database.Cursor cursor = null;
        int deletedCount = 0;
        try {
            cursor = dbHelper.getDeletedDiaries(userId);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int diaryId = cursor.getInt(cursor.getColumnIndex("diaryId"));
                    int result = dbHelper.permanentlyDeleteDiary(diaryId);
                    if (result > 0) {
                        deletedCount++;
                    }
                } while (cursor.moveToNext());
            }
            Toast.makeText(this, "已清空" + deletedCount + "篇日记", Toast.LENGTH_SHORT).show();
            loadRecycleList();
        } catch (Exception e) {
            Toast.makeText(this, "清空失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
