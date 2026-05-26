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
    public long addDiary(int userId, String title, String content, String createTime, String emotionType, String weatherTag, String moodTag, String activityTag) {
        return addDiary(userId, title, content, createTime, emotionType, weatherTag, moodTag, activityTag, null);
    }

    // 添加日记（带照片）
    public long addDiary(int userId, String title, String content, String createTime, String emotionType, String weatherTag, String moodTag, String activityTag, String photoPaths) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("title", title);
        values.put("content", content);
        values.put("createTime", createTime);
        values.put("emotionType", emotionType);
        values.put("weatherTag", weatherTag);
        values.put("moodTag", moodTag);
        values.put("activityTag", activityTag);
        values.put("photoPaths", photoPaths);
        long id = db.insert("diary", null, values);
        db.close();
        return id;
    }

    // 获取某个用户的所有未删除日记
    public List<Diary> getDiaryListByUserId(int userId) {
        List<Diary> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("diary", null, "userId=? AND is_deleted=0", new String[]{String.valueOf(userId)},
                null, null, "createTime desc, diaryId desc");

        if (cursor.moveToFirst()) {
            do {
                Diary diary = new Diary();
                diary.setDiaryId(cursor.getInt(cursor.getColumnIndexOrThrow("diaryId")));
                diary.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                diary.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                diary.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
                diary.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow("createTime")));
                diary.setEmotionType(cursor.getString(cursor.getColumnIndexOrThrow("emotionType")));
                diary.setWeatherTag(cursor.getString(cursor.getColumnIndexOrThrow("weatherTag")));
                diary.setMoodTag(cursor.getString(cursor.getColumnIndexOrThrow("moodTag")));
                diary.setActivityTag(cursor.getString(cursor.getColumnIndexOrThrow("activityTag")));
                diary.setPhotoPaths(cursor.getString(cursor.getColumnIndexOrThrow("photoPaths")));
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
                null, null, "createTime desc, diaryId desc");

        if (cursor.moveToFirst()) {
            do {
                Diary diary = new Diary();
                diary.setDiaryId(cursor.getInt(cursor.getColumnIndexOrThrow("diaryId")));
                diary.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                diary.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                diary.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
                diary.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow("createTime")));
                diary.setEmotionType(cursor.getString(cursor.getColumnIndexOrThrow("emotionType")));
                diary.setWeatherTag(cursor.getString(cursor.getColumnIndexOrThrow("weatherTag")));
                diary.setMoodTag(cursor.getString(cursor.getColumnIndexOrThrow("moodTag")));
                diary.setActivityTag(cursor.getString(cursor.getColumnIndexOrThrow("activityTag")));
                diary.setPhotoPaths(cursor.getString(cursor.getColumnIndexOrThrow("photoPaths")));
                diary.setIsDeleted(1);
                list.add(diary);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // 更新日记
    public int updateDiary(int diaryId, String title, String content, String emotionType, String weatherTag, String moodTag, String activityTag) {
        return updateDiary(diaryId, title, content, emotionType, weatherTag, moodTag, activityTag, null);
    }

    // 更新日记（带照片）
    public int updateDiary(int diaryId, String title, String content, String emotionType, String weatherTag, String moodTag, String activityTag, String photoPaths) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("emotionType", emotionType);
        values.put("weatherTag", weatherTag);
        values.put("moodTag", moodTag);
        values.put("activityTag", activityTag);
        values.put("photoPaths", photoPaths);
        int rows = db.update("diary", values, "diaryId=?", new String[]{String.valueOf(diaryId)});
        db.close();
        return rows;
    }

    // 根据ID查询日记
    public Diary getDiaryById(int diaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("diary", null, "diaryId=?", new String[]{String.valueOf(diaryId)},
                null, null, null);

        Diary diary = null;
        if (cursor.moveToFirst()) {
            diary = new Diary();
            diary.setDiaryId(cursor.getInt(cursor.getColumnIndexOrThrow("diaryId")));
            diary.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
            diary.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            diary.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
            diary.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow("createTime")));
            diary.setEmotionType(cursor.getString(cursor.getColumnIndexOrThrow("emotionType")));
            diary.setWeatherTag(cursor.getString(cursor.getColumnIndexOrThrow("weatherTag")));
            diary.setMoodTag(cursor.getString(cursor.getColumnIndexOrThrow("moodTag")));
            diary.setActivityTag(cursor.getString(cursor.getColumnIndexOrThrow("activityTag")));
            diary.setPhotoPaths(cursor.getString(cursor.getColumnIndexOrThrow("photoPaths")));
            diary.setIsDeleted(cursor.getInt(cursor.getColumnIndexOrThrow("is_deleted")));
        }
        cursor.close();
        db.close();
        return diary;
    }
}