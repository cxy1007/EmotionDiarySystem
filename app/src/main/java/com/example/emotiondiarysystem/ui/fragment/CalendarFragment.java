package com.example.emotiondiarysystem.ui.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.manager.DiaryManager;
import com.example.emotiondiarysystem.ui.diary.DiaryEditActivity;
import com.example.emotiondiarysystem.utils.SpUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private TextView tvYearMonth;
    private ImageView ivDropdown;
    private ImageView ivAdd;
    private GridView gridCalendar;
    private TextView tvSelectedDateHint;
    private RecyclerView recyclerDiaryList;

    private DiaryManager diaryManager;
    private CalendarAdapter calendarAdapter;
    private CalendarDiaryAdapter diaryAdapter;
    private List<Diary> diaryList = new ArrayList<>();

    private int selectedYear;
    private int selectedMonth;
    private int selectedDay = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initManagers();
        initCalendar();
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDiaryList();
    }

    private void initViews(View view) {
        tvYearMonth = view.findViewById(R.id.tv_year_month);
        ivDropdown = view.findViewById(R.id.iv_dropdown);
        ivAdd = view.findViewById(R.id.iv_add);
        gridCalendar = view.findViewById(R.id.grid_calendar);
        tvSelectedDateHint = view.findViewById(R.id.tv_selected_date_hint);
        recyclerDiaryList = view.findViewById(R.id.recycler_diary_list);
    }

    private void initManagers() {
        diaryManager = new DiaryManager(requireContext());
    }

    private void initCalendar() {
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);

        updateYearMonthDisplay();
        calendarAdapter = new CalendarAdapter();
        gridCalendar.setAdapter(calendarAdapter);

        diaryAdapter = new CalendarDiaryAdapter(diaryList);
        recyclerDiaryList.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerDiaryList.setAdapter(diaryAdapter);
    }

    private void updateYearMonthDisplay() {
        tvYearMonth.setText(selectedYear + "年" + (selectedMonth + 1) + "月");
    }

    private void setListeners() {
        ivDropdown.setOnClickListener(v -> showDatePicker());
        ivAdd.setOnClickListener(v -> {
            if (selectedDay == -1) {
                Toast.makeText(requireContext(), "请先选择日期", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(requireContext(), DiaryEditActivity.class);
            String dateStr = selectedYear + "-" + String.format(Locale.getDefault(), "%02d", selectedMonth + 1) + "-" + String.format(Locale.getDefault(), "%02d", selectedDay);
            intent.putExtra("date", dateStr);
            startActivity(intent);
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedYear = year;
                    selectedMonth = month;
                    selectedDay = dayOfMonth;
                    updateYearMonthDisplay();
                    calendarAdapter.notifyDataSetChanged();
                    loadDiaryList();
                },
                selectedYear,
                selectedMonth,
                selectedDay
        );
        datePickerDialog.show();
    }

    private void loadDiaryList() {
        int userId = SpUtil.getInt(requireContext(), "userId", -1);
        if (userId == -1) {
            diaryList.clear();
            diaryAdapter.notifyDataSetChanged();
            return;
        }

        List<Diary> allDiaries = diaryManager.getDiaryListByUserId(userId);
        diaryList.clear();

        if (selectedDay != -1) {
            String targetDate = selectedYear + "-" + String.format(Locale.getDefault(), "%02d", selectedMonth + 1) + "-" + String.format(Locale.getDefault(), "%02d", selectedDay);
            for (Diary diary : allDiaries) {
                if (diary.getCreateTime() != null && diary.getCreateTime().startsWith(targetDate)) {
                    diaryList.add(diary);
                }
            }
        }

        diaryAdapter.notifyDataSetChanged();
    }

    class CalendarAdapter extends android.widget.BaseAdapter {

        @Override
        public int getCount() {
            return 42;
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
                convertView = LayoutInflater.from(requireContext()).inflate(R.layout.item_calendar_day, parent, false);
                holder = new ViewHolder();
                holder.layoutItem = convertView.findViewById(R.id.layout_calendar_item);
                holder.tvDay = convertView.findViewById(R.id.tv_day);
                holder.tvLunar = convertView.findViewById(R.id.tv_lunar);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(selectedYear, selectedMonth, 1);
            int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            int day = position - firstDayOfWeek + 1;

            if (position < firstDayOfWeek || day > daysInMonth) {
                holder.tvDay.setText("");
                holder.tvLunar.setText("");
                holder.layoutItem.setSelected(false);
                holder.tvDay.setTextColor(requireContext().getResources().getColor(android.R.color.darker_gray));
                holder.tvLunar.setTextColor(requireContext().getResources().getColor(android.R.color.darker_gray));
            } else {
                holder.tvDay.setText(String.valueOf(day));
                holder.tvLunar.setText(getLunarDay(day));

                boolean isSelected = (day == selectedDay);
                holder.layoutItem.setSelected(isSelected);

                if (isSelected) {
                    holder.layoutItem.setBackgroundResource(R.drawable.bg_calendar_item_selected);
                    holder.tvDay.setTextColor(requireContext().getResources().getColor(android.R.color.white));
                    holder.tvLunar.setTextColor(requireContext().getResources().getColor(android.R.color.white));
                } else {
                    holder.layoutItem.setBackgroundResource(android.R.color.transparent);
                    holder.tvDay.setTextColor(requireContext().getResources().getColor(R.color.text_primary));
                    holder.tvLunar.setTextColor(requireContext().getResources().getColor(R.color.text_secondary));
                }

                final int finalDay = day;
                holder.layoutItem.setOnClickListener(v -> {
                    selectedDay = finalDay;
                    notifyDataSetChanged();
                    loadDiaryList();
                });
            }

            return convertView;
        }

        private String getLunarDay(int day) {
            String[] lunarDays = {"初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
                    "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
                    "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"};
            if (day >= 1 && day <= 30) {
                return lunarDays[day - 1];
            }
            return "";
        }

        class ViewHolder {
            View layoutItem;
            TextView tvDay;
            TextView tvLunar;
        }
    }

    class CalendarDiaryAdapter extends RecyclerView.Adapter<CalendarDiaryAdapter.ViewHolder> {
        private List<Diary> diaries;

        public CalendarDiaryAdapter(List<Diary> diaries) {
            this.diaries = diaries;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_calendar_diary, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Diary diary = diaries.get(position);
            String date = diary.getCreateTime();
            if (date != null && date.length() >= 10) {
                String day = date.substring(8, 10);
                String month = date.substring(5, 7);
                holder.tvDiaryDay.setText(day);
                holder.tvDiaryMonth.setText(Integer.parseInt(month) + "月");
            }

            String content = diary.getContent();
            if (content != null && !content.isEmpty()) {
                holder.tvDiaryContent.setText(content);
            }

            // 显示日记标题（如果有）
            String title = diary.getTitle();
            if (title != null && !title.isEmpty()) {
                holder.tvDiaryTitle.setText(title.length() > 10 ? title.substring(0, 10) + "..." : title);
            } else {
                // 如果没有标题，用内容第一行
                if (content != null && !content.isEmpty()) {
                    String[] lines = content.split("\n");
                    holder.tvDiaryTitle.setText(lines[0].length() > 10 ? lines[0].substring(0, 10) + "..." : lines[0]);
                }
            }

            // 显示真正保存的标签
            String weatherTag = diary.getWeatherTag();
            if (weatherTag != null && !weatherTag.isEmpty()) {
                holder.tvTagWeather.setText(weatherTag);
                holder.tvTagWeather.setVisibility(View.VISIBLE);
            } else {
                holder.tvTagWeather.setVisibility(View.GONE);
            }

            String moodTag = diary.getMoodTag();
            if (moodTag != null && !moodTag.isEmpty()) {
                holder.tvTagMood.setText(moodTag);
                holder.tvTagMood.setVisibility(View.VISIBLE);
            } else {
                holder.tvTagMood.setVisibility(View.GONE);
            }

            String activityTag = diary.getActivityTag();
            if (activityTag != null && !activityTag.isEmpty()) {
                holder.tvTagCategory.setText(activityTag);
                holder.tvTagCategory.setVisibility(View.VISIBLE);
            } else {
                holder.tvTagCategory.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), DiaryEditActivity.class);
                intent.putExtra("diaryId", diary.getDiaryId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return diaries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDiaryDay;
            TextView tvDiaryMonth;
            TextView tvDiaryTitle;
            TextView tvDiaryContent;
            TextView tvTagWeather;
            TextView tvTagMood;
            TextView tvTagCategory;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDiaryDay = itemView.findViewById(R.id.tv_diary_day);
                tvDiaryMonth = itemView.findViewById(R.id.tv_diary_month);
                tvDiaryTitle = itemView.findViewById(R.id.tv_diary_title);
                tvDiaryContent = itemView.findViewById(R.id.tv_diary_content);
                tvTagWeather = itemView.findViewById(R.id.tv_tag_weather);
                tvTagMood = itemView.findViewById(R.id.tv_tag_mood);
                tvTagCategory = itemView.findViewById(R.id.tv_tag_category);
            }
        }
    }
}
