package com.example.user.gnc.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by user on 2016-12-02.
 */

public class ShortcutDAO extends SQLiteOpenHelper{
    String TAG = this.getClass().getName();
    SQLiteDatabase db;
    public ShortcutDAO(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        this.db = DB;
        Log.d(TAG, "shortcut 테이블 생성");
        this.db.execSQL("create table shortcut (short_cut int, path varchar(200), method int);");
        insert(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "shortcut 테이블 호출");
    }

    public void insert(SQLiteDatabase DB){
        DB = getWritableDatabase();
        DB.execSQL("insert into shortcut ( short_cut) values (1)");
        DB.execSQL("insert into shortcut ( short_cut) values (2)");
        DB.execSQL("insert into shortcut ( short_cut) values (3)");
        DB.execSQL("insert into shortcut ( short_cut) values (4)");
        DB.execSQL("insert into shortcut ( short_cut) values (5)");
    }

    public void update(int i, String pkg, int method) {
        db = getWritableDatabase();
        Log.d(TAG, pkg);
        String sql = "update shortcut set path='"+pkg+"', method="+method+" where short_cut = "+i;
        Log.d(TAG, sql);
        db.execSQL(sql);
        Log.d(TAG, "update문 종료");
        selectAll();
    }

    public void selectAll(){
        db = getReadableDatabase();
        Cursor cs = db.rawQuery("select * from shortcut where short_cut = 1", null);
        Log.d(TAG, "selectAll문 실행");
        while(cs.moveToNext()){
            String result = cs.getString(1);
            Log.d(TAG, result);
        }
    }
}
