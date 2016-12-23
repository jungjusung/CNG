package com.sai.user.gnc.settings;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by wbhlkc0 on 2016-12-20.
 */

public class SubDB extends SQLiteOpenHelper {
    String TAG;
    public SubDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        TAG = this.getClass().getName();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate 호출");
        sqLiteDatabase.execSQL("create table flag(x int);");
        sqLiteDatabase.execSQL("insert into flag(x) values(0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "onUpGrade 호출");
    }
}
