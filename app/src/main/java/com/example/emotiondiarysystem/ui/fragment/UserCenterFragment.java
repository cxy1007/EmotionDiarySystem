package com.example.emotiondiarysystem.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.User;
import com.example.emotiondiarysystem.manager.UserManager;
import com.example.emotiondiarysystem.ui.diary.DiaryDataManageActivity;
import com.example.emotiondiarysystem.ui.login.LoginActivity;
import com.example.emotiondiarysystem.ui.setting.SettingActivity;
import com.example.emotiondiarysystem.ui.setting.ThemeSwitchActivity;
import com.example.emotiondiarysystem.utils.SessionUtil;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;

public class UserCenterFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvAccount;
    private Button btnLogin;
    private LinearLayout menuDataManage;
    private LinearLayout menuTheme;
    private LinearLayout menuRecycle;
    private LinearLayout menuSetting;
    private LinearLayout menuEditProfile;
    private Button btnLogout;
    private LinearLayout topBar;
    private View divider;
    private UserManager userManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();
        applyFullTheme();
    }

    private void initViews(View view) {
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvAccount = view.findViewById(R.id.tv_account);
        btnLogin = view.findViewById(R.id.btn_login);
        menuEditProfile = view.findViewById(R.id.menu_edit_profile);
        menuDataManage = view.findViewById(R.id.menu_data_manage);
        menuTheme = view.findViewById(R.id.menu_theme);
        menuRecycle = view.findViewById(R.id.menu_recycle);
        menuSetting = view.findViewById(R.id.menu_setting);
        btnLogout = view.findViewById(R.id.btn_logout);
        topBar = view.findViewById(R.id.top_bar);
        divider = view.findViewById(R.id.divider);
        userManager = new UserManager(requireContext());
    }

    private void setListeners() {
        btnLogin.setOnClickListener(v -> {
            int userId = SessionUtil.ensureUserId(requireContext());
            if (userId == -1) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            } else {
                ToastUtil.showShort(requireContext(), "您已登录");
            }
        });

        menuEditProfile.setOnClickListener(v -> {
            int userId = SessionUtil.ensureUserId(requireContext());
            if (userId == -1) {
                ToastUtil.showShort(requireContext(), "请先登录");
                startActivity(new Intent(requireContext(), LoginActivity.class));
            } else {
                Intent intent = new Intent(requireContext(), com.example.emotiondiarysystem.ui.user.UserInfoEditActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("account", SpUtil.getString(requireContext(), "account", ""));
                intent.putExtra("nickname", SpUtil.getString(requireContext(), "nickname", ""));
                startActivity(intent);
            }
        });

        menuDataManage.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), DiaryDataManageActivity.class));
        });

        menuTheme.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ThemeSwitchActivity.class));
        });

        menuRecycle.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.emotiondiarysystem.ui.diary.DiaryRecycleActivity.class));
        });

        menuSetting.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SettingActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            SpUtil.remove(requireContext(), "userId");
            SpUtil.remove(requireContext(), "account");
            SpUtil.remove(requireContext(), "nickname");
            ToastUtil.showShort(requireContext(), "已退出登录");
            updateUserInfo();
        });
    }

    private void updateUserInfo() {
        int userId = SessionUtil.ensureUserId(requireContext());
        if (userId == -1) {
            tvNickname.setText("未登录");
            tvAccount.setText("点击登录");
            btnLogin.setText("登录");
            btnLogout.setVisibility(View.GONE);
            if (ivAvatar != null) {
                ivAvatar.setImageResource(R.drawable.ic_default_avatar);
            }
        } else {
            User user = userManager.getUserById(userId);
            String nickname = SpUtil.getString(requireContext(), "nickname", "");
            String account = SpUtil.getString(requireContext(), "account", "");
            String avatarPath = null;
            if (user != null) {
                nickname = user.getNickname() == null ? "" : user.getNickname();
                account = user.getAccount() == null ? "" : user.getAccount();
                avatarPath = user.getAvatar();
                SpUtil.putString(requireContext(), "nickname", nickname);
                SpUtil.putString(requireContext(), "account", account);
            }

            tvNickname.setText(nickname.isEmpty() ? "用户" + userId : nickname);
            tvAccount.setText(account);
            btnLogin.setText("已登录");
            btnLogout.setVisibility(View.VISIBLE);

            if (ivAvatar != null) {
                if (avatarPath != null && !avatarPath.isEmpty()) {
                    Glide.with(this)
                            .load(avatarPath)
                            .placeholder(R.drawable.ic_default_avatar)
                            .error(R.drawable.ic_default_avatar)
                            .into(ivAvatar);
                } else {
                    ivAvatar.setImageResource(R.drawable.ic_default_avatar);
                }
            }
        }
    }

    private void applyFullTheme() {
        if (getView() == null) return;
        ThemeColorUtil.ThemeColors colors = ThemeColorUtil.getCurrentTheme(requireContext());
        boolean isDark = ThemeColorUtil.isDarkMode(requireContext());

        getView().setBackgroundColor(colors.background);

        if (topBar != null) topBar.setBackgroundColor(colors.surface);
        if (divider != null) divider.setBackgroundColor(colors.divider);

        tvNickname.setTextColor(colors.textPrimary);
        tvAccount.setTextColor(colors.textSecondary);

        GradientDrawable loginBg = new GradientDrawable();
        loginBg.setCornerRadius(8f);
        loginBg.setColor(colors.buttonBackground);
        btnLogin.setBackground(loginBg);
        btnLogin.setTextColor(colors.buttonText);

        GradientDrawable logoutBg = new GradientDrawable();
        logoutBg.setCornerRadius(8f);
        logoutBg.setColor(colors.buttonBackground);
        btnLogout.setBackground(logoutBg);
        btnLogout.setTextColor(colors.buttonText);

        // 菜单图标颜色
        applyMenuTheme(menuEditProfile, colors);
        applyMenuTheme(menuDataManage, colors);
        applyMenuTheme(menuTheme, colors);
        applyMenuTheme(menuRecycle, colors);
        applyMenuTheme(menuSetting, colors);

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getView(), colors, isDark);
    }

    private void applyMenuTheme(View menu, ThemeColorUtil.ThemeColors colors) {
        if (menu == null) return;
        ViewGroup vg = (ViewGroup) menu;
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(colors.iconTint);
            }
        }
    }
}