package com.example.emotiondiarysystem.ui.recommend;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.db.DBHelper;
import com.example.emotiondiarysystem.databinding.ActivityRecommendBinding;

public class RecommendActivity extends AppCompatActivity {

    private ActivityRecommendBinding binding;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRecommendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = DBHelper.getInstance(this);

        // 绑定查询按钮点击事件
        binding.btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryRecommendations();
            }
        });
    }

    private void queryRecommendations() {
        if (binding == null || binding.tvRecommendations == null) {
            Toast.makeText(this, "页面加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query("recommend", null, "user_id = ?", new String[]{"1"}, null, null, "create_time DESC");

            if (cursor != null && cursor.moveToFirst()) {
                StringBuilder sb = new StringBuilder();
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    String type = cursor.getString(cursor.getColumnIndex("recommend_type"));
                    sb.append("ID: " + id + "\n")
                      .append("类型: " + type + "\n")
                      .append("内容: " + content + "\n\n");
                } while (cursor.moveToNext());
                binding.tvRecommendations.setText(sb.toString());
                Toast.makeText(this, "查询到" + cursor.getCount() + "条推荐", Toast.LENGTH_SHORT).show();
            } else {
                binding.tvRecommendations.setText("暂无推荐内容");
                Toast.makeText(this, "暂无推荐内容", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "查询失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                }
            }
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
