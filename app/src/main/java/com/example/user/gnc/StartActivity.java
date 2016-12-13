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
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StartActivity extends Service implements View.OnTouchListener {

    String gestureResult = "";
    private LinearLayout li;
    Bitmap bitmap;


    private LinearLayout bli;
    private LinearLayout sub_li1, sub_li2, sub_li3;

    int iconX, iconY;
    int icon_width = 200;
    int icon_height = 200;
    RelativeLayout layout;
    RelativeLayout title;
    RelativeLayout copyright;
    //RelativeLayout layout_start;
    WindowManager.LayoutParams params, params2, params3, params4;
    TextView txt_setting;
    TextView txt_turn;
    static WindowManager windowManager;
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
    float touchedX, touchedY;

    static HeroIcon heroIcon;

    @Override
    public IBinder onBind(Intent intent) {
        stopService(intent);
        return null;
    }

    @Override
    public void onCreate() {
        TAG = this.getClass().getName();
        startActivity = this;

        super.onCreate();
    }

    public boolean[] hitTest(HeroIcon heroIcon, Block block) {
        boolean[] finalResult = new boolean[3];
        //두물체간 충돌 여부 판단
        //아래쪽이다.
        if ((heroIcon.y + (icon_height / 2)) > (initialPosY)) {
            finalResult[2] = false;
        }
        //위쪽이다.
        if ((heroIcon.y + (icon_height / 2)) < (initialPosY)) {
            finalResult[2] = true;
        }

        //오른쪽이다.
        if ((heroIcon.x + (icon_width / 2)) > (initialPosX)) {
            finalResult[1] = true;
        }

        //왼쪽이다.
        if ((heroIcon.x + (icon_width / 2)) < (initialPosX)) {
            finalResult[1] = false;
        }

        int me_x = Math.abs(heroIcon.x);
        int me_y = Math.abs(heroIcon.y);
        int me_width = heroIcon.width;
        int me_height = heroIcon.heigth;

        int target_x = Math.abs(block.x);
        int target_y = Math.abs(block.y);
        int target_width = block.width;
        int target_height = block.height;

        //Log.d(TAG,me_x + " "+me_y + " "+me_width + " "+me_height + " ");
        // Log.d(TAG,target_x + " "+target_y + " "+target_width + " "+target_height + " ");

        boolean result1 = (me_x >= target_x) && (me_x <= (target_x + target_width));//나의 x좌표위치가 타겟의 x range 내에 있는지 판단
        boolean result2 = (me_x + me_width >= target_x) && (me_x + me_width <= (target_x + target_width));  //나의 가로폭이 타겟의 가로폭 내에 있는지 판단

        boolean result3 = (me_y >= target_y) && (me_y <= (target_y + target_height));//나의 y좌표위치가 타겟의 세로폭 내에 있는지 판단
        boolean result4 = (me_y + me_height >= target_y) && (me_y + me_height <= (target_y + target_height));//나의 y폭이 타겟의 세로폭 내에 있는지 판단

        finalResult[0] = (result1 || result2) && (result3 || result4);
        // Log.d(TAG,finalResult[0]+" "+finalResult[1]+" "+finalResult[2]);
        return finalResult;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"나는 언제 실행되는것인가?");
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        //dm.widthPixels;
        limitY = (dm.heightPixels / 2) - 500;
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
        params2 = new WindowManager.LayoutParams(150, 150, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
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
                    //Toast.makeText(FloatingWindow.this, "원클릭", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(StartActivity.this, "롱클릭", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(StartActivity.this, "더블클릭", Toast.LENGTH_SHORT).show();

                    String sql = "select * from shortcut where short_cut=1";
                    Cursor rs = defaultAct.db.rawQuery(sql, null);

                    rs.moveToNext();
                    int method = rs.getInt(rs.getColumnIndex("method"));
                    if (method == START_PHONE_CALL) {
                        String number = rs.getString(rs.getColumnIndex("path"));
                        Toast.makeText(StartActivity.this, number, Toast.LENGTH_SHORT).show();
                        if (!number.equals(null))
                            callPhone(number);

                    } else if (method == START_APP_CALL) {
                        String number = rs.getString(rs.getColumnIndex("path"));
                        Toast.makeText(StartActivity.this, number, Toast.LENGTH_SHORT).show();
                        if (!number.equals(null)) {
                            Intent intent = getPackageManager().getLaunchIntentForPackage(number);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                    Toast.makeText(StartActivity.this, "원클릭", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "투명도: " + Float.toString(params2.alpha));
                    Log.d(TAG, "X좌표: " + Integer.toString(params2.x));
                    Log.d(TAG, "Y좌표: " + Integer.toString(params2.y));
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
                                windowManager.removeView(sub_li1);
                                windowManager.removeView(txt_turn);
                            }
                            if (windowManager != null) {
                                windowManager.removeView(sub_li2);
                                windowManager.removeView(txt_setting);
                            }
                            windowManager.removeView(bli);
                        }
                    });

                    sub_li1 = new LinearLayout(StartActivity.this);
                    txt_turn = new TextView(StartActivity.this);
                    LinearLayout.LayoutParams sub_liParameters1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    sub_li1.setBackgroundColor(Color.argb(66, 255, 0, 0));
                    sub_li1.setLayoutParams(sub_liParameters1);

                    txt_turn.setText("종료");

                    WindowManager.LayoutParams sub_parameters1 = new WindowManager.LayoutParams(150, 150, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                    WindowManager.LayoutParams txt_turn_parameters = new WindowManager.LayoutParams(150, 150, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

                    sub_parameters1.x = initialPosX;
                    if (initialPosY < -limitY) {
                        sub_parameters1.y = initialPosY + 200;
                    } else {
                        sub_parameters1.y = initialPosY - 200;
                    }

                    txt_turn_parameters.y = sub_parameters1.y;
                    txt_turn_parameters.x = sub_parameters1.x - 150 * 2;

                    sub_li1.setBackgroundResource(R.drawable.turn_on);
                    windowManager.addView(txt_turn, txt_turn_parameters);
                    windowManager.addView(sub_li1, sub_parameters1);

                    sub_li1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            Toast.makeText(StartActivity.this, "1번 클릭", Toast.LENGTH_SHORT).show();
                            stopSelf();
                            //stopService(new Intent(StartActivity.this, StartActivity.class));
                            if (windowManager != null) {
                                windowManager.removeView(sub_li1);
                                windowManager.removeView(txt_turn);

                            }
                            if (windowManager != null) {
                                windowManager.removeView(sub_li2);
                                windowManager.removeView(txt_setting);
                            }

                            windowManager.removeView(bli);
                        }
                    });


                    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    sub_li2 = new LinearLayout(StartActivity.this);
                    txt_setting = new TextView(StartActivity.this);
                    txt_setting.setText("설정");
                    LinearLayout.LayoutParams sub_liParameters2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    sub_li2.setBackgroundColor(Color.argb(66, 0, 255, 0));
                    sub_li2.setLayoutParams(sub_liParameters2);

                    WindowManager.LayoutParams sub_parameters2 = new WindowManager.LayoutParams(150, 150, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                    WindowManager.LayoutParams txt_setting_parameters = new WindowManager.LayoutParams(150, 150, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

                    sub_parameters2.x = initialPosX;
                    if (initialPosY < -limitY) {
                        sub_parameters2.y = initialPosY + 400;
                    } else {
                        sub_parameters2.y = initialPosY - 400;
                    }

                    txt_setting_parameters.y = sub_parameters2.y;
                    txt_setting_parameters.x = sub_parameters2.x - 150 * 2;
                    sub_li2.setBackgroundResource(R.drawable.setting);
                    windowManager.addView(txt_setting, txt_setting_parameters);
                    windowManager.addView(sub_li2, sub_parameters2);

                    sub_li2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /*--- 2번째 버튼 클릭 했을시 SettingActivity로 전환 */
                            Intent settingIntent = new Intent(StartActivity.this, SettingActivity.class);
                            settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(settingIntent);


                            if (windowManager != null) {
                                windowManager.removeView(sub_li1);
                                windowManager.removeView(txt_turn);
                            }
                            if (windowManager != null) {
                                windowManager.removeView(sub_li2);
                                windowManager.removeView(txt_setting);
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
                btnParameters1.x = iconX + 300;
                btnParameters1.y = iconY;


                block_right = new Block(StartActivity.this, btnParameters1.x, btnParameters1.y, 50, 250);
                windowManager.addView(block_right, btnParameters1);

                //좌측 버튼 트랩
                WindowManager.LayoutParams btnParameters2 = new WindowManager.LayoutParams(50, 250, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                btnParameters2.x = iconX - 300;
                btnParameters2.y = iconY;

                block_left = new Block(StartActivity.this, btnParameters2.x, btnParameters2.y, 50, 250);
                windowManager.addView(block_left, btnParameters2);

                //위쪽 버튼 트랩
                WindowManager.LayoutParams btnParameters3 = new WindowManager.LayoutParams(250, 50, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                btnParameters3.x = iconX;
                btnParameters3.y = iconY + 300;

                block_top = new Block(StartActivity.this, btnParameters3.x, btnParameters3.y, 250, 50);
                windowManager.addView(block_top, btnParameters3);

                //아래쪽 버튼 트랩
                WindowManager.LayoutParams btnParameters4 = new WindowManager.LayoutParams(250, 50, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                btnParameters4.x = iconX;
                btnParameters4.y = iconY - 300;

                block_bottom = new Block(StartActivity.this, btnParameters4.x, btnParameters4.y, 250, 50);
                windowManager.addView(block_bottom, btnParameters4);
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d(TAG,"무브");

                updatedParameters.x = (int) (iconX + (motionEvent.getRawX() - touchedX));
                updatedParameters.y = (int) (iconY + (motionEvent.getRawY() - touchedY));

                heroIcon.x = updatedParameters.x - icon_width / 2;
                heroIcon.y = updatedParameters.y - icon_height / 2;

                windowManager.updateViewLayout(heroIcon, updatedParameters);


                boolean[] result1 = hitTest(heroIcon, block_bottom);
                boolean[] result2 = hitTest(heroIcon, block_top);
                boolean[] result3 = hitTest(heroIcon, block_right);
                boolean[] result4 = hitTest(heroIcon, block_left);
                if (result1[0] && (result1[2] == true)) {
                    //Log.d(TAG,"위쪽");
                    gestureResult = "위쪽";
                    break;
                }
                if (result2[0] && (result2[2] == false)) {
                    //Log.d(TAG,"아래쪽");
                    gestureResult = "아래쪽";
                    break;
                }
                if (result3[0] && (result3[1] == true)) {
                    //Log.d(TAG,"오른쪽");
                    gestureResult = "오른쪽";
                    break;
                }
                if (result4[0] && (result4[1] == false)) {
                    //Log.d(TAG,"왼쪽");
                    gestureResult = "왼쪽";
                              /*  updatedParameters.x = initialPosX;
                                updatedParameters.y = initialPosY;
                                wm.updateViewLayout(heroIcon,updatedParameters);*/
                                /*btn_wm1.removeView(block_right);
                                btn_wm2.removeView(block_left);
                                btn_wm3.removeView(block_top);
                                btn_wm4.removeView(block_bottom);
                                stopSelf();
                                */
                    break;
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
                        Toast.makeText(StartActivity.this, "왼쪽", Toast.LENGTH_SHORT).show();
                        String sql = "select * from shortcut where short_cut=4";
                        Cursor rs = defaultAct.db.rawQuery(sql, null);

                        rs.moveToNext();
                        int method = rs.getInt(rs.getColumnIndex("method"));
                        if (method == START_PHONE_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            Toast.makeText(StartActivity.this, number, Toast.LENGTH_SHORT).show();
                            if (!number.equals(null))
                                callPhone(number);

                        } else if (method == START_APP_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            Toast.makeText(StartActivity.this, number, Toast.LENGTH_SHORT).show();
                            if (!number.equals(null)) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(number);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else if (method == START_WEB_CALL) {

                        }

                    } else if (gestureResult.equals("오른쪽")) {
                        Toast.makeText(StartActivity.this, "오른쪽", Toast.LENGTH_SHORT).show();
                        String sql = "select * from shortcut where short_cut=5";
                        Cursor rs = defaultAct.db.rawQuery(sql, null);
                        rs.moveToNext();
                        int method = rs.getInt(rs.getColumnIndex("method"));
                        if (method == START_PHONE_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            Toast.makeText(StartActivity.this, number, Toast.LENGTH_SHORT).show();
                            if (!number.equals(null))
                                callPhone(number);


                        } else if (method == START_APP_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            Toast.makeText(StartActivity.this, number, Toast.LENGTH_SHORT).show();
                            if (!number.equals(null)) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(number);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else if (method == START_WEB_CALL) {

                        }
                    } else if (gestureResult.equals("아래쪽")) {
                        Toast.makeText(StartActivity.this, "아래쪽", Toast.LENGTH_SHORT).show();
                        String sql = "select * from shortcut where short_cut=3";
                        Cursor rs = defaultAct.db.rawQuery(sql, null);
                        rs.moveToNext();
                        int method = rs.getInt(rs.getColumnIndex("method"));
                        if (method == START_PHONE_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            Toast.makeText(StartActivity.this, number, Toast.LENGTH_SHORT).show();
                            if (!number.equals(null))
                                callPhone(number);
                        } else if (method == START_APP_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            Toast.makeText(StartActivity.this, number, Toast.LENGTH_SHORT).show();
                            if (!number.equals(null)) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(number);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else if (method == START_WEB_CALL) {

                        }

                    } else if (gestureResult.equals("위쪽")) {
                        Toast.makeText(StartActivity.this, "위쪽", Toast.LENGTH_SHORT).show();
                        String sql = "select * from shortcut where short_cut=2";
                        Cursor rs = defaultAct.db.rawQuery(sql, null);
                        rs.moveToNext();
                        int method = rs.getInt(rs.getColumnIndex("method"));
                        if (method == START_PHONE_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            Toast.makeText(StartActivity.this, number, Toast.LENGTH_SHORT).show();
                            if (!number.equals(null))
                                callPhone(number);
                        } else if (method == START_APP_CALL) {
                            String number = rs.getString(rs.getColumnIndex("path"));
                            Toast.makeText(StartActivity.this, number, Toast.LENGTH_SHORT).show();
                            if (!number.equals(null)) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(number);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                        } else if (method == START_WEB_CALL) {

                        }

                        gestureResult = "";
                    }
                } else {
                    updatedParameters.x = (int) (iconX + (motionEvent.getRawX() - touchedX));
                    updatedParameters.y = (int) (iconY + (motionEvent.getRawY() - touchedY));
                    heroIcon.x = updatedParameters.x - icon_width / 2;
                    heroIcon.y = updatedParameters.y - icon_height / 2;
                    windowManager.updateViewLayout(heroIcon, updatedParameters);
                    initialPosX = updatedParameters.x;
                    initialPosY = updatedParameters.y;
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

