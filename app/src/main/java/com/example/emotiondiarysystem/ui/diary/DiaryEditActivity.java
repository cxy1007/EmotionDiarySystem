package com.example.emotiondiarysystem.ui.diary;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.emotiondiarysystem.manager.DiaryManager;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.utils.DateUtil;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class DiaryEditActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextView tvTitle;
    private TextView tvDate;
    private TextInputEditText etContent;
    private LinearLayout layoutEmotionResult;
    private TextView tvEmotionResult;
    private Button btnAnalyze;
    private Button btnSave;
    private LinearLayout topBar;
    private View divider;

    private DiaryManager diaryManager;
    private int editDiaryId = -1;
    private String currentEmotionType = "中性";
    private boolean isEditMode = false;

    private static final Map<String, Integer> POSITIVE_KEYWORDS = new HashMap<>();
    private static final Map<String, Integer> NEGATIVE_KEYWORDS = new HashMap<>();

    static {
        // 积极关键词及权重
        POSITIVE_KEYWORDS.put("开心", 2);      POSITIVE_KEYWORDS.put("高兴", 2);      POSITIVE_KEYWORDS.put("快乐", 2);
        POSITIVE_KEYWORDS.put("幸福", 2);      POSITIVE_KEYWORDS.put("美好", 2);      POSITIVE_KEYWORDS.put("棒", 2);
        POSITIVE_KEYWORDS.put("优秀", 2);      POSITIVE_KEYWORDS.put("成功", 2);      POSITIVE_KEYWORDS.put("顺利", 2);
        POSITIVE_KEYWORDS.put("感谢", 2);      POSITIVE_KEYWORDS.put("爱", 2);        POSITIVE_KEYWORDS.put("喜欢", 2);
        POSITIVE_KEYWORDS.put("满足", 2);      POSITIVE_KEYWORDS.put("兴奋", 2);      POSITIVE_KEYWORDS.put("激动", 2);
        POSITIVE_KEYWORDS.put("收获", 2);      POSITIVE_KEYWORDS.put("进步", 2);      POSITIVE_KEYWORDS.put("完美", 2);
        POSITIVE_KEYWORDS.put("太棒了", 2);    POSITIVE_KEYWORDS.put("很棒", 2);      POSITIVE_KEYWORDS.put("非常好", 2);
        POSITIVE_KEYWORDS.put("很顺利", 2);    POSITIVE_KEYWORDS.put("不错", 2);      POSITIVE_KEYWORDS.put("加油", 2);
        POSITIVE_KEYWORDS.put("希望", 2);      POSITIVE_KEYWORDS.put("乐观", 2);      POSITIVE_KEYWORDS.put("自信", 2);
        POSITIVE_KEYWORDS.put("温暖", 2);      POSITIVE_KEYWORDS.put("感动", 2);      POSITIVE_KEYWORDS.put("欣慰", 2);
        POSITIVE_KEYWORDS.put("轻松", 1);      POSITIVE_KEYWORDS.put("愉快", 2);      POSITIVE_KEYWORDS.put("欢乐", 2);
        POSITIVE_KEYWORDS.put("甜蜜", 2);      POSITIVE_KEYWORDS.put("满足感", 2);    POSITIVE_KEYWORDS.put("心旷神怡", 3);
        POSITIVE_KEYWORDS.put("神清气爽", 3);  POSITIVE_KEYWORDS.put("精神抖擞", 3);  POSITIVE_KEYWORDS.put("精力充沛", 3);
        POSITIVE_KEYWORDS.put("充满力量", 2);  POSITIVE_KEYWORDS.put("心情好", 2);    POSITIVE_KEYWORDS.put("心情不错", 2);
        POSITIVE_KEYWORDS.put("心情愉快", 2);  POSITIVE_KEYWORDS.put("心情舒畅", 2);  POSITIVE_KEYWORDS.put("心情明朗", 2);
        POSITIVE_KEYWORDS.put("一切顺利", 2);  POSITIVE_KEYWORDS.put("越来越好", 2);  POSITIVE_KEYWORDS.put("非常好", 2);
        POSITIVE_KEYWORDS.put("特别开心", 2);  POSITIVE_KEYWORDS.put("非常高兴", 2);  POSITIVE_KEYWORDS.put("非常快乐", 2);
        POSITIVE_KEYWORDS.put("很幸福", 2);    POSITIVE_KEYWORDS.put("很满足", 2);    POSITIVE_KEYWORDS.put("超开心", 2);
        POSITIVE_KEYWORDS.put("超级开心", 2);  POSITIVE_KEYWORDS.put("太开心", 2);    POSITIVE_KEYWORDS.put("好开心", 2);
        POSITIVE_KEYWORDS.put("好高兴", 2);    POSITIVE_KEYWORDS.put("好快乐", 2);    POSITIVE_KEYWORDS.put("很乐观", 2);
        POSITIVE_KEYWORDS.put("很有信心", 2);  POSITIVE_KEYWORDS.put("很开心", 2);    POSITIVE_KEYWORDS.put("欢乐时光", 2);

        // 消极关键词及权重
        NEGATIVE_KEYWORDS.put("难过", 2);      NEGATIVE_KEYWORDS.put("伤心", 2);      NEGATIVE_KEYWORDS.put("痛苦", 2);
        NEGATIVE_KEYWORDS.put("糟糕", 2);      NEGATIVE_KEYWORDS.put("失败", 2);      NEGATIVE_KEYWORDS.put("烦恼", 2);
        NEGATIVE_KEYWORDS.put("压力", 2);      NEGATIVE_KEYWORDS.put("焦虑", 2);      NEGATIVE_KEYWORDS.put("生气", 2);
        NEGATIVE_KEYWORDS.put("愤怒", 2);      NEGATIVE_KEYWORDS.put("失望", 2);      NEGATIVE_KEYWORDS.put("后悔", 2);
        NEGATIVE_KEYWORDS.put("累", 2);        NEGATIVE_KEYWORDS.put("疲惫", 2);      NEGATIVE_KEYWORDS.put("无奈", 2);
        NEGATIVE_KEYWORDS.put("郁闷", 2);      NEGATIVE_KEYWORDS.put("孤独", 2);      NEGATIVE_KEYWORDS.put("害怕", 2);
        NEGATIVE_KEYWORDS.put("担心", 2);      NEGATIVE_KEYWORDS.put("紧张", 2);      NEGATIVE_KEYWORDS.put("难受", 2);
        NEGATIVE_KEYWORDS.put("沮丧", 2);      NEGATIVE_KEYWORDS.put("难过", 2);      NEGATIVE_KEYWORDS.put("悲观", 2);
        NEGATIVE_KEYWORDS.put("绝望", 3);     NEGATIVE_KEYWORDS.put("崩溃", 3);     NEGATIVE_KEYWORDS.put("痛苦", 2);
        NEGATIVE_KEYWORDS.put("悲伤", 2);      NEGATIVE_KEYWORDS.put("难过", 2);      NEGATIVE_KEYWORDS.put("失落", 2);
        NEGATIVE_KEYWORDS.put("心情差", 2);    NEGATIVE_KEYWORDS.put("心情不好", 2); NEGATIVE_KEYWORDS.put("心情糟糕", 2);
        NEGATIVE_KEYWORDS.put("不顺利", 2);   NEGATIVE_KEYWORDS.put("很烦", 2);     NEGATIVE_KEYWORDS.put("很烦", 2);
        NEGATIVE_KEYWORDS.put("倒霉", 2);      NEGATIVE_KEYWORDS.put("郁闷", 2);      NEGATIVE_KEYWORDS.put("压抑", 2);
        NEGATIVE_KEYWORDS.put("消沉", 2);      NEGATIVE_KEYWORDS.put("无精打采", 2); NEGATIVE_KEYWORDS.put("心烦", 2);
        NEGATIVE_KEYWORDS.put("心塞", 2);     NEGATIVE_KEYWORDS.put("心累", 2);     NEGATIVE_KEYWORDS.put("焦虑不安", 2);
        NEGATIVE_KEYWORDS.put("心神不宁", 2); NEGATIVE_KEYWORDS.put("坐立不安", 2); NEGATIVE_KEYWORDS.put("好累", 2);
        NEGATIVE_KEYWORDS.put("好烦", 2);     NEGATIVE_KEYWORDS.put("好难过", 2);   NEGATIVE_KEYWORDS.put("好伤心", 2);
        NEGATIVE_KEYWORDS.put("很伤心", 2);   NEGATIVE_KEYWORDS.put("很失望", 2);   NEGATIVE_KEYWORDS.put("很生气", 2);
        NEGATIVE_KEYWORDS.put("很后悔", 2);   NEGATIVE_KEYWORDS.put("很疲惫", 2);   NEGATIVE_KEYWORDS.put("非常烦", 2);
        NEGATIVE_KEYWORDS.put("非常累", 2);   NEGATIVE_KEYWORDS.put("很难过", 2);   NEGATIVE_KEYWORDS.put("非常难过", 2);
        NEGATIVE_KEYWORDS.put("超级烦", 2);   NEGATIVE_KEYWORDS.put("超级累", 2);   NEGATIVE_KEYWORDS.put("特别烦", 2);
        NEGATIVE_KEYWORDS.put("特别累", 2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diary_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initManagers();
        handleIntent();
        setListeners();
        applyFullTheme();

        tvDate.setText(DateUtil.getCurrentTime("yyyy-MM-dd"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFullTheme();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_title);
        tvDate = findViewById(R.id.tv_date);
        etContent = findViewById(R.id.et_content);
        layoutEmotionResult = findViewById(R.id.layout_emotion_result);
        tvEmotionResult = findViewById(R.id.tv_emotion_result);
        btnAnalyze = findViewById(R.id.btn_analyze);
        btnSave = findViewById(R.id.btn_save);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);
    }

    private void initManagers() {
        diaryManager = new DiaryManager(this);
    }

    private void handleIntent() {
        editDiaryId = getIntent().getIntExtra("diaryId", -1);
        if (editDiaryId != -1) {
            isEditMode = true;
            tvTitle.setText("编辑日记");
            String content = getIntent().getStringExtra("content");
            String emotionType = getIntent().getStringExtra("emotionType");
            String createTime = getIntent().getStringExtra("createTime");

            if (content != null) etContent.setText(content);
            if (emotionType != null) {
                currentEmotionType = emotionType;
                showEmotionResult(emotionType);
            }
            if (createTime != null && createTime.length() >= 10) {
                tvDate.setText(createTime.substring(0, 10));
            }
        }
    }

    private void setListeners() {
        btnBack.setOnClickListener(this);
        btnAnalyze.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            finish();
        } else if (v.getId() == R.id.btn_analyze) {
            analyzeEmotion();
        } else if (v.getId() == R.id.btn_save) {
            saveDiary();
        }
    }

    private void analyzeEmotion() {
        String content = etContent.getText() != null ? etContent.getText().toString() : "";
        if (TextUtils.isEmpty(content.trim())) {
            ToastUtil.showShort(this, "请先输入日记内容");
            return;
        }

        String emotionType = analyzeEmotionType(content);
        currentEmotionType = emotionType;
        showEmotionResult(emotionType);
        ToastUtil.showShort(this, "情感分析完成：" + emotionType);
    }

    private String analyzeEmotionType(String content) {
        int positiveScore = 0;
        int negativeScore = 0;
        String lowerContent = content.toLowerCase();

        // 精确匹配关键词（带权重）
        for (Map.Entry<String, Integer> entry : POSITIVE_KEYWORDS.entrySet()) {
            if (content.contains(entry.getKey())) {
                positiveScore += entry.getValue();
            }
        }
        for (Map.Entry<String, Integer> entry : NEGATIVE_KEYWORDS.entrySet()) {
            if (content.contains(entry.getKey())) {
                negativeScore += entry.getValue();
            }
        }

        // 否定反转检测（消极词前有否定词，情感反转）
        String[] negationWords = {"不", "没", "无", "非", "别", "未", "不会", "没有", "不太", "不怎么"};
        for (String neg : negationWords) {
            if (lowerContent.contains(neg + "开心") || lowerContent.contains(neg + "高兴")
                    || lowerContent.contains(neg + "快乐") || lowerContent.contains(neg + "顺利")
                    || lowerContent.contains(neg + "累") || lowerContent.contains(neg + "烦")
                    || lowerContent.contains(neg + "好") || lowerContent.contains(neg + "棒")) {
                // 否定反转，降低积极分，增加消极分
                positiveScore -= 2;
                negativeScore += 1;
            }
        }

        // 双重否定检测（转为积极）
        if ((lowerContent.contains("不") && lowerContent.contains("不"))
                || lowerContent.contains("没有不")) {
            positiveScore += 2;
        }

        // 感叹号增强效果
        long exclaimCount = content.chars().filter(c -> c == '！' || c == '!').count();
        positiveScore = (int) (positiveScore * (1 + 0.1 * exclaimCount));
        negativeScore = (int) (negativeScore * (1 + 0.1 * exclaimCount));

        if (positiveScore > negativeScore) return "积极";
        else if (negativeScore > positiveScore) return "消极";
        else return "中性";
    }

    private void showEmotionResult(String emotionType) {
        layoutEmotionResult.setVisibility(View.VISIBLE);
        tvEmotionResult.setText(emotionType);

        int color;
        switch (emotionType) {
            case "积极": color = 0xFF48BB78; break;
            case "消极": color = 0xFFF56565; break;
            default: color = 0xFFECC94B; break;
        }
        tvEmotionResult.setTextColor(color);
    }

    private void saveDiary() {
        String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showShort(this, "请输入日记内容");
            return;
        }

        // 如果用户没有先点击分析按钮，自动分析情感
        if ("中性".equals(currentEmotionType) && !isEditMode) {
            String emotionType = analyzeEmotionType(content);
            currentEmotionType = emotionType;
        }

        int userId = SpUtil.getInt(this, "userId", -1);
        if (userId == -1) {
            ToastUtil.showShort(this, "请先登录");
            return;
        }

        String currentTime = DateUtil.getCurrentTime();
        long result;

        if (isEditMode) {
            result = diaryManager.updateDiary(editDiaryId, content, currentEmotionType);
            if (result > 0) {
                ToastUtil.showShort(this, "日记已更新");
                setResult(RESULT_OK);
                finish();
            } else {
                ToastUtil.showShort(this, "更新失败");
            }
        } else {
            result = diaryManager.addDiary(userId, content, currentTime, currentEmotionType);
            if (result > 0) {
                ToastUtil.showShort(this, "日记已保存");
                setResult(RESULT_OK);
                finish();
            } else {
                ToastUtil.showShort(this, "保存失败");
            }
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
        tvTitle.setTextColor(colors.textPrimary);
        tvDate.setTextColor(colors.textSecondary);

        applyButtonStyle(btnAnalyze, colors);
        applyButtonStyle(btnSave, colors);

        TextView tvEmotionResult = findViewById(R.id.tv_emotion_result);
        if (tvEmotionResult != null) tvEmotionResult.setTextColor(colors.textSecondary);

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