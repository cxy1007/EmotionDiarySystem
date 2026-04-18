package com.example.emotiondiarysystem.ui.login;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import com.example.emotiondiarysystem.ui.BaseActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.db.DBHelper;
import com.example.emotiondiarysystem.ui.diary.DiaryHomeActivity;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.databinding.ActivityLoginBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = DBHelper.getInstance(this);

        // 绑定登录按钮点击事件
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // 绑定注册按钮点击事件
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void login() {
        if (binding == null || binding.etAccount == null || binding.etPassword == null) {
            Toast.makeText(this, "页面加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        String account = binding.etAccount.getText() != null ? binding.etAccount.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (account.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "账号和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 检查用户是否存在
            boolean exists = dbHelper.checkUser(account, password);
            if (exists) {
                // 保存用户ID到SharedPreferences
                int userId = dbHelper.getUserId(account);
                SpUtil.putInt(LoginActivity.this, "userId", userId);
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                // 跳转到主页
                startActivity(new Intent(LoginActivity.this, DiaryHomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "登录失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}