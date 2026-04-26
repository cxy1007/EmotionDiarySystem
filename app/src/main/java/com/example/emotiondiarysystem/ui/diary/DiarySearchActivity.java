package com.example.emotiondiarysystem.ui.diary;

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

        // 搜索逻辑
        for (Diary diary : allDiaries) {
            boolean matched = false;

            // 1. 关键词模糊搜索（标题和内容）
            if (diary.getContent() != null && diary.getContent().contains(keyword)) {
                matched = true;
            }

            // 2. 日期搜索
            if (diary.getCreateTime() != null && diary.getCreateTime().contains(keyword)) {
                matched = true;
            }

            // 3. 情绪标签搜索
            if (diary.getEmotionType() != null && diary.getEmotionType().equals(keyword)) {
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
            View view = getLayoutInflater().inflate(R.layout.item_diary_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Diary diary = diaries.get(position);
            holder.tvContent.setText(diary.getContent());
            holder.tvDate.setText(diary.getCreateTime().substring(0, 10));
            holder.tvEmotion.setText(diary.getEmotionType());

            // 设置情感标签颜色
            switch (diary.getEmotionType()) {
                case "积极":
                    holder.tvEmotion.setTextColor(0xFF48BB78);
                    break;
                case "消极":
                    holder.tvEmotion.setTextColor(0xFFF56565);
                    break;
                default:
                    holder.tvEmotion.setTextColor(0xFFECC94B);
                    break;
            }

            // 标记是否在回收站
            if (diary.isDeleted()) {
                holder.tvRecycleTag.setVisibility(View.VISIBLE);
            } else {
                holder.tvRecycleTag.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return diaries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvContent;
            TextView tvDate;
            TextView tvEmotion;
            TextView tvRecycleTag;

            public ViewHolder(View itemView) {
                super(itemView);
                tvContent = itemView.findViewById(R.id.tv_content);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvEmotion = itemView.findViewById(R.id.tv_emotion);

                // 添加回收站标签
                tvRecycleTag = new TextView(DiarySearchActivity.this);
                tvRecycleTag.setText("回收站");
                tvRecycleTag.setTextSize(12);
                tvRecycleTag.setTextColor(0xFFF56565);
                tvRecycleTag.setBackgroundColor(0xFFFEF2F2);
                tvRecycleTag.setPadding(8, 4, 8, 4);
                tvRecycleTag.setGravity(View.TEXT_ALIGNMENT_CENTER);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMarginStart(8);
                tvRecycleTag.setLayoutParams(params);

                // 获取CardView内部的LinearLayout并添加标签
                LinearLayout linearLayout = itemView.findViewById(android.R.id.content);
                if (linearLayout == null) {
                    if (itemView instanceof androidx.cardview.widget.CardView) {
                        androidx.cardview.widget.CardView cardView = (androidx.cardview.widget.CardView) itemView;
                        if (cardView.getChildCount() > 0 && cardView.getChildAt(0) instanceof LinearLayout) {
                            linearLayout = (LinearLayout) cardView.getChildAt(0);
                        }
                    }
                }
                if (linearLayout != null) {
                    linearLayout.addView(tvRecycleTag);
                }
            }
        }
    }
}
