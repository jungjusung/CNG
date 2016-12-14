package com.example.user.gnc.com.example.user.gnc.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.user.gnc.R;

/**
 * Created by user on 2016-12-13.
 */

public class ManualAppListActivity extends Activity implements CompoundButton.OnCheckedChangeListener{

    CheckBox check_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_applist_layout);
        check_view = (CheckBox) findViewById(R.id.applist_check_view);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        check_view.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == R.id.applist_check_view) {
            if (compoundButton.isChecked()) {
                finish();
            }
        }
    }
}
