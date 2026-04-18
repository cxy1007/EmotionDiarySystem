package com.example.emotiondiarysystem.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // 数据库名称
    private static final String DB_NAME = "emotion_diary.db";
    // 数据库版本
    private static final int DB_VERSION = 1;

    // 单例
    private static DBHelper instance;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ====================== 建表语句 ======================
    // 用户表
    private static final String CREATE_TABLE_USER =
            "CREATE TABLE user (" +
                    "userId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "account TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "nickname TEXT," +
                    "createTime TEXT)";

    // 日记表
    private static final String CREATE_TABLE_DIARY =
            "CREATE TABLE diary (" +
                    "diaryId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER NOT NULL," +
                    "content TEXT," +
                    "createTime TEXT," +
                    "emotionType TEXT)";

    // 情感记录表
    private static final String CREATE_TABLE_EMOTION =
            "CREATE TABLE emotion (" +
                    "emotionId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER NOT NULL," +
                    "emotionScore REAL," +
                    "statisticsTime TEXT)";

    // 语音转文字表
    private static final String CREATE_TABLE_AUDIO_NOTE =
            "CREATE TABLE audio_note (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "audio_path TEXT," +
                    "transcript TEXT," +
                    "duration INTEGER," +
                    "create_time TEXT," +
                    "emotion_tag TEXT)";

    // 智能推荐表
    private static final String CREATE_TABLE_RECOMMEND =
            "CREATE TABLE recommend (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "recommend_type TEXT," +
                    "content TEXT," +
                    "reason TEXT," +
                    "create_time TEXT)";

    // 心情打卡表
    private static final String CREATE_TABLE_MOOD_CHECKIN =
            "CREATE TABLE mood_checkin (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "mood_score INTEGER," +
                    "mood_tag TEXT," +
                    "note TEXT," +
                    "create_time TEXT)";

    // ====================== 创建数据库 ======================
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_DIARY);
        db.execSQL(CREATE_TABLE_EMOTION);
        db.execSQL(CREATE_TABLE_AUDIO_NOTE);
        db.execSQL(CREATE_TABLE_RECOMMEND);
        db.execSQL(CREATE_TABLE_MOOD_CHECKIN);
    }

    // ====================== 更新数据库 ======================
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS diary");
        db.execSQL("DROP TABLE IF EXISTS emotion");
        db.execSQL("DROP TABLE IF EXISTS audio_note");
        db.execSQL("DROP TABLE IF EXISTS recommend");
        db.execSQL("DROP TABLE IF EXISTS mood_checkin");
        onCreate(db);
    }

    // ====================== 用户相关操作 ======================
    // 检查用户是否存在
    public boolean checkUser(String account, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            if (password.isEmpty()) {
                // 只检查账号是否存在
                cursor = db.query("user", null, "account = ?", new String[]{account}, null, null, null);
            } else {
                // 检查账号和密码
                cursor = db.query("user", null, "account = ? AND password = ?", new String[]{account, password}, null, null, null);
            }
            return cursor != null && cursor.moveToFirst();
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                }
            }
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                }
            }
        }
    }

    // 注册用户
    public long registerUser(String account, String password, String nickname, String createTime) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("account", account);
            values.put("password", password);
            values.put("nickname", nickname);
            values.put("createTime", createTime);
            return db.insert("user", null, values);
        } catch (Exception e) {
            return -1;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                }
            }
        }
    }

    // 获取用户ID
    public int getUserId(String account) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            cursor = db.query("user", new String[]{"userId"}, "account = ?", new String[]{account}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("userId"));
            }
            return -1;
        } catch (Exception e) {
            return -1;
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                }
            }
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                }
            }
        }
    }

    // ====================== 日记相关操作 ======================
    // 添加日记
    public long addDiary(int userId, String content, String createTime, String emotionType) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("userId", userId);
            values.put("content", content);
            values.put("createTime", createTime);
            values.put("emotionType", emotionType);
            return db.insert("diary", null, values);
        } catch (Exception e) {
            return -1;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                }
            }
        }
    }

    // 获取所有日记
    public Cursor getAllDiaries(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query("diary", null, "userId = ?", new String[]{String.valueOf(userId)}, null, null, "createTime DESC");
    }

    // 更新日记
    public int updateDiary(int diaryId, String content, String emotionType) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("content", content);
            values.put("emotionType", emotionType);
            return db.update("diary", values, "diaryId = ?", new String[]{String.valueOf(diaryId)});
        } catch (Exception e) {
            return 0;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                }
            }
        }
    }

    // 删除日记
    public int deleteDiary(int diaryId) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            return db.delete("diary", "diaryId = ?", new String[]{String.valueOf(diaryId)});
        } catch (Exception e) {
            return 0;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                }
            }
        }
    }

    // ====================== 心情打卡相关操作 ======================
    // 添加心情打卡
    public long addMoodCheckin(int userId, int moodScore, String moodTag, String note, String createTime) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("mood_score", moodScore);
            values.put("mood_tag", moodTag);
            values.put("note", note);
            values.put("create_time", createTime);
            return db.insert("mood_checkin", null, values);
        } catch (Exception e) {
            return -1;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                }
            }
        }
    }

    // ====================== 语音转文字相关操作 ======================
    // 添加语音笔记
    public long addAudioNote(int userId, String audioPath, String transcript, int duration, String createTime, String emotionTag) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("audio_path", audioPath);
            values.put("transcript", transcript);
            values.put("duration", duration);
            values.put("create_time", createTime);
            values.put("emotion_tag", emotionTag);
            return db.insert("audio_note", null, values);
        } catch (Exception e) {
            return -1;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                }
            }
        }
    }

    // ====================== 智能推荐相关操作 ======================
    // 添加推荐
    public long addRecommend(int userId, String recommendType, String content, String reason, String createTime) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("recommend_type", recommendType);
            values.put("content", content);
            values.put("reason", reason);
            values.put("create_time", createTime);
            return db.insert("recommend", null, values);
        } catch (Exception e) {
            return -1;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                }
            }
        }
    }

    // 获取推荐列表
    public Cursor getRecommendations(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query("recommend", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, "create_time DESC");
    }
}