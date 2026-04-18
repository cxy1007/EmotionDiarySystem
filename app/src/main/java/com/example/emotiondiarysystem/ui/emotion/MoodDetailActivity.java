package com.example.emotiondiarysystem.ui.emotion;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import com.example.emotiondiarysystem.ui.BaseActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.db.DBHelper;
import com.example.emotiondiarysystem.utils.SpUtil;

public class MoodDetailActivity extends BaseActivity {

    private LinearLayout checkinListContainer;
    private TextView tvDateTitle;
    private DBHelper dbHelper;
    private int year;
    private int month;
    private int day;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mood_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("打卡详情");
        }

        tvDateTitle = findViewById(R.id.tvDateTitle);
        checkinListContainer = findViewById(R.id.checkinListContainer);

        dbHelper = DBHelper.getInstance(this);
        userId = SpUtil.getInt(this, "userId", 1);

        year = getIntent().getIntExtra("year", 2024);
        month = getIntent().getIntExtra("month", 1);
        day = getIntent().getIntExtra("day", 1);

        tvDateTitle.setText(year + "年" + month + "月" + day + "日");

        loadDayCheckins();
    }

    private void loadDayCheckins() {
        String monthStr = month < 10 ? "0" + month : String.valueOf(month);
        String dayStr = day < 10 ? "0" + day : String.valueOf(day);
        String targetDate = year + "-" + monthStr + "-" + dayStr;

        String sql = "SELECT mood_score, mood_tag, note, create_time " +
                "FROM mood_checkin " +
                "WHERE user_id=? AND DATE(create_time)=? " +
                "ORDER BY create_time DESC";

        Cursor cursor = null;

        try {
            cursor = dbHelper.getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(userId), targetDate});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int moodScore = cursor.getInt(cursor.getColumnIndexOrThrow("mood_score"));
                    String moodTag = cursor.getString(cursor.getColumnIndexOrThrow("mood_tag"));
                    String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                    String createTime = cursor.getString(cursor.getColumnIndexOrThrow("create_time"));

                    View checkinItem = getLayoutInflater().inflate(R.layout.item_mood_checkin, null);
                    TextView tvMoodScore = checkinItem.findViewById(R.id.tvMoodScore);
                    TextView tvMoodTag = checkinItem.findViewById(R.id.tvMoodTag);
                    TextView tvNote = checkinItem.findViewById(R.id.tvNote);
                    TextView tvTime = checkinItem.findViewById(R.id.tvTime);

                    tvMoodScore.setText(String.valueOf(moodScore));
                    tvMoodTag.setText(getMoodEmoji(moodScore) + " " + moodTag);
                    tvNote.setText(note != null && !note.isEmpty() ? note : "无备注");
                    tvTime.setText(formatTime(createTime));

                    checkinListContainer.addView(checkinItem);
                } while (cursor.moveToNext());
            } else {
                showEmptyState();
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载打卡记录失败", Toast.LENGTH_SHORT).show();
            showEmptyState();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // 加载当日日记链接
        loadDayDiary();
    }

    private void loadDayDiary() {
        String monthStr = month < 10 ? "0" + month : String.valueOf(month);
        String dayStr = day < 10 ? "0" + day : String.valueOf(day);
        String targetDate = year + "-" + monthStr + "-" + dayStr;

        // 查询当天的日记
        String diarySql = "SELECT id, title, content " +
                "FROM diary " +
                "WHERE user_id=? AND DATE(create_time)=? AND is_deleted=0 " +
                "ORDER BY create_time DESC LIMIT 1";

        Cursor cursor = null;

        try {
            cursor = dbHelper.getReadableDatabase().rawQuery(diarySql, new String[]{String.valueOf(userId), targetDate});

            // 无论是否有日记，都添加一个日记链接区域
            View diaryContainer = getLayoutInflater().inflate(R.layout.item_mood_checkin, null);
            TextView tvMoodScore = diaryContainer.findViewById(R.id.tvMoodScore);
            TextView tvMoodTag = diaryContainer.findViewById(R.id.tvMoodTag);
            TextView tvNote = diaryContainer.findViewById(R.id.tvNote);
            TextView tvTime = diaryContainer.findViewById(R.id.tvTime);

            if (cursor != null && cursor.moveToFirst()) {
                int diaryId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                
                tvMoodScore.setText("📝");
                tvMoodTag.setText("当日日记");
                tvNote.setText(title != null && !title.isEmpty() ? title : "无标题");
                tvTime.setText("查看日记");

                // 添加点击事件，跳转到日记详情页
                diaryContainer.setOnClickListener(v -> {
                    try {
                        Class<?> diaryDetailClass = Class.forName("com.example.emotiondiarysystem.ui.diary.DiaryDetailActivity");
                        Intent intent = new Intent(MoodDetailActivity.this, diaryDetailClass);
                        intent.putExtra("diaryId", diaryId);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MoodDetailActivity.this, "日记详情页不存在", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // 没有日记时显示提示
                tvMoodScore.setText("📝");
                tvMoodTag.setText("当日日记");
                tvNote.setText("当天暂无日记");
                tvTime.setText("去写日记");

                // 添加点击事件，跳转到写日记页面
                diaryContainer.setOnClickListener(v -> {
                    try {
                        Class<?> diaryEditClass = Class.forName("com.example.emotiondiarysystem.ui.diary.DiaryEditActivity");
                        Intent intent = new Intent(MoodDetailActivity.this, diaryEditClass);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MoodDetailActivity.this, "写日记页面不存在", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            checkinListContainer.addView(diaryContainer);
        } catch (Exception e) {
            // 加载日记失败，不影响打卡记录显示
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void showEmptyState() {
        View emptyView = getLayoutInflater().inflate(R.layout.item_empty_state, null);
        TextView tvEmpty = emptyView.findViewById(R.id.tvEmpty);
        tvEmpty.setText("当天暂无打卡记录");
        checkinListContainer.addView(emptyView);
    }

    private String getMoodEmoji(int score) {
        if (score <= 25) return "😢";
        if (score <= 50) return "😞";
        if (score <= 75) return "😐";
        if (score <= 90) return "🙂";
        return "😄";
    }

    private String formatTime(String createTime) {
        if (createTime == null || createTime.length() < 19) return createTime;
        return createTime.substring(11, 16);
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