package com.sai.user.gnc;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by wbhlkc0 on 2016-12-24.
 */

public class OverlayPermiissionActivity extends AppCompatActivity{
    String TAG;
    private static final int WINDOW_ALERT_REQUEST = 1;
    OverlayPermiissionActivity overlayPermiissionActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overlayPermiissionActivity = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean floatingWindowPermission = Settings.canDrawOverlays(overlayPermiissionActivity);
            Log.d(TAG, floatingWindowPermission + " permission");
            if (floatingWindowPermission == false) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, WINDOW_ALERT_REQUEST);
            } else {
                Intent intent = new Intent(overlayPermiissionActivity,defaultAct.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(overlayPermiissionActivity,defaultAct.class);
            startActivity(intent);
            finish();
        }
        //권한 주기
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

    public void restartApp() {
        finish();
        Intent intent = new Intent(overlayPermiissionActivity,defaultAct.class);
        startActivity(intent);
    }


}