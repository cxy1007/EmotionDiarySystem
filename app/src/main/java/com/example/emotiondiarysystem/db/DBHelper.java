package com.example.emotiondiarysystem.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "emotion_diary.db";
    private static final int DB_VERSION = 2;

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

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE user (" +
                    "userId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "account TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "nickname TEXT," +
                    "createTime TEXT)";

    private static final String CREATE_TABLE_DIARY =
            "CREATE TABLE diary (" +
                    "diaryId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER NOT NULL," +
                    "content TEXT," +
                    "createTime TEXT," +
                    "emotionType TEXT," +
                    "is_deleted INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_EMOTION =
            "CREATE TABLE emotion (" +
                    "emotionId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER NOT NULL," +
                    "emotionScore REAL," +
                    "statisticsTime TEXT)";

    private static final String CREATE_TABLE_AUDIO_NOTE =
            "CREATE TABLE audio_note (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "audio_path TEXT," +
                    "transcript TEXT," +
                    "duration INTEGER," +
                    "create_time TEXT," +
                    "emotion_tag TEXT)";

    private static final String CREATE_TABLE_RECOMMEND =
            "CREATE TABLE recommend (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "recommend_type TEXT," +
                    "content TEXT," +
                    "reason TEXT," +
                    "create_time TEXT)";

    private static final String CREATE_TABLE_MOOD_CHECKIN =
            "CREATE TABLE mood_checkin (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "mood_score INTEGER," +
                    "mood_tag TEXT," +
                    "note TEXT," +
                    "create_time TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_DIARY);
        db.execSQL(CREATE_TABLE_EMOTION);
        db.execSQL(CREATE_TABLE_AUDIO_NOTE);
        db.execSQL(CREATE_TABLE_RECOMMEND);
        db.execSQL(CREATE_TABLE_MOOD_CHECKIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE diary ADD COLUMN is_deleted INTEGER DEFAULT 0");
        }
    }

    public boolean checkUser(String account, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            if (password.isEmpty()) {
                cursor = db.query("user", null, "account = ?", new String[]{account}, null, null, null);
            } else {
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

    public String getPasswordByUserId(int userId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            cursor = db.query("user", new String[]{"password"}, "userId = ?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("password"));
            }
            return null;
        } catch (Exception e) {
            return null;
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

    public long addDiary(int userId, String content, String createTime, String emotionType) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("userId", userId);
            values.put("content", content);
            values.put("createTime", createTime);
            values.put("emotionType", emotionType);
            values.put("is_deleted", 0);
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

    public Cursor getAllDiaries(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query("diary", null, "userId = ? AND is_deleted = 0", new String[]{String.valueOf(userId)}, null, null, "createTime DESC");
    }

    public Cursor getDeletedDiaries(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query("diary", null, "userId = ? AND is_deleted = 1", new String[]{String.valueOf(userId)}, null, null, "createTime DESC");
    }

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

    public int softDeleteDiary(int diaryId) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("is_deleted", 1);
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

    public int restoreDiary(int diaryId) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("is_deleted", 0);
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

    public int permanentlyDeleteDiary(int diaryId) {
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

    public int deleteDiary(int diaryId) {
        return softDeleteDiary(diaryId);
    }

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

    public Cursor getRecommendations(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query("recommend", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, "create_time DESC");
    }
}
