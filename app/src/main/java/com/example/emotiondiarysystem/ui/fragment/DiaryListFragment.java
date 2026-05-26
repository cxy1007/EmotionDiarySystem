package com.example.emotiondiarysystem.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.example.emotiondiarysystem.ui.diary.DiaryDetailActivity;
import com.example.emotiondiarysystem.ui.diary.DiaryEditActivity;
import com.example.emotiondiarysystem.ui.setting.SettingActivity;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class DiaryListFragment extends Fragment {

    private RecyclerView recyclerDiary;
    private LinearLayout layoutEmpty;
    private Button btnWriteDiary;
    private ImageButton btnSearch;
    private ImageButton btnSetting;
    private LinearLayout topBar;
    private LinearLayout quoteCard;
    private TextView tvQuote;
    private TextView tvQuoteAuthor;

    private DiaryManager diaryManager;
    private List<Diary> diaryList = new ArrayList<>();
    private MooDiaryAdapter adapter;

    // 心情语录库
    private static final String[] QUOTES = {
        "「生活中不缺少美，缺少的是发现美的眼睛。」",
        "「每一个不曾起舞的日子，都是对生命的辜负。」",
        "「世界上只有一种真正的英雄主义，那就是认清生活的真相后依然热爱生活。」",
        "「不要着急，最好的总会在最不经意的时候出现。」",
        "「人的一切痛苦，本质上都是对自己无能的愤怒。」",
        "「活着不是为了改变世界，而是为了不让世界改变自己。」",
        "「真正的平静，不是远离车马喧嚣，而是在内心修篱种菊。」",
        "「不管前方的路有多苦，只要走的方向正确，不管多么崎岖不平，都比站在原地更接近幸福。」",
        "「温柔的晚风，傍晚的晚霞，解暑的西瓜，冒泡的可乐，人间的美好多着呢。」",
        "「愿你所有的日子，都比不上明天的光辉。」"
    };

    private static final String[] QUOTE_AUTHORS = {
        "— 罗丹", "— 尼采", "— 罗曼·罗兰", "— 泰戈尔", "— 王小波",
        "— 电影台词", "— 林徽因", "— 宫崎骏", "— 网络语录", "— 莎士比亚"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initManagers();
        setupRecyclerView();
        setListeners();
        applyFullTheme();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDiaryList();
        loadMoodQuote();
        applyFullTheme();
    }

    // 加载心情语录推荐
    private void loadMoodQuote() {
        if (tvQuote == null || tvQuoteAuthor == null) return;

        // 随机推荐一条语录
        int index = (int) (Math.random() * QUOTES.length);
        index = Math.max(0, Math.min(index, QUOTES.length - 1));
        tvQuote.setText(QUOTES[index]);
        tvQuoteAuthor.setText(QUOTE_AUTHORS[index]);
    }

    private void initViews(View view) {
        recyclerDiary = view.findViewById(R.id.recycler_diary);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        btnWriteDiary = view.findViewById(R.id.btn_write_diary);
        btnSearch = view.findViewById(R.id.btn_search);
        btnSetting = view.findViewById(R.id.btn_setting);
        topBar = view.findViewById(R.id.top_bar);
        quoteCard = view.findViewById(R.id.quote_card);
        tvQuote = view.findViewById(R.id.tv_quote);
        tvQuoteAuthor = view.findViewById(R.id.tv_quote_author);
    }

    private void initManagers() {
        diaryManager = new DiaryManager(requireContext());
    }

    private void setupRecyclerView() {
        adapter = new MooDiaryAdapter(diaryList, new MooDiaryAdapter.OnDiaryActionListener() {
            @Override
            public void onWriteDiaryClick() {
                // 点击引导卡片的"记录我的今天"按钮
                Intent intent = new Intent(requireContext(), DiaryEditActivity.class);
                startActivity(intent);
            }

            @Override
            public void onDiaryClick(Diary diary, int position) {
                // 点击日记卡片，跳转到编辑页并回填内容
                Intent intent = new Intent(requireContext(), DiaryEditActivity.class);
                intent.putExtra("diaryId", diary.getDiaryId());
                startActivity(intent);
            }
        });
        // 横向滚动布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerDiary.setLayoutManager(layoutManager);
        recyclerDiary.setAdapter(adapter);
    }

    private void setListeners() {
        // 隐藏底部写日记按钮（引导卡片内已有）
        if (btnWriteDiary != null) {
            btnWriteDiary.setVisibility(View.GONE);
        }

        btnSearch.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.emotiondiarysystem.ui.diary.DiarySearchActivity.class));
        });

        btnSetting.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SettingActivity.class));
        });
    }

    private void loadDiaryList() {
        int userId = SpUtil.getInt(requireContext(), "userId", -1);
        if (userId == -1) {
            diaryList.clear();
            adapter.notifyDataSetChanged();
            updateEmptyState();
            return;
        }
        diaryList = diaryManager.getDiaryListByUserId(userId);
        adapter.updateData(diaryList);
        updateEmptyState();
    }

    private void updateEmptyState() {
        // 现在有引导卡片，始终显示RecyclerView，不需要空状态布局
        recyclerDiary.setVisibility(View.VISIBLE);
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void applyFullTheme() {
        if (getView() == null) return;
        ThemeColorUtil.ThemeColors colors = ThemeColorUtil.getCurrentTheme(requireContext());
        boolean isDark = ThemeColorUtil.isDarkMode(requireContext());

        getView().setBackgroundColor(colors.background);

        if (topBar != null) topBar.setBackgroundColor(colors.surface);

        if (btnSearch != null) btnSearch.setColorFilter(colors.iconTint);
        if (btnSetting != null) btnSetting.setColorFilter(colors.iconTint);

        if (btnWriteDiary != null) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(8f);
            drawable.setColor(colors.buttonBackground);
            btnWriteDiary.setBackground(drawable);
            btnWriteDiary.setTextColor(colors.buttonText);
        }

        if (layoutEmpty != null) {
            layoutEmpty.setBackgroundColor(colors.background);
        }

        // 心情语录卡片主题设置
        if (quoteCard != null) {
            quoteCard.setBackgroundColor(colors.surface);
        }
        if (tvQuote != null) {
            tvQuote.setTextColor(colors.textPrimary);
        }
        if (tvQuoteAuthor != null) {
            tvQuoteAuthor.setTextColor(colors.textSecondary);
        }

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getView(), colors, isDark);
    }
}