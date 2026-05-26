package com.example.emotiondiarysystem.ui.diary;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.emotiondiarysystem.R;

import java.io.File;

public class PhotoViewActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTO_PATH = "photo_path";

    private ImageButton btnBack;
    private ImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        
        initViews();
        loadPhoto();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        ivPhoto = findViewById(R.id.iv_photo);

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadPhoto() {
        String photoPath = getIntent().getStringExtra(EXTRA_PHOTO_PATH);
        if (photoPath == null || photoPath.isEmpty()) {
            Toast.makeText(this, "图片路径为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        File photoFile = new File(photoPath);
        if (!photoFile.exists()) {
            Toast.makeText(this, "图片文件不存在：" + photoPath, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 使用和PhotoAdapter一样的方式加载图片
        Glide.with(this)
                .load(photoFile)
                .fitCenter()
                .into(ivPhoto);
    }
}
