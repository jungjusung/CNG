package com.sai.user.gnc.settings;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sai.user.gnc.MyAsyncTask;
import com.sai.user.gnc.R;
import com.sai.user.gnc.StartActivity;

import java.util.ArrayList;

/*
 WebList!!
 */

public class WebListAdapter extends BaseAdapter{
    Context context;
    LayoutInflater inflater;
    public WebListActivity webListActivity;
    MyAsyncTask myAsyncTask;
    public ArrayList<String> list = new ArrayList<String>();
    TextView txt_url;
    ImageView urlImage;
    TextView urlTitle;
    TextView urlContent;
    String TAG;
    public ArrayList<MemoryCache> caches=new ArrayList<>();

    Cache cache= Cache.getInstance();
    public WebListAdapter(Context context,WebListActivity webListActivity) {
        TAG=this.getClass().getName();
        this.context = context;
        this.webListActivity=webListActivity;
        /*인플레이터 생성*/
        if(inflater==null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        init();

    }

    public void init(){
        String sql="select * from web";
        Cursor rs= StartActivity.db.rawQuery(sql,null);
        Log.d(TAG,"이닛?");
        list.clear();
        caches.clear();
        while(rs.moveToNext()){
            Log.d(TAG,"이닛?");
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
        return i;
    }

    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = null;
        Log.d(TAG,"겟뷰");
        if (convertView == null) {
            webListActivity.setLoadingView(true);
            view = inflater.inflate(R.layout.web_item, null);
            view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,400));
        }else{
            view=convertView;
        }
        txt_url = (TextView) view.findViewById(R.id.txt_url);
        urlImage=(ImageView)view.findViewById(R.id.UrlImage);
        urlTitle=(TextView)view.findViewById(R.id.UrlTitle);
        urlContent=(TextView)view.findViewById(R.id.UrlText);
        txt_url.setText(list.get(i));

        Bitmap bitmap = (Bitmap) Cache.getInstance().getLru().get(txt_url.getText().toString());
        String title= Cache.getInstance().getTitle().get(txt_url.getText().toString());
        String content= Cache.getInstance().getContent().get(txt_url.getText().toString());
        if(bitmap!=null&&title!=null&&content!=null){
            urlImage.setImageBitmap(bitmap);
            urlTitle.setText(title);
            urlContent.setText(content);
            webListActivity.setLoadingView(false);
        }else{
            myAsyncTask = new MyAsyncTask(view, this,i);
            myAsyncTask.execute(txt_url.getText().toString());
        }



        return view;
    }

}
