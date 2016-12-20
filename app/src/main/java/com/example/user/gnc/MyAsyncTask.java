package com.example.user.gnc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.gnc.com.example.user.gnc.settings.Cache;
import com.example.user.gnc.com.example.user.gnc.settings.ItemDAO;
import com.example.user.gnc.com.example.user.gnc.settings.MemoryCache;
import com.example.user.gnc.com.example.user.gnc.settings.WebListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jusung on 2016. 12. 19..
 */

public class MyAsyncTask extends AsyncTask<String, Void, String[]> {


    OpenGraph openGraph;
    URL convertUrl;
    WebListAdapter webListAdapter;
    public static OpenGraphData data = null;
    Bitmap bitmap;
    String TAG;
    View view;



    ImageView urlImage;
    TextView urlText;
    TextView urlTitle;
    TextView url_text;
    BitmapFactory.Options ops;
    ItemDAO itemDAO;
    public MyAsyncTask(View view,WebListAdapter webListAdapter) {
        this.view=view;
        this.webListAdapter=webListAdapter;
        TAG = this.getClass().getName();
    }

    @Override

    protected void onPreExecute() {
        ops = new BitmapFactory.Options();
        ops.inJustDecodeBounds = true;

//                          Find the correct scale value. It should be the power of 2.
        final int REQUIRED_SIZE = 50;
        int width_tmp = ops.outWidth, height_tmp = ops.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        ops.inJustDecodeBounds = false;
        ops.inSampleSize = scale;
    }


    @Override
    protected String[] doInBackground(String... params) {
        openGraph = new OpenGraph.Builder(params[0])
                .logger(new OpenGraph.Logger() {
                    @Override
                    public void log(String tag, String msg) {
                        // print log
                    }
                })
                .build();
        Document doc = null;
        try {

            doc = Jsoup.connect(params[0]).get();
            if (doc != null) {
                data = openGraph.getOpenGraph(doc.toString());
                Log.d(TAG, "도메인 : " + data.getDomain());
                convertUrl = new URL(data.getImage());
                bitmap = getBitmap(convertUrl.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"비트맵? : "+bitmap.toString());
        if (doc != null) {
            String[] retParams = {
                    data.getDescription(), data.getTitle(), data.getDomain(), params[0]
            };
            return retParams;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {

    }

    @Override
    protected void onPostExecute(String[] s) {
        urlImage = (ImageView) view.findViewById(R.id.UrlImage);
        urlText = (TextView) view.findViewById(R.id.UrlText);
        urlTitle = (TextView) view.findViewById(R.id.UrlTitle);
        url_text=(TextView)view.findViewById(R.id.txt_url);

        if (s != null) {
            urlImage.setImageBitmap(bitmap);

            Log.d(TAG,"캐시파일 : "+s[3]);
            Cache.getInstance().getLru().put(s[3], bitmap);
            Cache.getInstance().getTitle().put(s[3],s[0]);
            Cache.getInstance().getTitle().put(s[3],s[1]);
            //webListAdapter.caches.add(memoryCache);
            urlText.setText(s[0]);
            urlTitle.setText(s[1]);
        }
    }

    private Bitmap getBitmap(String url) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        Bitmap retBitmap = null;

        try {
            imgUrl = new URL(url);
            Log.d(TAG, imgUrl.toString());
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true); //url로 input받는 flag 허용
            connection.connect(); //연결
            is = connection.getInputStream(); // get inputstream

            retBitmap = BitmapFactory.decodeStream(is, null, ops);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            return retBitmap;
        }
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
    public void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable) d).getBitmap();
            b.recycle();
        } // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

        d.setCallback(null);
    }
}
