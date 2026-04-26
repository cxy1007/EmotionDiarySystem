package com.example.emotiondiarysystem.ui.login;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.example.emotiondiarysystem.manager.UserManager;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.utils.DateUtil;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextInputEditText etAccount;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextInputEditText etNickname;
    private TextInputLayout tilAccount;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private LinearLayout topBar;
    private View divider;

    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
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
        btnBack = findViewById(R.id.btn_back);
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etNickname = findViewById(R.id.et_nickname);
        tilAccount = findViewById(R.id.til_account);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);
    }

    private void initManagers() {
        userManager = new UserManager(this);
    }

    private void setListeners() {
        btnBack.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            finish();
        } else if (v.getId() == R.id.btn_register) {
            handleRegister();
        } else if (v.getId() == R.id.tv_login) {
            finish();
        }
    }

    private void handleRegister() {
        String account = etAccount.getText() != null ? etAccount.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";
        String nickname = etNickname.getText() != null ? etNickname.getText().toString().trim() : "";

        if (TextUtils.isEmpty(account)) { tilAccount.setError("请输入账号"); return; }
        if (account.length() < 4) { tilAccount.setError("账号至少4个字符"); return; }
        tilAccount.setErrorEnabled(false);

        if (TextUtils.isEmpty(password)) { tilPassword.setError("请输入密码"); return; }
        if (password.length() < 6) { tilPassword.setError("密码至少6个字符"); return; }
        tilPassword.setErrorEnabled(false);

        if (TextUtils.isEmpty(confirmPassword)) { tilConfirmPassword.setError("请确认密码"); return; }
        if (!password.equals(confirmPassword)) { tilConfirmPassword.setError("两次密码不一致"); return; }
        tilConfirmPassword.setErrorEnabled(false);

        if (userManager.isAccountExist(account)) { tilAccount.setError("该账号已被注册"); return; }
        tilAccount.setErrorEnabled(false);

        String createTime = DateUtil.getCurrentTime();
        long result = userManager.register(account, password, nickname, null, createTime);

        if (result > 0) {
            var user = userManager.login(account);
            if (user != null) {
                SpUtil.putInt(this, "userId", user.getUserId());
                SpUtil.putString(this, "account", user.getAccount());
                SpUtil.putString(this, "nickname", user.getNickname() != null ? user.getNickname() : "");
            }
            ToastUtil.showShort(this, "注册成功");
            Intent intent = new Intent(this, com.example.emotiondiarysystem.ui.diary.DiaryHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            ToastUtil.showShort(this, "注册失败，请重试");
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

        if (btnBack != null) btnBack.setColorFilter(colors.iconTint);

        TextView tvTitle = findViewById(R.id.tv_title);
        if (tvTitle != null) tvTitle.setTextColor(colors.textPrimary);

        tvLogin.setTextColor(colors.textSecondary);

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
