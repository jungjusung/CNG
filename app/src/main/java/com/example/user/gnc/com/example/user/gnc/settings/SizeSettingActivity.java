package com.example.user.gnc.com.example.user.gnc.settings;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.gnc.HeroIcon;
import com.example.user.gnc.R;

import com.example.user.gnc.RecycleUtils;
import com.example.user.gnc.StartActivity;
import com.example.user.gnc.defaultAct;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class SizeSettingActivity extends Activity implements View.OnClickListener{

    SeekBar sizeBar;
    ImageView imageView;
    Button btn_size;
    public static WindowManager.LayoutParams iconParam;
    ViewGroup.LayoutParams params;
    WindowManager wm;
    HeroIcon heroIcon;
    String sql;
    Cursor rs;
    int init;
    int step = 1;
    int min = 80;
    int max = 220;
    String TAG;
    int width;
    int height;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getName();
        setContentView(R.layout.size_setting_activity);

     /*=======================================
        광고니까 지우지마ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ
         ========================================
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ========================================*/


        btn_size=(Button)findViewById(R.id.btn_size);
        sql = "select * from img_info";
        rs = defaultAct.db.rawQuery(sql, null);
        rs.moveToNext();
        init = rs.getInt(rs.getColumnIndex("size"));

        Log.d(TAG,"초기사이즈 : "+init);
        iconParam = StartActivity.params2;
        heroIcon = StartActivity.heroIcon;
        wm = StartActivity.windowManager;
        sizeBar = (SeekBar) findViewById(R.id.sizeBar);
        imageView = (ImageView) findViewById(R.id.change_image);
        sizeBar.setMax((max - min) / step);
        sizeBar.setProgress(init-min);
        params=imageView.getLayoutParams();
        params.width=init;
        params.height=init;
        btn_size.setOnClickListener(this);
        imageView.setLayoutParams(params);




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

                width = seekBar.getProgress() + min;
                height = seekBar.getProgress() + min;

            }
        });
    }



    protected void onDestroy() {
        Log.d(TAG, "내가 꺼졌따~");
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        iconParam.width=width;
        iconParam.height=height;
        sql = "update img_info set size=?";
        defaultAct.db.execSQL(sql, new String[]{
                Integer.toString(iconParam.width)
        });
        StartActivity.params2.width = iconParam.width;
        StartActivity.params2.height = iconParam.height;
        StartActivity.icon_height = iconParam.height;
        StartActivity.icon_width = iconParam.width;
        StartActivity.main_parameters1.width = iconParam.width * 2;
        StartActivity.main_parameters1.height = iconParam.height;
        StartActivity.main_parameters2.width = iconParam.width * 2;
        StartActivity.main_parameters2.height = iconParam.height;
        StartActivity.sub_parameters1.width = iconParam.width;
        StartActivity.sub_parameters1.height = iconParam.height;
        StartActivity.sub_parameters2.width = iconParam.width;
        StartActivity.sub_parameters2.height = iconParam.height;
        StartActivity.txt_turn_parameters.width = iconParam.width;
        StartActivity.txt_turn_parameters.height = iconParam.height;
        StartActivity.txt_turn.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        StartActivity.txt_setting_parameters.width = iconParam.width;
        StartActivity.txt_setting_parameters.height = iconParam.height;
        StartActivity.txt_setting.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        StartActivity.sub_li1.setLayoutParams(StartActivity.sub_parameters1);
        StartActivity.sub_li2.setLayoutParams(StartActivity.sub_parameters2);
        StartActivity.txt_setting.setLayoutParams(StartActivity.txt_setting_parameters);
        StartActivity.txt_turn.setLayoutParams(StartActivity.txt_turn_parameters);

        StartActivity.main_li1.setLayoutParams(StartActivity.main_parameters1);
        StartActivity.main_li2.setLayoutParams(StartActivity.main_parameters2);
        wm.updateViewLayout(heroIcon, iconParam);
    }
}

