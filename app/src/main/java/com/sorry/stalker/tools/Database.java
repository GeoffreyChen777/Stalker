package com.sorry.stalker.tools;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sorry.stalker.datastructure.ShowsInfor;

import java.util.ArrayList;

/**
 * Created by sorry on 2016/5/10.
 */
public class Database extends AppCompatActivity {
    private SQLiteDatabase db;
    public Database(){
        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS SelectedShow (id VARCHAR PRIMARY KEY, name VARCHAR, engname VARCHAR, status VARCHAR, overview VARCHAR, imgUrl VARCHAR, posterImgUrl VARCHAR, airDate VARCHAR, airedSeason VARCHAR, airedEpisodeNumber VARCHAR)");
    }

    public ArrayList<ShowsInfor> getData(){
        ArrayList<ShowsInfor> ShowList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from SelectedShow",null);

        if(cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++){
                cursor.move(i);
                ShowsInfor showsInfor = new ShowsInfor();
                showsInfor.ID = cursor.getString(0);
                showsInfor.name = cursor.getString(1);
                showsInfor.engname = cursor.getString(2);
                showsInfor.status = cursor.getString(3);
                showsInfor.overview = cursor.getString(4);
                showsInfor.imgUrl = cursor.getString(5);
                showsInfor.posterImgUrl = cursor.getString(6);
                showsInfor.airDate = cursor.getString(7);
                showsInfor.airedSeason = cursor.getString(8);
                showsInfor.airedEpisodeNumber = cursor.getString(9);
                ShowList.add(showsInfor);
            }
        }
        cursor.close();
        return ShowList;
    }

    public void insertData(ShowsInfor showsInfor){
        ContentValues cValue = new ContentValues();
        cValue.put("id",showsInfor.ID);
        cValue.put("name",showsInfor.name);
        cValue.put("engname",showsInfor.engname);
        cValue.put("status",showsInfor.status);
        cValue.put("overview",showsInfor.overview);
        cValue.put("imgUrl",showsInfor.imgUrl);
        cValue.put("posterImgUrl",showsInfor.posterImgUrl);
        cValue.put("airDate",showsInfor.airDate);
        cValue.put("airedSeason",showsInfor.airedSeason);
        cValue.put("airedEpisodeNumber",showsInfor.airedEpisodeNumber);
        db.insert("SelectedShow", null, cValue);
    }

    public void deleteData(ShowsInfor showsInfor){
        String sql = "delete from SelectedShow where id ='" + showsInfor.ID +"'";
        db.execSQL(sql);
    }

    public void deleteAllData(){
        String sql = "delete from SelectedShow";
        db.execSQL(sql);
    }

}
