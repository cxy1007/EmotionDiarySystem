package com.example.emotiondiarysystem.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.Tag;
import com.example.emotiondiarysystem.ui.adapter.TagAdapter;
import com.example.emotiondiarysystem.ui.adapter.TagPageAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TagSelectorDialog extends Dialog {

    private ViewPager2 viewPager;
    private ImageButton btnPrev, btnNext;
    private Button btnClose;
    private LinearLayout dotContainer;
    private TagPageAdapter pageAdapter;

    private List<List<Tag>> pages;
    private List<String> greetings;
    private OnTagSelectedListener listener;
    private String initialTagType;
    private int initialPage;

    public interface OnTagSelectedListener {
        void onTagsSelected(String tagType, List<String> weatherTags, List<String> moodTags, List<String> activityTags);
    }

    public TagSelectorDialog(@NonNull Context context, String tagType, OnTagSelectedListener listener) {
        super(context);
        this.initialTagType = tagType;
        this.listener = listener;
        
        switch (tagType) {
            case "weather":
                initialPage = 0;
                break;
            case "mood":
                initialPage = 1;
                break;
            case "activity":
                initialPage = 2;
                break;
            default:
                initialPage = 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_tag_selector);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 2 / 3);
            params.dimAmount = 0.6f;
            params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, params.height);
        }

        initViews();
        initData();
        setupViewPager();
        setupListeners();
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnClose = findViewById(R.id.btn_close);
        dotContainer = findViewById(R.id.dot_container);
    }

    private void initData() {
        pages = new ArrayList<>();
        greetings = new ArrayList<>();

        pages.add(createWeatherTags());
        greetings.add(getTimeBasedGreeting() + "你那里的天气怎么样？");
        
        pages.add(createMoodTags());
        greetings.add("那么，这一天的心情是怎么样的呢");
        
        pages.add(createActivityTags());
        greetings.add("最后，今天做了些什么呢");
    }

    private List<Tag> createWeatherTags() {
        List<Tag> tags = new ArrayList<>();
        String[] weatherTags = {"晴天", "多云", "阴天", "小雨", "大雨", "雷暴", "雷阵雨", "小雪", "大雪", "大风", "冰雹"};
        for (String tag : weatherTags) {
            tags.add(new Tag(tag));
        }
        tags.add(new Tag("自定义", true));
        return tags;
    }

    private List<Tag> createMoodTags() {
        List<Tag> tags = new ArrayList<>();
        String[] moodTags = {"开心", "得意", "暖心", "平静", "惊喜", "难过", "孤独", "烦躁", "生气", "迷茫", "尴尬", "委屈", "疲惫", "甜蜜", "充实", "期待", "治愈", "逃避", "梦境", "不知道"};
        for (String tag : moodTags) {
            tags.add(new Tag(tag));
        }
        tags.add(new Tag("自定义", true));
        return tags;
    }

    private List<Tag> createActivityTags() {
        List<Tag> tags = new ArrayList<>();
        String[] activityTags = {"学习", "工作", "逛街", "运动", "追剧", "听歌", "社交", "独处", "旅行", "休息", "游戏", "阅读", "家务", "睡觉", "发呆", "上网", "网购", "吃瓜", "熬夜", "画画", "烹饪", "植物", "美容", "存款", "奶茶", "养生"};
        for (String tag : activityTags) {
            tags.add(new Tag(tag));
        }
        tags.add(new Tag("自定义", true));
        return tags;
    }

    private String getTimeBasedGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            return "早上好呀～";
        } else if (hour >= 12 && hour < 14) {
            return "中午好呀～";
        } else if (hour >= 14 && hour < 18) {
            return "下午好呀～";
        } else {
            return "晚上好呀～";
        }
    }

    private void setupViewPager() {
        pageAdapter = new TagPageAdapter(pages, greetings, new TagAdapter.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag) {
            }

            @Override
            public void onCustomTagInput(String customText) {
            }
        });

        pageAdapter.setPageDataChangedListener(() -> {
        });

        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(initialPage, false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
                updateArrowVisibility(position);
            }
        });

        updateDots(initialPage);
        updateArrowVisibility(initialPage);
    }

    private void setupListeners() {
        btnPrev.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1, true);
            }
        });

        btnNext.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < pages.size() - 1) {
                viewPager.setCurrentItem(currentItem + 1, true);
            }
        });

        btnClose.setOnClickListener(v -> {
            collectSelectedTags();
            dismiss();
        });
    }

    private void updateDots(int selectedPosition) {
        dotContainer.removeAllViews();
        for (int i = 0; i < pages.size(); i++) {
            View dot = new View(getContext());
            int size = (int) (8 * getContext().getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(size / 2, 0, size / 2, 0);
            dot.setLayoutParams(params);

            if (i == selectedPosition) {
                dot.setBackgroundResource(R.drawable.bg_dot_selected);
            } else {
                dot.setBackgroundResource(R.drawable.bg_dot_normal);
            }
            dotContainer.addView(dot);
        }
    }

    private void updateArrowVisibility(int position) {
        btnPrev.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        btnNext.setVisibility(position == pages.size() - 1 ? View.INVISIBLE : View.VISIBLE);
    }

    private void collectSelectedTags() {
        List<String> weatherTags = new ArrayList<>();
        List<String> moodTags = new ArrayList<>();
        List<String> activityTags = new ArrayList<>();
        
        for (Tag tag : pages.get(0)) {
            if (tag.isSelected()) {
                weatherTags.add(tag.getName());
            }
        }
        
        for (Tag tag : pages.get(1)) {
            if (tag.isSelected()) {
                moodTags.add(tag.getName());
            }
        }
        
        for (Tag tag : pages.get(2)) {
            if (tag.isSelected()) {
                activityTags.add(tag.getName());
            }
        }

        if (listener != null) {
            listener.onTagsSelected(initialTagType, weatherTags, moodTags, activityTags);
        }
    }
}
