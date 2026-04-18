package com.example.emotiondiarysystem.ui.personal;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.db.DBHelper;
import com.example.emotiondiarysystem.ui.setting.SettingActivity;
import com.example.emotiondiarysystem.ui.about.AboutUsActivity;
import com.example.emotiondiarysystem.ui.user.UserInfoEditActivity;
import com.example.emotiondiarysystem.utils.SpUtil;

public class PersonalCenterActivity extends AppCompatActivity {

    private TextView tvUsername;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_center);

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

        tvUsername = findViewById(R.id.tvUsername);
        dbHelper = DBHelper.getInstance(this);

        findViewById(R.id.tvEditProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, UserInfoEditActivity.class));
        });

        findViewById(R.id.tvSettings).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingActivity.class));
        });

        findViewById(R.id.tvAbout).setOnClickListener(v -> {
            startActivity(new Intent(this, AboutUsActivity.class));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
    }

    private void loadUserInfo() {
        int userId = SpUtil.getInt(this, "userId", 1);
        Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().query("user", null,
                    "userId=?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"));
                tvUsername.setText(nickname != null ? nickname : "用户");
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载用户信息失败", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
