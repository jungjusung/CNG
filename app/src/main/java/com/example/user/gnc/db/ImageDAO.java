package com.example.user.gnc.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by user on 2016-12-02.
 */

public class ImageDAO extends SQLiteOpenHelper{

    String TAG = this.getClass().getName();

    public ImageDAO(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "image_info 테이블 생성");
        db.execSQL("create table image_info (x int, y int, size int, path varchar(200));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "image_info 테이블 호출");
    }

    public void insert(){

        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG, "imageDAO insert 실행");
        db.execSQL("insert into image_info (x, y, size, path) values (0, 0, 0, '')");
        db.close();
        Log.d(TAG, "imageDAO insert 종료");

    }

    public void selectAll(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cs = db.rawQuery("select * from image_info", null);

        while(cs.moveToNext()){

        }
    }
}