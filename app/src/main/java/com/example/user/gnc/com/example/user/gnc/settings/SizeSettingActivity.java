package com.example.user.gnc.com.example.user.gnc.settings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.gnc.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SizeSettingActivity extends Activity {

    SeekBar sizeBar;
    ImageView imageView;
    TextView textView;
    String TAG;
    int step=1;
    int min=60;
    int max=180;
    private static final int INIT_SIZE=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.size_setting_activity);

  /*      AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        sizeBar = (SeekBar) findViewById(R.id.sizeBar);
        imageView = (ImageView) findViewById(R.id.change_image);

        sizeBar.setProgress(INIT_SIZE);
        sizeBar.setMax((max-min)/step);

        TAG = this.getClass().getName();
        Log.d(TAG, "액티비티생성");
        Log.d(TAG, sizeBar.toString());

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Toast.makeText(SizeSettingActivity.this, i + "의 값", Toast.LENGTH_SHORT).show();
                imageView.setScaleX((float)i/100);
                imageView.setScaleY((float)i/100);
                Log.d(TAG, "I 의 값 " + Integer.toString(i));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(SizeSettingActivity.this, "onStartTrackingTouch", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(SizeSettingActivity.this, seekBar.getProgress() + "의 값", Toast.LENGTH_SHORT).show();
                //Log.d(TAG, "I 의 값 " + Integer.toString(seekBar.getProgress()));
            }
        });
    }
    }
