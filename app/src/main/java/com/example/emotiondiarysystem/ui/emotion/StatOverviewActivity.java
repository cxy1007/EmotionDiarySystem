package com.example.emotiondiarysystem.ui.emotion;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
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
import com.example.emotiondiarysystem.manager.MoodManager;
import com.example.emotiondiarysystem.utils.SpUtil;

public class StatOverviewActivity extends AppCompatActivity {

    private TextView tvTotalDiaries, tvAvgMoodScore, tvTotalCheckins, tvCheckinStreak, tvMonthTitle, tvYearTitle;
    private LinearLayout checkinListContainer;
    private GridView calendarGridView;
    private DBHelper dbHelper;
    private int currentYear;
    private int currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stat_overview);

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

        tvTotalDiaries = findViewById(R.id.tvTotalDiaries);
        tvAvgMoodScore = findViewById(R.id.tvAvgMoodScore);
        tvTotalCheckins = findViewById(R.id.tvTotalCheckins);
        tvCheckinStreak = findViewById(R.id.tvCheckinStreak);
        checkinListContainer = findViewById(R.id.checkinListContainer);
        calendarGridView = findViewById(R.id.calendarGridView);
        tvMonthTitle = findViewById(R.id.tvMonthTitle);
        tvYearTitle = findViewById(R.id.tvYearTitle);
        Button btnPrevMonth = findViewById(R.id.btnPrevMonth);
        Button btnNextMonth = findViewById(R.id.btnNextMonth);
        Button btnPrevYear = findViewById(R.id.btnPrevYear);
        Button btnNextYear = findViewById(R.id.btnNextYear);

        dbHelper = DBHelper.getInstance(this);

        // 初始化当前年份和月份
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        currentYear = calendar.get(java.util.Calendar.YEAR);
        currentMonth = calendar.get(java.util.Calendar.MONTH) + 1;

        updateYearMonthTitle();
        loadStatistics();

        // 绑定月份翻页按钮点击事件
        btnPrevMonth.setOnClickListener(v -> {
            // 月份减 1
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
                updateYearMonthTitle();
            } else {
                tvMonthTitle.setText(currentMonth + "月");
            }
            loadStatistics();
        });

        btnNextMonth.setOnClickListener(v -> {
            // 月份加 1
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
                updateYearMonthTitle();
            } else {
                tvMonthTitle.setText(currentMonth + "月");
            }
            loadStatistics();
        });

        // 绑定年份翻页按钮点击事件
        btnPrevYear.setOnClickListener(v -> {
            // 年份减 1
            currentYear--;
            tvYearTitle.setText(currentYear + "年");
            loadStatistics();
        });

        btnNextYear.setOnClickListener(v -> {
            // 年份加 1
            currentYear++;
            tvYearTitle.setText(currentYear + "年");
            loadStatistics();
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

    private void updateYearMonthTitle() {
        tvYearTitle.setText(currentYear + "年");
        tvMonthTitle.setText(currentMonth + "月");
    }

    private void loadStatistics() {
        int userId = SpUtil.getInt(this, "userId", 1);
        Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().query("diary", new String[]{"COUNT(*)"},
                    "userId=? AND is_deleted = 0", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int totalDiaries = cursor.getInt(0);
                tvTotalDiaries.setText(String.valueOf(totalDiaries));
            }

            cursor.close();

            MoodManager moodManager = MoodManager.getInstance(this);
            double avgScore = moodManager.getAverageMoodScore(userId);
            tvAvgMoodScore.setText(String.format("%.1f", avgScore));

            // 计算打卡总次数
            cursor = dbHelper.getReadableDatabase().query("mood_checkin", new String[]{"COUNT(*)"},
                    "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int totalCheckins = cursor.getInt(0);
                tvTotalCheckins.setText(String.valueOf(totalCheckins));
            }
            cursor.close();

            // 计算连续打卡天数
            String streakSql = "SELECT DISTINCT DATE(create_time) as checkin_date " +
                             "FROM mood_checkin " +
                             "WHERE user_id=? " +
                             "ORDER BY checkin_date DESC";
            cursor = dbHelper.getReadableDatabase().rawQuery(streakSql, new String[]{String.valueOf(userId)});
            int streakDays = 0;
            if (cursor != null && cursor.moveToFirst()) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                java.util.Date today = calendar.getTime();
                
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                
                do {
                    String checkinDateStr = cursor.getString(cursor.getColumnIndexOrThrow("checkin_date"));
                    try {
                        java.util.Date checkinDate = sdf.parse(checkinDateStr);
                        
                        // 计算日期差
                        long diff = today.getTime() - checkinDate.getTime();
                        long diffDays = diff / (24 * 60 * 60 * 1000);
                        
                        if (diffDays == streakDays) {
                            streakDays++;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                } while (cursor.moveToNext());
            }
            tvCheckinStreak.setText(streakDays + "天");

            cursor.close();

            // 加载当月打卡记录
            loadMonthlyCheckins(userId);
        } catch (Exception e) {
            Toast.makeText(this, "加载统计失败", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void loadMonthlyCheckins(int userId) {
        checkinListContainer.removeAllViews();
        
        // 获取当前月份的开始日期（格式：yyyy-MM-01）
        String monthStr = currentMonth < 10 ? "0" + currentMonth : String.valueOf(currentMonth);
        String startDate = currentYear + "-" + monthStr + "-01";
        
        // 计算下个月的第一天作为结束日期
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(currentYear, currentMonth - 1, 1);
        calendar.add(java.util.Calendar.MONTH, 1);
        int nextYear = calendar.get(java.util.Calendar.YEAR);
        int nextMonth = calendar.get(java.util.Calendar.MONTH) + 1;
        String nextMonthStr = nextMonth < 10 ? "0" + nextMonth : String.valueOf(nextMonth);
        String endDate = nextYear + "-" + nextMonthStr + "-01";
        
        // 构建查询，按日期分组计算平均心情指数
        String sql = "SELECT DATE(create_time) as checkin_date, AVG(mood_score) as avg_score " +
                     "FROM mood_checkin " +
                     "WHERE user_id=? AND create_time >= ? AND create_time < ? " +
                     "GROUP BY DATE(create_time) " +
                     "ORDER BY checkin_date DESC";
        
        Cursor cursor = null;
        java.util.Map<Integer, Integer> moodScores = new java.util.HashMap<>();
        
        try {
            cursor = dbHelper.getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(userId), startDate, endDate});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String checkinDate = cursor.getString(cursor.getColumnIndexOrThrow("checkin_date"));
                    double avgScore = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_score"));
                    
                    // 格式化日期显示（例如：2024-04-18 -> 4月18日）
                    String[] dateParts = checkinDate.split("-");
                    String displayDate = dateParts[1] + "月" + dateParts[2] + "日";
                    int day = Integer.parseInt(dateParts[2]);
                    int score = (int) Math.round(avgScore);
                    
                    // 存储日期和心情指数
                    moodScores.put(day, score);
                    
                    // 创建显示条目
                    TextView checkinItem = new TextView(this);
                    checkinItem.setText(displayDate + " 平均心情指数：" + score);
                    checkinItem.setTextSize(14);
                    checkinItem.setPadding(0, 8, 0, 8);
                    
                    checkinListContainer.addView(checkinItem);
                } while (cursor.moveToNext());
            } else {
                // 显示空状态
                TextView emptyItem = new TextView(this);
                emptyItem.setText("本月暂无打卡记录");
                emptyItem.setTextSize(14);
                emptyItem.setTextColor(getResources().getColor(android.R.color.darker_gray));
                emptyItem.setPadding(0, 16, 0, 16);
                checkinListContainer.addView(emptyItem);
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载打卡记录失败", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        // 初始化日历视图
        initCalendarView(currentYear, currentMonth, moodScores, userId);
    }

    private void initCalendarView(int year, int month, java.util.Map<Integer, Integer> moodScores, int userId) {
        CalendarAdapter adapter = new CalendarAdapter(this, year, month, moodScores);
        calendarGridView.setAdapter(adapter);
        
        // 添加点击事件
        calendarGridView.setOnItemClickListener((parent, view, position, id) -> {
            // 计算实际日期
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(year, month - 1, 1);
            int firstDayOfMonth = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1; // 0-6
            int day = position - firstDayOfMonth + 1;
            
            if (day >= 1 && day <= calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)) {
                // 显示当天的详细打卡记录
                showDayCheckins(userId, year, month, day);
            }
        });
    }

    private void showDayCheckins(int userId, int year, int month, int day) {
        // 构建日期字符串
        String monthStr = month < 10 ? "0" + month : String.valueOf(month);
        String dayStr = day < 10 ? "0" + day : String.valueOf(day);
        String targetDate = year + "-" + monthStr + "-" + dayStr;
        
        // 查询当天的打卡记录
        String sql = "SELECT mood_score, note, create_time " +
                     "FROM mood_checkin " +
                     "WHERE user_id=? AND DATE(create_time)=? " +
                     "ORDER BY create_time DESC";
        
        Cursor cursor = null;
        StringBuilder checkinsBuilder = new StringBuilder();
        
        try {
            cursor = dbHelper.getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(userId), targetDate});
            
            if (cursor != null && cursor.moveToFirst()) {
                int count = 0;
                do {
                    int moodScore = cursor.getInt(cursor.getColumnIndexOrThrow("mood_score"));
                    String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                    String createTime = cursor.getString(cursor.getColumnIndexOrThrow("create_time"));
                    
                    count++;
                    checkinsBuilder.append("记录 " + count + "\n");
                    checkinsBuilder.append("时间：" + createTime + "\n");
                    checkinsBuilder.append("心情指数：" + moodScore + "\n");
                    checkinsBuilder.append("备注：" + (note != null && !note.isEmpty() ? note : "无") + "\n\n");
                } while (cursor.moveToNext());
            } else {
                checkinsBuilder.append("当天无打卡记录");
            }
        } catch (Exception e) {
            checkinsBuilder.append("加载失败：" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        // 显示对话框
        new android.app.AlertDialog.Builder(this)
            .setTitle(year + "年" + month + "月" + day + "日 打卡记录")
            .setMessage(checkinsBuilder.toString())
            .setPositiveButton("确定", null)
            .show();
    }
}
