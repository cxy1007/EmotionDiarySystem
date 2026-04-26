package com.example.emotiondiarysystem.ui.setting;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;

public class ThemeSwitchActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private LinearLayout themePink;
    private LinearLayout themeBlue;
    private LinearLayout themeGreen;
    private LinearLayout themeDefault;
    private ImageView checkPink;
    private ImageView checkBlue;
    private ImageView checkGreen;
    private ImageView checkDefault;
    private TextView tvTitle;

    private LinearLayout topBar;

    private String currentTheme = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_theme_switch);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadCurrentTheme();
        applyFullTheme();
        setListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        themePink = findViewById(R.id.theme_pink);
        themeBlue = findViewById(R.id.theme_blue);
        themeGreen = findViewById(R.id.theme_green);
        themeDefault = findViewById(R.id.theme_default);
        checkPink = findViewById(R.id.check_pink);
        checkBlue = findViewById(R.id.check_blue);
        checkGreen = findViewById(R.id.check_green);
        checkDefault = findViewById(R.id.check_default);
        tvTitle = findViewById(R.id.tv_title);
        topBar = findViewById(R.id.top_bar);
    }

    private void loadCurrentTheme() {
        currentTheme = SpUtil.getString(this, "appTheme", "default");
        updateThemeCheck();
    }

    private void setListeners() {
        btnBack.setOnClickListener(this);
        themePink.setOnClickListener(this);
        themeBlue.setOnClickListener(this);
        themeGreen.setOnClickListener(this);
        themeDefault.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            finish();
        } else if (v.getId() == R.id.theme_pink) {
            selectTheme("pink", "粉色少女风");
        } else if (v.getId() == R.id.theme_blue) {
            selectTheme("blue", "蓝色天空风");
        } else if (v.getId() == R.id.theme_green) {
            selectTheme("green", "绿色自然风");
        } else if (v.getId() == R.id.theme_default) {
            selectTheme("default", "经典深灰风");
        }
    }

    private void selectTheme(String theme, String themeName) {
        currentTheme = theme;
        SpUtil.putString(this, "appTheme", theme);
        updateThemeCheck();
        applyFullTheme();
        ToastUtil.showShort(this, "已切换为：" + themeName);
    }

    private void updateThemeCheck() {
        checkPink.setVisibility(View.INVISIBLE);
        checkBlue.setVisibility(View.INVISIBLE);
        checkGreen.setVisibility(View.INVISIBLE);
        checkDefault.setVisibility(View.INVISIBLE);

        switch (currentTheme) {
            case "pink":
                checkPink.setVisibility(View.VISIBLE);
                break;
            case "blue":
                checkBlue.setVisibility(View.VISIBLE);
                break;
            case "green":
                checkGreen.setVisibility(View.VISIBLE);
                break;
            default:
                checkDefault.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 应用完整主题到页面所有元素
     */
    private void applyFullTheme() {
        ThemeColorUtil.ThemeColors colors = getCurrentColors();
        if (colors == null) {
            colors = ThemeColorUtil.getCurrentTheme(this);
        }
        boolean isDark = ThemeColorUtil.isDarkMode(this);

        // 应用到主背景
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            mainView.setBackgroundColor(colors.background);
        }

        // 应用到顶部导航栏
        if (topBar != null) {
            topBar.setBackgroundColor(colors.surface);
        }

        // 应用到标题
        tvTitle.setTextColor(colors.textPrimary);

        // 应用到返回按钮颜色
        btnBack.setColorFilter(colors.iconTint);

        // 应用到主题卡片背景
        themePink.setBackgroundColor(colors.surface);
        themeBlue.setBackgroundColor(colors.surface);
        themeGreen.setBackgroundColor(colors.surface);
        themeDefault.setBackgroundColor(colors.surface);

        // 应用到勾选图标颜色
        checkPink.setColorFilter(colors.primary);
        checkBlue.setColorFilter(colors.primary);
        checkGreen.setColorFilter(colors.primary);
        checkDefault.setColorFilter(colors.primary);

        // 应用到分隔线
        View divider = findViewById(R.id.divider);
        if (divider != null) {
            divider.setBackgroundColor(colors.divider);
        }

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }
}
