package com.example.emotiondiarysystem.ui.diary;

import android.app.AlertDialog;
import android.content.Intent;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.manager.DiaryManager;
import com.example.emotiondiarysystem.ui.adapter.PhotoAdapter;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.ui.dialog.TagSelectorDialog;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DiaryDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextView tvDate;
    private TextView tvDiaryTitle;
    private TextView tvContent;
    private TextView tvEmotion;
    private TextView tvWeatherTag;
    private TextView tvMoodTag;
    private TextView tvActivityTag;
    private Button btnDelete;
    private Button btnEdit;
    private LinearLayout topBar;
    private View divider;
    private RecyclerView rvPhotos;

    private DiaryManager diaryManager;
    private Diary currentDiary;
    
    private String selectedWeather = "";
    private String selectedMood = "";
    private String selectedActivity = "";
    
    private PhotoAdapter photoAdapter;
    private List<String> photoPathsList = new ArrayList<>();

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
        setListeners(); // 再次设置监听器，防止主题覆盖
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDiaryDetail();
        applyFullTheme();
        setListeners(); // 重新设置监听器，确保点击事件正常
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvDate = findViewById(R.id.tv_date);
        tvDiaryTitle = findViewById(R.id.tv_diary_title);
        tvContent = findViewById(R.id.tv_content);
        tvEmotion = findViewById(R.id.tv_emotion);
        tvWeatherTag = findViewById(R.id.tv_weather_tag);
        tvMoodTag = findViewById(R.id.tv_mood_tag);
        tvActivityTag = findViewById(R.id.tv_activity_tag);
        btnDelete = findViewById(R.id.btn_delete);
        btnEdit = findViewById(R.id.btn_edit);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);
        rvPhotos = findViewById(R.id.rv_photos);

        // 设置照片RecyclerView - 编辑模式为false，不显示添加按钮和删除按钮
        photoAdapter = new PhotoAdapter(photoPathsList, new PhotoAdapter.OnPhotoClickListener() {
            @Override
            public void onAddClick() {
                // 详情页不需要添加
            }

            @Override
            public void onPhotoClick(int position) {
                previewPhoto(position);
            }

            @Override
            public void onDeleteClick(int position) {
                // 详情页不需要删除
            }
        });
        photoAdapter.setEditMode(false);
        rvPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        rvPhotos.setAdapter(photoAdapter);
    }

    private void initManagers() {
        diaryManager = new DiaryManager(this);
    }

    private void setListeners() {
        btnBack.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        
        tvWeatherTag.setOnClickListener(v -> {
            ToastUtil.showShort(this, "点击天气标签");
            showTagSelector("weather");
        });
        tvMoodTag.setOnClickListener(v -> {
            ToastUtil.showShort(this, "点击心情标签");
            showTagSelector("mood");
        });
        tvActivityTag.setOnClickListener(v -> {
            ToastUtil.showShort(this, "点击活动标签");
            showTagSelector("activity");
        });
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

        String title = currentDiary.getTitle();
        if (!TextUtils.isEmpty(title)) {
            tvDiaryTitle.setText(title);
            tvDiaryTitle.setVisibility(View.VISIBLE);
        } else {
            tvDiaryTitle.setVisibility(View.GONE);
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

        // 从数据库加载已保存的标签
        String weatherTag = currentDiary.getWeatherTag();
        String moodTag = currentDiary.getMoodTag();
        String activityTag = currentDiary.getActivityTag();
        if (!TextUtils.isEmpty(weatherTag)) {
            selectedWeather = weatherTag;
        }
        if (!TextUtils.isEmpty(moodTag)) {
            selectedMood = moodTag;
        }
        if (!TextUtils.isEmpty(activityTag)) {
            selectedActivity = activityTag;
        }
        
        // 加载照片路径
        photoPathsList.clear();
        String photoPaths = currentDiary.getPhotoPaths();
        if (!TextUtils.isEmpty(photoPaths)) {
            String[] paths = photoPaths.split(",");
            for (String path : paths) {
                if (!path.trim().isEmpty()) {
                    photoPathsList.add(path.trim());
                }
            }
        }
        photoAdapter.notifyDataSetChanged();
        
        updateTagDisplay();
    }
    
    private void previewPhoto(int position) {
        String path = photoPathsList.get(position);
        File file = new File(path);
        if (file.exists()) {
            Intent intent = new Intent(this, PhotoViewActivity.class);
            intent.putExtra(PhotoViewActivity.EXTRA_PHOTO_PATH, path);
            startActivity(intent);
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
    
    private void showTagSelector(String tagType) {
        try {
            TagSelectorDialog dialog = new TagSelectorDialog(this, tagType, (type, weatherTags, moodTags, activityTags) -> {
                if (!weatherTags.isEmpty()) {
                    selectedWeather = String.join(", ", weatherTags);
                }
                if (!moodTags.isEmpty()) {
                    selectedMood = String.join(", ", moodTags);
                }
                if (!activityTags.isEmpty()) {
                    selectedActivity = String.join(", ", activityTags);
                }
                updateTagDisplay();
                // 保存标签到数据库
                saveTagsToDatabase();
            });
            
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(this, "打开失败: " + e.getMessage());
        }
    }
    
    private void saveTagsToDatabase() {
        if (currentDiary != null) {
            int result = diaryManager.updateDiary(
                currentDiary.getDiaryId(),
                currentDiary.getTitle(),
                currentDiary.getContent(),
                currentDiary.getEmotionType(),
                selectedWeather,
                selectedMood,
                selectedActivity,
                currentDiary.getPhotoPaths()
            );
            if (result > 0) {
                // 更新本地对象
                currentDiary.setWeatherTag(selectedWeather);
                currentDiary.setMoodTag(selectedMood);
                currentDiary.setActivityTag(selectedActivity);
            }
        }
    }
    
    private void updateTagDisplay() {
        if (!selectedWeather.isEmpty()) {
            tvWeatherTag.setText("☀️ " + selectedWeather);
            tvWeatherTag.setVisibility(View.VISIBLE);
        } else {
            tvWeatherTag.setText("☀️ 天气");
            tvWeatherTag.setVisibility(View.VISIBLE);
        }
        
        if (!selectedMood.isEmpty()) {
            tvMoodTag.setText("😊 " + selectedMood);
            tvMoodTag.setVisibility(View.VISIBLE);
        } else {
            tvMoodTag.setText("😊 心情");
            tvMoodTag.setVisibility(View.VISIBLE);
        }
        
        if (!selectedActivity.isEmpty()) {
            tvActivityTag.setText("📚 " + selectedActivity);
            tvActivityTag.setVisibility(View.VISIBLE);
        } else {
            tvActivityTag.setText("📚 活动");
            tvActivityTag.setVisibility(View.VISIBLE);
        }
    }
}
