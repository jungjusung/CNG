package com.example.user.gnc.com.example.user.gnc.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.gnc.R;
import com.example.user.gnc.defaultAct;

/**
 * Created by wbhlkc0 on 2016-12-13.
 */

public class WebListActivity extends AppCompatActivity{
    TextView naver,google,youtube,daum;
    EditText textAddress;
    int short_id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_url_setting);
        Intent intent = getIntent();
        short_id = Integer.parseInt(intent.getStringExtra("short_id"));
         naver = (TextView)findViewById(R.id.naver);
         google = (TextView)findViewById(R.id.google);
         youtube = (TextView)findViewById(R.id.youtube);
         daum = (TextView)findViewById(R.id.daum);
        textAddress = (EditText)findViewById(R.id.textAddress);
    }

    public void btnClick(View view){
        String sql = "update shortcut set path=?,name=?,method=3 where short_cut=?";
        switch (view.getId()){
            case R.id.naver:
                defaultAct.db.execSQL(sql, new String[]{
                        naver.getText().toString(),naver.getText().toString(),Integer.toString(short_id)
                });
                break;
            case R.id.google:
                defaultAct.db.execSQL(sql, new String[]{
                        google.getText().toString(),google.getText().toString(),Integer.toString(short_id)
                });
                break;
            case R.id.youtube:
                defaultAct.db.execSQL(sql, new String[]{
                        youtube.getText().toString(),youtube.getText().toString(),Integer.toString(short_id)
                });
                break;
            case R.id.daum:
                defaultAct.db.execSQL(sql, new String[]{
                        daum.getText().toString(),daum.getText().toString(),Integer.toString(short_id)
                });
                break;
            case R.id.textAddressButton:
                defaultAct.db.execSQL(sql, new String[]{
                        textAddress.getText().toString(),textAddress.getText().toString(),Integer.toString(short_id)
                });
        }
        Intent intent = new Intent(this,KeySettingActivity.class);
        startActivity(intent);
    }


}
