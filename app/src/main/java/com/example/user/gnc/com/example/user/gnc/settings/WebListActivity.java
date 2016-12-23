package com.example.user.gnc.com.example.user.gnc.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.gnc.R;
import com.example.user.gnc.RecycleUtils;
import com.example.user.gnc.StartActivity;
import com.example.user.gnc.defaultAct;

import java.util.List;


public class WebListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    TextView txt_url;
    EditText textAddress;
    int short_id;

    /*WebList 변수*/
    View webLoadingContainer;
    LinearLayout webCompleteContainer;
    WebListAdapter webListAdapter;
    ListView webListView;
    EditText edit_url;

    ImageView add_url;
    Cursor rs;
    String TAG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getName();
        Intent intent = getIntent();
        short_id = Integer.parseInt(intent.getStringExtra("short_id"));
        Log.d(TAG,"여기오냐고?1");
        setContentView(R.layout.web_list_layout);
        webListView=(ListView)findViewById(R.id.webListView);
        webListAdapter=new WebListAdapter(this,this);
        webListView.setAdapter(webListAdapter);
        webListView.setOnItemClickListener(this);
        Log.d(TAG,"여기오냐고?");
        add_url = (ImageView) findViewById(R.id.add_url);
        add_url.setOnClickListener(this);


        edit_url = (EditText) findViewById(R.id.edit_url);
        edit_url.setSelection(edit_url.length());

        webLoadingContainer=findViewById(R.id.web_loading_container);
        /*webListView 생성*/
        setLoadingView(false);
    }

    public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        txt_url = (TextView) view.findViewById(R.id.txt_url);
        final String url = txt_url.getText().toString();

        alert_confirm.setTitle(url).setMessage(R.string.this_address).setCancelable(true)
                .setPositiveButton(R.string.add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES'
                                String sql = "update shortcut set name = ?, path=?, method=3 where short_cut=?";
                                StartActivity.db.execSQL(sql, new String[]{
                                        url, url, Integer.toString(short_id)
                                });
                                Intent intent = new Intent(WebListActivity.this, KeySettingActivity.class);
                                startActivity(intent);
                                WebListActivity.this.finish();
                            }
                        })
                .setNegativeButton(R.string.deleted_from_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sql = "delete from web where url = ?";
                        StartActivity.db.execSQL(sql, new String[]{url});
                        webListAdapter.init();
                        webListAdapter.notifyDataSetChanged();
                        Toast.makeText(WebListActivity.this, url +" "+ getString(R.string.was_deleted), Toast.LENGTH_SHORT).show();
                        edit_url.setText("http://");
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    public void onClick(View view) {
        String sql = null;
        if (view.getId() == R.id.add_url) {
            String url = edit_url.getText().toString();
            sql = "select * from web where url=\"" + url + "\"";
            rs = StartActivity.db.rawQuery(sql, null);

            if (rs.getCount() != 0) {
                Toast.makeText(this, "입력하신 " + url + "이 중복됩니다.", Toast.LENGTH_SHORT).show();
            } else {
                if (Patterns.WEB_URL.matcher(url).matches()) {
                    //유효한 url;
                    sql = "insert into web(url) values(?)";
                    StartActivity.db.execSQL(sql, new String[]{
                            url
                    });
                    webListAdapter.init();
                    webListAdapter.notifyDataSetChanged();
                    Toast.makeText(this, url + getString(R.string.was_added), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Url 주소가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                edit_url.setText("http://");
                edit_url.setSelection(edit_url.length());


            }
            if (rs != null)
                rs.close();
        }
    }

    protected void onDestroy() {
        Log.d(TAG, "내가 꺼졌따~");
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }
    public void setLoadingView(boolean isView) {
        if (isView) {
            // 화면 로딩뷰 표시
            webLoadingContainer.setVisibility(View.VISIBLE);
            webListView.setVisibility(View.GONE);
            add_url.setVisibility(View.GONE);
            edit_url.setVisibility(View.GONE);
        } else {
            // 화면 어플 리스트 표시
            webListView.setVisibility(View.VISIBLE);
            add_url.setVisibility(View.VISIBLE);
            edit_url.setVisibility(View.VISIBLE);
            webLoadingContainer.setVisibility(View.GONE);
        }
    }

}
