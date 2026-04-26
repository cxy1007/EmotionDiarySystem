package com.example.emotiondiarysystem.ui.setting;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.User;
import com.example.emotiondiarysystem.manager.UserManager;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.ui.login.LoginActivity;
import com.example.emotiondiarysystem.utils.SessionUtil;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnSubmit;
    private LinearLayout topBar;
    private View divider;
    private LinearLayout contentContainer;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        userManager = new UserManager(this);
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
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSubmit = findViewById(R.id.btn_submit);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);
        contentContainer = findViewById(R.id.content_container);
    }

    private void setListeners() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            finish();
        } else if (v.getId() == R.id.btn_submit) {
            handleChangePassword();
        }
    }

    private void handleChangePassword() {
        int userId = SessionUtil.ensureUserId(this);
        if (userId <= 0) {
            ToastUtil.showShort(this, "登录状态失效，请重新登录");
            return;
        }

        String oldPassword = etOldPassword.getText() != null ? etOldPassword.getText().toString().trim() : "";
        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(oldPassword)) {
            ToastUtil.showShort(this, "请输入旧密码");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            ToastUtil.showShort(this, "请输入新密码");
            return;
        }
        if (newPassword.length() < 6) {
            ToastUtil.showShort(this, "新密码至少 6 位");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            ToastUtil.showShort(this, "两次输入的新密码不一致");
            return;
        }
        if (oldPassword.equals(newPassword)) {
            ToastUtil.showShort(this, "新密码不能与旧密码相同");
            return;
        }

        User user = userManager.getUserById(userId);
        if (user == null) {
            ToastUtil.showShort(this, "用户不存在，请重新登录");
            return;
        }
        if (!userManager.verifyPassword(user.getPassword(), oldPassword)) {
            ToastUtil.showShort(this, "旧密码错误");
            return;
        }

        boolean success = userManager.updatePassword(userId, newPassword);
        if (success) {
            // 修改密码后强制重新登录，避免旧会话继续使用
            SpUtil.remove(this, "userId");
            SpUtil.remove(this, "account");
            SpUtil.remove(this, "nickname");
            ToastUtil.showShort(this, "密码修改成功，请重新登录");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            ToastUtil.showShort(this, "密码修改失败，请重试");
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
        if (contentContainer != null) contentContainer.setBackgroundColor(colors.surface);

        if (btnBack != null) btnBack.setColorFilter(colors.iconTint);
        TextView tvTitle = findViewById(R.id.tv_title);
        if (tvTitle != null) tvTitle.setTextColor(colors.textPrimary);

        if (etOldPassword != null) {
            etOldPassword.setTextColor(colors.textPrimary);
            etOldPassword.setHintTextColor(colors.textSecondary);
        }
        if (etNewPassword != null) {
            etNewPassword.setTextColor(colors.textPrimary);
            etNewPassword.setHintTextColor(colors.textSecondary);
        }
        if (etConfirmPassword != null) {
            etConfirmPassword.setTextColor(colors.textPrimary);
            etConfirmPassword.setHintTextColor(colors.textSecondary);
        }

        if (btnSubmit != null) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(8f);
            drawable.setColor(colors.buttonBackground);
            btnSubmit.setBackground(drawable);
            btnSubmit.setTextColor(colors.buttonText);
        }

        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }
}
