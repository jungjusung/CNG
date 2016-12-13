package com.example.user.gnc;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.gnc.com.example.user.gnc.settings.KeySettingActivity;
import com.example.user.gnc.com.example.user.gnc.settings.LocationSettingActivity;
import com.example.user.gnc.com.example.user.gnc.settings.SizeSettingActivity;

import java.io.FileNotFoundException;
import java.io.IOException;


public class SettingActivity extends Activity {
    String TAG;
    String data;

    static String name;
    String imgName;
    Bitmap bitmap;
    HeroIcon heroIcon;
    LinearLayout bt_icon, bt_key, bt_size, bt_location;
    static final int REQ_CODE_SELECT_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        bt_key = (LinearLayout) findViewById(R.id.bt_key);
        bt_icon = (LinearLayout) findViewById(R.id.bt_icon);
        bt_size = (LinearLayout) findViewById(R.id.bt_size);
        bt_location = (LinearLayout) findViewById(R.id.bt_location);

        TAG = this.getClass().getName();
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.bt_key:
                Toast.makeText(this, "키 변경하기", Toast.LENGTH_SHORT).show();
                Intent key_intent = new Intent(this, KeySettingActivity.class);
                startActivity(key_intent);
                break;
            case R.id.bt_icon:
                Toast.makeText(this, "아이콘 변경하기", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "아이콘변경하기1");
                Intent icon_intent = new Intent(Intent.ACTION_PICK);
                Log.d(TAG, "아이콘변경하기2");
                icon_intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                Log.d(TAG, "아이콘변경하기3");
                icon_intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Log.d(TAG, "아이콘변경하기4");
                startActivityForResult(icon_intent, REQ_CODE_SELECT_IMAGE);
                break;
            case R.id.bt_location:
                Toast.makeText(this, "위치 변경하기", Toast.LENGTH_SHORT).show();
                Intent location_intent = new Intent(this, LocationSettingActivity.class);
                startActivity(location_intent);
                break;
            case R.id.bt_size:
                Toast.makeText(this, "크기 변경하기", Toast.LENGTH_SHORT).show();
                Intent size_intent = new Intent(this, SizeSettingActivity.class);
                startActivity(size_intent);
                break;
            case R.id.img_icon: //설정창에 이미지 아이콘
                Toast.makeText(this, "아이콘 이미지 변경하기", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(SettingActivity.this, StartActivity.class);
                serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                serviceIntent.putExtra("data", name);
                Log.d(TAG, "name값" + name);
                startService(serviceIntent);
                finish();
                break;
        }
    }

    /* 세팅에 아이콘 이미지 변경 및 경로*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, requestCode + "리퀘스트코드" + resultCode + "resultcode");
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView) findViewById(R.id.img_icon);
                    Log.d(TAG, "비트맵 " + image_bitmap);

                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);
                    Uri uri = data.getData();
                    Log.d(TAG, "uri" + uri);


                    String sql = "update img_info set path=?";
                    defaultAct.db.execSQL(sql, new String[]{
                             uri.toString()
                    });

                    Bitmap change_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    Log.d(TAG, StartActivity.startActivity.heroIcon+"스타트액티비티");
                    StartActivity.startActivity.heroIcon.setImageBitmap(change_bitmap);


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

/*    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        return imgPath;
    }*/
}