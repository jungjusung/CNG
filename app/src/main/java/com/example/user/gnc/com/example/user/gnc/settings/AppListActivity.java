package com.example.user.gnc.com.example.user.gnc.settings;

/**
 * Created by user on 2016-12-02.
 */


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.gnc.R;
import com.example.user.gnc.RecycleUtils;
import com.example.user.gnc.defaultAct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppListActivity extends Activity implements AdapterView.OnItemClickListener{
    private static final String TAG = AppListActivity.class.getSimpleName();
    // 메뉴 KEY
    private final int MENU_DOWNLOAD = 0;
    private final int MENU_ALL = 1;
    private int MENU_MODE = MENU_DOWNLOAD;

    private PackageManager pm;
    private View mLoadingContainer;
    private ListView mListView = null;
    private IAAdapter mAdapter = null;
    public final int ONE = 1;
    public final int TWO = 2;
    public int short_cut = -1;
    private static final int SELECT_APP = 2;
    public ArrayList<AppInfo> mListData = new ArrayList<AppInfo>();
    String pkg;
    String title;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list_layout);
        short_cut = Integer.parseInt(getIntent().getStringExtra("short_cut"));
        mLoadingContainer = findViewById(R.id.loading_container);
        mListView = (ListView) findViewById(R.id.listView1);
        mAdapter = new IAAdapter(this);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        Log.d(TAG,Integer.toString(mAdapter.getCount()));
        registerForContextMenu(mListView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 작업 시작
        startTask();
    }

    /**
     * 작업 시작
     */
    private void startTask() {
        new AppTask().execute();
    }

    /**
     * 로딩뷰 표시 설정
     *
     * @param isView 표시 유무
     */
    private void setLoadingView(boolean isView) {
        if (isView) {
            // 화면 로딩뷰 표시
            mLoadingContainer.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            // 화면 어플 리스트 표시
            mListView.setVisibility(View.VISIBLE);
            mLoadingContainer.setVisibility(View.GONE);
        }
    }

    /**
     * List Fast Holder
     *
     * @author nohhs
     */
    private class ViewHolder {
        // App Icon
        public ImageView mIcon;
        // App Name
        public TextView mName;
        // App Package Name
        public TextView mPacakge;

    }

    /**
     * List Adapter
     *
     * @author nohhs
     */
    private class IAAdapter extends BaseAdapter {
        private Context mContext = null;

        private List<ApplicationInfo> mAppList = null;

        public IAAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        public int getCount() {
            return mListData.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_layout, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.app_icon);
                holder.mName = (TextView) convertView.findViewById(R.id.app_name);
                holder.mPacakge = (TextView) convertView.findViewById(R.id.app_package);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AppInfo data = mListData.get(position);

            if (data.mIcon != null) {
                holder.mIcon.setImageDrawable(data.mIcon);
            }

            holder.mName.setText(data.mAppName);
            holder.mPacakge.setText(data.mAppPackage);

            return convertView;
        }

        /**
         * 어플리케이션 리스트 작성
         */
        public void rebuild() {
            if (mAppList == null) {

                Log.d(TAG, "Is Empty Application List");
                // 패키지 매니저 취득
                pm = AppListActivity.this.getPackageManager();

                // 설치된 어플리케이션 취득
                mAppList = pm
                        .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES
                                | PackageManager.GET_DISABLED_COMPONENTS);
            }

            AppInfo.AppFilter filter;
            switch (MENU_MODE) {
                case MENU_DOWNLOAD:
                    filter = AppInfo.THIRD_PARTY_FILTER;
                    break;
                default:
                    filter = null;
                    break;
            }

            if (filter != null) {
                filter.init();
            }

            // 기존 데이터 초기화
            mListData.clear();

            AppInfo addInfo = null;
            ApplicationInfo info = null;
            for (ApplicationInfo app : mAppList) {
                info = app;

                if (filter == null || filter.filterApp(info)) {
                    // 필터된 데이터
                    if(!app.packageName.equals("com.example.user.gnc")) {
                        addInfo = new AppInfo();
                        // App Icon
                        addInfo.mIcon = app.loadIcon(pm);
                        // App Name
                        addInfo.mAppName = app.loadLabel(pm).toString();
                        // App Package Name
                        addInfo.mAppPackage = app.packageName;
                        mListData.add(addInfo);
                    }
                }
            }

            // 알파벳 이름으로 소트(한글, 영어)

            Collections.sort(mListData, AppInfo.ALPHA_COMPARATOR);
        }
    }

    /**
     * 작업 태스크
     *
     * @author nohhs
     */
    private class AppTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // 로딩뷰 시작
            setLoadingView(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // 어플리스트 작업시작
            mAdapter.rebuild();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // 어댑터 갱신
            mAdapter.notifyDataSetChanged();

            // 로딩뷰 정지
            setLoadingView(false);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        title = mListData.get(position).mAppName;
        pkg = mListData.get(position).mAppPackage;

        try {
            alert_confirm.setTitle(title)
                    .setIcon(getPackageManager().getApplicationIcon(pkg))
                    .setMessage(title).setCancelable(true)
                    .setPositiveButton(R.string.add,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 'YES'
                                    String sql = "update shortcut set path=?, name=?, method=2 where short_cut=?";
                                    defaultAct.db.execSQL(sql, new String[]{pkg, title, Integer.toString(short_cut)});
                                    AppListActivity.this.finish();
                                }
                            })
                    .setNegativeButton(R.string.cancel, null);
            AlertDialog alert = alert_confirm.create();
            alert.show();
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
    }

    private int checkFlag() {
        String sql = "select applist from manual_flags";
        Cursor rs = defaultAct.db.rawQuery(sql, null);
        rs.moveToNext();
        return rs.getInt(0);
    }

    protected void onDestroy() {
        Log.d(TAG, "내가 꺼졌따~");
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }
}

