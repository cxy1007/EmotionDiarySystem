package com.example.emotiondiarysystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ResetPwdActivity : AppCompatActivity() {
    private lateinit var etOldPwd: EditText
    private lateinit var etNewPwd: EditText
    private lateinit var etConfirmPwd: EditText
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pwd)

        // 初始化控件
        etOldPwd = findViewById(R.id.et_old_pwd)
        etNewPwd = findViewById(R.id.et_new_pwd)
        etConfirmPwd = findViewById(R.id.et_confirm_pwd)
        btnConfirm = findViewById(R.id.btn_confirm)

        // 确认修改
        btnConfirm.setOnClickListener {
            val oldPwd = etOldPwd.text.toString().trim()
            val newPwd = etNewPwd.text.toString().trim()
            val confirmPwd = etConfirmPwd.text.toString().trim()

            if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
            } else if (newPwd != confirmPwd) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
