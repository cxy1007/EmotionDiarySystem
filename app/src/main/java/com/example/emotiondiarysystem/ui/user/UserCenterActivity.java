package com.example.emotiondiarysystem.ui.user;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class UserCenterActivity extends BaseActivity {

    private TextView tvNickname;
    private TextView tvAccount;
    private Button btnLogin;
    private LinearLayout menuDataManage;
    private LinearLayout menuTheme;
    private LinearLayout menuSetting;
    private Button btnLogout;
    private LinearLayout topBar;
    private View divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_center);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setListeners();
        applyFullTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserInfo();
        applyFullTheme();
    }

    private void initViews() {
        tvNickname = findViewById(R.id.tv_nickname);
        tvAccount = findViewById(R.id.tv_account);
        btnLogin = findViewById(R.id.btn_login);
        menuDataManage = findViewById(R.id.menu_data_manage);
        menuTheme = findViewById(R.id.menu_theme);
        menuSetting = findViewById(R.id.menu_setting);
        btnLogout = findViewById(R.id.btn_logout);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);
    }

    private void setListeners() {
        btnLogin.setOnClickListener(v -> {
            ToastUtil.showShort(this, "请在主页面登录");
            finish();
        });

        menuDataManage.setOnClickListener(v -> {
            ToastUtil.showShort(this, "请在主页面进入数据管理");
        });

        menuTheme.setOnClickListener(v -> {
            ToastUtil.showShort(this, "请在主页面进入主题切换");
        });

        menuSetting.setOnClickListener(v -> {
            ToastUtil.showShort(this, "请在主页面进入设置");
        });

        btnLogout.setOnClickListener(v -> {
            SpUtil.remove(this, "userId");
            SpUtil.remove(this, "account");
            SpUtil.remove(this, "nickname");
            ToastUtil.showShort(this, "已退出登录");
            updateUserInfo();
        });
    }

    private void updateUserInfo() {
        int userId = SpUtil.getInt(this, "userId", -1);
        if (userId == -1) {
            tvNickname.setText("未登录");
            tvAccount.setText("点击登录");
            btnLogin.setText("登录");
            btnLogout.setVisibility(View.GONE);
        } else {
            String nickname = SpUtil.getString(this, "nickname", "");
            String account = SpUtil.getString(this, "account", "");
            tvNickname.setText(nickname.isEmpty() ? "用户" + userId : nickname);
            tvAccount.setText(account);
            btnLogin.setText("已登录");
            btnLogout.setVisibility(View.VISIBLE);
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

        tvNickname.setTextColor(colors.textPrimary);
        tvAccount.setTextColor(colors.textSecondary);

        GradientDrawable loginBg = new GradientDrawable();
        loginBg.setCornerRadius(8f);
        loginBg.setColor(colors.buttonBackground);
        btnLogin.setBackground(loginBg);
        btnLogin.setTextColor(colors.buttonText);

        GradientDrawable logoutBg = new GradientDrawable();
        logoutBg.setCornerRadius(8f);
        logoutBg.setColor(colors.buttonBackground);
        btnLogout.setBackground(logoutBg);
        btnLogout.setTextColor(colors.buttonText);

        applyMenuTheme(menuDataManage, colors);
        applyMenuTheme(menuTheme, colors);
        applyMenuTheme(menuSetting, colors);

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }

    private void applyMenuTheme(View menu, ThemeColorUtil.ThemeColors colors) {
        if (menu == null) return;
        android.view.ViewGroup vg = (android.view.ViewGroup) menu;
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(colors.iconTint);
            }
        }
    }
}