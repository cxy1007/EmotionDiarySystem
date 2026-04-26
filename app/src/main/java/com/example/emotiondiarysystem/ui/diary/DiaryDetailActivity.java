package com.example.emotiondiarysystem.ui.diary;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.manager.DiaryManager;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;

import java.util.List;

public class DiaryDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextView tvDate;
    private TextView tvContent;
    private TextView tvEmotion;
    private Button btnDelete;
    private Button btnEdit;
    private LinearLayout topBar;
    private View divider;

    private DiaryManager diaryManager;
    private Diary currentDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diary_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initManagers();
        setListeners();
        loadDiaryDetail();
        applyFullTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDiaryDetail();
        applyFullTheme();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvDate = findViewById(R.id.tv_date);
        tvContent = findViewById(R.id.tv_content);
        tvEmotion = findViewById(R.id.tv_emotion);
        btnDelete = findViewById(R.id.btn_delete);
        btnEdit = findViewById(R.id.btn_edit);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);
    }

    private void initManagers() {
        diaryManager = new DiaryManager(this);
    }

    private void setListeners() {
        btnBack.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }

    private void loadDiaryDetail() {
        int diaryId = getIntent().getIntExtra("diaryId", -1);
        if (diaryId == -1) {
            ToastUtil.showShort(this, "日记不存在");
            finish();
            return;
        }

        int userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            userId = SpUtil.getInt(this, "userId", -1);
        }

        List<Diary> diaries = diaryManager.getDiaryListByUserId(userId);
        for (Diary diary : diaries) {
            if (diary.getDiaryId() == diaryId) {
                currentDiary = diary;
                break;
            }
        }

        if (currentDiary == null) {
            ToastUtil.showShort(this, "日记不存在");
            finish();
            return;
        }

        String date = currentDiary.getCreateTime();
        if (date != null && date.length() >= 10) {
            tvDate.setText(date.substring(0, 10));
        } else {
            tvDate.setText(date);
        }

        tvContent.setText(currentDiary.getContent() != null ? currentDiary.getContent() : "");

        String emotion = currentDiary.getEmotionType();
        if (emotion != null) {
            tvEmotion.setText(emotion);
            switch (emotion) {
                case "积极":
                    tvEmotion.setBackgroundColor(0xFF48BB78);
                    break;
                case "中性":
                    tvEmotion.setBackgroundColor(0xFFECC94B);
                    break;
                case "消极":
                    tvEmotion.setBackgroundColor(0xFFF56565);
                    break;
                default:
                    tvEmotion.setBackgroundColor(0xFF888888);
                    break;
            }
        } else {
            tvEmotion.setText("未知");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            finish();
        } else if (v.getId() == R.id.btn_delete) {
            showDeleteConfirmDialog();
        } else if (v.getId() == R.id.btn_edit) {
            if (currentDiary != null) {
                Intent intent = new Intent(this, DiaryEditActivity.class);
                intent.putExtra("diaryId", currentDiary.getDiaryId());
                intent.putExtra("content", currentDiary.getContent());
                intent.putExtra("emotionType", currentDiary.getEmotionType());
                intent.putExtra("createTime", currentDiary.getCreateTime());
                startActivity(intent);
            }
        }
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("确认移至回收站")
                .setMessage("确定要将这篇日记移至回收站吗？可在回收站中永久删除。")
                .setPositiveButton("移至回收站", (dialog, which) -> {
                    if (currentDiary != null) {
                        int result = diaryManager.deleteDiary(currentDiary.getDiaryId());
                        if (result > 0) {
                            ToastUtil.showShort(this, "日记已移至回收站");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            ToastUtil.showShort(this, "操作失败");
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
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

        TextView tvTitle = findViewById(R.id.tv_title);
        if (tvTitle != null) tvTitle.setTextColor(colors.textPrimary);

        tvDate.setTextColor(colors.textSecondary);
        tvContent.setTextColor(colors.textPrimary);

        applyButtonStyle(btnDelete, colors);
        applyButtonStyle(btnEdit, colors);

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }

    private void applyButtonStyle(Button button, ThemeColorUtil.ThemeColors colors) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(8f);
        drawable.setColor(colors.buttonBackground);
        button.setBackground(drawable);
        button.setTextColor(colors.buttonText);
    }
}