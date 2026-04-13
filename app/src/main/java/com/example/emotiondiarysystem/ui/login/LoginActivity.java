package com.example.emotiondiarysystem.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.emotiondiarysystem.MainActivity;
import com.example.emotiondiarysystem.R;

public class LoginActivity extends AppCompatActivity {

    // 声明页面控件，对应XML布局ID
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化控件
        initView();
        // 设置按钮点击事件
        setClickListeners();
    }

    // 初始化控件，绑定XML中的ID
    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
    }

    // 设置按钮点击事件，只做页面交互
    private void setClickListeners() {
        // 登录按钮点击事件
        btnLogin.setOnClickListener(v -> {
            // 获取输入框内容，去除首尾空格
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // 基础输入校验，符合常规登录逻辑
            if (username.isEmpty()) {
                Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }

            // 这里只做页面跳转，核心登录校验逻辑由群主的Manager层实现，不修改
            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            // 跳转到群主建好的MainActivity主页
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            // 关闭当前登录页
            finish();
        });

        // 注册按钮点击事件，跳转到注册页
        btnRegister.setOnClickListener(v -> {
            // 后续可直接跳转到群主建好的RegisterActivity，这里先做提示
            Toast.makeText(LoginActivity.this, "跳转到注册页面", Toast.LENGTH_SHORT).show();
            // 如需直接跳转，取消下面这行的注释即可
            // startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}
