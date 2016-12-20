package com.example.user.gnc.com.example.user.gnc.settings;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.gnc.MyAsyncTask;
import com.example.user.gnc.R;
import com.example.user.gnc.defaultAct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.combineMeasuredStates;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/*
 WebList!!
 */

public class WebListAdapter extends BaseAdapter{
    Context context;
    LayoutInflater inflater;

    ArrayList<String> list = new ArrayList<String>();
    TextView txt_url;
    ImageView urlImage;
    TextView urlTitle;
    TextView urlContent;

    public ArrayList<MemoryCache> caches=new ArrayList<>();

    Cache cache=Cache.getInstance();
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
        caches.clear();
        while(rs.moveToNext()){
            list.add(rs.getString(rs.getColumnIndex("url")));
        }
        //myAsyncTaskList=new MyAsyncTask[list.size()];
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
        if (convertView == null) {

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

        Bitmap bitmap = (Bitmap)Cache.getInstance().getLru().get(txt_url.getText().toString());
        String title=Cache.getInstance().getTitle().get(txt_url.getText().toString());
        String content=Cache.getInstance().getTitle().get(txt_url.getText().toString());
        if(bitmap!=null&&title!=null&&content!=null){
            urlImage.setImageBitmap(bitmap);
            urlTitle.setText(title);
            urlContent.setText(content);
        }else{
            MyAsyncTask myAsyncTask = new MyAsyncTask(view, this);
            myAsyncTask.execute(txt_url.getText().toString());
        }



        return view;
    }
    public void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable)d).getBitmap();
            b.recycle();
        } // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

        d.setCallback(null);
    }

}
