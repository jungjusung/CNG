package com.sai.user.gnc;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by wbhlkc0 on 2016-12-19.
 */

public class initPermissionActivity extends AppCompatActivity {
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
            checkAccessPermission();
        } else {
            if (isRunning) {
                Toast.makeText(this, R.string.already_running, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                finish();
                Intent intent = new Intent(this, defaultAct.class);
                startActivity(intent);
                /*if (!isInstalled) {
                    addShortcut(this);
                }*/
            }
        }
    }

    public void checkAccessPermission() {
        int accessPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int iconPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int accessCall = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (accessPermission == PackageManager.PERMISSION_DENIED || accessCall == PackageManager.PERMISSION_DENIED || iconPermission == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_ACCESS_CALL);
        }

        if (accessPermission == PackageManager.PERMISSION_GRANTED && accessCall == PackageManager.PERMISSION_GRANTED && iconPermission == PackageManager.PERMISSION_GRANTED) {
            if (isRunning) {
                Toast.makeText(this, "이미 실행 중입니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                finish();
                Intent intent = new Intent(this, defaultAct.class);
                startActivity(intent);
                /*if (!isInstalled) {
                    addShortcut(this);
                }
*/

            }
        }
    }

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
                    finish();
                }
                break;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, defaultAct.class);
            startActivity(intent);
            /*if (!isInstalled) {
                addShortcut(this);
            }*/
        }

    }

/*
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
    }*/

    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
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

    public void showMsg(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        builder.setTitle(title).setMessage(msg).setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    @Override
    public void onBackPressed() {
    }
}
