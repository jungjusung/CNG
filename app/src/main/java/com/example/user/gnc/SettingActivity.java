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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.gnc.com.example.user.gnc.settings.ImageUtils;
import com.example.user.gnc.com.example.user.gnc.settings.KeySettingActivity;
import com.example.user.gnc.com.example.user.gnc.settings.SizeSettingActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.previewImage;


public class SettingActivity extends Activity {

    String TAG;
    String data;
    Uri uri;
    File file;
    String filePath;


    static String name;
    LinearLayout bt_icon, bt_key, bt_size, bt_location, bt_language;
    static final int REQ_CODE_SELECT_IMAGE = 100;
    WindowManager.LayoutParams windowParameters;
    int iconX, iconY;
    ImageView flagImg;


    /*------------------------------------------------------*/

    String sql;
    Cursor rs;
    LinearLayout layout;
    LinearLayout.LayoutParams layoutParams;
    Bitmap bitmap;
    Bitmap change_bitmap;

    /*------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (checkFlag() == 0) {

            Intent intent = new Intent(this, ManualSettingActivity.class);
            startActivity(intent);
        } else if (checkFlag() == -1) {
            Intent intent = new Intent(this, ManualSettingActivity.class);
            startActivity(intent);
        }

        //checkAccessPermission();


        setContentView(R.layout.setting_layout);

        /*=======================================
        광고니까 지우지마ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ
         ========================================
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ========================================*/

        bt_key = (LinearLayout) findViewById(R.id.bt_key);
        bt_icon = (LinearLayout) findViewById(R.id.bt_icon);
        bt_size = (LinearLayout) findViewById(R.id.bt_size);
        bt_location = (LinearLayout) findViewById(R.id.bt_location);
        bt_language = (LinearLayout) findViewById(R.id.bt_language);

        TAG = this.getClass().getName();
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.bt_key:
                Intent key_intent = new Intent(this, KeySettingActivity.class);
                startActivity(key_intent);
                break;
            case R.id.bt_icon:
                showMsg(getString(R.string.notice), getString(R.string.please_use_default_gallery));
                break;
            case R.id.bt_location:
                if (flagImg == null) {
                    Toast.makeText(this, R.string.Set_your_location_with_flag, Toast.LENGTH_SHORT).show();
                    windowParameters = new WindowManager.LayoutParams(200, 200, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                    layout = new LinearLayout(this);
                    layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layout.setLayoutParams(layoutParams);

                    flagImg = new ImageView(this);
                    flagImg.setImageResource(R.drawable.initlocationflag);
                    flagImg.setLayoutParams(layoutParams);
                    layout.addView(flagImg);
                    StartActivity.windowManager.addView(layout, windowParameters);

                    layout.setOnTouchListener(new View.OnTouchListener() {
                        private WindowManager.LayoutParams updatedParameters = windowParameters;
                        float touchedX, touchedY;

                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {

                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_DOWN:

                                    iconX = updatedParameters.x;
                                    iconY = updatedParameters.y;

                                    touchedX = motionEvent.getRawX();
                                    touchedY = motionEvent.getRawY();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    updatedParameters.x = (int) (iconX + (motionEvent.getRawX() - touchedX));
                                    updatedParameters.y = (int) (iconY + (motionEvent.getRawY() - touchedY));
                                    StartActivity.windowManager.updateViewLayout(layout, updatedParameters);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    StartActivity.initialPosX = updatedParameters.x;
                                    StartActivity.initialPosY = updatedParameters.y;
                                    StartActivity.windowManager.removeView(layout);
                                    updatedParameters.width = StartActivity.icon_width;
                                    updatedParameters.height = StartActivity.icon_height;
                                    StartActivity.windowManager.updateViewLayout(StartActivity.heroIcon, updatedParameters);
                                    sql = "update initialpos set x=?, y=?";

                                    StartActivity.db.execSQL(sql, new String[]{
                                            Integer.toString(StartActivity.initialPosX), Integer.toString(StartActivity.initialPosY)
                                    });


                                    sql = "select * from initialpos";
                                    rs = StartActivity.db.rawQuery(sql, null);

                                    rs.moveToNext();
                                    StartActivity.initialPosX = rs.getInt(rs.getColumnIndex("x"));
                                    StartActivity.initialPosY = rs.getInt(rs.getColumnIndex("y"));
                                    StartActivity.params2.x = StartActivity.initialPosX;
                                    StartActivity.params2.y = StartActivity.initialPosY;

                                    flagImg = null;
                                    break;
                            }
                            return false;
                        }
                    });
                }
                break;
            case R.id.bt_size:
                Intent size_intent = new Intent(this, SizeSettingActivity.class);
                startActivity(size_intent);
                break;
            case R.id.img_icon: //설정창에 이미지 아이콘
                Intent serviceIntent = new Intent(SettingActivity.this, StartActivity.class);
                serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                serviceIntent.putExtra("data", name);
                startService(serviceIntent);
                finish();
                break;

            case R.id.backToDefault: // 설정 초기화

                AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

                builder.setTitle(R.string.reset_all_setting).setMessage(R.string.do_you_want_to_reset).setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                doBackToDefault();
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            // 취소 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기

                break;

            case R.id.bt_language:
                Intent lang_intent = new Intent(Intent.ACTION_MAIN);
                lang_intent.setClassName("com.android.settings", "com.android.settings.LanguageSettings");
                startActivity(lang_intent);
                break;
        }
    }

    public void doBackToDefault() {

        StartActivity.initialPosX = 350;
        StartActivity.initialPosY = 600;
        StartActivity.icon_width = 150;
        StartActivity.icon_height = 150;
        StartActivity.params2.x = 350;
        StartActivity.params2.y = 600;
        StartActivity.params2.width = 150;
        StartActivity.params2.height = 150;
        if (SizeSettingActivity.iconParam != null) {
            StartActivity.main_parameters1.width = SizeSettingActivity.iconParam.width * 2;
            StartActivity.main_parameters1.height = SizeSettingActivity.iconParam.height;
            StartActivity.main_parameters2.width = SizeSettingActivity.iconParam.width * 2;
            StartActivity.main_parameters2.height = SizeSettingActivity.iconParam.height;
            StartActivity.sub_parameters1.width = SizeSettingActivity.iconParam.width;
            StartActivity.sub_parameters1.height = SizeSettingActivity.iconParam.height;
            StartActivity.sub_parameters2.width = SizeSettingActivity.iconParam.width;
            StartActivity.sub_parameters2.height = SizeSettingActivity.iconParam.height;
            StartActivity.txt_turn_parameters.width = SizeSettingActivity.iconParam.width;
            StartActivity.txt_setting_parameters.width = SizeSettingActivity.iconParam.width;
        } else {
            StartActivity.main_parameters1.width = StartActivity.icon_width * 2;
            StartActivity.main_parameters1.height = StartActivity.icon_height;
            StartActivity.main_parameters2.width = StartActivity.icon_width * 2;
            StartActivity.main_parameters2.height = StartActivity.icon_height;
            StartActivity.sub_parameters1.width = StartActivity.icon_width;
            StartActivity.sub_parameters1.height = StartActivity.icon_height;
            StartActivity.sub_parameters2.width = StartActivity.icon_width;
            StartActivity.sub_parameters2.height = StartActivity.icon_height;
            StartActivity.txt_turn_parameters.width = StartActivity.icon_width;
            StartActivity.txt_setting_parameters.width = StartActivity.icon_width;
        }
        StartActivity.windowManager.updateViewLayout(StartActivity.heroIcon, StartActivity.params2);
        StartActivity.heroIcon.setImageResource(R.drawable.logo2);

        String deleteImg_info = "delete from img_info";
        String deleteManual_flags = "delete from manual_flags";
        String deleteShortCut = "delete from shortcut";
        String deleteInitialPos = "delete from initialpos";
        String deleteWeb = "delete from web";

        StartActivity.db.execSQL(deleteImg_info);
        StartActivity.db.execSQL(deleteManual_flags);
        StartActivity.db.execSQL(deleteShortCut);
        StartActivity.db.execSQL(deleteInitialPos);
        StartActivity.db.execSQL(deleteWeb);

        String insertDefaultImg_info = "insert into img_info(x,y,size,path) values(350,600,150,'')";
        String insertDefaultManual_flags = "insert into manual_flags values( 0, 0, 0)";
        String insertDefaultShortCut1 = "insert into shortcut ( short_cut) values (1)";
        String insertDefaultShortCut2 = "insert into shortcut ( short_cut) values (2)";
        String insertDefaultShortCut3 = "insert into shortcut ( short_cut) values (3)";
        String insertDefaultShortCut4 = "insert into shortcut ( short_cut) values (4)";
        String insertDefaultShortCut5 = "insert into shortcut ( short_cut) values (5)";
        String insertDefaultInitailPos = "insert into initialpos(x,y) values(350,600)";
        String insertDefaultWeb1 = "insert into web(url) values('http://www.naver.com')";
        String insertDefaultWeb2 = "insert into web(url) values('http://www.daum.net')";
        String insertDefaultWeb3 = "insert into web(url) values('http://www.google.com')";
        String insertDefaultWeb4 = "insert into web(url) values('http://www.youtube.com')";

        StartActivity.db.execSQL(insertDefaultImg_info);
        StartActivity.db.execSQL(insertDefaultManual_flags);
        StartActivity.db.execSQL(insertDefaultShortCut1);
        StartActivity.db.execSQL(insertDefaultShortCut2);
        StartActivity.db.execSQL(insertDefaultShortCut3);
        StartActivity.db.execSQL(insertDefaultShortCut4);
        StartActivity.db.execSQL(insertDefaultShortCut5);
        StartActivity.db.execSQL(insertDefaultInitailPos);
        StartActivity.db.execSQL(insertDefaultWeb1);
        StartActivity.db.execSQL(insertDefaultWeb2);
        StartActivity.db.execSQL(insertDefaultWeb3);
        StartActivity.db.execSQL(insertDefaultWeb4);

        Toast.makeText(this, "초기화 완료", Toast.LENGTH_SHORT).show();

    }

    /* 세팅에 아이콘 이미지 변경 및 경로*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_IMAGE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Bundle extras = data.getExtras();
                    Log.d(TAG, "1");
                    //이미지 데이터를 비트맵으로 받아온다.
                    bitmap = extras.getParcelable("data");

                    filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CNG" + String.valueOf(System.currentTimeMillis()) + ".png";
                    file = new File(filePath);

                    uri = Uri.parse(String.valueOf(Uri.fromFile(file)));

                    ImageUtils.normalizeImageForUri(this.getApplicationContext(), uri);

                    storeCropImage(bitmap, filePath);
                    //배치해놓은 ImageView에 set

                    sql = "update img_info set path=?";
                    StartActivity.db.execSQL(sql, new String[]{
                            uri.toString()
                    });

                    change_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    StartActivity.startActivity.heroIcon.setImageBitmap(change_bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int checkFlag() {

        String sql = "select setting from manual_flags";

        Cursor cs = StartActivity.db.rawQuery(sql, null);
        if (cs != null) {
            cs.moveToNext();
            return cs.getInt(0);
        } else
            return -1;

    }

    private void storeCropImage(Bitmap bitmap, String filePath) {
        File copyFile = new File(filePath);
        BufferedOutputStream out = null;
        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void onDestroy() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (change_bitmap != null) {
            change_bitmap.recycle();
            change_bitmap = null;
        }
//        recycleBitmap(flagImg);
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();

        flagImg = null;

        super.onDestroy();
    }


    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Log.d(TAG,"강제종료된다.");

    }

    private static void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable) d).getBitmap();
            b.recycle();
        } // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

        d.setCallback(null);
    }

  public void showMsg(String title, String msg){
      AlertDialog.Builder alert= new AlertDialog.Builder(this);
      alert.setTitle(title).setMessage(msg).setCancelable(false)
              .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialogInterface, int i) {
                      Intent icon_intent = new Intent("com.android.camera.action.CROP");
                      icon_intent.setType("image/*");
                      icon_intent.putExtra("crop", "true");
                      icon_intent.putExtra("outputX", 200);
                      icon_intent.putExtra("outputY", 200);
                      icon_intent.putExtra("aspectX", 1);
                      icon_intent.putExtra("aspectY", 1);
                      icon_intent.putExtra("scale", true);
                      icon_intent.putExtra("scaleUpIfNeeded", true);
                      icon_intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

                      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                          icon_intent.setAction(Intent.ACTION_GET_CONTENT);
                          Log.d(TAG,"333");
                      } else {
                          icon_intent.setAction(Intent.ACTION_PICK);
                          icon_intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                          Log.d(TAG,"444");
                      }
                      startActivityForResult(icon_intent, REQ_CODE_SELECT_IMAGE);
                  }
              }).show();
  }
}