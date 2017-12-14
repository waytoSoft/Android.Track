package com.wayto.track;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.leakcanary.LeakCanary;
import com.wayto.track.common.SharedPreferencesUtils;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.service.LocationService;
import com.wayto.track.storage.DaoMaster;
import com.wayto.track.storage.DaoSession;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/21 14:58
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class DataApplication extends Application {

    private DaoSession mDaoSession;
    private SQLiteDatabase db;

    private static DataApplication instance;

    public static DataApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);


        initDB();
    }

    /**
     * 初始化数据库
     * <p>
     * author: hezhiWu
     * created at 2017/11/21 15:02
     */
    public void initDB() {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "Wayto_Track", null);
        db = openHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return db;
    }

    /**
     * 开户采集服务
     * <p>
     * author: hezhiWu
     * created at 2017/11/24 11:38
     */
    public void startServiceGather(Context context) {
        SharedPreferencesUtils.setValue(instance, TrackConstant.LOCATION_STATUS_KEY, TrackConstant.LOCATION_START_FLAG);

        Intent intent = new Intent(context, LocationService.class);
        context.startService(intent);
    }

    /**
     * 停止采集服务
     * <p>
     * author: hezhiWu
     * created at 2017/11/24 11:38
     */
    public void stopServiceGather(Context context) {
        SharedPreferencesUtils.setValue(instance, TrackConstant.LOCATION_STATUS_KEY, TrackConstant.LOCATION_STOP_FLAG);

        Intent intent = new Intent(context, LocationService.class);
        context.startService(intent);
    }

    /**
     * 继续采集服务
     * <p>
     * author: hezhiWu
     * created at 2017/11/27 20:12
     */
    public void contiuneServiceGather(Context context) {
        SharedPreferencesUtils.setValue(instance, TrackConstant.LOCATION_STATUS_KEY, TrackConstant.LOCATION_CONTINUE_FLAG);

        Intent intent = new Intent(context, LocationService.class);
        context.startService(intent);
    }

    /**
     * 注销采集服务
     * <p>
     * author: hezhiWu
     * created at 2017/11/24 11:46
     */
    public void destroyServiceGather(Context context) {
        SharedPreferencesUtils.setValue(instance, TrackConstant.LOCATION_STATUS_KEY, TrackConstant.LOCATION_DESTROY_FLAG);

        Intent intent = new Intent(context, LocationService.class);
        context.startService(intent);
    }
}
