package com.example.emotiondiarysystem.utils;

import android.content.Context;
import android.text.TextUtils;

import com.example.emotiondiarysystem.bean.User;
import com.example.emotiondiarysystem.manager.UserManager;

/**
 * 登录态自修复工具：
 * 当 userId 丢失时，尝试使用 account 回查数据库并补齐会话信息。
 */
public final class SessionUtil {

    private SessionUtil() {
    }

    public static int ensureUserId(Context context) {
        int userId = SpUtil.getInt(context, "userId", -1);
        if (userId > 0) {
            return userId;
        }

        String account = SpUtil.getString(context, "account", "");
        if (TextUtils.isEmpty(account)) {
            return -1;
        }

        User user = new UserManager(context).login(account);
        if (user == null || user.getUserId() <= 0) {
            return -1;
        }

        // 自修复会话，避免后续页面继续因为 userId 缺失失败
        SpUtil.putInt(context, "userId", user.getUserId());
        SpUtil.putString(context, "account", user.getAccount() == null ? "" : user.getAccount());
        SpUtil.putString(context, "nickname", user.getNickname() == null ? "" : user.getNickname());
        return user.getUserId();
    }
}
