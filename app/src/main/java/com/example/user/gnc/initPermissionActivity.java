package com.example.user.gnc;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.gnc.com.example.user.gnc.settings.MyDB;
import com.example.user.gnc.com.example.user.gnc.settings.SubDB;

import java.util.List;

/**
 * Created by wbhlkc0 on 2016-12-19.
 */

public class initPermissionActivity extends AppCompatActivity{
    String TAG;

    private static final int REQUEST_ACCESS_CALL = 2;
    public SharedPreferences preferences;
    public boolean isInstalled;

    boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getName();

        isRunning = isServiceRunningCheck();
        preferences = getSharedPreferences("what", MODE_PRIVATE);
        isInstalled = preferences.getBoolean("isInstalled", false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "하이유 하이유~~");
            checkAccessPermission();
        } else {
            if (isRunning) {
                Toast.makeText(this, "이미 실행 중입니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                finish();
                Intent intent = new Intent(this, defaultAct.class);
                startActivity(intent);
                if (!isInstalled) {
                    addShortcut(this);
                }
            }
        }
    }

    public boolean isActivityTop(){
        ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1);
        if(info.get(0).topActivity.getClassName().equals(initPermissionActivity.this.getClass().getName())){
            return true;
        }else{
            return false;
        }
    }

    public void checkAccessPermission() {
        int accessPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int iconPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int accessCall = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (accessPermission == PackageManager.PERMISSION_DENIED || accessCall == PackageManager.PERMISSION_DENIED || iconPermission == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, REQUEST_ACCESS_CALL);
        }

        if(accessPermission == PackageManager.PERMISSION_GRANTED || accessCall == PackageManager.PERMISSION_GRANTED && iconPermission == PackageManager.PERMISSION_GRANTED){
            if(isRunning){
                Toast.makeText(this, "이미 실행 중입니다.", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                finish();
                Intent intent = new Intent(this, defaultAct.class);
                startActivity(intent);
                if (!isInstalled) {
                    addShortcut(this);
                }
            }
        }
        Log.d(TAG, "checkAccess 메서드 종료");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onresultper " + Integer.toString(requestCode));
        switch (requestCode) {
            case REQUEST_ACCESS_CALL:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showMsg("안내", "연락처 사용권한을 주셔야 사용이 가능합니다.");
                    finish();
                } else if (permissions.length > 0 && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    showMsg("안내", "전화 사용권한을 주셔야 사용이 가능합니다.");
                    finish();
                } else if (permissions.length > 0 && grantResults[2] == PackageManager.PERMISSION_DENIED) {
                    showMsg("안내", "외부저장소 사용권한을 주셔야 사용이 가능합니다.");
                    finish();
                } else {
                    finish();
                }
                break;
        }

        Log.d(TAG,"여기는 오는데?");
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED&& grantResults[2] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"여기는 안오니??");
            Intent intent = new Intent(this,defaultAct.class);
            startActivity(intent);
            if (!isInstalled) {
                addShortcut(this);
            }
           // String updateSql = "update flag set x=1";
           // sub_db.execSQL(updateSql);
        }

    }

    public void showMsg(String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title).setMessage(msg).show();
    }

    private void addShortcut(Context context) {
        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcutIntent.setClassName(context, getClass().getName());
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getResources().getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context,
                        R.drawable.logo2));
        intent.putExtra("duplicate", false);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        sendBroadcast(intent);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isInstalled", true);
        editor.commit();
    }

    protected void onDestroy() {
        Log.d(TAG, "마지막에 내가 꺼졌따~");
        /*RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();*/
        super.onDestroy();
    }


    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.user.gnc.StartActivity".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
