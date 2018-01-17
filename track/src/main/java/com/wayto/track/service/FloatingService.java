package com.wayto.track.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import com.wayto.track.DataApplication;
import com.wayto.track.R;
import com.wayto.track.TrackActivity;
import com.wayto.track.common.SharedPreferencesUtils;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.data.source.TrackRemote;
import com.wayto.track.utils.IStringUtils;

import butterknife.ButterKnife;

/**
 * 足迹悬浮框Service
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0.0
 * created at 2017/9/18 11:01
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class FloatingService extends Service {
    /**
     * 定义浮动窗口布局
     */
    private LinearLayout mlayout;
    /**
     * 悬浮窗控件
     */
    private ImageButton floatingActionButton;
    /**
     * 悬浮窗的布局
     */
    private WindowManager.LayoutParams wmParams;
    private LayoutInflater inflater;
    /**
     * 创建浮动窗口设置布局参数的对象
     */
    private WindowManager mWindowManager;

    /**
     * 添加悬浮Button
     * <p>
     * author: hezhiWu
     * created at 2017/9/19 17:15
     */
    public static void addFloatingButton(Context context) {
        Intent intent = new Intent(context, FloatingService.class);
        context.startService(intent);
    }

    /**
     * 移除悬浮Button
     * <p>
     * author: hezhiWu
     * created at 2017/9/19 17:16
     */
    public static void removeLoatingButton(Context context) {
        Intent intent = new Intent(context, FloatingService.class);
        context.stopService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long trackId = IStringUtils.toLong(SharedPreferencesUtils.getValue(DataApplication.getInstance(), TrackConstant.TRACK_ID_KEY, "").toString());
        int status = TrackRemote.newInstall().queryTrackStatus(trackId);

        if (status == TrackConstant.TRACK_START
                || status == TrackConstant.TRACK_CONTINUE
                || status == TrackConstant.TRACK_STOP) {

            initWindow();
            initFloating();

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mlayout != null) {
            // 移除悬浮窗口
            mWindowManager.removeView(mlayout);
            mlayout = null;
        }
    }


    /**
     * 初始化windowManager
     * <p>
     * author: hezhiWu
     * created at 2017/9/19 17:12
     */
    private void initWindow() {
        mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        wmParams = getParams(wmParams);
        // 悬浮窗默认显示以左上角为起始坐标
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        //悬浮窗的开始位置，因为设置的是从左上角开始，所以屏幕左上角是x=0;y=0
        DisplayMetrics dm = getResources().getDisplayMetrics();
        wmParams.x = dm.widthPixels - 20;
        wmParams.y = dm.heightPixels / 3;
        //得到容器，通过这个inflater来获得悬浮窗控件
        inflater = LayoutInflater.from(getApplication());

        if (mlayout != null) {
            // 移除悬浮窗口
            mWindowManager.removeView(mlayout);
            mlayout = null;
        }
        // 获取浮动窗口视图所在布局
        mlayout = (LinearLayout) inflater.inflate(R.layout.floating_layout, null);
//        FloatingActionButton button=new FloatingActionButton(this);
        // 添加悬浮窗的视图
        mWindowManager.addView(mlayout, wmParams);
    }

    /**
     * 对windowManager进行设置
     * <p>
     * author: hezhiWu
     * created at 2017/9/19 17:12
     */
    public WindowManager.LayoutParams getParams(WindowManager.LayoutParams wmParams) {
        wmParams = new WindowManager.LayoutParams();
        //设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
        //wmParams.type = LayoutParams.TYPE_PHON E;
        //wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        //wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        //设置可以显示在状态栏上
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return wmParams;
    }

    /**
     * 找到悬浮窗的图标，并且设置事件
     * 设置悬浮窗的点击、滑动事件
     * <p>
     * author: hezhiWu
     * created at 2017/9/19 17:11
     */
    private void initFloating() {
        floatingActionButton = ButterKnife.findById(mlayout, R.id.Track_FloatingButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrackActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        floatingActionButton.setOnTouchListener(new FloatingListener());
    }

    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
    private int mStartX, mStartY, mStopX, mStopY;
    private boolean isMove;//判断悬浮窗是否移动

    /**
     * FloatingButton OnTouch事件
     * <p>
     * author: hezhiWu <hezhi.woo@gmail.com>
     * version: V1.0.0
     * created at 2017/9/19 17:11
     * <p>
     * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
     */
    private class FloatingListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View arg0, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchCurrentX = (int) event.getRawX();
                    mTouchCurrentY = (int) event.getRawY();
                    wmParams.x += mTouchCurrentX - mTouchStartX;
                    wmParams.y += mTouchCurrentY - mTouchStartY;

                    if (mlayout != null) {
                        mWindowManager.updateViewLayout(mlayout, wmParams);
                    }

                    mTouchStartX = mTouchCurrentX;
                    mTouchStartY = mTouchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
                    mStopX = (int) event.getX();
                    mStopY = (int) event.getY();
                    //System.out.println("|X| = "+ Math.abs(mStartX - mStopX));
                    //System.out.println("|Y| = "+ Math.abs(mStartY - mStopY));
                    if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
                        isMove = true;
                    }
                    break;
            }
            return isMove;
        }
    }
}
