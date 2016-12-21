package com.example.user.gnc.com.example.user.gnc.settings;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Sam on 2016-12-20.
 */

public class TestService extends Service implements Runnable{
    String TAG;
    Thread thread;
    boolean flag=true;
    int count=0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TAG=this.getClass().getName();
        thread = new Thread();
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        flag=false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        while(flag){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "서비스 실행 중..."+count);
            count++;
        }
    }
}
