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
import android.widget.Toast;

import com.example.user.gnc.com.example.user.gnc.settings.MyDB;
import com.example.user.gnc.com.example.user.gnc.settings.SubDB;

import java.util.List;

/**
 * Created by wbhlkc0 on 2016-12-19.
 */

public class initPermissionActivity extends AppCompatActivity{

    private static final int REQUEST_ACCESS_CALL = 2;
    String TAG;
    public SharedPreferences preferences;
    public boolean isInstalled;

    SubDB subDB;
    public static SQLiteDatabase sub_db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getName();


        if(savedInstanceState!=null) {

            Bundle bundle = savedInstanceState.getParcelable("exitBundle");

            if (bundle != null) {

                String data = bundle.getString("exit");
                Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
            }
        }

        Log.d(TAG, "온크리에이트 시작");
        init();
        String sql = "select * from flag";
        Cursor rs = sub_db.rawQuery(sql,null);
        rs.moveToNext();
        int count=rs.getInt(rs.getColumnIndex("x"));
        Log.d(TAG,Integer.toString(count)+"카운트다");

        if(count==0) {//맨처음
            //setContentView(R.layout.init_permission_activity);
            preferences = getSharedPreferences("what", MODE_PRIVATE);
            isInstalled = preferences.getBoolean("isInstalled", false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "하이유 하이유~~");
                checkAccessPermission();
            } else {
                Intent intent = new Intent(this, defaultAct.class);
                startActivity(intent);

                if (!isInstalled) {
                    addShortcut(this);
                }
            }

        }else if(count==1){//2번째 실행
            finish();
            Toast.makeText(this, "이미 실행되고 있습니다.", Toast.LENGTH_SHORT).show();
            /*if(StartActivity.heroIcon==null){
                Log.d(TAG,"나야 나 없으면 나 실행시켜~");
                Intent intent = new Intent(this,defaultAct.class);
                startActivity(intent);
            }*/
            /*Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);*/

        }

    }

    public void init() {
        Log.d(TAG, "디비 생성");
        subDB = new SubDB(this, "initiot.sqlite", null, 1);
        sub_db = subDB.getWritableDatabase();
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.user.gnc.CNG".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
        }else{
            Intent intent = new Intent(this,defaultAct.class);
            startActivity(intent);
            if (!isInstalled) {
                addShortcut(this);
            }
            String updateSql = "update flag set x=1";
            sub_db.execSQL(updateSql);
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
            String updateSql = "update flag set x=1";
            sub_db.execSQL(updateSql);
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
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG,"강제종료");
        super.onSaveInstanceState(outState);
        Bundle bundle=new Bundle();
        bundle.putString("exit","강제종료됬습니다.");
        outState.putParcelable("exitBundle",bundle);
    }
    protected void onDestroy() {
        Log.d(TAG, "마지막에 내가 꺼졌따~");
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }


}
