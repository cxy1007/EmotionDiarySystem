package com.example.emotiondiarysystem.ui.reminder;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.ui.main.MainActivity;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;

public class ReminderSettingActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "diary_reminder_channel";
    private static final String CHANNEL_NAME = "日记提醒";
    private static final int NOTIFICATION_ID = 1001;

    private SwitchMaterial switchReminder;
    private TextView tvReminderTime;
    private MaterialButton btnSave;
    private int selectedHour = 20;
    private int selectedMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reminder_setting);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createNotificationChannel();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        switchReminder = findViewById(R.id.switchReminder);
        tvReminderTime = findViewById(R.id.tvReminderTime);
        btnSave = findViewById(R.id.btnSave);

        boolean reminderEnabled = SpUtil.getBoolean(this, "reminder_enabled", false);
        switchReminder.setChecked(reminderEnabled);

        int savedHour = SpUtil.getInt(this, "reminder_hour", 20);
        int savedMinute = SpUtil.getInt(this, "reminder_minute", 0);
        selectedHour = savedHour;
        selectedMinute = savedMinute;
        tvReminderTime.setText(String.format("%02d:%02d", savedHour, savedMinute));

        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !canScheduleExactAlarms()) {
                switchReminder.setChecked(false);
                Toast.makeText(this, "请允许通知权限", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.cardTime).setOnClickListener(v -> showTimePicker());

        btnSave.setOnClickListener(v -> saveReminder());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("每日日记提醒");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        }
        return true;
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        android.app.TimePickerDialog dialog = new android.app.TimePickerDialog(
                this,
                (view, hourOfDay, minuteOfHour) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minuteOfHour;
                    tvReminderTime.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour));
                },
                hour,
                minute,
                true
        );
        dialog.show();
    }

    private void saveReminder() {
        boolean enabled = switchReminder.isChecked();
        SpUtil.putBoolean(this, "reminder_enabled", enabled);
        SpUtil.putInt(this, "reminder_hour", selectedHour);
        SpUtil.putInt(this, "reminder_minute", selectedMinute);

        if (enabled) {
            scheduleDailyReminder();
            Toast.makeText(this, "提醒已开启", Toast.LENGTH_SHORT).show();
        } else {
            cancelDailyReminder();
            Toast.makeText(this, "提醒已关闭", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void scheduleDailyReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("from_reminder", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        } else {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

    private void cancelDailyReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }
}
