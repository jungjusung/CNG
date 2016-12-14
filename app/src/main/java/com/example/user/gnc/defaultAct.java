package com.example.user.gnc;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.example.user.gnc.com.example.user.gnc.settings.MyDB;
import com.example.user.gnc.db.ImageDAO;
import com.example.user.gnc.db.ShortcutDAO;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jusung on 2016. 11. 29..
 */

public class defaultAct extends Activity {
    private static final int WINDOW_ALERT_REQUEST = 1;
    private static final int REQUEST_ACCESS_CONTACTS = 2;
    private static final int REQUEST_ACCESS_CALL = 3;
    private static final int REQUEST_EXTERNAL_STORAGE = 4;

    boolean window_flag = false;
    boolean contact_flag = false;
    boolean storage_flag = false;
    boolean call_flag = false;
    String TAG;
    public static ImageDAO imageDAO;
    public static ShortcutDAO shortcutDAO;
    public static com.example.user.gnc.defaultAct defaultAct;
    MyDB myDB;
    public static SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        TAG = this.getClass().getName();
        defaultAct = this;
        imageDAO = new ImageDAO(this, "image_info.db", null, 1);
        shortcutDAO = new ShortcutDAO(this, "shortcut.db", null, 1);

        //권한 주기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean floatingWindowPermission = Settings.canDrawOverlays(this);
            Log.d(TAG, floatingWindowPermission + " permission");
            checkAccessPermission();
            if (floatingWindowPermission == false) {
                Log.d(TAG, "windowPermission if문");
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, WINDOW_ALERT_REQUEST);

            }else {
                startService(new Intent(this, StartActivity.class));
            }
        } else {
            startService(new Intent(this, StartActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onacivityresult "+Integer.toString(resultCode));
        restartApp();
        switch (requestCode) {
            case WINDOW_ALERT_REQUEST:
                if (resultCode == RESULT_OK) {
                    showMsg("안내", "다른 앱 위에 그리기 권한을 허용해 주셔야 사용이 가능합니다.");
                    break;
                }
        }
    }

    public void init(){
        myDB = new MyDB(this,"iot.sqlite",null,1);
        db =myDB.getWritableDatabase();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onresultper "+Integer.toString(requestCode));
        switch(requestCode){
            case REQUEST_ACCESS_CONTACTS:
                if(permissions.length>0&&grantResults[0]==PackageManager.PERMISSION_DENIED){
                    showMsg("안내", "연락처 사용권한을 주셔야 사용이 가능합니다.");
                }else{
                    contact_flag = true;
                }
                break;
            case REQUEST_ACCESS_CALL:
                if(permissions.length>0&&grantResults[0]==PackageManager.PERMISSION_DENIED){
                    showMsg("안내", "전화 사용권한을 주셔야 사용이 가능합니다.");
                }else{
                    call_flag = true;
                }
                break;
            case REQUEST_EXTERNAL_STORAGE:
                if(permissions.length>0&&grantResults[0]==PackageManager.PERMISSION_DENIED){
                    showMsg("안내", "외부저장소 사용권한을 주셔야 사용이 가능합니다.");
                }else{
                    storage_flag = true;
                }
                break;
        }
    }

    /*연락처, 전화, 사진 권한 요청*/
    public void checkAccessPermission() {
        int accessPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int iconPermission= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int accessCall= ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE);
        if (accessPermission == PackageManager.PERMISSION_DENIED||accessCall==PackageManager.PERMISSION_DENIED||iconPermission== PackageManager.PERMISSION_DENIED) {
            //유저에게 권한 줄것을 요청
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS,
            }, REQUEST_ACCESS_CONTACTS);
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CALL_PHONE
            }, REQUEST_ACCESS_CALL);
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            }, REQUEST_EXTERNAL_STORAGE);
        }
        Log.d(TAG, "checkAccess 메서드 종료");
    }

    public void showMsg(String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title).setMessage(msg).show();
    }

    public void restartApp(){
        PendingIntent i = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getIntent()), getIntent().getFlags());
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + 50, i);
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
