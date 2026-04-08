package com.example.emotiondiarysystem;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.emotiondiarysystem.ui.diary.DiaryHomeActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 跳转到真正的日记主页
        Intent intent = new Intent(this, DiaryHomeActivity.class);
        startActivity(intent);
        // 关闭启动页，用户按返回键不会回到空壳
        finish();
    }
}
