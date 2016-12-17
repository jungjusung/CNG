package com.example.user.gnc.com.example.user.gnc.settings;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.gnc.R;
import com.example.user.gnc.defaultAct;

import java.util.ArrayList;

/*
 WebList!!
 */

public class WebListAdapter extends BaseAdapter{
    Context context;
    LayoutInflater inflater;

    ArrayList<String> list = new ArrayList<String>();
    TextView txt_url;

    public WebListAdapter(Context context) {
        this.context = context;
        /*인플레이터 생성*/
        if(inflater==null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        init();

    }

    public void init(){
        String sql="select * from web";
        Cursor rs=defaultAct.db.rawQuery(sql,null);

        list.clear();
        while(rs.moveToNext()){
            list.add(rs.getString(rs.getColumnIndex("url")));
        }
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int i) {
        return list.get(i);
    }

    public long getItemId(int i) {
        return 0;
    }

    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = null;

        if (convertView == null) {
            view = inflater.inflate(R.layout.web_item, null);
        } else {
            view = convertView;
        }
        txt_url = (TextView) view.findViewById(R.id.txt_url);
        txt_url.setText(list.get(i));

        return view;
    }

}
