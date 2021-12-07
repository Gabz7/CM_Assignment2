package com.example.challenge2_m2.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    public static final String NOTES_TABLE = "notes";
    public static final String TOPICS_TABLE = "topics";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CONTENT = "content";
    private static final String DATABASE_NAME = "Notes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String NOTES_TABLE_CREATE =
            "CREATE TABLE " + NOTES_TABLE + " (" +
                    KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    KEY_NAME + " TEXT, " +
                    KEY_CONTENT + " TEXT);";

    private static final String TOPICS_TABLE_CREATE =
            "CREATE TABLE " + TOPICS_TABLE + " (" +
                    KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    KEY_NAME + " TEXT);";

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NOTES_TABLE_CREATE);
        db.execSQL(TOPICS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("I UPDATED");
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TOPICS_TABLE);
        onCreate(db);
    }
}
