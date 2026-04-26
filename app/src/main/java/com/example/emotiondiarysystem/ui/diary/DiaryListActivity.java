package com.example.emotiondiarysystem.ui.diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class DiaryListActivity extends BaseActivity {

    private RecyclerView recyclerDiary;
    private LinearLayout layoutEmpty;
    private Button btnWriteDiary;
    private ImageButton btnSearch;
    private ImageButton btnSetting;
    private LinearLayout topBar;
    private View divider;

    private DiaryManager diaryManager;
    private List<Diary> diaryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diary_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        diaryManager = new DiaryManager(this);
        setListeners();
        applyFullTheme();
    }

    private void initViews() {
        recyclerDiary = findViewById(R.id.recycler_diary);
        layoutEmpty = findViewById(R.id.layout_empty);
        btnWriteDiary = findViewById(R.id.btn_write_diary);
        btnSearch = findViewById(R.id.btn_search);
        btnSetting = findViewById(R.id.btn_setting);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);
    }

    private void setListeners() {
        btnWriteDiary.setOnClickListener(v -> {
            startActivity(new Intent(this, DiaryEditActivity.class));
        });

        btnSearch.setOnClickListener(v -> {
            startActivity(new Intent(this, DiarySearchActivity.class));
        });

        btnSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.emotiondiarysystem.ui.setting.SettingActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDiaryList();
        applyFullTheme();
    }

    private void loadDiaryList() {
        int userId = SpUtil.getInt(this, "userId", -1);
        if (userId == -1) {
            diaryList.clear();
            updateEmptyState();
            return;
        }
        diaryList = diaryManager.getDiaryListByUserId(userId);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (diaryList == null || diaryList.isEmpty()) {
            recyclerDiary.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerDiary.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
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

        btnWriteDiary.setBackgroundColor(colors.buttonBackground);
        btnWriteDiary.setTextColor(colors.buttonText);

        btnSearch.setColorFilter(colors.iconTint);
        btnSetting.setColorFilter(colors.iconTint);

        TextView tvTitle = findViewById(R.id.tv_title);
        if (tvTitle != null) tvTitle.setTextColor(colors.textPrimary);

        TextView tvEmptyHint = findViewById(R.id.tv_empty_hint);
        if (tvEmptyHint != null) tvEmptyHint.setTextColor(colors.textSecondary);

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }
}