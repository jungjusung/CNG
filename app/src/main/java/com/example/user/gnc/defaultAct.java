package com.example.user.gnc;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.user.gnc.com.example.user.gnc.settings.MyDB;

public class defaultAct extends Activity {
    private static final int WINDOW_ALERT_REQUEST = 1;

    /*public SharedPreferences preferences;
    public boolean isInstalled;*/
    private static final int REQUEST_ACCESS_CALL = 2;
    String TAG;
    public static com.example.user.gnc.defaultAct defaultAct;
    MyDB myDB;
    public static SQLiteDatabase db;

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "나 디폴트냐??");

        /*preferences = getSharedPreferences("what", MODE_PRIVATE);
        isInstalled = preferences.getBoolean("isInstalled", false);
*/
        TAG = this.getClass().getName();
        defaultAct = this;
        //권한 주기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean floatingWindowPermission = Settings.canDrawOverlays(this);
            Log.d(TAG, floatingWindowPermission + " permission");
            if (floatingWindowPermission == false) {
                Log.d(TAG, "windowPermission if문");
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, WINDOW_ALERT_REQUEST);
            } else {

                startService(new Intent(this, StartActivity.class));

            }
        } else {
            startService(new Intent(this, StartActivity.class));

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onactivityresult " + Integer.toString(resultCode));
        restartApp();
        switch (requestCode) {
            case WINDOW_ALERT_REQUEST:
                if (resultCode == RESULT_OK) {
                    showMsg("안내", "다른 앱 위에 그리기 권한을 허용해 주셔야 사용이 가능합니다.");
                    break;
                }
        }
    }

    public void init() {
        myDB = new MyDB(this, "iot.sqlite", null, 1);
        db = myDB.getWritableDatabase();
    }

    public void showMsg(String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title).setMessage(msg).show();
    }

    public void restartApp() {
        PendingIntent i = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getIntent()), getIntent().getFlags());
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + 50, i);
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
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

    /*권한 설정!! 사진, 전화, 외부저장소*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onresultper " + Integer.toString(requestCode));
        switch (requestCode) {
            case REQUEST_ACCESS_CALL:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showMsg("안내", "연락처 사용권한을 주셔야 사용이 가능합니다.");
                } else if (permissions.length > 0 && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    showMsg("안내", "전화 사용권한을 주셔야 사용이 가능합니다.");
                } else if (permissions.length > 0 && grantResults[2] == PackageManager.PERMISSION_DENIED) {
                    showMsg("안내", "외부저장소 사용권한을 주셔야 사용이 가능합니다.");
                } else {
                }
                break;
        }
    }

    /*연락처, 전화, 사진 권한 요청*/
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
        Log.d(TAG, "checkAccess 메서드 종료");

    }


    protected void onDestroy() {
        Log.d(TAG, "내가 꺼졌따~");
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }


}

