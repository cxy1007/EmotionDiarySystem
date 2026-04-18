package com.example.emotiondiarysystem.ui.user;

import android.content.ContentValues;
import android.database.Cursor;
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

public class UserInfoEditActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etPhone;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_info_edit);

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

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        dbHelper = DBHelper.getInstance(this);

        loadUserInfo();

        findViewById(R.id.btnSave).setOnClickListener(v -> saveUserInfo());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserInfo() {
        int userId = SpUtil.getInt(this, "userId", 1);
        Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().query("user", null,
                    "userId=?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"));
                if (etUsername != null) {
                    etUsername.setText(nickname);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载用户信息失败", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void saveUserInfo() {
        if (etUsername == null) {
            Toast.makeText(this, "页面加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        if (username.isEmpty()) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = SpUtil.getInt(this, "userId", 1);
        try {
            ContentValues values = new ContentValues();
            values.put("nickname", username);
            int result = dbHelper.getWritableDatabase().update("user", values,
                    "userId=?", new String[]{String.valueOf(userId)});
            if (result > 0) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "保存失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
