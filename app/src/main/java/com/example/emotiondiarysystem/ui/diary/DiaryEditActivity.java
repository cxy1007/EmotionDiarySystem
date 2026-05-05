package com.example.emotiondiarysystem.ui.diary;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import com.example.emotiondiarysystem.utils.DateUtil;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DiaryEditActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextView tvDay;
    private TextView tvYearMonth;
    private ImageView ivDateDropdown;
    private ImageButton ivDelete;
    private ImageButton ivSave;
    private RecyclerView rvPhotos;
    private EditText etTitle;
    private TextView btnWeather;
    private TextView btnMood;
    private TextView btnActivity;
    private EditText etContent;
    private TextView tvEmotionHint;

    private DiaryManager diaryManager;
    private int editDiaryId = -1;
    private String currentEmotionType = "中性";
    private boolean isEditMode = false;

    private String selectedWeather = "";
    private String selectedMood = "";
    private String selectedActivity = "";

    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    private PhotoAdapter photoAdapter;
    private List<String> photoPathsList = new ArrayList<>();
    private String currentPhotoPath = null;

    private static final Map<String, Integer> POSITIVE_KEYWORDS = new HashMap<>();
    private static final Map<String, Integer> NEGATIVE_KEYWORDS = new HashMap<>();

    static {
        POSITIVE_KEYWORDS.put("开心", 2);
        POSITIVE_KEYWORDS.put("高兴", 2);
        POSITIVE_KEYWORDS.put("快乐", 2);
        POSITIVE_KEYWORDS.put("幸福", 2);
        POSITIVE_KEYWORDS.put("美好", 2);
        POSITIVE_KEYWORDS.put("棒", 2);
        POSITIVE_KEYWORDS.put("优秀", 2);
        POSITIVE_KEYWORDS.put("成功", 2);
        POSITIVE_KEYWORDS.put("顺利", 2);
        POSITIVE_KEYWORDS.put("感谢", 2);
        POSITIVE_KEYWORDS.put("爱", 2);
        POSITIVE_KEYWORDS.put("喜欢", 2);
        POSITIVE_KEYWORDS.put("满足", 2);
        POSITIVE_KEYWORDS.put("兴奋", 2);
        POSITIVE_KEYWORDS.put("激动", 2);
        POSITIVE_KEYWORDS.put("收获", 2);
        POSITIVE_KEYWORDS.put("进步", 2);
        POSITIVE_KEYWORDS.put("完美", 2);
        POSITIVE_KEYWORDS.put("太棒了", 2);
        POSITIVE_KEYWORDS.put("很棒", 2);
        POSITIVE_KEYWORDS.put("非常好", 2);
        POSITIVE_KEYWORDS.put("很顺利", 2);
        POSITIVE_KEYWORDS.put("不错", 2);
        POSITIVE_KEYWORDS.put("加油", 2);
        POSITIVE_KEYWORDS.put("希望", 2);
        POSITIVE_KEYWORDS.put("乐观", 2);
        POSITIVE_KEYWORDS.put("自信", 2);
        POSITIVE_KEYWORDS.put("温暖", 2);
        POSITIVE_KEYWORDS.put("感动", 2);
        POSITIVE_KEYWORDS.put("欣慰", 2);
        POSITIVE_KEYWORDS.put("轻松", 1);
        POSITIVE_KEYWORDS.put("愉快", 2);
        POSITIVE_KEYWORDS.put("欢乐", 2);
        POSITIVE_KEYWORDS.put("甜蜜", 2);
        POSITIVE_KEYWORDS.put("满足感", 2);
        POSITIVE_KEYWORDS.put("心旷神怡", 3);
        POSITIVE_KEYWORDS.put("神清气爽", 3);
        POSITIVE_KEYWORDS.put("精神抖擞", 3);
        POSITIVE_KEYWORDS.put("精力充沛", 3);
        POSITIVE_KEYWORDS.put("充满力量", 2);
        POSITIVE_KEYWORDS.put("心情好", 2);
        POSITIVE_KEYWORDS.put("心情不错", 2);
        POSITIVE_KEYWORDS.put("心情愉快", 2);
        POSITIVE_KEYWORDS.put("心情舒畅", 2);
        POSITIVE_KEYWORDS.put("心情明朗", 2);
        POSITIVE_KEYWORDS.put("一切顺利", 2);
        POSITIVE_KEYWORDS.put("越来越好", 2);
        POSITIVE_KEYWORDS.put("特别开心", 2);
        POSITIVE_KEYWORDS.put("非常高兴", 2);
        POSITIVE_KEYWORDS.put("非常快乐", 2);
        POSITIVE_KEYWORDS.put("很幸福", 2);
        POSITIVE_KEYWORDS.put("很满足", 2);
        POSITIVE_KEYWORDS.put("超开心", 2);
        POSITIVE_KEYWORDS.put("超级开心", 2);
        POSITIVE_KEYWORDS.put("太开心", 2);
        POSITIVE_KEYWORDS.put("好开心", 2);
        POSITIVE_KEYWORDS.put("好高兴", 2);
        POSITIVE_KEYWORDS.put("好快乐", 2);
        POSITIVE_KEYWORDS.put("很乐观", 2);
        POSITIVE_KEYWORDS.put("很有信心", 2);
        POSITIVE_KEYWORDS.put("很开心", 2);
        POSITIVE_KEYWORDS.put("欢乐时光", 2);

        NEGATIVE_KEYWORDS.put("难过", 2);
        NEGATIVE_KEYWORDS.put("伤心", 2);
        NEGATIVE_KEYWORDS.put("痛苦", 2);
        NEGATIVE_KEYWORDS.put("糟糕", 2);
        NEGATIVE_KEYWORDS.put("失败", 2);
        NEGATIVE_KEYWORDS.put("烦恼", 2);
        NEGATIVE_KEYWORDS.put("压力", 2);
        NEGATIVE_KEYWORDS.put("焦虑", 2);
        NEGATIVE_KEYWORDS.put("生气", 2);
        NEGATIVE_KEYWORDS.put("愤怒", 2);
        NEGATIVE_KEYWORDS.put("失望", 2);
        NEGATIVE_KEYWORDS.put("后悔", 2);
        NEGATIVE_KEYWORDS.put("累", 2);
        NEGATIVE_KEYWORDS.put("疲惫", 2);
        NEGATIVE_KEYWORDS.put("无奈", 2);
        NEGATIVE_KEYWORDS.put("郁闷", 2);
        NEGATIVE_KEYWORDS.put("孤独", 2);
        NEGATIVE_KEYWORDS.put("害怕", 2);
        NEGATIVE_KEYWORDS.put("担心", 2);
        NEGATIVE_KEYWORDS.put("紧张", 2);
        NEGATIVE_KEYWORDS.put("难受", 2);
        NEGATIVE_KEYWORDS.put("沮丧", 2);
        NEGATIVE_KEYWORDS.put("悲观", 2);
        NEGATIVE_KEYWORDS.put("绝望", 3);
        NEGATIVE_KEYWORDS.put("崩溃", 3);
        NEGATIVE_KEYWORDS.put("悲伤", 2);
        NEGATIVE_KEYWORDS.put("失落", 2);
        NEGATIVE_KEYWORDS.put("心情差", 2);
        NEGATIVE_KEYWORDS.put("心情不好", 2);
        NEGATIVE_KEYWORDS.put("心情糟糕", 2);
        NEGATIVE_KEYWORDS.put("不顺利", 2);
        NEGATIVE_KEYWORDS.put("很烦", 2);
        NEGATIVE_KEYWORDS.put("倒霉", 2);
        NEGATIVE_KEYWORDS.put("压抑", 2);
        NEGATIVE_KEYWORDS.put("消沉", 2);
        NEGATIVE_KEYWORDS.put("无精打采", 2);
        NEGATIVE_KEYWORDS.put("心烦", 2);
        NEGATIVE_KEYWORDS.put("心塞", 2);
        NEGATIVE_KEYWORDS.put("心累", 2);
        NEGATIVE_KEYWORDS.put("焦虑不安", 2);
        NEGATIVE_KEYWORDS.put("心神不宁", 2);
        NEGATIVE_KEYWORDS.put("坐立不安", 2);
        NEGATIVE_KEYWORDS.put("好累", 2);
        NEGATIVE_KEYWORDS.put("好烦", 2);
        NEGATIVE_KEYWORDS.put("好难过", 2);
        NEGATIVE_KEYWORDS.put("好伤心", 2);
        NEGATIVE_KEYWORDS.put("很伤心", 2);
        NEGATIVE_KEYWORDS.put("很失望", 2);
        NEGATIVE_KEYWORDS.put("很生气", 2);
        NEGATIVE_KEYWORDS.put("很后悔", 2);
        NEGATIVE_KEYWORDS.put("很疲惫", 2);
        NEGATIVE_KEYWORDS.put("非常烦", 2);
        NEGATIVE_KEYWORDS.put("非常累", 2);
        NEGATIVE_KEYWORDS.put("很难过", 2);
        NEGATIVE_KEYWORDS.put("特别烦", 2);
        NEGATIVE_KEYWORDS.put("特别累", 2);
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean cameraGranted = result.get(Manifest.permission.CAMERA);
                Boolean storageGranted = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                if ((cameraGranted != null && cameraGranted) || (storageGranted != null && storageGranted)) {
                    showPhotoSelectDialog();
                } else {
                    ToastUtil.showShort(this, "需要权限才能上传照片");
                }
            }
    );

    private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && currentPhotoPath != null) {
                    photoPathsList.add(currentPhotoPath);
                    photoAdapter.notifyDataSetChanged();
                }
            }
    );

    private final ActivityResultLauncher<Intent> pickPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        String path = getRealPathFromURI(selectedImage);
                        if (path != null) {
                            photoPathsList.add(path);
                            photoAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
    );

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
        initDate();
        handleIntent();
        setListeners();
        applyFullTheme();
        setListeners();
        updateTagDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFullTheme();
        setListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvDay = findViewById(R.id.tv_day);
        tvYearMonth = findViewById(R.id.tv_year_month);
        ivDateDropdown = findViewById(R.id.iv_date_dropdown);
        ivDelete = findViewById(R.id.iv_delete);
        ivSave = findViewById(R.id.iv_save);
        rvPhotos = findViewById(R.id.rv_photos);
        etTitle = findViewById(R.id.et_title);
        btnWeather = findViewById(R.id.btn_weather);
        btnMood = findViewById(R.id.btn_mood);
        btnActivity = findViewById(R.id.btn_activity);
        etContent = findViewById(R.id.et_content);
        tvEmotionHint = findViewById(R.id.tv_emotion_hint);

        // 设置照片RecyclerView
        photoAdapter = new PhotoAdapter(photoPathsList, new PhotoAdapter.OnPhotoClickListener() {
            @Override
            public void onAddClick() {
                checkPermissionsAndShowPhotoSelectDialog();
            }

            @Override
            public void onPhotoClick(int position) {
                previewPhoto(position);
            }

            @Override
            public void onDeleteClick(int position) {
                photoPathsList.remove(position);
                photoAdapter.notifyDataSetChanged();
            }
        });
        rvPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        rvPhotos.setAdapter(photoAdapter);
    }

    private void initManagers() {
        diaryManager = new DiaryManager(this);
    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        updateDateDisplay();
    }

    private void updateDateDisplay() {
        tvDay.setText(String.format(Locale.getDefault(), "%02d", selectedDay));
        tvYearMonth.setText(selectedYear + "年" + String.format(Locale.getDefault(), "%02d", selectedMonth + 1) + "月");
    }

    private void handleIntent() {
        editDiaryId = getIntent().getIntExtra("diaryId", -1);
        String dateExtra = getIntent().getStringExtra("date");

        if (dateExtra != null && dateExtra.length() >= 10) {
            selectedYear = Integer.parseInt(dateExtra.substring(0, 4));
            selectedMonth = Integer.parseInt(dateExtra.substring(5, 7)) - 1;
            selectedDay = Integer.parseInt(dateExtra.substring(8, 10));
            updateDateDisplay();
        }

        if (editDiaryId != -1) {
            isEditMode = true;
            Diary diary = diaryManager.getDiaryById(editDiaryId);
            if (diary != null) {
                String title = diary.getTitle();
                String content = diary.getContent();
                String emotionType = diary.getEmotionType();
                String createTime = diary.getCreateTime();
                String weatherTag = diary.getWeatherTag();
                String moodTag = diary.getMoodTag();
                String activityTag = diary.getActivityTag();
                String photoPaths = diary.getPhotoPaths();

                if (title != null) etTitle.setText(title);
                if (content != null) etContent.setText(content);
                if (emotionType != null) {
                    currentEmotionType = emotionType;
                    updateEmotionHint(emotionType);
                }
                if (createTime != null && createTime.length() >= 10) {
                    selectedYear = Integer.parseInt(createTime.substring(0, 4));
                    selectedMonth = Integer.parseInt(createTime.substring(5, 7)) - 1;
                    selectedDay = Integer.parseInt(createTime.substring(8, 10));
                    updateDateDisplay();
                }
                if (weatherTag != null) selectedWeather = weatherTag;
                if (moodTag != null) selectedMood = moodTag;
                if (activityTag != null) selectedActivity = activityTag;
                if (photoPaths != null && !photoPaths.isEmpty()) {
                    String[] paths = photoPaths.split(",");
                    for (String path : paths) {
                        if (!path.trim().isEmpty()) {
                            photoPathsList.add(path.trim());
                        }
                    }
                    photoAdapter.notifyDataSetChanged();
                }
            } else {
                ToastUtil.showShort(this, "日记不存在");
                finish();
            }
        }

        ivDelete.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    private void setListeners() {
        btnBack.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        ivSave.setOnClickListener(this);
        ivDateDropdown.setOnClickListener(this);
        btnWeather.setOnClickListener(this);
        btnMood.setOnClickListener(this);
        btnActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            finish();
        } else if (v.getId() == R.id.iv_delete) {
            showDeleteConfirmDialog();
        } else if (v.getId() == R.id.iv_save) {
            saveDiary();
        } else if (v.getId() == R.id.iv_date_dropdown) {
            showDatePicker();
        } else if (v.getId() == R.id.btn_weather) {
            showTagSelector("weather");
        } else if (v.getId() == R.id.btn_mood) {
            showTagSelector("mood");
        } else if (v.getId() == R.id.btn_activity) {
            showTagSelector("activity");
        }
    }

    private void checkPermissionsAndShowPhotoSelectDialog() {
        List<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        if (permissions.isEmpty()) {
            showPhotoSelectDialog();
        } else {
            requestPermissionLauncher.launch(permissions.toArray(new String[0]));
        }
    }

    private void showPhotoSelectDialog() {
        new AlertDialog.Builder(this)
                .setTitle("选择照片")
                .setItems(new String[]{"拍摄照片", "从相册选择"}, (dialog, which) -> {
                    if (which == 0) {
                        takePhoto();
                    } else {
                        pickPhoto();
                    }
                })
                .show();
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                currentPhotoPath = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.emotiondiarysystem.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePhotoLauncher.launch(intent);
            }
        }
    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhotoLauncher.launch(intent);
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
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

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("确认移至回收站")
                .setMessage("确定要将这篇日记移至回收站吗？可在回收站中永久删除或恢复。")
                .setPositiveButton("移至回收站", (dialog, which) -> {
                    if (editDiaryId != -1) {
                        int result = diaryManager.deleteDiary(editDiaryId);
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

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedYear = year;
                    selectedMonth = month;
                    selectedDay = dayOfMonth;
                    updateDateDisplay();
                },
                selectedYear,
                selectedMonth,
                selectedDay
        );
        datePickerDialog.show();
    }

    private void analyzeEmotion() {
        String content = etContent.getText() != null ? etContent.getText().toString() : "";
        if (TextUtils.isEmpty(content.trim())) {
            return;
        }

        String emotionType = analyzeEmotionType(content);
        currentEmotionType = emotionType;
        updateEmotionHint(emotionType);
    }

    private String analyzeEmotionType(String content) {
        int positiveScore = 0;
        int negativeScore = 0;
        String lowerContent = content.toLowerCase();

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

        String[] negationWords = {"不", "没", "无", "非", "别", "未", "不会", "没有", "不太", "不怎么"};
        for (String neg : negationWords) {
            if (lowerContent.contains(neg + "开心") || lowerContent.contains(neg + "高兴")
                    || lowerContent.contains(neg + "快乐") || lowerContent.contains(neg + "顺利")
                    || lowerContent.contains(neg + "累") || lowerContent.contains(neg + "烦")
                    || lowerContent.contains(neg + "好") || lowerContent.contains(neg + "棒")) {
                positiveScore -= 2;
                negativeScore += 1;
            }
        }

        if ((lowerContent.contains("不") && lowerContent.contains("不"))
                || lowerContent.contains("没有不")) {
            positiveScore += 2;
        }

        long exclaimCount = content.chars().filter(c -> c == '！' || c == '!').count();
        positiveScore = (int) (positiveScore * (1 + 0.1 * exclaimCount));
        negativeScore = (int) (negativeScore * (1 + 0.1 * exclaimCount));

        if (positiveScore > negativeScore) return "积极";
        else if (negativeScore > positiveScore) return "消极";
        else return "中性";
    }

    private void updateEmotionHint(String emotionType) {
        tvEmotionHint.setText("情感分析：" + emotionType);
        int color;
        switch (emotionType) {
            case "积极":
                color = 0xFF48BB78;
                break;
            case "消极":
                color = 0xFFF56565;
                break;
            default:
                color = 0xFFBBBBBB;
                break;
        }
        tvEmotionHint.setTextColor(color);
    }

    private void saveDiary() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showShort(this, "请输入日记内容");
            return;
        }

        if ("中性".equals(currentEmotionType)) {
            String emotionType = analyzeEmotionType(content);
            currentEmotionType = emotionType;
        }

        int userId = SpUtil.getInt(this, "userId", -1);
        if (userId == -1) {
            ToastUtil.showShort(this, "请先登录");
            return;
        }

        String currentTime = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d:%02d",
                selectedYear, selectedMonth + 1, selectedDay,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                Calendar.getInstance().get(Calendar.SECOND));

        String photoPaths = photoPathsList.isEmpty() ? null : TextUtils.join(",", photoPathsList);

        long result;
        if (isEditMode) {
            result = diaryManager.updateDiary(editDiaryId, title, content, currentEmotionType, selectedWeather, selectedMood, selectedActivity, photoPaths);
            if (result > 0) {
                ToastUtil.showShort(this, "日记已更新");
                setResult(RESULT_OK);
                finish();
            } else {
                ToastUtil.showShort(this, "更新失败");
            }
        } else {
            result = diaryManager.addDiary(userId, title, content, currentTime, currentEmotionType, selectedWeather, selectedMood, selectedActivity, photoPaths);
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

        btnBack.setColorFilter(colors.iconTint);
        tvDay.setTextColor(colors.textPrimary);
        tvYearMonth.setTextColor(colors.textSecondary);

        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
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
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(this, "打开选择器失败：" + e.getMessage());
        }
    }

    private void updateTagDisplay() {
        if (!selectedWeather.isEmpty()) {
            btnWeather.setText(selectedWeather);
        } else {
            btnWeather.setText("天气");
        }

        if (!selectedMood.isEmpty()) {
            btnMood.setText(selectedMood);
        } else {
            btnMood.setText("心情");
        }

        if (!selectedActivity.isEmpty()) {
            btnActivity.setText(selectedActivity);
        } else {
            btnActivity.setText("活动");
        }
    }
}
