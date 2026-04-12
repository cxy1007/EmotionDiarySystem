package com.example.emotiondiarysystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    private lateinit var etAccount: EditText
    private lateinit var etPwd: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var tvForgetPwd: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 初始化控件
        etAccount = findViewById(R.id.et_account)
        etPwd = findViewById(R.id.et_pwd)
        btnLogin = findViewById(R.id.btn_login)
        tvRegister = findViewById(R.id.tv_register)
        tvForgetPwd = findViewById(R.id.tv_forget_pwd)

        // 登录按钮点击事件
        btnLogin.setOnClickListener {
            val account = etAccount.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            if (account.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                // 后续可跳转到主页
            }
        }

        // 去注册
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // 忘记密码
        tvForgetPwd.setOnClickListener {
            startActivity(Intent(this, ForgetPwdActivity::class.java))
        }
    }
}
