package com.example.emotiondiarysystem.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.emotiondiarysystem.bean.Emotion;
import com.example.emotiondiarysystem.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class EmotionManager {
    private final DBHelper dbHelper;

    public EmotionManager(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    // 添加情感记录
    public long addEmotion(int userId, float emotionScore, String statisticsTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("emotionScore", emotionScore);
        values.put("statisticsTime", statisticsTime);
        long id = db.insert("emotion", null, values);
        db.close();
        return id;
    }

    // 获取用户情感记录
    public List<Emotion> getEmotionList(int userId) {
        List<Emotion> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("emotion", null, "userId=?", new String[]{String.valueOf(userId)},
                null, null, "emotionId desc");

        if (cursor.moveToFirst()) {
            do {
                Emotion e = new Emotion();
                e.setEmotionId(cursor.getInt(cursor.getColumnIndexOrThrow("emotionId")));
                e.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                e.setEmotionScore(cursor.getFloat(cursor.getColumnIndexOrThrow("emotionScore")));
                e.setStatisticsTime(cursor.getString(cursor.getColumnIndexOrThrow("statisticsTime")));
                list.add(e);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}