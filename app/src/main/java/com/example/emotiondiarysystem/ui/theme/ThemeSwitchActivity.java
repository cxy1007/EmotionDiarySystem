package com.example.emotiondiarysystem.ui.theme;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import com.example.emotiondiarysystem.ui.BaseActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.utils.SpUtil;

public class ThemeSwitchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_theme_switch);

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

        findViewById(R.id.tvLightTheme).setOnClickListener(v -> {
            SpUtil.putString(this, "theme", "light");
            Toast.makeText(this, "已切换到浅色主题", Toast.LENGTH_SHORT).show();
            recreate();
        });

        findViewById(R.id.tvDarkTheme).setOnClickListener(v -> {
            SpUtil.putString(this, "theme", "dark");
            Toast.makeText(this, "已切换到深色主题", Toast.LENGTH_SHORT).show();
            recreate();
        });

        findViewById(R.id.tvSystemTheme).setOnClickListener(v -> {
            SpUtil.putString(this, "theme", "system");
            Toast.makeText(this, "已切换到跟随系统", Toast.LENGTH_SHORT).show();
            recreate();
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
}
