package com.example.emotiondiarysystem.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.emotiondiarysystem.bean.User;
import com.example.emotiondiarysystem.db.DBHelper;

public class UserManager {
    private static final String TAG = "UserManager";
    private final DBHelper dbHelper;

    public UserManager(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    // ====================== 1. 用户注册 ======================
    public long register(String account, String password, String nickname, String createTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("account", account);
        values.put("password", password);
        values.put("nickname", nickname);
        values.put("createTime", createTime);

        long result = db.insert("user", null, values);
        db.close();
        return result;
    }

    // ====================== 2. 用户登录（根据账号查密码） ======================
    public User login(String account) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("user",
                null,
                "account=?",
                new String[]{account},
                null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            try {
                user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                user.setAccount(cursor.getString(cursor.getColumnIndexOrThrow("account")));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow("nickname")));
                user.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow("createTime")));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "login: 解析用户数据失败", e);
            } finally {
                cursor.close();
            }
        } else {
            cursor.close();
        }

        db.close();
        return user;
    }

    // ====================== 3. 判断账号是否已存在 ======================
    public boolean isAccountExist(String account) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("user",
                new String[]{"userId"},
                "account=?",
                new String[]{account},
                null, null, null);

        boolean exist = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exist;
    }
}