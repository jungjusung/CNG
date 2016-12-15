package com.example.user.gnc.com.example.user.gnc.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/*
 WebList!!
 */

public class WebListAdapter extends BaseAdapter{
    Context context;
    LayoutInflater inflater;

    ArrayList<WebList> list = new ArrayList();

    public WebListAdapter(Context context) {
        this.context = context;
        /*인플레이터 생성*/
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public View getView(int i, View view, ViewGroup viewGroup) {

        return null;
    }
}
