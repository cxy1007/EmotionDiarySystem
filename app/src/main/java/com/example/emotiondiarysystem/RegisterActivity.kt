package com.example.emotiondiarysystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {
    private lateinit var etPhone: EditText
    private lateinit var etCode: EditText
    private lateinit var etPwd: EditText
    private lateinit var etConfirmPwd: EditText
    private lateinit var btnGetCode: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 初始化控件
        etPhone = findViewById(R.id.et_phone)
        etCode = findViewById(R.id.et_code)
        etPwd = findViewById(R.id.et_pwd)
        etConfirmPwd = findViewById(R.id.et_confirm_pwd)
        btnGetCode = findViewById(R.id.btn_get_code)
        btnRegister = findViewById(R.id.btn_register)

        // 获取验证码
        btnGetCode.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            if (phone.isEmpty()) {
                Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "验证码已发送", Toast.LENGTH_SHORT).show()
            }
        }

        // 注册
        btnRegister.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            val code = etCode.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val confirmPwd = etConfirmPwd.text.toString().trim()

            if (phone.isEmpty() || code.isEmpty() || pwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
            } else if (pwd != confirmPwd) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                finish() // 关闭页面，返回登录页
            }
        }
    }
}
