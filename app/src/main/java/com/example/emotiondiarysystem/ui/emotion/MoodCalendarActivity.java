package com.example.emotiondiarysystem.ui.emotion;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MoodCalendarActivity extends BaseActivity {

    private GridView calendarGridView;
    private TextView tvMonthTitle;
    private DBHelper dbHelper;
    private int currentYear;
    private int currentMonth;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mood_calendar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("心情日历");
        }

        calendarGridView = findViewById(R.id.calendarGridView);
        tvMonthTitle = findViewById(R.id.tvMonthTitle);
        TextView tvYearTitle = findViewById(R.id.tvYearTitle);
        Button btnPrevMonth = findViewById(R.id.btnPrevMonth);
        Button btnNextMonth = findViewById(R.id.btnNextMonth);
        Button btnPrevYear = findViewById(R.id.btnPrevYear);
        Button btnNextYear = findViewById(R.id.btnNextYear);

        dbHelper = DBHelper.getInstance(this);
        userId = SpUtil.getInt(this, "userId", 1);

        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;

        updateYearMonthTitle(tvYearTitle, tvMonthTitle);
        loadMonthlyCheckins();

        // 绑定月份翻页按钮点击事件
        btnPrevMonth.setOnClickListener(v -> {
            // 月份减 1
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
                updateYearMonthTitle(tvYearTitle, tvMonthTitle);
            } else {
                tvMonthTitle.setText(currentMonth + "月");
            }
            loadMonthlyCheckins();
        });

        btnNextMonth.setOnClickListener(v -> {
            // 月份加 1
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
                updateYearMonthTitle(tvYearTitle, tvMonthTitle);
            } else {
                tvMonthTitle.setText(currentMonth + "月");
            }
            loadMonthlyCheckins();
        });

        // 绑定年份翻页按钮点击事件
        btnPrevYear.setOnClickListener(v -> {
            // 年份减 1
            currentYear--;
            tvYearTitle.setText(currentYear + "年");
            loadMonthlyCheckins();
        });

        btnNextYear.setOnClickListener(v -> {
            // 年份加 1
            currentYear++;
            tvYearTitle.setText(currentYear + "年");
            loadMonthlyCheckins();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMonthlyCheckins();
    }

    private void updateYearMonthTitle(TextView tvYearTitle, TextView tvMonthTitle) {
        tvYearTitle.setText(currentYear + "年");
        tvMonthTitle.setText(currentMonth + "月");
    }

    private void loadMonthlyCheckins() {
        String monthStr = currentMonth < 10 ? "0" + currentMonth : String.valueOf(currentMonth);
        String startDate = currentYear + "-" + monthStr + "-01";
        
        // 计算下个月的第一天作为结束日期
        Calendar calendar = Calendar.getInstance();
        calendar.set(currentYear, currentMonth - 1, 1);
        calendar.add(Calendar.MONTH, 1);
        int nextYear = calendar.get(Calendar.YEAR);
        int nextMonth = calendar.get(Calendar.MONTH) + 1;
        String nextMonthStr = nextMonth < 10 ? "0" + nextMonth : String.valueOf(nextMonth);
        String endDate = nextYear + "-" + nextMonthStr + "-01";

        String sql = "SELECT DATE(create_time) as checkin_date, AVG(mood_score) as avg_score " +
                "FROM mood_checkin " +
                "WHERE user_id=? AND create_time >= ? AND create_time < ? " +
                "GROUP BY DATE(create_time) " +
                "ORDER BY checkin_date DESC";

        Cursor cursor = null;
        Map<Integer, Integer> moodScores = new HashMap<>();

        try {
            cursor = dbHelper.getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(userId), startDate, endDate});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String checkinDate = cursor.getString(cursor.getColumnIndexOrThrow("checkin_date"));
                    double avgScore = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_score"));

                    String[] dateParts = checkinDate.split("-");
                    int day = Integer.parseInt(dateParts[2]);
                    int score = (int) Math.round(avgScore);

                    moodScores.put(day, score);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载打卡记录失败", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        initCalendarView(moodScores);
    }

    private void initCalendarView(Map<Integer, Integer> moodScores) {
        CalendarAdapter adapter = new CalendarAdapter(this, currentYear, currentMonth, moodScores);
        calendarGridView.setAdapter(adapter);

        calendarGridView.setOnItemClickListener((parent, view, position, id) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(currentYear, currentMonth - 1, 1);
            int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int day = position - firstDayOfMonth + 1;

            if (day >= 1 && day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                Intent intent = new Intent(MoodCalendarActivity.this, MoodDetailActivity.class);
                intent.putExtra("year", currentYear);
                intent.putExtra("month", currentMonth);
                intent.putExtra("day", day);
                startActivity(intent);
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
}