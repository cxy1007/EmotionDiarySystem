package com.example.emotiondiarysystem.ui.diary;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.ui.fragment.DiaryListFragment;
import com.example.emotiondiarysystem.ui.fragment.EmotionStatFragment;
import com.example.emotiondiarysystem.ui.fragment.UserCenterFragment;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DiaryHomeActivity extends BaseActivity {

    private FrameLayout fragmentContainer;
    private BottomNavigationView bottomNavigation;

    private DiaryListFragment diaryListFragment;
    private EmotionStatFragment emotionStatFragment;
    private UserCenterFragment userCenterFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_home);

        initViews();
        initFragments();
        setListeners();
        applyFullTheme();

        if (savedInstanceState == null) {
            showFragment(diaryListFragment);
            bottomNavigation.setSelectedItemId(R.id.nav_diary);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFullTheme();
    }

    private void initViews() {
        fragmentContainer = findViewById(R.id.fragment_container);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void initFragments() {
        diaryListFragment = new DiaryListFragment();
        emotionStatFragment = new EmotionStatFragment();
        userCenterFragment = new UserCenterFragment();
    }

    private void setListeners() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_diary) {
                showFragment(diaryListFragment);
                return true;
            } else if (itemId == R.id.nav_emotion) {
                showFragment(emotionStatFragment);
                return true;
            } else if (itemId == R.id.nav_user) {
                showFragment(userCenterFragment);
                return true;
            }
            return false;
        });
    }

    private void showFragment(Fragment fragment) {
        if (fragment == currentFragment) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        if (!fragment.isAdded()) {
            transaction.add(R.id.fragment_container, fragment);
        }

        transaction.show(fragment);
        transaction.commit();
        currentFragment = fragment;
    }

    private void applyFullTheme() {
        ThemeColorUtil.ThemeColors colors = getCurrentColors();
        if (colors == null) colors = ThemeColorUtil.getCurrentTheme(this);
        boolean isDark = ThemeColorUtil.isDarkMode(this);

        View main = findViewById(R.id.main);
        if (main != null) main.setBackgroundColor(colors.background);

        if (bottomNavigation != null) {
            bottomNavigation.setBackgroundColor(colors.surface);
            bottomNavigation.setItemIconTintList(android.content.res.ColorStateList.valueOf(colors.iconTint));
            bottomNavigation.setItemTextColor(android.content.res.ColorStateList.valueOf(colors.textPrimary));
        }

        applyThemeRecursive(getWindow().getDecorView(), colors);

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }

    private void applyThemeRecursive(View view, ThemeColorUtil.ThemeColors colors) {
        if (view == null) return;

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                applyThemeRecursive(vg.getChildAt(i), colors);
            }
        }
    }
}