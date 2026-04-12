package com.example.emotiondiarysystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast

class UserEditActivity : AppCompatActivity() {
    private lateinit var btnUploadAvatar: Button
    private lateinit var etNickname: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var etIntro: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit)

        // 初始化控件
        btnUploadAvatar = findViewById(R.id.btn_upload_avatar)
        etNickname = findViewById(R.id.et_nickname)
        rgGender = findViewById(R.id.rg_gender)
        etIntro = findViewById(R.id.et_intro)
        btnSave = findViewById(R.id.btn_save)

        // 上传头像（预留接口）
        btnUploadAvatar.setOnClickListener {
            Toast.makeText(this, "头像上传功能", Toast.LENGTH_SHORT).show()
        }

        // 保存信息
        btnSave.setOnClickListener {
            val nickname = etNickname.text.toString().trim()
            val gender = when (rgGender.checkedRadioButtonId) {
                R.id.rb_male -> "男"
                R.id.rb_female -> "女"
                R.id.rb_secret -> "保密"
                else -> "保密"
            }
            val intro = etIntro.text.toString().trim()

            if (nickname.isEmpty()) {
                Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "信息保存成功", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
