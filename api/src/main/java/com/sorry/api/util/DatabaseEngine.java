package com.sorry.api.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseEngine {
    private static final String TAG = "DatabaseEngine";
    private static DatabaseEngine instance = null;
    private static SQLiteDatabase writedb;
    private static SQLiteDatabase readdb;
    private static SQLiteHelper sqLiteHelper;

    private DatabaseEngine(Context context){
        sqLiteHelper = new SQLiteHelper(context, "BodyData.db", null, 1);
        writedb = sqLiteHelper.getWritableDatabase();
        readdb = sqLiteHelper.getReadableDatabase();
    }

    public static DatabaseEngine getInstance(Context context){
        if(instance == null){
            instance = new DatabaseEngine(context);
        }
        return instance;
    }

    public Cursor select(String sql){
        return readdb.rawQuery(sql ,null);
    }

    public void exec(String sql){
        writedb.execSQL(sql);
    }

    public void insert(ContentValues contentValues, String table){
        writedb.insert(table, null, contentValues);
    }



}
