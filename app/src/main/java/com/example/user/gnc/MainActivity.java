package com.example.user.gnc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    static MainActivity mainActivity;
    String TAG;

    Button bt_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(this, StartActivity.class));
        super.onCreate(savedInstanceState);

        mainActivity=this;

        setContentView(R.layout.activity_main);

        bt_setting = (Button)this.findViewById(R.id.bt_setting);
        bt_setting.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Setting(v);
    }

    public void Setting(View view){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);

    }


    @Override
    protected void onDestroy() {
        Log.d(TAG,"내가 켜졌따~");
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        finish();
        super.onDestroy();
    }
}
