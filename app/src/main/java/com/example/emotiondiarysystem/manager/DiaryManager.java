package com.example.emotiondiarysystem.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class DiaryManager {
    private final DBHelper dbHelper;

    public DiaryManager(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    // 添加日记
    public long addDiary(int userId, String content, String createTime, String emotionType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("content", content);
        values.put("createTime", createTime);
        values.put("emotionType", emotionType);
        long id = db.insert("diary", null, values);
        db.close();
        return id;
    }

    // 获取某个用户的所有未删除日记
    public List<Diary> getDiaryListByUserId(int userId) {
        List<Diary> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("diary", null, "userId=? AND is_deleted=0", new String[]{String.valueOf(userId)},
                null, null, "diaryId desc");

        if (cursor.moveToFirst()) {
            do {
                Diary diary = new Diary();
                diary.setDiaryId(cursor.getInt(cursor.getColumnIndexOrThrow("diaryId")));
                diary.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                diary.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
                diary.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow("createTime")));
                diary.setEmotionType(cursor.getString(cursor.getColumnIndexOrThrow("emotionType")));
                diary.setIsDeleted(0);
                list.add(diary);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // 删除日记（软删除）
    public int deleteDiary(int diaryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_deleted", 1);
        int rows = db.update("diary", values, "diaryId=?", new String[]{String.valueOf(diaryId)});
        db.close();
        return rows;
    }

    // 恢复日记
    public int restoreDiary(int diaryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_deleted", 0);
        int rows = db.update("diary", values, "diaryId=?", new String[]{String.valueOf(diaryId)});
        db.close();
        return rows;
    }

    // 彻底删除日记（物理删除）
    public int deleteDiaryPermanently(int diaryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("diary", "diaryId=?", new String[]{String.valueOf(diaryId)});
        db.close();
        return rows;
    }

    // 获取已删除的日记
    public List<Diary> getDeletedDiaryListByUserId(int userId) {
        List<Diary> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("diary", null, "userId=? AND is_deleted=1", new String[]{String.valueOf(userId)},
                null, null, "diaryId desc");

        if (cursor.moveToFirst()) {
            do {
                Diary diary = new Diary();
                diary.setDiaryId(cursor.getInt(cursor.getColumnIndexOrThrow("diaryId")));
                diary.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                diary.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
                diary.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow("createTime")));
                diary.setEmotionType(cursor.getString(cursor.getColumnIndexOrThrow("emotionType")));
                diary.setIsDeleted(1);
                list.add(diary);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // 修改日记
    public int updateDiary(int diaryId, String content, String emotionType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", content);
        values.put("emotionType", emotionType);
        int rows = db.update("diary", values, "diaryId=?", new String[]{String.valueOf(diaryId)});
        db.close();
        return rows;
    }
}