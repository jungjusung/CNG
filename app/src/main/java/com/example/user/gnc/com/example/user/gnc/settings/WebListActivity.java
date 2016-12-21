package com.example.user.gnc.com.example.user.gnc.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.gnc.R;
import com.example.user.gnc.RecycleUtils;
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
    Cursor rs;
    String TAG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getName();
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
        edit_url.setSelection(edit_url.length());

    }

    public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        txt_url = (TextView) view.findViewById(R.id.txt_url);
        final String url = txt_url.getText().toString();

        alert_confirm.setTitle(url).setMessage("이 주소를").setCancelable(true)
                .setPositiveButton("제스쳐와 연동",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES'
                                String sql = "update shortcut set name = ?, path=?, method=3 where short_cut=?";
                                defaultAct.db.execSQL(sql, new String[]{
                                        url, url, Integer.toString(short_id)
                                });
                                Intent intent = new Intent(WebListActivity.this, KeySettingActivity.class);
                                startActivity(intent);
                                WebListActivity.this.finish();
                            }
                        })
                .setNegativeButton("목록에서 삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sql = "delete from web where url = ?";
                        defaultAct.db.execSQL(sql, new String[]{url});
                        webListAdapter.init();
                        webListAdapter.notifyDataSetChanged();
                        Toast.makeText(WebListActivity.this, url + "이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
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
            rs = defaultAct.db.rawQuery(sql, null);

            if (rs.getCount() != 0) {
                Toast.makeText(this, "입력하신 " + url + "이 중복됩니다.", Toast.LENGTH_SHORT).show();
            } else {
                if (Patterns.WEB_URL.matcher(url).matches()) {
                    //유효한 url;
                    sql = "insert into web(url) values(?)";
                    defaultAct.db.execSQL(sql, new String[]{
                            url
                    });
                    webListAdapter.init();
                    webListAdapter.notifyDataSetChanged();
                    Toast.makeText(this, url + "이 추가되었습니다.", Toast.LENGTH_SHORT).show();
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

}
