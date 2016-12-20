package com.example.user.gnc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends Service implements View.OnTouchListener {

    String gestureResult = "";

    boolean startAnimationFlag = false;
    private LinearLayout bli;
    public static LinearLayout sub_li1, sub_li2;
    public static LinearLayout main_li1, main_li2;
    int iconX, iconY;
    public static int icon_width = 150;
    public static int icon_height = 150;
    RelativeLayout layout;
    RelativeLayout title;
    RelativeLayout copyright;

    public static WindowManager.LayoutParams params, params2, params3, params4;
    public static WindowManager.LayoutParams main_parameters1, main_parameters2, sub_parameters1, sub_parameters2, txt_turn_parameters, txt_setting_parameters;
    public static LinearLayout.LayoutParams main_liParameters1, main_liParameters2, sub_liParameters1, sub_liParameters2;
    public static TextView txt_setting, txt_turn;

    public static WindowManager windowManager;
    DisplayMetrics dm;
    Boolean longClickOn = false;
    public static StartActivity startActivity;


    static int initialPosX;
    static int initialPosY;
    String TAG;
    Handler handler, handler2, handler3;
    Thread thread;
    int dy;
    float alpha = 1.0f;
    float alpha2 = 0.0f;
    int cnt = 0;
    boolean moveDown = false;
    Runnable task, task2, task3;
    GestureDetector mGestureDetector;
    Block block_left, block_right, block_top, block_bottom;


    private static final int START_PHONE_CALL = 1;
    private static final int START_APP_CALL = 2;
    private static final int START_WEB_CALL = 3;

    static int limitY;
    static int limitX;
    float touchedX, touchedY;

    public static HeroIcon heroIcon;

    /*카운트*/
    Thread countThread;


    /*------------------------------- 코드 정리 ----------------------------------*/

    String sql;
    Cursor rs;
    RelativeLayout.LayoutParams layoutParams;
    WindowManager.LayoutParams parameters;
    Bitmap change_bitmap;
    LinearLayout.LayoutParams bliParameters;
    WindowManager.LayoutParams bparameters;
    WindowManager.LayoutParams btnParameters1, btnParameters2, btnParameters3, btnParameters4;

    /*------------------------------------------------------------------------*/
    @Override
    public IBinder onBind(Intent intent) {
        stopService(intent);
        return null;
    }

    @Override
    public void onCreate() {
        TAG = this.getClass().getName();

        /*StartActivity*/
        Log.d(TAG,"나 스타트엑티비티 생성된다.");

        startActivity = this;
        sql = "select * from img_info";
        rs = defaultAct.db.rawQuery(sql, null);
        rs.moveToNext();
        if (icon_width != rs.getInt(rs.getColumnIndex("size"))) {
            icon_width = rs.getInt(rs.getColumnIndex("size"));
            icon_height = rs.getInt(rs.getColumnIndex("size"));
        }
        main_parameters1 = new WindowManager.LayoutParams(icon_width * 2, icon_width, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        main_parameters2 = new WindowManager.LayoutParams(icon_width * 2, icon_width, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);


        layout = new RelativeLayout(this);
        copyright = new RelativeLayout(this);
        title = new RelativeLayout(this);


        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params2 = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params3 = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params4 = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        parameters = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        bli = new LinearLayout(StartActivity.this);
        bliParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bparameters = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        main_li1 = new LinearLayout(StartActivity.this);
        main_li2 = new LinearLayout(StartActivity.this);
        sub_li1 = new LinearLayout(StartActivity.this);
        sub_li2 = new LinearLayout(StartActivity.this);
        txt_turn = new TextView(StartActivity.this);
        txt_setting = new TextView(StartActivity.this);

        main_liParameters1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sub_liParameters1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        main_liParameters2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sub_liParameters2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        sub_parameters1 = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        txt_turn_parameters = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        sub_parameters2 = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        txt_setting_parameters = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        btnParameters1 = new WindowManager.LayoutParams(50, 250, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        btnParameters2 = new WindowManager.LayoutParams(50, 250, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        btnParameters3 = new WindowManager.LayoutParams(50, 250, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        btnParameters4 = new WindowManager.LayoutParams(50, 250, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        handler = new Handler() {
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String str = bundle.getString("thread");
                int dy = Integer.parseInt(str);
                params.y = dy;
                windowManager.updateViewLayout(layout, params);
            }
        };


        handler2 = new Handler() {
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String str = bundle.getString("threadAlpha");
                float alpha = Float.parseFloat(str);
                params.alpha = alpha;
                windowManager.updateViewLayout(layout, params);
                windowManager.updateViewLayout(title, params);
                windowManager.updateViewLayout(copyright, params);
            }
        };
        handler3 = new Handler() {
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String str = bundle.getString("Alpha");
                float alpha = Float.parseFloat(str);
                params2.alpha = alpha;
                windowManager.updateViewLayout(heroIcon, params2);
                startAnimationFlag = true;
            }
        };

        task2 = new Runnable() {
            public void run() {
                while (alpha >= 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }

                    alpha -= 0.08;

                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("threadAlpha", Float.toString(alpha));
                    message.setData(bundle);
                    handler2.sendMessage(message);
                }
                windowManager.removeView(layout);
                windowManager.removeView(title);
                windowManager.removeView(copyright);
                defaultAct.defaultAct.finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        task3 = new Runnable() {
            public void run() {
                while (alpha2 <= 1.0f) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }

                    alpha2 += 0.05;

                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("Alpha", Float.toString(alpha2));
                    message.setData(bundle);
                    handler3.sendMessage(message);
                }
            }

        };
        task = new Runnable() {
            public void run() {
                while (cnt < 8) {
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                    }

                    if (!moveDown) {
                        dy -= 5;
                        if (dy <= 50) {
                            moveDown = true;
                            cnt++;
                        }
                    } else {
                        dy += (int) 5 + Math.pow(2, 2);
                        if (dy >= 150) {
                            moveDown = false;
                            cnt++;
                        }
                    }
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("thread", Integer.toString(dy));
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                Thread threadNew = new Thread(task3);
                Thread threadAlpha = new Thread(task2);
                threadNew.start();
                threadAlpha.start();

            }

        };


        super.onCreate();

    }


    public int onStartCommand(Intent intent, int flags, int startId) {


        dm = Resources.getSystem().getDisplayMetrics();
        limitY = (dm.heightPixels / 2) - 500;
        limitX = (dm.widthPixels / 2) - icon_width * 2;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //초기 위치 설정
        sql = "select * from initialpos";
        rs = defaultAct.db.rawQuery(sql, null);

        rs.moveToNext();
        initialPosX = rs.getInt(rs.getColumnIndex("x"));
        initialPosY = rs.getInt(rs.getColumnIndex("y"));
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        layout.setBackgroundColor(Color.argb(100, 255, 0, 0));
        layoutParams.setMargins(0, 20, 0, 0);
        layout.setLayoutParams(layoutParams);
        copyright.setLayoutParams(layoutParams);
        title.setLayoutParams(layoutParams);


        layout.setBackgroundResource(R.drawable.m_logo);
        copyright.setBackgroundResource(R.drawable.m_copy);
        title.setBackgroundResource(R.drawable.m_title);


        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

        //크기 조절 문제점이 여기에 있다.!! params2

        params2.alpha = 0f;
        params2.x = initialPosX;
        params2.y = initialPosY;

        params3.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params4.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.y = 150;
        params3.y = 150;
        params4.y = 150;

        windowManager.addView(title, params3);
        windowManager.addView(copyright, params4);
        windowManager.addView(layout, params);

        parameters.x = initialPosX;
        parameters.y = initialPosY;
        parameters.alpha = 0f;
        heroIcon = new HeroIcon(this, initialPosX, initialPosY, icon_width, icon_height);

        sql = "select path from img_info";
        Cursor cursor = defaultAct.db.rawQuery(sql, null);
        cursor.moveToNext();
        String imgPath = cursor.getString(cursor.getColumnIndex("path"));
        if (imgPath.equals("")) {
            heroIcon.setImageResource(R.drawable.logo2);
        } else {
            try {
                change_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imgPath));
                StartActivity.startActivity.heroIcon.setImageBitmap(change_bitmap);
                change_bitmap.recycle();
                change_bitmap=null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        windowManager.addView(heroIcon, parameters);


        dy = params.y;

        thread = new Thread(task);
        thread.start();

        if (windowManager != null) {
            GestureDetector.SimpleOnGestureListener mOnSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
                public boolean onSingleTapUp(MotionEvent e) {
                    return false;
                }

                public void onShowPress(MotionEvent e) {
                }

                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return true;
                }

                public void onLongPress(MotionEvent e) {
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(40);
                    longClickOn = true;
                }

                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return true;
                }

                public boolean onDown(MotionEvent e) {
                    return false;
                }

                public boolean onDoubleTap(MotionEvent e) {
                    sql = "select * from shortcut where short_cut=1";
                    rs = defaultAct.db.rawQuery(sql, null);

                    rs.moveToNext();
                    int method = rs.getInt(rs.getColumnIndex("method"));
                    if (method == START_PHONE_CALL) {
                        String number = rs.getString(rs.getColumnIndex("path"));
                        if (!number.equals(null))
                            callPhone(number);
                    } else if (method == START_APP_CALL) {
                        String number = rs.getString(rs.getColumnIndex("path"));
                        if (!number.equals(null)) {
                            Intent intent = getPackageManager().getLaunchIntentForPackage(number);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    } else if (method == START_WEB_CALL) {
                        String urlPath = rs.getString(rs.getColumnIndex("path"));
                        Log.d(TAG, "URL PATh : " + urlPath);
                        if (!urlPath.equals(null)) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse(urlPath));
                            startActivity(intent);
                        }
                    }
                    return super.onDoubleTap(e);
                }


                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    return super.onDoubleTapEvent(e);
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    bli.setBackgroundColor(Color.argb(150, 23, 20, 23));

                    bli.setLayoutParams(bliParameters);

                    bparameters.x = 0;
                    bparameters.y = 0;
                    bparameters.gravity = Gravity.CENTER | Gravity.CENTER;

                    windowManager.addView(bli, bparameters);

                    bli.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (windowManager != null) {
                                windowManager.removeView(main_li1);
                            }
                            if (windowManager != null) {
                                windowManager.removeView(main_li2);
                            }
                            windowManager.removeView(bli);
                        }
                    });

                    main_li1.setLayoutParams(main_liParameters1);
                    main_li1.setOrientation(LinearLayout.HORIZONTAL);

                    sub_li1.setBackgroundColor(Color.argb(66, 255, 0, 0));
                    sub_li1.setLayoutParams(sub_liParameters1);

                    txt_turn.setText(R.string.exit);
                    txt_turn.setGravity(Gravity.CENTER_VERTICAL);
                    main_parameters1.x = initialPosX;

                    if (initialPosY < -limitY) {
                        main_parameters1.y = initialPosY + (icon_height + 50);
                    } else {
                        main_parameters1.y = initialPosY - (icon_height + 50);
                    }

                    if(params2.x<0) {
                        if (params2.x-icon_width<(dm.widthPixels/2-icon_width/2)*(-1)){
                            main_li1.removeAllViews();////////
                            main_parameters1.x=main_parameters1.x+icon_width/2;
                            main_li1.addView(sub_li1, sub_parameters1);
                            main_li1.addView(txt_turn,txt_turn_parameters);
                            txt_turn.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
                        }else{
                            main_li1.removeAllViews();////////
                            main_parameters1.x=main_parameters1.x-icon_width/2;
                            main_li1.addView(txt_turn,txt_turn_parameters);
                            main_li1.addView(sub_li1, sub_parameters1);
                        }
                    } else {
                        main_parameters1.x = main_parameters1.x - icon_width / 2;
                        main_li1.removeAllViews();
                        main_li1.addView(txt_turn, txt_turn_parameters);
                        main_li1.addView(sub_li1, sub_parameters1);
                    }
                    sub_li1.setBackgroundResource(R.drawable.turn_on);

                    windowManager.addView(main_li1, main_parameters1);

                    sub_li1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            stopSelf();
                            if (windowManager != null) {
                                windowManager.removeView(main_li1);

                            }
                            if (windowManager != null) {
                                windowManager.removeView(main_li2);
                            }

                            windowManager.removeView(bli);
                        }
                    });


                    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


                    txt_setting.setGravity(Gravity.CENTER_VERTICAL);
                    txt_setting.setText(R.string.setting);

                    main_li2.setLayoutParams(main_liParameters2);
                    main_li2.setOrientation(LinearLayout.HORIZONTAL);


                    sub_li2.setBackgroundColor(Color.argb(66, 0, 255, 0));
                    sub_li2.setLayoutParams(sub_liParameters2);


                    main_parameters2.x = initialPosX;
                    if (initialPosY < -limitY) {
                        main_parameters2.y = initialPosY + (icon_height + 50) * 2;
                    } else {
                        main_parameters2.y = initialPosY - (icon_height + 50) * 2;
                    }

                    if (params2.x < 0) {
                        if (params2.x - icon_width < (dm.widthPixels / 2 - icon_width / 2) * (-1)) {
                            main_parameters2.x = main_parameters2.x + icon_width / 2;
                            main_li2.removeAllViews();
                            main_li2.addView(sub_li2, sub_parameters2);
                            main_li2.addView(txt_setting, txt_setting_parameters);
                            txt_setting.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                        } else {
                            main_parameters2.x = main_parameters2.x - icon_width / 2;
                            main_li2.removeAllViews();
                            main_li2.addView(txt_setting, txt_setting_parameters);
                            main_li2.addView(sub_li2, sub_parameters2);
                        }
                    } else {
                        main_parameters2.x = main_parameters2.x - icon_width / 2;
                        main_li2.removeAllViews();
                        main_li2.addView(txt_setting, txt_setting_parameters);
                        main_li2.addView(sub_li2, sub_parameters2);
                    }

                    sub_li2.setBackgroundResource(R.drawable.setting);


                    windowManager.addView(main_li2, main_parameters2);


                    sub_li2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /*--- 2번째 버튼 클릭 했을시 SettingActivity로 전환 */
                            Intent settingIntent = new Intent(StartActivity.this, SettingActivity.class);
                            settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(settingIntent);

                            if (windowManager != null) {
                                windowManager.removeView(main_li1);
                            }
                            if (windowManager != null) {
                                windowManager.removeView(main_li2);
                            }

                            windowManager.removeView(bli);
                        }
                    });

                    return super.onSingleTapConfirmed(e);
                }
            };

            heroIcon.setOnTouchListener(this);
            mGestureDetector = new GestureDetector(this, mOnSimpleOnGestureListener);
        }

        return START_NOT_STICKY;
    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        WindowManager.LayoutParams updatedParameters = params2;


        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "다운");
                iconX = updatedParameters.x;
                iconY = updatedParameters.y;

                touchedX = motionEvent.getRawX();
                touchedY = motionEvent.getRawY();

                btnParameters1.x = params2.x + 300;
                btnParameters1.y = params2.y;


                block_right = new Block(StartActivity.this, btnParameters1.x, btnParameters1.y, 50, 250);

                windowManager.addView(block_right, btnParameters1);

                btnParameters2.x = params2.x - 300;
                btnParameters2.y = params2.y;

                block_left = new Block(StartActivity.this, btnParameters2.x, btnParameters2.y, 50, 250);
                windowManager.addView(block_left, btnParameters2);


                btnParameters3.x = params2.x;
                btnParameters3.y = params2.y + 300;

                block_top = new Block(StartActivity.this, btnParameters3.x, btnParameters3.y, 250, 50);
                windowManager.addView(block_top, btnParameters3);

                btnParameters4.x = params2.x;
                btnParameters4.y = params2.y - 300;

                block_bottom = new Block(StartActivity.this, btnParameters4.x, btnParameters4.y, 250, 50);
                windowManager.addView(block_bottom, btnParameters4);
                break;

            case MotionEvent.ACTION_MOVE:
                updatedParameters.x = (int) (initialPosX + (motionEvent.getRawX() - touchedX));
                updatedParameters.y = (int) (initialPosY + (motionEvent.getRawY() - touchedY));
                if (updatedParameters.y < 0) {
                    if (Math.abs(updatedParameters.y) > (dm.heightPixels / 2 - icon_height / 2)) {
                        updatedParameters.y = (dm.heightPixels / 2 - icon_height / 2) * (-1);
                    }
                } else {
                    if (Math.abs(updatedParameters.y) > (dm.heightPixels / 2 - icon_height / 2)) {
                        updatedParameters.y = (dm.heightPixels / 2 - icon_height / 2);
                    }
                }

                if (updatedParameters.x < 0) {
                    if (Math.abs(updatedParameters.x) > (dm.widthPixels / 2 - icon_width / 2)) {
                        updatedParameters.x = (dm.widthPixels / 2 - icon_width / 2) * (-1);
                    }
                } else {
                    if (Math.abs(updatedParameters.x) > (dm.widthPixels / 2 - icon_width / 2)) {
                        updatedParameters.x = (dm.widthPixels / 2 - icon_width / 2);
                    }
                }


                heroIcon.x = updatedParameters.x;
                heroIcon.y = updatedParameters.y;

                windowManager.updateViewLayout(heroIcon, updatedParameters);
                params2 = updatedParameters;
                List<Block> list = new ArrayList<Block>();
                list.add(block_bottom);
                list.add(block_top);
                list.add(block_right);
                list.add(block_left);

                for (int i = 0; i < 4; i++) {
                    boolean result = hitTest(heroIcon, list.get(i));
                    if (result) {
                        if (i == 0)
                            gestureResult = "위쪽";
                        else if (i == 1)
                            gestureResult = "아래쪽";
                        else if (i == 2)
                            gestureResult = "오른쪽";
                        else if (i == 3)
                            gestureResult = "왼쪽";
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                updatedParameters.x = initialPosX;
                updatedParameters.y = initialPosY;
                windowManager.updateViewLayout(heroIcon, updatedParameters);
                windowManager.removeView(block_right);

                windowManager.removeView(block_left);

                windowManager.removeView(block_top);

                windowManager.removeView(block_bottom);

                if (longClickOn == false) {

                    if (gestureResult.equals("왼쪽")) {
                        sql = "select * from shortcut where short_cut=4";
                        rs = defaultAct.db.rawQuery(sql, null);

                        rs.moveToNext();
                        int method = rs.getInt(rs.getColumnIndex("method"));
                        String number = null;
                        if (method == START_PHONE_CALL) {
                            number = rs.getString(rs.getColumnIndex("path"));
                            if (!number.equals(null))
                                callPhone(number);

                        } else if (method == START_APP_CALL) {
                            number = rs.getString(rs.getColumnIndex("path"));
                            if (!number.equals(null)) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(number);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else if (method == START_WEB_CALL) {
                            String urlPath = rs.getString(rs.getColumnIndex("path"));
                            if (!urlPath.equals(null)) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setData(Uri.parse(urlPath));
                                startActivity(intent);
                            }
                        }

                    } else if (gestureResult.equals("오른쪽")) {
                        sql = "select * from shortcut where short_cut=5";
                        rs = defaultAct.db.rawQuery(sql, null);
                        rs.moveToNext();
                        int method = rs.getInt(rs.getColumnIndex("method"));
                        if (method == START_PHONE_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            if (!number.equals(null))
                                callPhone(number);

                        } else if (method == START_APP_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            if (!number.equals(null)) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(number);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else if (method == START_WEB_CALL) {
                            String urlPath = rs.getString(rs.getColumnIndex("path"));
                            if (!urlPath.equals(null)) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setData(Uri.parse(urlPath));
                                startActivity(intent);
                            }
                        }
                    } else if (gestureResult.equals("아래쪽")) {
                        sql = "select * from shortcut where short_cut=3";
                        rs = defaultAct.db.rawQuery(sql, null);
                        rs.moveToNext();
                        int method = rs.getInt(rs.getColumnIndex("method"));
                        if (method == START_PHONE_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            if (!number.equals(null))
                                callPhone(number);
                        } else if (method == START_APP_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            if (!number.equals(null)) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(number);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else if (method == START_WEB_CALL) {
                            String urlPath = rs.getString(rs.getColumnIndex("path"));
                            if (!urlPath.equals(null)) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setData(Uri.parse(urlPath));
                                startActivity(intent);
                            }
                        }

                    } else if (gestureResult.equals("위쪽")) {
                        sql = "select * from shortcut where short_cut=2";
                        rs = defaultAct.db.rawQuery(sql, null);
                        rs.moveToNext();
                        int method = rs.getInt(rs.getColumnIndex("method"));
                        if (method == START_PHONE_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            if (!number.equals(null))
                                callPhone(number);
                        } else if (method == START_APP_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            if (!number.equals(null)) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(number);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                        } else if (method == START_WEB_CALL) {
                            String urlPath = rs.getString(rs.getColumnIndex("path"));
                            if (!urlPath.equals(null)) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setData(Uri.parse(urlPath));
                                startActivity(intent);
                            }

                        }

                    }
                    gestureResult = "";
                } else {
                    updatedParameters.x = (int) (initialPosX + (motionEvent.getRawX() - touchedX));
                    updatedParameters.y = (int) (initialPosY + (motionEvent.getRawY() - touchedY));
                    if (updatedParameters.y < 0) {
                        if (Math.abs(updatedParameters.y) > (dm.heightPixels / 2 - icon_height / 2)) {
                            updatedParameters.y = (dm.heightPixels / 2 - icon_height / 2) * (-1);
                        }
                    } else {
                        if (Math.abs(updatedParameters.y) > (dm.heightPixels / 2 - icon_height / 2)) {
                            updatedParameters.y = (dm.heightPixels / 2 - icon_height / 2);
                        }
                    }
                    if (updatedParameters.x < 0) {
                        if (Math.abs(updatedParameters.x) > (dm.widthPixels / 2 - icon_width / 2)) {
                            updatedParameters.x = (dm.widthPixels / 2 - icon_width / 2) * (-1);
                        }
                    } else {
                        if (Math.abs(updatedParameters.x) > (dm.widthPixels / 2 - icon_width / 2)) {
                            updatedParameters.x = (dm.widthPixels / 2 - icon_width / 2);
                        }
                    }

                    heroIcon.x = updatedParameters.x;
                    heroIcon.y = updatedParameters.y;

                    initialPosX = updatedParameters.x;
                    initialPosY = updatedParameters.y;

                    windowManager.updateViewLayout(heroIcon, updatedParameters);
                    params2 = updatedParameters;


                    String sqlPos = "update initialpos set x=?, y=?";

                    defaultAct.db.execSQL(sqlPos, new String[]{
                            Integer.toString(initialPosX), Integer.toString(initialPosY)
                    });

                    sql = "select * from initialpos";
                    rs = defaultAct.db.rawQuery(sql, null);

                    rs.moveToNext();
                    initialPosX = rs.getInt(rs.getColumnIndex("x"));
                    initialPosY = rs.getInt(rs.getColumnIndex("y"));

                    gestureResult = "";
                }
                longClickOn = false;
                break;
            default:
                break;
        }

        return mGestureDetector.onTouchEvent(motionEvent);

    }

    public boolean hitTest(HeroIcon heroIcon, Block block) {

        int me_x = heroIcon.x;
        int me_y = heroIcon.y;
        int me_width = heroIcon.width;
        int me_height = heroIcon.heigth;


        int target_x = block.x;
        int target_y = block.y;
        int target_width = block.width;
        int target_height = block.height;

        //내가 오른쪽에서 왼쪽으로 움직이다가 타겟을 만나면 result1 true
        boolean result1 = (me_x - me_width / 2 >= target_x - target_width / 2) && (me_x - me_width / 2 <= (target_x + target_width + target_width / 2));//나의 x좌표위치가 타겟의 x range 내에 있는지 판단
        //내가 왼쪽에서 오른쪽으로 움직이다가 만나면 result2 true
        boolean result2 = (me_x + me_width / 2 >= target_x - target_width / 2) && (me_x + me_width / 2 <= (target_x + target_width / 2));  //나의 가로폭이 타겟의 가로폭 내에 있는지 판단

        //내가 아래에서 위로
        boolean result3 = (me_y - me_height / 2 >= target_y - target_height / 2) && (me_y - me_height / 2 <= (target_y + target_height + target_height / 2));//나의 y좌표위치가 타겟의 세로폭 내에 있는지 판단

        //내가 위에서 아래로
        boolean result4 = (me_y + me_height / 2 >= target_y - target_height / 2) && (me_y + me_height / 2 <= (target_y + target_height / 2));//나의 y폭이 타겟의 세로폭 내에 있는지 판단

        return (result1 || result2) && (result3 || result4);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "지워짐?");
        //android.os.Process.killProcess(android.os.Process.myPid());
        System.gc();
        System.exit(0);
        super.onDestroy();
    }

    /*전화 permission*/
    public void callPhone(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

