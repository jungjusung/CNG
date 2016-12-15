package com.example.user.gnc.com.example.user.gnc.settings;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.gnc.HeroIcon;
import com.example.user.gnc.R;

import com.example.user.gnc.StartActivity;
import com.example.user.gnc.defaultAct;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class SizeSettingActivity extends Activity {

    SeekBar sizeBar;
    ImageView imageView;
    WindowManager.LayoutParams iconParam;
    ViewGroup.LayoutParams params;
    WindowManager wm;
    HeroIcon heroIcon;
    String sql;
    Cursor rs;
    int init;
    int step = 1;
    int min = 80;
    int max = 220;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.size_setting_activity);
     /*   AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        sql = "select * from img_info";
        rs = defaultAct.db.rawQuery(sql, null);
        rs.moveToNext();
        int size = rs.getInt(rs.getColumnIndex("size"));
        init=size;
        iconParam = StartActivity.params2;
        heroIcon = StartActivity.heroIcon;

        wm = StartActivity.windowManager;
        sizeBar = (SeekBar) findViewById(R.id.sizeBar);
        imageView = (ImageView) findViewById(R.id.change_image);
        params=imageView.getLayoutParams();
        params.width=init;
        params.height=init;
        imageView.setLayoutParams(params);
        sizeBar.setProgress(init);
        sizeBar.setMax((max - min) / step);

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < 0)
                    seekBar.setProgress(0);
                if (i > max)
                    seekBar.setProgress(max);

                params.width=i+min;
                params.height=i+min;
                imageView.setLayoutParams(params);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(SizeSettingActivity.this, "onStartTrackingTouch", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                iconParam.width = seekBar.getProgress() + min;
                iconParam.height = seekBar.getProgress() + min;

            }
        });
    }

    public void btnSize(View view) {

        sql = "update img_info set size=?";
        defaultAct.db.execSQL(sql, new String[]{
                Integer.toString(iconParam.width)
        });
        StartActivity.params2.width=iconParam.width;
        StartActivity.params2.height=iconParam.height;
        StartActivity.icon_height=iconParam.height;
        StartActivity.icon_width=iconParam.width;
        StartActivity.main_parameters1.width=iconParam.width*2;
        StartActivity.main_parameters1.height=iconParam.height;
        StartActivity.main_parameters2.width=iconParam.width*2;
        StartActivity.main_parameters2.height=iconParam.height;
        StartActivity.sub_parameters1.width=iconParam.width;
        StartActivity.sub_parameters1.height=iconParam.height;
        StartActivity.sub_parameters2.width=iconParam.width;
        StartActivity.sub_parameters2.height=iconParam.height;
        StartActivity.txt_turn_parameters.width=iconParam.width;
        StartActivity.txt_setting_parameters.width=iconParam.width;


        StartActivity.sub_li1.setLayoutParams(StartActivity.sub_parameters1);
        StartActivity.sub_li2.setLayoutParams(StartActivity.sub_parameters2);
        StartActivity.txt_setting.setLayoutParams(StartActivity.txt_setting_parameters);
        StartActivity.txt_turn.setLayoutParams(StartActivity.txt_turn_parameters);

        StartActivity.main_li1.setLayoutParams(StartActivity.main_parameters1);
        StartActivity.main_li2.setLayoutParams(StartActivity.main_parameters2);
        wm.updateViewLayout(heroIcon, iconParam);
    }


}

