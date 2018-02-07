package com.wayto.track.data.source;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.wayto.track.DataApplication;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.service.data.LocationEntity;
import com.wayto.track.storage.TemTable;
import com.wayto.track.storage.TemTableDao;
import com.wayto.track.storage.TrackPointTable;
import com.wayto.track.storage.TrackPointTableDao;
import com.wayto.track.storage.TrackTable;
import com.wayto.track.storage.TrackTableDao;
import com.wayto.track.utils.IStringUtils;

import java.text.DecimalFormat;
import java.util.List;

import static com.wayto.track.common.TrackConstant.TRACK_LOCATION_KEY;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/24 16:22
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackRemote implements TrackDataSource {

    private TrackCallBack mTrackCallBack;

    private static TrackRemote install;

    private TrackBroadcastReceiver receiver;

    private double lat, lng;

    public TrackRemote() {
        registerReceiver(DataApplication.getInstance());
    }

    public static TrackRemote newInstall() {
        if (install == null)
            install = new TrackRemote();

        return install;
    }

    /**
     * 注册广播
     * <p>
     * author: hezhiWu
     * created at 2018/1/17 10:35
     */
    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TrackConstant.TRACK_REFRESH_DURATION_BROCAST_ACTION);
        filter.addAction(TrackConstant.TRACK_REFRESH_LOCATION_BROCAST_ACTION);

        receiver = new TrackBroadcastReceiver();

        try {
            context.registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 广播解绑
     * <p>
     * author: hezhiWu
     * created at 2018/1/17 10:35
     */
    private void unRegisterReceiver(Context context) {
        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartLocation(Context context) {
        DataApplication.getInstance().startLocation(context);
    }

    @Override
    public void onSetCallBack(TrackCallBack callBack) {
        mTrackCallBack = callBack;
    }

    @Override
    public void onCheckTrack(long trackId) {
        List<TrackTable> tables = DataApplication.getInstance().getDaoSession().getTrackTableDao()
                .queryBuilder()
                .where(TrackTableDao.Properties.Id.eq(trackId))
                .list();

        if (tables == null || tables.size() == 0)
            return;

        if (mTrackCallBack == null)
            return;

        TrackTable table = tables.get(0);

//        mTrackCallBack.getTrackStatus(table.getStatus());
        mTrackCallBack.getTrackDistance(new DecimalFormat("######0.00").format(table.getDistance() / 1000));
        mTrackCallBack.onTrackSpeed(new DecimalFormat("######0.00").format(table.getSpeed()));
        mTrackCallBack.onTrackTime(IStringUtils.showTimeCount(table.getDuration()));
    }

    @Override
    public void onStartTrackGather(Context context) {
        DataApplication.getInstance().startServiceGather(context);
    }

    @Override
    public void onStopTrackGather(Context context) {
        DataApplication.getInstance().stopServiceGather(context);
    }

    @Override
    public void onContinueTrackGather(Context context) {
        DataApplication.getInstance().contiuneServiceGather(context);
    }

    @Override
    public void onEndTrackGater(Context context) {
        DataApplication.getInstance().destroyServiceGather(context);
    }

    @Override
    public void onQueryTrackPoint(Context context, long trackId) {
        List<TrackPointTable> tables = DataApplication.getInstance().getDaoSession().getTrackPointTableDao()
                .queryBuilder()
                .where(TrackPointTableDao.Properties.TarckTableId.eq(trackId))
                .list();

        if (mTrackCallBack != null)
            mTrackCallBack.onQueryTrackPointTables(tables);
    }

    @Override
    public int queryTrackStatus(long trackId) {
        List<TrackTable> tables = DataApplication.getInstance().getDaoSession().getTrackTableDao()
                .queryBuilder()
                .where(TrackTableDao.Properties.Id.eq(trackId))
                .list();

        if (tables != null && tables.size() > 0) {
            return tables.get(0).getStatus();
        }

        return 0;
    }

    @Override
    public long insterTrackTable() {
        TrackTable table = new TrackTable();
        table.setStartLatitude(lat);
        table.setStartLongitude(lng);
        long id = DataApplication.getInstance().getDaoSession().getTrackTableDao().insert(table);
        return id;
    }

    @Override
    public void instertTem(long trackId, int trackStatus) {
        TemTable table = new TemTable();
        table.setTrackId(trackId);
        table.setTrackStatus(trackStatus);
        DataApplication.getInstance().getDaoSession().getTemTableDao().insert(table);
    }

    @Override
    public long queryTrackIdFromTem() {
        List<TemTable> tables = DataApplication.getInstance().getDaoSession().getTemTableDao().loadAll();
        if (tables != null && tables.size() > 0)
            return tables.get(0).getTrackId();

        return 0;
    }

    @Override
    public int queryTrackStatusFromTem() {
        List<TemTable> tables = DataApplication.getInstance().getDaoSession().getTemTableDao().loadAll();
        if (tables != null && tables.size() > 0)
            return tables.get(0).getTrackStatus();

        return 0;
    }

    @Override
    public void updateTem(long trackId, int trackStatus) {
        TemTable table = DataApplication.getInstance().getDaoSession().getTemTableDao()
                .queryBuilder()
                .where(TemTableDao.Properties.TrackId.eq(trackId))
                .build()
                .unique();

        if (table != null) {
            table.setTrackStatus(trackStatus);

            DataApplication.getInstance().getDaoSession().getTemTableDao().update(table);
        }
    }

    @Override
    public void deleteTem() {
        DataApplication.getInstance().getDaoSession().getTemTableDao().deleteAll();
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver(DataApplication.getInstance());
        install = null;
        receiver = null;
    }

    class TrackBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackConstant.TRACK_REFRESH_DURATION_BROCAST_ACTION)) {/*时长更新*/
                if (mTrackCallBack == null)
                    return;

                /*Duration*/
                long duration = intent.getLongExtra(TrackConstant.TRACK_DURATION_KEY, 0);
                mTrackCallBack.onTrackTime(IStringUtils.showTimeCount(duration));

                 /*GPS状态*/
                int gpsStatus = intent.getIntExtra(TrackConstant.TRACK_GPSSTATUS_KEY, AMapLocation.GPS_ACCURACY_BAD);
                mTrackCallBack.onTrackGpsStatues(gpsStatus);

                /*Distance*/
                double distance = intent.getDoubleExtra(TrackConstant.TRACK_DISTANCE_KEY, 0);
                mTrackCallBack.getTrackDistance(new DecimalFormat("######0.00").format(distance / 1000));

                 /*Speed*/
                float speed = intent.getFloatExtra(TrackConstant.TRACK_SPEED_KEY, 0);
                mTrackCallBack.onTrackSpeed(new DecimalFormat("######0.00").format(speed));

                double lat = intent.getDoubleExtra(TrackConstant.LAT, 0);
                double lng = intent.getDoubleExtra(TrackConstant.LNG, 0);
                mTrackCallBack.onDrawableTrackLine(lat, lng);

            } else if (intent.getAction().equals(TrackConstant.TRACK_REFRESH_LOCATION_BROCAST_ACTION)) {
                if (mTrackCallBack == null)
                    return;

                lat = intent.getDoubleExtra(TrackConstant.LAT, 0);
                lng = intent.getDoubleExtra(TrackConstant.LNG, 0);
                mTrackCallBack.onRefreshLocation(lat, lng);
            }
        }
    }
}
