package com.example.user.gnc.com.example.user.gnc.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.gnc.R;
import com.example.user.gnc.defaultAct;

import java.util.List;


public class WebListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    TextView txt_url;
    EditText textAddress;
    int short_id;

    /*WebList 변수*/
    WebListAdapter webListAdapter;
    ListView webListView;
    EditText edit_url;

    ImageView add_url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        short_id = Integer.parseInt(intent.getStringExtra("short_id"));

        setContentView(R.layout.web_list_layout);

        /*webListView 생성*/

        webListAdapter = new WebListAdapter(this);
        webListView = (ListView) findViewById(R.id.webListView);
        webListView.setAdapter(webListAdapter);
        webListView.setOnItemClickListener(this);

        add_url = (ImageView) findViewById(R.id.add_url);
        add_url.setOnClickListener(this);

        edit_url = (EditText) findViewById(R.id.edit_url);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        txt_url = (TextView) view.findViewById(R.id.txt_url);
        String url = txt_url.getText().toString();
        String TAG = this.getClass().getName();
        Log.d(TAG, "웹리스트" + url);
        String sql = "update shortcut set name = ?, path=?, method=3 where short_cut=?";
        defaultAct.db.execSQL(sql, new String[]{
                url, url, Integer.toString(short_id)
        });
        Intent intent = new Intent(this, KeySettingActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.add_url) {
            String url = edit_url.getText().toString();

            String sql = "insert into web(url) values(?)";
            defaultAct.db.execSQL(sql, new String[]{
                    url
            });
            webListAdapter.init();
            webListAdapter.notifyDataSetChanged();
            Toast.makeText(this, url+"이 추가되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
