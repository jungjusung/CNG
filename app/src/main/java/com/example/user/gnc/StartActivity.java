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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends Service implements View.OnTouchListener {

    String gestureResult = "";
    private LinearLayout li;
    Bitmap bitmap;


    private LinearLayout bli;
    public static LinearLayout sub_li1, sub_li2;
    public static LinearLayout main_li1, main_li2;
    int iconX, iconY;
    public static int icon_width = 150;
    public static int icon_height = 150;
    RelativeLayout layout;
    RelativeLayout title;
    RelativeLayout copyright;
    //RelativeLayout layout_start;
    public static WindowManager.LayoutParams params, params2, params3, params4;
    public static WindowManager.LayoutParams main_parameters1,main_parameters2,sub_parameters1,sub_parameters2,txt_turn_parameters,txt_setting_parameters;
    public static LinearLayout.LayoutParams main_liParameters1,main_liParameters2,sub_liParameters1,sub_liParameters2;
    public static TextView txt_setting,txt_turn;

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

    private static final int DOUBLE_CLICK = 1;
    private static final int MOVE_UP = 2;
    private static final int MOVE_DOWN = 3;
    private static final int MOVE_LEFT = 4;
    private static final int MOVE_RIGHT = 5;

    static int limitY;
    static int limitX;
    float touchedX, touchedY;

    public static HeroIcon heroIcon;

    @Override
    public IBinder onBind(Intent intent) {
        stopService(intent);
        return null;
    }

    @Override
    public void onCreate() {
        TAG = this.getClass().getName();
        startActivity = this;
        String sql = "select * from img_info";
        Cursor rs = defaultAct.db.rawQuery(sql, null);
        rs.moveToNext();
        if(icon_width!=rs.getInt(rs.getColumnIndex("size"))) {
            icon_width = rs.getInt(rs.getColumnIndex("size"));
            icon_height = rs.getInt(rs.getColumnIndex("size"));
        }
        main_parameters1 = new WindowManager.LayoutParams(icon_width*2, icon_width, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        main_parameters2 = new WindowManager.LayoutParams(icon_width*2, icon_width, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        super.onCreate();
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

        //Log.d(TAG,"내위치 x : "+me_x +" 내위치 y : "+me_y +" 내 너비 : "+me_width + " 내 높이 : "+me_height + " ");
        //Log.d(TAG,"블락위치 x : "+ target_x + " 블락 위치 y : "+target_y + " 블락 너비 : "+target_width + " 블락 높이 : "+target_height + " ");

        //내가 오른쪽에서 왼쪽으로 움직이다가 타겟을 만나면 result1 true
        boolean result1 = (me_x-me_width/2 >= target_x-target_width/2) && (me_x-me_width/2 <= (target_x + target_width+target_width/2));//나의 x좌표위치가 타겟의 x range 내에 있는지 판단
        //내가 왼쪽에서 오른쪽으로 움직이다가 만나면 result2 true
        boolean result2 = (me_x + me_width/2 >= target_x-target_width/2) && (me_x + me_width/2 <= (target_x + target_width/2));  //나의 가로폭이 타겟의 가로폭 내에 있는지 판단

        //내가 아래에서 위로
        boolean result3 = (me_y-me_height/2 >= target_y-target_height/2) && (me_y-me_height/2 <= (target_y + target_height+target_height/2));//나의 y좌표위치가 타겟의 세로폭 내에 있는지 판단

        //내가 위에서 아래로
        boolean result4 = (me_y + me_height/2 >= target_y-target_height/2) && (me_y + me_height/2 <= (target_y + target_height/2));//나의 y폭이 타겟의 세로폭 내에 있는지 판단

        return (result1||result2)&&(result3||result4);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"나는 언제 실행되는것인가?");
        dm = Resources.getSystem().getDisplayMetrics();
        //dm.widthPixels;
        limitY = (dm.heightPixels / 2) - 500;
        limitX = (dm.widthPixels / 2)-icon_width*2;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //초기 위치 설정
        String sql = "select * from initialpos";
        Cursor rs = defaultAct.db.rawQuery(sql, null);

        rs.moveToNext();
        initialPosX = rs.getInt(rs.getColumnIndex("x"));
        initialPosY = rs.getInt(rs.getColumnIndex("y"));
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        layout = new RelativeLayout(this);
        copyright = new RelativeLayout(this);
        title = new RelativeLayout(this);

        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);


        layout.setBackgroundColor(Color.argb(100, 255, 0, 0));

        layoutParams.setMargins(0, 20, 0, 0);
        layout.setLayoutParams(layoutParams);
        copyright.setLayoutParams(layoutParams);
        title.setLayoutParams(layoutParams);


        layout.setBackgroundResource(R.drawable.m_logo);
        copyright.setBackgroundResource(R.drawable.m_copy);
        title.setBackgroundResource(R.drawable.m_title);

        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

        //크기 조절 문제점이 여기에 있다.!! params2
        params2 = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params2.alpha = 0f;
        params2.x = initialPosX;
        params2.y = initialPosY;

        params3 = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params4 = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params3.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params4.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.y = 150;
        params3.y = 150;
        params4.y = 150;
        windowManager.addView(title, params3);
        windowManager.addView(copyright, params4);
        windowManager.addView(layout, params);
        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        parameters.x = initialPosX;
        parameters.y = initialPosY;
        parameters.alpha = 0f;
        heroIcon = new HeroIcon(this, initialPosX, initialPosY, icon_width, icon_height);
        String sql1 = "select path from img_info";
        Cursor cursor = defaultAct.db.rawQuery(sql1, null);
        cursor.moveToNext();
        String imgPath = cursor.getString(cursor.getColumnIndex("path"));
        if (imgPath.equals("")) {
            heroIcon.setImageResource(R.drawable.logo2);
            Log.d(TAG, "디비에 없어서 이미지 디폴트");

        } else {

            Bitmap change_bitmap = null;
            try {
                change_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imgPath));
                StartActivity.startActivity.heroIcon.setImageBitmap(change_bitmap);
                Log.d(TAG, "디비에 있으니까 디비에서 이미지 가져옴ㅋㅋ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        windowManager.addView(heroIcon, parameters);

        //windowManager.addView(layout_start, params2);

        dy = params.y;
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
        thread = new Thread(task);
        thread.start();

        if (windowManager != null) {
            GestureDetector.SimpleOnGestureListener mOnSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    Log.d(TAG,"롱클릭");
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(40);
                    longClickOn = true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                    return true;
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    String sql = "select * from shortcut where short_cut=1";
                    Cursor rs = defaultAct.db.rawQuery(sql, null);

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

                    }else if(method == START_WEB_CALL){
                        String urlPath = rs.getString(rs.getColumnIndex("path"));
                        if(!urlPath.equals(null)){
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
                    Log.d(TAG, "확인사항 " + Integer.toString(main_parameters1.x));

                    Log.d(TAG, "전체 스크린 width " +dm.widthPixels);
                    Log.d(TAG, "전체 스크린 height " +dm.heightPixels);
                    Log.d(TAG,"확인 할 x 좌표 "+Integer.toString(initialPosX-(dm.widthPixels/2-icon_width/2)));
                    Log.d(TAG, "투명도: " + Float.toString(params2.alpha));
                    Log.d(TAG, "X좌표: " + initialPosX);
                    Log.d(TAG, "Y좌표: " + initialPosY);
                    bli = new LinearLayout(StartActivity.this);

                    LinearLayout.LayoutParams bliParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    bli.setBackgroundColor(Color.argb(150, 23, 20, 23));

                    bli.setLayoutParams(bliParameters);

                    WindowManager.LayoutParams bparameters = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

                    bparameters.x = 0;
                    bparameters.y = 0;
                    bparameters.gravity = Gravity.CENTER | Gravity.CENTER;

                    windowManager.addView(bli, bparameters);

                    bli.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (windowManager != null) {
                                windowManager.removeView(main_li1);
                                //windowManager.removeView(txt_turn);
                            }
                            if (windowManager != null) {
                                windowManager.removeView(main_li2);
                                //windowManager.removeView(txt_setting);
                            }
                            windowManager.removeView(bli);
                        }
                    });
                    main_li1=new LinearLayout(StartActivity.this);
                    sub_li1 = new LinearLayout(StartActivity.this);
                    txt_turn = new TextView(StartActivity.this);
                    main_liParameters1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    sub_liParameters1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    main_li1.setLayoutParams(main_liParameters1);
                    main_li1.setOrientation(LinearLayout.HORIZONTAL);

                    sub_li1.setBackgroundColor(Color.argb(66, 255, 0, 0));
                    sub_li1.setLayoutParams(sub_liParameters1);

                    txt_turn.setText("종료");
                    txt_turn.setGravity(Gravity.CENTER_VERTICAL);
                    //txt_turn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                    sub_parameters1 = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                    txt_turn_parameters = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                    //txt_turn_parameters.gravity=Gravity.CENTER;
                    main_parameters1.x=initialPosX;
                    if (initialPosY < -limitY) {
                        main_parameters1.y = initialPosY + (icon_height+50);
                    } else {
                        main_parameters1.y = initialPosY - (icon_height+50);
                    }

                    if(params2.x<0) {
                        if (params2.x-icon_width<(dm.widthPixels/2-icon_width/2)*(-1)){
                            main_parameters1.x=main_parameters1.x+icon_width/2;
                            main_li1.addView(sub_li1, sub_parameters1);
                            main_li1.addView(txt_turn,txt_turn_parameters);
                            txt_turn.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
                            Log.d(TAG,"왼쪽한계 지점");
                        }else{
                            main_parameters1.x=main_parameters1.x-icon_width/2;
                            main_li1.addView(txt_turn,txt_turn_parameters);
                            main_li1.addView(sub_li1, sub_parameters1);
                        }
                    }else{
                        main_parameters1.x=main_parameters1.x-icon_width/2;
                        main_li1.addView(txt_turn,txt_turn_parameters);
                        main_li1.addView(sub_li1, sub_parameters1);
                    }
                    sub_li1.setBackgroundResource(R.drawable.turn_on);

                    //main_parameters1.x=main_parameters1.x-icon_width/2;


                    windowManager.addView(main_li1,main_parameters1);

                    sub_li1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            stopSelf();
                            //stopService(new Intent(StartActivity.this, StartActivity.class));
                            if (windowManager != null) {
                                windowManager.removeView(main_li1);
                                //windowManager.removeView(txt_turn);

                            }
                            if (windowManager != null) {
                                windowManager.removeView(main_li2);
                                //windowManager.removeView(txt_setting);
                            }

                            windowManager.removeView(bli);
                        }
                    });


                    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    main_li2=new LinearLayout(StartActivity.this);
                    sub_li2 = new LinearLayout(StartActivity.this);
                    txt_setting = new TextView(StartActivity.this);



                    txt_setting.setGravity(Gravity.CENTER_VERTICAL);
                    txt_setting.setText("설정");
                    sub_liParameters2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    main_liParameters2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    main_li2.setLayoutParams(main_liParameters2);
                    main_li2.setOrientation(LinearLayout.HORIZONTAL);


                    sub_li2.setBackgroundColor(Color.argb(66, 0, 255, 0));
                    sub_li2.setLayoutParams(sub_liParameters2);


                    sub_parameters2 = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                    txt_setting_parameters = new WindowManager.LayoutParams(icon_width, icon_height, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);



                    main_parameters2.x = initialPosX;
                    if (initialPosY < -limitY) {
                        main_parameters2.y = initialPosY + (icon_height+50)*2;
                    } else {
                        main_parameters2.y = initialPosY - (icon_height+50)*2;
                    }

                    if(params2.x<0) {
                        if (params2.x-icon_width<(dm.widthPixels/2-icon_width/2)*(-1)){
                            main_parameters2.x=main_parameters2.x+icon_width/2;
                            main_li2.addView(sub_li2,sub_parameters2);
                            main_li2.addView(txt_setting,txt_setting_parameters);
                            txt_setting.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
                            Log.d(TAG,"왼쪽한계 지점");
                        }else{
                            main_parameters2.x=main_parameters2.x-icon_width/2;
                            main_li2.addView(txt_setting,txt_setting_parameters);
                            main_li2.addView(sub_li2,sub_parameters2);
                        }
                    }else{
                        main_parameters2.x=main_parameters2.x-icon_width/2;
                        main_li2.addView(txt_setting,txt_setting_parameters);
                        main_li2.addView(sub_li2,sub_parameters2);
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
                                //windowManager.removeView(txt_turn);
                            }
                            if (windowManager != null) {
                                windowManager.removeView(main_li2);
                                //windowManager.removeView(txt_setting);
                            }

                            windowManager.removeView(bli);
                        }
                    });

                    return super.onSingleTapConfirmed(e);
                }
            };
            mGestureDetector = new GestureDetector(this, mOnSimpleOnGestureListener);


            //heroIcon ->> layout_start

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


                Log.d(TAG, iconX + " " + iconY + " " + touchedX + " " + touchedY);
                //우측 버튼 트랩
                WindowManager.LayoutParams btnParameters1 = new WindowManager.LayoutParams(50, 250, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                btnParameters1.x = params2.x + 300;
                btnParameters1.y = params2.y;


                block_right = new Block(StartActivity.this, btnParameters1.x, btnParameters1.y, 50, 250);
        //        block_right.setBackgroundColor(Color.RED);
                windowManager.addView(block_right, btnParameters1);


                //좌측 버튼 트랩
                WindowManager.LayoutParams btnParameters2 = new WindowManager.LayoutParams(50, 250, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                btnParameters2.x = params2.x - 300;
                btnParameters2.y = params2.y;

                block_left = new Block(StartActivity.this, btnParameters2.x, btnParameters2.y, 50, 250);
      //          block_left.setBackgroundColor(Color.RED);
                windowManager.addView(block_left, btnParameters2);

                //위쪽 버튼 트랩
                WindowManager.LayoutParams btnParameters3 = new WindowManager.LayoutParams(250, 50, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                btnParameters3.x = params2.x;
                btnParameters3.y = params2.y + 300;

                block_top = new Block(StartActivity.this, btnParameters3.x, btnParameters3.y, 250, 50);
           //     block_top.setBackgroundColor(Color.RED);
                windowManager.addView(block_top, btnParameters3);

                //아래쪽 버튼 트랩
                WindowManager.LayoutParams btnParameters4 = new WindowManager.LayoutParams(250, 50, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                btnParameters4.x = params2.x;
                btnParameters4.y = params2.y - 300;

                block_bottom = new Block(StartActivity.this, btnParameters4.x, btnParameters4.y, 250, 50);
          //      block_bottom.setBackgroundColor(Color.RED);
                windowManager.addView(block_bottom, btnParameters4);

                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d(TAG,"무브");


                updatedParameters.x = (int) (initialPosX + (motionEvent.getRawX() - touchedX));
                updatedParameters.y = (int) (initialPosY + (motionEvent.getRawY() - touchedY));
                if(updatedParameters.y<0) {
                    if (Math.abs(updatedParameters.y) > (dm.heightPixels / 2 - icon_height / 2)){
                        updatedParameters.y=(dm.heightPixels / 2 - icon_height / 2)*(-1);
                    }
                }else{
                    if (Math.abs(updatedParameters.y) > (dm.heightPixels / 2 - icon_height / 2)){
                        updatedParameters.y=(dm.heightPixels / 2 - icon_height / 2);
                    }
                }

                if(updatedParameters.x<0) {
                    if (Math.abs(updatedParameters.x) > (dm.widthPixels / 2 - icon_width / 2)){
                        updatedParameters.x=(dm.widthPixels / 2 - icon_width / 2)*(-1);
                    }
                }else{
                    if (Math.abs(updatedParameters.x) > (dm.widthPixels / 2 - icon_width / 2)){
                        updatedParameters.x=(dm.widthPixels / 2 - icon_width / 2);
                    }
                }



                //heroIcon.x = updatedParameters.x - icon_width / 2;
                //heroIcon.y = updatedParameters.y - icon_height / 2;
                heroIcon.x = updatedParameters.x;
                heroIcon.y = updatedParameters.y;

                windowManager.updateViewLayout(heroIcon, updatedParameters);
                params2=updatedParameters;
                Log.d(TAG,"갱신되는 x "+params2.x+"   갱신되는 y "+params2.y);
                List<Block> list=new ArrayList<Block>();
                list.add(block_bottom);
                list.add(block_top);
                list.add(block_right);
                list.add(block_left);

                for(int i=0;i<4;i++){
                    boolean result=hitTest(heroIcon,list.get(i));
                    //Log.d(TAG,i+1+"번째 히트 테스트");
                    if(result){
                        if(i==0)
                            gestureResult="위쪽";
                        else if(i==1)
                            gestureResult="아래쪽";
                        else if(i==2)
                            gestureResult="오른쪽";
                        else if(i==3)
                            gestureResult="왼쪽";
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

                    //Log.d(TAG,"끝");
                    if (gestureResult.equals("왼쪽")) {
                        Log.d(TAG,"왼쪽");
                        String sql = "select * from shortcut where short_cut=4";
                        Cursor rs = defaultAct.db.rawQuery(sql, null);

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
                            if(!urlPath.equals(null)){
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setData(Uri.parse(urlPath));
                                startActivity(intent);
                            }
                        }

                    } else if (gestureResult.equals("오른쪽")) {
                        Log.d(TAG,"오른쪽");
                        String sql = "select * from shortcut where short_cut=5";
                        Cursor rs = defaultAct.db.rawQuery(sql, null);
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
                            if(!urlPath.equals(null)){
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setData(Uri.parse(urlPath));
                                startActivity(intent);
                            }
                        }
                    } else if (gestureResult.equals("아래쪽")) {
                        Log.d(TAG,"아래쪽");
                        String sql = "select * from shortcut where short_cut=3";
                        Cursor rs = defaultAct.db.rawQuery(sql, null);
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
                            if(!urlPath.equals(null)){
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setData(Uri.parse(urlPath));
                                startActivity(intent);
                            }
                        }

                    } else if (gestureResult.equals("위쪽")) {
                        Log.d(TAG,"위쪽");
                        String sql = "select * from shortcut where short_cut=2";
                        Cursor rs = defaultAct.db.rawQuery(sql, null);
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
                            if(!urlPath.equals(null)){
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
                    if(updatedParameters.y<0) {
                        if (Math.abs(updatedParameters.y) > (dm.heightPixels / 2 - icon_height / 2)){
                            updatedParameters.y=(dm.heightPixels / 2 - icon_height / 2)*(-1);
                        }
                    }else{
                        if (Math.abs(updatedParameters.y) > (dm.heightPixels / 2 - icon_height / 2)){
                            updatedParameters.y=(dm.heightPixels / 2 - icon_height / 2);
                        }
                    }

                    if(updatedParameters.x<0) {
                        if (Math.abs(updatedParameters.x) > (dm.widthPixels / 2 - icon_width / 2)){
                            updatedParameters.x=(dm.widthPixels / 2 - icon_width / 2)*(-1);
                        }
                    }else{
                        if (Math.abs(updatedParameters.x) > (dm.widthPixels / 2 - icon_width / 2)){
                            updatedParameters.x=(dm.widthPixels / 2 - icon_width / 2);
                        }
                    }



                    //heroIcon.x = updatedParameters.x - icon_width / 2;
                    //heroIcon.y = updatedParameters.y - icon_height / 2;
                    heroIcon.x = updatedParameters.x;
                    heroIcon.y = updatedParameters.y;

                    initialPosX = updatedParameters.x;
                    initialPosY = updatedParameters.y;

                    windowManager.updateViewLayout(heroIcon, updatedParameters);
                    params2=updatedParameters;
                    Log.d(TAG," 롱클릭에서 갱신되는 x "+params2.x+" 롱클릭에서 갱신되는 y "+params2.y);

                    String sqlPos = "update initialpos set x=?, y=?";

                    defaultAct.db.execSQL(sqlPos, new String[]{
                            Integer.toString(initialPosX), Integer.toString(initialPosY)
                    });

                    String sql1 = "select * from initialpos";
                    Cursor rs1 = defaultAct.db.rawQuery(sql1, null);

                    rs1.moveToNext();
                    initialPosX = rs1.getInt(rs1.getColumnIndex("x"));
                    initialPosY = rs1.getInt(rs1.getColumnIndex("y"));

                    Log.d(TAG, initialPosX + " " + initialPosY);
                    gestureResult = "";
                }
                longClickOn = false;
                break;
            default:
                break;
        }

        return mGestureDetector.onTouchEvent(motionEvent);

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "지워짐?");
        //android.os.Process.killProcess(android.os.Process.myPid());
        System.runFinalizersOnExit(true);

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

