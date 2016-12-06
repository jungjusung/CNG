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

/**
 * Created by user on 2016-11-30.
 */

public class SettingActivity extends Activity {
    String TAG;
    String data;

    static String name;
    String imgName;
    Bitmap bitmap;
    HeroIcon heroIcon;
    LinearLayout bt_icon,bt_key,bt_size,bt_location;
    static final int REQ_CODE_SELECT_IMAGE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        bt_key=(LinearLayout)findViewById(R.id.bt_key);
        bt_icon=(LinearLayout)findViewById(R.id.bt_icon);
        bt_size=(LinearLayout)findViewById(R.id.bt_size);
        bt_location=(LinearLayout)findViewById(R.id.bt_location);



    }
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.bt_key:
                Toast.makeText(this, "키 변경하기", Toast.LENGTH_SHORT).show();
                Intent key_intent = new Intent(this, KeySettingActivity.class);
                startActivity(key_intent);
                break;
            case R.id.bt_icon:
                Toast.makeText(this, "아이콘 변경하기", Toast.LENGTH_SHORT).show();
                Intent icon_intent = new Intent(Intent.ACTION_PICK);
                icon_intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                icon_intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                serviceIntent.putExtra("data", name);
                Log.d(TAG, "name값"+name);
                startService(serviceIntent);
                finish();
                break;
        }
    }
    /* 세팅에 아이콘 이미지 변경 및 경로*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView) findViewById(R.id.img_icon);
                    Log.d(TAG, "비트맵 "+image_bitmap);

                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);
                    Uri uri=data.getData();
                    Log.d(TAG, "uri"+uri);
                    name=getImageNameToUri(uri);
                    Log.d(TAG, "name값은 "+name);


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

    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        return imgPath;
    }
}