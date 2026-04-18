package com.example.emotiondiarysystem.ui.login;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.db.DBHelper;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etOldPassword, etNewPassword, etConfirmPassword;
    private DBHelper dbHelper;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        dbHelper = DBHelper.getInstance(this);

        findViewById(R.id.btnSave).setOnClickListener(v -> changePassword());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changePassword() {
        if (etOldPassword == null || etNewPassword == null || etConfirmPassword == null) {
            Toast.makeText(this, "页面加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldPassword = etOldPassword.getText() != null ? etOldPassword.getText().toString().trim() : "";
        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "新密码至少6位", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = SpUtil.getInt(this, "userId", 1);
        try {
            String currentPassword = dbHelper.getPasswordByUserId(userId);
            if (currentPassword == null || !currentPassword.equals(oldPassword)) {
                Toast.makeText(this, "原密码错误", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("password", newPassword);
            int result = dbHelper.getWritableDatabase().update("user", values,
                    "userId=?", new String[]{String.valueOf(userId)});

            if (result > 0) {
                Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "密码修改失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "修改失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
