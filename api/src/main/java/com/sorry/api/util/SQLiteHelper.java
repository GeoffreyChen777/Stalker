package com.sorry.api.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sorry on 8/31/16.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    //调用父类构造器
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 当数据库首次创建时执行该方法，一般将创建表等初始化操作放在该方法中执行.
     * 重写onCreate方法，调用execSQL方法创建表
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS BodyData (date VARCHAR PRIMARY KEY, heartrate VARCHAR, step VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS PerdayHeartRateData (no INTEGER PRIMARY KEY autoincrement,time VARCHAR, heartrate VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS PerdayStepData (no INTEGER PRIMARY KEY autoincrement,time VARCHAR, step VARCHAR)");
    }

    //当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}