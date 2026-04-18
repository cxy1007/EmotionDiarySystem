package com.example.emotiondiarysystem.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.emotiondiarysystem.bean.MoodCheckin;
import com.example.emotiondiarysystem.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class MoodManager {
    private static MoodManager instance;
    private final DBHelper dbHelper;

    private MoodManager(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public static synchronized MoodManager getInstance(Context context) {
        if (instance == null) {
            instance = new MoodManager(context);
        }
        return instance;
    }

    public long addMoodCheckin(int userId, int moodScore, String moodTag, String note, String createTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("mood_score", moodScore);
        values.put("mood_tag", moodTag);
        values.put("note", note);
        values.put("create_time", createTime);
        long result = db.insert("mood_checkin", null, values);
        db.close();
        return result;
    }

    public List<MoodCheckin> getMoodCheckinList(int userId) {
        List<MoodCheckin> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("mood_checkin", null, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, "create_time DESC");

        if (cursor.moveToFirst()) {
            do {
                MoodCheckin mood = new MoodCheckin();
                mood.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                mood.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                mood.setMoodScore(cursor.getInt(cursor.getColumnIndexOrThrow("mood_score")));
                mood.setMoodTag(cursor.getString(cursor.getColumnIndexOrThrow("mood_tag")));
                mood.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                mood.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow("create_time")));
                list.add(mood);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public int deleteMoodCheckin(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("mood_checkin", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public double getAverageMoodScore(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(mood_score) FROM mood_checkin WHERE user_id=?",
                new String[]{String.valueOf(userId)});
        double avg = 0;
        if (cursor.moveToFirst()) {
            avg = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return avg;
    }
}
