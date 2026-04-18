package com.example.emotiondiarysystem.ui.emotion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.emotiondiarysystem.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private Calendar calendar;
    private int year;
    private int month;
    private int firstDayOfMonth;
    private int daysInMonth;
    private Map<Integer, Integer> moodScores; // 日期 -> 心情指数

    public CalendarAdapter(Context context, int year, int month, Map<Integer, Integer> moodScores) {
        this.context = context;
        this.year = year;
        this.month = month;
        this.moodScores = moodScores != null ? moodScores : new HashMap<>();
        
        // 初始化日历
        calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0-6
        daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public int getCount() {
        return 42; // 6行7列
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
            holder = new ViewHolder();
            holder.dayText = convertView.findViewById(R.id.tvDay);
            holder.moodCircle = convertView.findViewById(R.id.moodCircle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 计算实际日期
        int day = position - firstDayOfMonth + 1;
        if (day < 1 || day > daysInMonth) {
            // 非当月日期
            holder.dayText.setText("");
            holder.moodCircle.setVisibility(View.GONE);
        } else {
            // 当月日期
            holder.dayText.setText(String.valueOf(day));
            holder.moodCircle.setVisibility(View.VISIBLE);
            Integer moodScore = moodScores.get(day);
            if (moodScore != null) {
                holder.moodCircle.setProgress(moodScore);
            } else {
                // 没有心情数据时显示默认背景圆环
                holder.moodCircle.setProgress(0);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView dayText;
        MoodCircleView moodCircle;
    }
}
