package com.example.user.gnc.com.example.user.gnc.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.gnc.R;
import com.example.user.gnc.defaultAct;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class KeySettingActivity extends Activity {
    String number;
    String TAG;
    String name, phoneNumber;

    /*이미지 변수 선언 중 @@*/
    String call_img;

    int DOUBLE_CLICK = 1;
    int TOP_CLICK = 2;
    int BOTTOM_CLICK = 3;
    int LEFT_CLICK = 4;
    int RIGHT_CLICK = 5;

    int confirmNum = -1;
    private static final int REQUEST_SELECT_PHONE_NUMBER = 1;

    private static final int START_PHONE_CALL = 1;
    private static final int START_APP_CALL = 2;
    private static final int START_WEB_CALL = 3;
    Animation FabRotateClockWise,FabRotateAntiClockWise,FadeOut,FadeIn;

    TextView txt_doubleClick, txt_right, txt_left, txt_bottom, txt_top;
    ImageView img_doubleClick, img_top, img_bottom, img_right, img_left;
    ImageView addKey1, addKey2, addKey3, addKey4, addKey5;
    List<ImageView> viewList;
    List<ImageView> imgList;
    List<TextView> txtList;
    boolean[] viewChk;


    /*------------------------------------------------------------
       정리 하기 위한 곳
    * -------------------------------------------------------------*/
    String sql,path;
    Cursor rs;



    /*---------------------------------------------------------------*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_setting_activity);
        TAG = this.getClass().getName();
        viewList = new ArrayList<>();
        imgList = new ArrayList<>();
        txtList = new ArrayList<>();
        addKey1 = (ImageView) findViewById(R.id.addKey1);
        addKey2 = (ImageView) findViewById(R.id.addKey2);
        addKey3 = (ImageView) findViewById(R.id.addKey3);
        addKey4 = (ImageView) findViewById(R.id.addKey4);
        addKey5 = (ImageView) findViewById(R.id.addKey5);

        viewList.add(addKey1);
        viewList.add(addKey2);
        viewList.add(addKey3);
        viewList.add(addKey4);
        viewList.add(addKey5);

        FadeIn=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
        FadeOut=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeout);
        FabRotateClockWise= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        FabRotateAntiClockWise= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
        viewChk = new boolean[viewList.size() + 1];

        /*-----------------------------------------------------------
        아이콘 변화
        * ----------------------------------------------------------*/

        for (int i = 0; i < viewList.size(); i++) {
            sql = "select * from shortcut where short_cut=" + (i + 1);
            rs = defaultAct.db.rawQuery(sql, null);
            Log.d(TAG,"이거 호출 되는지??");
            rs.moveToNext();

            path = rs.getString(rs.getColumnIndex("path"));
            if (path != null) {
                if (i == DOUBLE_CLICK - 1) {
                    viewChk[i+1] = true;
                    viewList.get(i).setImageResource(R.drawable.chk_ori);
                }
                if (i == TOP_CLICK - 1) {
                    viewChk[i+1] = true;
                    viewList.get(i).setImageResource(R.drawable.chk_ori);
                }
                if (i == BOTTOM_CLICK - 1) {
                    viewChk[i+1] = true;
                    viewList.get(i).setImageResource(R.drawable.chk_ori);
                }
                if (i == LEFT_CLICK - 1) {
                    viewChk[i+1] = true;
                    viewList.get(i).setImageResource(R.drawable.chk_ori);
                }
                if (i == RIGHT_CLICK - 1) {
                    viewChk[i+1] = true;
                    viewList.get(i).setImageResource(R.drawable.chk_ori);
                }
            } else {
                viewChk[i+1] = false;
                viewList.get(i).setImageResource(R.drawable.chk_sel);
            }

        }

        if (checkFlag() == 0) {
            Intent intent = new Intent(this, ManualKeySettingActivity.class);
            startActivity(intent);
        }


        txt_doubleClick = (TextView) findViewById(R.id.txt_doubleClick);
        txt_right = (TextView) findViewById(R.id.txt_right);
        txt_left = (TextView) findViewById(R.id.txt_left);
        txt_top = (TextView) findViewById(R.id.txt_top);
        txt_bottom = (TextView) findViewById(R.id.txt_bottom);
        txtList.add(txt_doubleClick);
        txtList.add(txt_top);
        txtList.add(txt_bottom);
        txtList.add(txt_left);
        txtList.add(txt_right);
        img_doubleClick = (ImageView) findViewById(R.id.img_doubleClick);
        img_top = (ImageView) findViewById(R.id.img_top);
        img_bottom = (ImageView) findViewById(R.id.img_bottom);
        img_right = (ImageView) findViewById(R.id.img_right);
        img_left = (ImageView) findViewById(R.id.img_left);
        imgList.add(img_doubleClick);
        imgList.add(img_top);
        imgList.add(img_bottom);
        imgList.add(img_left);
        imgList.add(img_right);


        for (int i = 1; i <= 5; i++) {

            sql = "select * from shortcut where short_cut=" + i;
            rs = defaultAct.db.rawQuery(sql, null);
            rs.moveToNext();

            int method = rs.getInt(rs.getColumnIndex("method"));
            if (method != START_PHONE_CALL && method != START_APP_CALL && method != START_WEB_CALL) {
                if (i == 1) {
                    viewChk[1] = false;
                    viewList.get(i-1).setImageResource(R.drawable.chk_sel);
                    img_doubleClick.setImageResource(R.drawable.logo2);
                }
                if (i == 2) {
                    viewChk[2] = false;
                    viewList.get(i-1).setImageResource(R.drawable.chk_sel);
                    img_top.setImageResource(R.drawable.logo2);
                }
                if (i == 3) {
                    viewChk[3] = false;
                    viewList.get(i-1).setImageResource(R.drawable.chk_sel);
                    img_bottom.setImageResource(R.drawable.logo2);
                }
                if (i == 4) {
                    viewChk[4] = false;
                    viewList.get(i-1).setImageResource(R.drawable.chk_sel);
                    img_left.setImageResource(R.drawable.logo2);
                }
                if (i == 5) {
                    viewChk[5] = false;
                    viewList.get(i-1).setImageResource(R.drawable.chk_sel);
                    img_right.setImageResource(R.drawable.logo2);
                }
            } else if (method == START_PHONE_CALL) {
                if (i == 1) {
                    viewChk[1] = true;
                    viewList.get(i-1).setImageResource(R.drawable.chk_ori);
                    img_doubleClick.setImageResource(R.drawable.phone);
                }
                if (i == 2) {
                    viewChk[2] = true;
                    viewList.get(i-1).setImageResource(R.drawable.chk_ori);
                    img_top.setImageResource(R.drawable.phone);
                }
                if (i == 3) {
                    viewChk[3] = true;
                    viewList.get(i-1).setImageResource(R.drawable.chk_ori);
                    img_bottom.setImageResource(R.drawable.phone);
                }
                if (i == 4) {
                    viewChk[4] = true;
                    viewList.get(i-1).setImageResource(R.drawable.chk_ori);
                    img_left.setImageResource(R.drawable.phone);
                }
                if (i == 5) {
                    viewChk[5] = true;
                    viewList.get(i-1).setImageResource(R.drawable.chk_ori);
                    img_right.setImageResource(R.drawable.phone);
                }
            } else if (method == START_WEB_CALL) {
                if (i == 1) {
                    viewChk[1] = true;
                    viewList.get(i-1).setImageResource(R.drawable.chk_ori);
                    img_doubleClick.setImageResource(R.drawable.internet);
                }
                if (i == 2) {
                    viewChk[2] = true;
                    viewList.get(i-1).setImageResource(R.drawable.chk_ori);
                    img_top.setImageResource(R.drawable.internet);
                }
                if (i == 3) {
                    viewChk[3] = true;
                    viewList.get(i-1).setImageResource(R.drawable.chk_ori);
                    img_bottom.setImageResource(R.drawable.internet);
                }
                if (i == 4) {
                    viewChk[4] = true;
                    viewList.get(i-1).setImageResource(R.drawable.chk_ori);
                    img_left.setImageResource(R.drawable.internet);
                }
                if (i == 5) {
                    viewChk[5] = true;
                    viewList.get(i-1).setImageResource(R.drawable.chk_ori);
                    img_right.setImageResource(R.drawable.internet);
                }
            }
        }

        addKey1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewChk[DOUBLE_CLICK])
                    showSelectedDialog(DOUBLE_CLICK);
                else
                    deleteKeySetting(DOUBLE_CLICK);

            }
        });
        addKey2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewChk[TOP_CLICK])
                    showSelectedDialog(TOP_CLICK);
                else {
                    deleteKeySetting(TOP_CLICK);
                }
            }
        });
        addKey3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewChk[BOTTOM_CLICK])
                    showSelectedDialog(BOTTOM_CLICK);
                else
                    deleteKeySetting(BOTTOM_CLICK);

            }
        });
        addKey4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewChk[LEFT_CLICK])
                    showSelectedDialog(LEFT_CLICK);
                else
                    deleteKeySetting(LEFT_CLICK);
            }
        });
        addKey5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewChk[RIGHT_CLICK])
                    showSelectedDialog(RIGHT_CLICK);
                else
                    deleteKeySetting(RIGHT_CLICK);
            }
        });
    }

    public void showSelectedDialog(final int short_id) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                KeySettingActivity.this);
        alertBuilder.setIcon(R.drawable.logo);
        alertBuilder.setTitle("항목중에 하나를 선택하세요.");

        // List Adapter 생성
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                KeySettingActivity.this,
                android.R.layout.select_dialog_singlechoice);
        adapter.add("전화 걸기");
        adapter.add("앱 실행");
        adapter.add("웹 실행");


        // 버튼 생성
        alertBuilder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });

        // Adapter 셋팅
        alertBuilder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {

                        String strName = adapter.getItem(id);
                        if (strName.equals("전화 걸기")) {
                            selectContact(short_id);
                        } else if (strName.equals("앱 실행")) {
                            selectApp(short_id);
                        } else if (strName.equals("웹 실행")) {
                            selectWeb(short_id);
                        }
                    }
                });
        alertBuilder.show();
    }

    /*전화번호부 가져오기*/
    public void selectContact(int short_id) {
        // Start an activity for the user to pick a phone number from contacts
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
            confirmNum = short_id;
        }
    }

    public void selectApp(int short_cut) {
        Intent intent = new Intent(this, AppListActivity.class);
        intent.putExtra("short_cut", Integer.toString(short_cut));
        startActivity(intent);
    }

    /*@webListView 호출@*/
    public void selectWeb(int short_id) {
        Intent intent = new Intent(this, WebListActivity.class);
        intent.putExtra("short_id", Integer.toString(short_id));
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume호출");

        for (int i = 1; i <= 5; i++) {
            sql = "select * from shortcut where short_cut=" + i;
            rs = defaultAct.db.rawQuery(sql, null);
            rs.moveToNext();
            String name = rs.getString(rs.getColumnIndex("name"));
            String icon_img = rs.getString(rs.getColumnIndex("path"));
            int method = rs.getInt(rs.getColumnIndex("method"));

            if (name != null && i == 1) {
                txt_doubleClick.setText(name);
                try {
                    viewChk[1] = true;
                    addKey1.setImageResource(R.drawable.chk_ori);
                    if(method == 3){
                        img_doubleClick.setImageResource(R.drawable.internet);
                    }else {
                        img_doubleClick.setImageDrawable(getPackageManager().getApplicationIcon(icon_img));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (name != null && i == 2) {
                txt_top.setText(name);
                try {
                    viewChk[2] = true;
                    addKey2.setImageResource(R.drawable.chk_ori);
                    if(method == 3){
                        img_top.setImageResource(R.drawable.internet);
                    }else{
                        img_top.setImageDrawable(getPackageManager().getApplicationIcon(icon_img));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (name != null && i == 3) {
                txt_bottom.setText(name);
                try {
                    viewChk[3] = true;
                    addKey3.setImageResource(R.drawable.chk_ori);
                    if(method == 3){
                        img_bottom.setImageResource(R.drawable.internet);
                    }else{
                        img_bottom.setImageDrawable(getPackageManager().getApplicationIcon(icon_img));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (name != null && i == 4) {
                txt_left.setText(name);
                try {
                    viewChk[4] = true;
                    addKey4.setImageResource(R.drawable.chk_ori);
                    if(method == 3){
                        img_left.setImageResource(R.drawable.internet);
                    }else{
                        img_left.setImageDrawable(getPackageManager().getApplicationIcon(icon_img));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (name != null && i == 5) {
                txt_right.setText(name);
                try {
                    viewChk[5] = true;
                    addKey5.setImageResource(R.drawable.chk_ori);
                    if(method == 3){
                        img_right.setImageResource(R.drawable.internet);
                    }else{
                        img_right.setImageDrawable(getPackageManager().getApplicationIcon(icon_img));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

                number = cursor.getString(numberIndex);
                name = cursor.getString(nameIndex);

                // Do something with the phone number
                if (confirmNum == 1) {
                    viewChk[1] = true;
                    txt_doubleClick.setText(number);
                    addKey1.setImageResource(R.drawable.chk_ori);
                    img_doubleClick.setImageResource(R.drawable.phone);
                } else if (confirmNum == 2) {
                    viewChk[2] = true;
                    txt_top.setText(number);
                    addKey2.setImageResource(R.drawable.chk_ori);
                    img_top.setImageResource(R.drawable.phone);
                } else if (confirmNum == 3) {
                    viewChk[3] = true;
                    txt_bottom.setText(number);
                    addKey3.setImageResource(R.drawable.chk_ori);
                    img_bottom.setImageResource(R.drawable.phone);
                } else if (confirmNum == 4) {
                    viewChk[4] = true;
                    txt_left.setText(number);
                    addKey4.setImageResource(R.drawable.chk_ori);
                    img_left.setImageResource(R.drawable.phone);
                } else if (confirmNum == 5) {
                    viewChk[5] = true;
                    txt_right.setText(number);
                    addKey5.setImageResource(R.drawable.chk_ori);
                    img_right.setImageResource(R.drawable.phone);
                }

                sql = "update shortcut set name=?, path=?, method=? where short_cut=?";

                defaultAct.db.execSQL(sql, new String[]{
                        name + "에게 전화걸기", number, Integer.toString(START_PHONE_CALL), Integer.toString(confirmNum)
                });

                confirmNum = -1;
            }
        }
    }

    public int checkFlag() {
        sql = "select key_setting from manual_flags";
        rs = defaultAct.db.rawQuery(sql, null);
        rs.moveToNext();
        return rs.getInt(0);
    }

    public void deleteKeySetting(int index) {
        sql = "update shortcut set name=NULL, path=NULL, method=NULL where short_cut=?";

        defaultAct.db.execSQL(sql, new String[]{
                Integer.toString(index)
        });
        viewChk[index]=false;
        for(int i=1;i<=5;i++) {
            if(i==index)
                continue;
            imgList.get(i-1).clearAnimation();
        }
        imgList.get(index-1).startAnimation(FadeOut);
        imgList.get(index-1).setImageResource(R.drawable.logo2);
        imgList.get(index-1).startAnimation(FadeIn);
        txtList.get(index-1).setText("");
        viewList.get(index-1).startAnimation(FabRotateAntiClockWise);
        viewList.get(index-1).setImageResource(R.drawable.chk_sel);
    }
}
