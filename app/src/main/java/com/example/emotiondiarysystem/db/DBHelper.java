package com.example.emotiondiarysystem.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // 数据库名称
    private static final String DB_NAME = "emotion_diary.db";
    // 数据库版本
    private static final int DB_VERSION = 3;

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
                    "avatar TEXT," +
                    "createTime TEXT)";

    // 日记表
    private static final String CREATE_TABLE_DIARY =
            "CREATE TABLE diary (" +
                    "diaryId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER NOT NULL," +
                    "content TEXT," +
                    "createTime TEXT," +
                    "emotionType TEXT," +
                    "is_deleted INTEGER DEFAULT 0)";

    // 情感记录表
    private static final String CREATE_TABLE_EMOTION =
            "CREATE TABLE emotion (" +
                    "emotionId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER NOT NULL," +
                    "emotionScore REAL," +
                    "statisticsTime TEXT)";

    // ====================== 创建数据库 ======================
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_DIARY);
        db.execSQL(CREATE_TABLE_EMOTION);
    }

    // ====================== 更新数据库 ======================
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS diary");
        db.execSQL("DROP TABLE IF EXISTS emotion");
        onCreate(db);
    }
}