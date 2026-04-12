package com.example.emotiondiarysystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ForgetPwdActivity : AppCompatActivity() {
    private lateinit var etPhone: EditText
    private lateinit var etCode: EditText
    private lateinit var etNewPwd: EditText
    private lateinit var etConfirmPwd: EditText
    private lateinit var btnGetCode: Button
    private lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_pwd)

        // 初始化控件
        etPhone = findViewById(R.id.et_phone)
        etCode = findViewById(R.id.et_code)
        etNewPwd = findViewById(R.id.et_new_pwd)
        etConfirmPwd = findViewById(R.id.et_confirm_pwd)
        btnGetCode = findViewById(R.id.btn_get_code)
        btnReset = findViewById(R.id.btn_reset)

        // 获取验证码
        btnGetCode.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            if (phone.isEmpty()) {
                Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "验证码已发送", Toast.LENGTH_SHORT).show()
            }
        }

        // 提交重置
        btnReset.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            val code = etCode.text.toString().trim()
            val newPwd = etNewPwd.text.toString().trim()
            val confirmPwd = etConfirmPwd.text.toString().trim()

            if (phone.isEmpty() || code.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
            } else if (newPwd != confirmPwd) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "密码重置成功", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
