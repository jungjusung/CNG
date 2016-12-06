package com.example.user.gnc.com.example.user.gnc.settings;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by wbhlkc0 on 2016-12-02.
 */

public class MyDB extends SQLiteOpenHelper{
    String TAG;
    public MyDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        TAG=this.getClass().getName();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG,"onCreate 호출");
        /*어플리케이션에 필요한 테이블이 있으면 테이블은 이 시점에 구축하자.*/
        sqLiteDatabase.execSQL("create table shortcut(short_cut int, path varchar(200), name varchar(20), method int);");
        sqLiteDatabase.execSQL("insert into shortcut ( short_cut) values (1)");
        sqLiteDatabase.execSQL("insert into shortcut ( short_cut) values (2)");
        sqLiteDatabase.execSQL("insert into shortcut ( short_cut) values (3)");
        sqLiteDatabase.execSQL("insert into shortcut ( short_cut) values (4)");
        sqLiteDatabase.execSQL("insert into shortcut ( short_cut) values (5)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG,"onUpGrade 호출");
    }
}
