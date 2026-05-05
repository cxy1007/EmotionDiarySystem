package com.example.emotiondiarysystem.ui.diary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.manager.DiaryManager;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class DiarySearchActivity extends BaseActivity implements View.OnClickListener {

    private EditText etSearch;
    private ImageButton btnClear;
    private ImageButton btnSearch;
    private ImageButton btnBack;
    private RecyclerView rvSearchResults;
    private LinearLayout tvEmpty;
    private LinearLayout topBar;
    private View divider;

    private DiaryManager diaryManager;
    private SearchAdapter adapter;
    private List<Diary> searchResults = new ArrayList<>();
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_search);

        initViews();
        initManagers();
        setListeners();
        applyFullTheme();
        
        // 自动聚焦搜索框
        etSearch.requestFocus();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        btnClear = findViewById(R.id.btn_clear);
        btnSearch = findViewById(R.id.btn_search);
        btnBack = findViewById(R.id.btn_back);
        rvSearchResults = findViewById(R.id.rv_search_results);
        tvEmpty = findViewById(R.id.tv_empty);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initManagers() {
        diaryManager = new DiaryManager(this);
    }

    private void setListeners() {
        btnClear.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 显示/隐藏清除按钮
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

                // 延迟300ms执行搜索
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> performSearch(s.toString());
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_clear) {
            etSearch.setText("");
            searchResults.clear();
            updateSearchResults();
        } else if (v.getId() == R.id.btn_search) {
            performSearch(etSearch.getText().toString());
        } else if (v.getId() == R.id.btn_back) {
            finish();
        }
    }

    private void performSearch(String keyword) {
        int userId = SpUtil.getInt(this, "userId", -1);
        if (userId == -1) {
            updateSearchResults();
            return;
        }

        // 获取所有日记（包括已删除的）
        List<Diary> allDiaries = new ArrayList<>();
        allDiaries.addAll(diaryManager.getDiaryListByUserId(userId));
        allDiaries.addAll(diaryManager.getDeletedDiaryListByUserId(userId));

        searchResults.clear();

        if (keyword.isEmpty()) {
            updateSearchResults();
            return;
        }

        String lowerKeyword = keyword.toLowerCase();

        // 搜索逻辑
        for (Diary diary : allDiaries) {
            boolean matched = false;

            // 1. 标题搜索（不区分大小写）
            if (diary.getTitle() != null && diary.getTitle().toLowerCase().contains(lowerKeyword)) {
                matched = true;
            }

            // 2. 内容搜索（不区分大小写）
            if (diary.getContent() != null && diary.getContent().toLowerCase().contains(lowerKeyword)) {
                matched = true;
            }

            // 3. 日期搜索
            if (diary.getCreateTime() != null && diary.getCreateTime().contains(keyword)) {
                matched = true;
            }

            // 4. 情绪标签搜索（不区分大小写）
            if (diary.getEmotionType() != null && diary.getEmotionType().toLowerCase().contains(lowerKeyword)) {
                matched = true;
            }

            // 5. 天气标签搜索（不区分大小写）
            if (diary.getWeatherTag() != null && diary.getWeatherTag().toLowerCase().contains(lowerKeyword)) {
                matched = true;
            }

            // 6. 心情标签搜索（不区分大小写）
            if (diary.getMoodTag() != null && diary.getMoodTag().toLowerCase().contains(lowerKeyword)) {
                matched = true;
            }

            // 7. 活动标签搜索（不区分大小写）
            if (diary.getActivityTag() != null && diary.getActivityTag().toLowerCase().contains(lowerKeyword)) {
                matched = true;
            }

            if (matched) {
                searchResults.add(diary);
            }
        }

        updateSearchResults();
    }

    private void updateSearchResults() {
        if (searchResults.isEmpty()) {
            rvSearchResults.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvSearchResults.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            adapter = new SearchAdapter(searchResults, etSearch.getText().toString());
            rvSearchResults.setAdapter(adapter);
        }
    }

    private void applyFullTheme() {
        ThemeColorUtil.ThemeColors colors = getCurrentColors();
        if (colors == null) colors = ThemeColorUtil.getCurrentTheme(this);
        boolean isDark = ThemeColorUtil.isDarkMode(this);

        View main = findViewById(R.id.main);
        if (main != null) main.setBackgroundColor(colors.background);

        if (topBar != null) topBar.setBackgroundColor(colors.surface);
        if (divider != null) divider.setBackgroundColor(colors.divider);

        btnBack.setColorFilter(colors.iconTint);
        btnClear.setColorFilter(colors.iconTint);
        btnSearch.setColorFilter(colors.iconTint);

        etSearch.setTextColor(colors.textPrimary);
        etSearch.setHintTextColor(colors.textSecondary);

        // 设置空状态提示文本颜色
        if (tvEmpty != null) {
            TextView textView = tvEmpty.findViewById(android.R.id.text1);
            if (textView == null) {
                // 如果找不到text1，遍历子视图寻找TextView
                for (int i = 0; i < tvEmpty.getChildCount(); i++) {
                    View child = tvEmpty.getChildAt(i);
                    if (child instanceof TextView) {
                        textView = (TextView) child;
                        break;
                    }
                }
            }
            if (textView != null) {
                textView.setTextColor(colors.textSecondary);
            }
        }

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

        private List<Diary> diaries;
        private String keyword;

        public SearchAdapter(List<Diary> diaries, String keyword) {
            this.diaries = diaries;
            this.keyword = keyword;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_search_result, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Diary diary = diaries.get(position);

            // 日期
            String createTime = diary.getCreateTime();
            if (createTime != null && createTime.length() >= 10) {
                holder.tvDate.setText(createTime.substring(0, 10));
            } else {
                holder.tvDate.setText("");
            }

            // 回收站标签
            if (diary.isDeleted()) {
                holder.tvRecycleTag.setVisibility(View.VISIBLE);
            } else {
                holder.tvRecycleTag.setVisibility(View.GONE);
            }

            // 情感标签
            String emotionType = diary.getEmotionType();
            if (emotionType != null) {
                holder.tvEmotion.setText(emotionType);
                // 设置情感标签颜色
                switch (emotionType) {
                    case "积极":
                        holder.tvEmotion.setBackgroundColor(0xFF48BB78);
                        break;
                    case "消极":
                        holder.tvEmotion.setBackgroundColor(0xFFF56565);
                        break;
                    default:
                        holder.tvEmotion.setBackgroundColor(0xFFECC94B);
                        break;
                }
            } else {
                holder.tvEmotion.setText("");
            }

            // 标题
            String title = diary.getTitle();
            if (title != null && !title.isEmpty()) {
                holder.tvTitle.setText(title);
                holder.tvTitle.setVisibility(View.VISIBLE);
            } else {
                holder.tvTitle.setVisibility(View.GONE);
            }

            // 内容
            String content = diary.getContent();
            if (content != null) {
                holder.tvContent.setText(content);
            } else {
                holder.tvContent.setText("");
            }

            // 天气标签
            String weatherTag = diary.getWeatherTag();
            if (weatherTag != null && !weatherTag.isEmpty()) {
                holder.tvTagWeather.setText(weatherTag);
                holder.tvTagWeather.setVisibility(View.VISIBLE);
            } else {
                holder.tvTagWeather.setVisibility(View.GONE);
            }

            // 心情标签
            String moodTag = diary.getMoodTag();
            if (moodTag != null && !moodTag.isEmpty()) {
                holder.tvTagMood.setText(moodTag);
                holder.tvTagMood.setVisibility(View.VISIBLE);
            } else {
                holder.tvTagMood.setVisibility(View.GONE);
            }

            // 活动标签
            String activityTag = diary.getActivityTag();
            if (activityTag != null && !activityTag.isEmpty()) {
                holder.tvTagActivity.setText(activityTag);
                holder.tvTagActivity.setVisibility(View.VISIBLE);
            } else {
                holder.tvTagActivity.setVisibility(View.GONE);
            }

            // 点击事件
            holder.itemView.setOnClickListener(v -> {
                if (diary.isDeleted()) {
                    ToastUtil.showShort(DiarySearchActivity.this, "该日记在回收站，请先恢复");
                } else {
                    Intent intent = new Intent(DiarySearchActivity.this, DiaryDetailActivity.class);
                    intent.putExtra("diaryId", diary.getDiaryId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return diaries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate;
            TextView tvRecycleTag;
            TextView tvEmotion;
            TextView tvTitle;
            TextView tvContent;
            TextView tvTagWeather;
            TextView tvTagMood;
            TextView tvTagActivity;

            public ViewHolder(View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvRecycleTag = itemView.findViewById(R.id.tv_recycle_tag);
                tvEmotion = itemView.findViewById(R.id.tv_emotion);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvContent = itemView.findViewById(R.id.tv_content);
                tvTagWeather = itemView.findViewById(R.id.tv_tag_weather);
                tvTagMood = itemView.findViewById(R.id.tv_tag_mood);
                tvTagActivity = itemView.findViewById(R.id.tv_tag_activity);
            }
        }
    }
}
