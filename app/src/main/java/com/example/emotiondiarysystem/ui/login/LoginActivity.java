package com.example.emotiondiarysystem.ui.login;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.manager.UserManager;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.ui.diary.DiaryHomeActivity;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextInputEditText etAccount;
    private TextInputEditText etPassword;
    private TextInputLayout tilAccount;
    private TextInputLayout tilPassword;
    private TextView tvForgetPassword;
    private Button btnLogin;
    private Button btnRegister;

    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initManagers();
        setListeners();
        applyFullTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFullTheme();
    }

    private void initViews() {
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        tilAccount = findViewById(R.id.til_account);
        tilPassword = findViewById(R.id.til_password);
        tvForgetPassword = findViewById(R.id.tv_forget_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void initManagers() {
        userManager = new UserManager(this);
    }

    private void setListeners() {
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            handleLogin();
        } else if (v.getId() == R.id.btn_register) {
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (v.getId() == R.id.tv_forget_password) {
            ToastUtil.showShort(this, "忘记密码功能开发中...");
        }
    }

    private void handleLogin() {
        String account = etAccount.getText() != null ? etAccount.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(account)) {
            tilAccount.setError("请输入账号");
            return;
        }
        tilAccount.setErrorEnabled(false);

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("请输入密码");
            return;
        }
        tilPassword.setErrorEnabled(false);

        var user = userManager.login(account);

        if (user == null) {
            ToastUtil.showShort(this, "账号不存在，请先注册");
            return;
        }

        if (!userManager.verifyPassword(user.getPassword(), password)) {
            tilPassword.setError("密码错误");
            return;
        }
        tilPassword.setErrorEnabled(false);

        // 历史明文密码登录成功后，自动升级为 MD5 存储
        userManager.updatePassword(user.getUserId(), password);

        SpUtil.putInt(this, "userId", user.getUserId());
        SpUtil.putString(this, "account", user.getAccount());
        SpUtil.putString(this, "nickname", user.getNickname() != null ? user.getNickname() : "");

        ToastUtil.showShort(this, "登录成功");
        Intent intent = new Intent(this, DiaryHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void applyFullTheme() {
        ThemeColorUtil.ThemeColors colors = getCurrentColors();
        if (colors == null) colors = ThemeColorUtil.getCurrentTheme(this);
        boolean isDark = ThemeColorUtil.isDarkMode(this);

        View main = findViewById(R.id.main);
        if (main != null) main.setBackgroundColor(colors.background);

        TextView tvSubtitle = findViewById(R.id.tv_app_subtitle);
        if (tvSubtitle != null) tvSubtitle.setTextColor(colors.textSecondary);

        tvForgetPassword.setTextColor(colors.textSecondary);

        applyButtonStyle(btnLogin, colors);
        applyButtonStyle(btnRegister, colors);

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
