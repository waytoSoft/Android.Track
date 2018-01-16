package com.wayto.track.data.source;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.PolylineOptions;
import com.wayto.track.DataApplication;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.service.data.LocationEntity;
import com.wayto.track.storage.TrackPointTable;
import com.wayto.track.storage.TrackPointTableDao;
import com.wayto.track.storage.TrackTable;
import com.wayto.track.storage.TrackTableDao;
import com.wayto.track.utils.IStringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
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

    private static TrackCallBack trackCallBack;



    public TrackRemote(TrackCallBack callBack) {
        this.trackCallBack = callBack;
    }

    @Override
    public void onCheckTrack(long trackId) {
        List<TrackTable> tables=DataApplication.getInstance().getDaoSession().getTrackTableDao()
                .queryBuilder()
                .where(TrackTableDao.Properties.Id.eq(trackId))
                .list();

        if (tables==null || tables.size()==0)
            return;

        if (trackCallBack==null)
            return;

        TrackTable table=tables.get(0);

//        trackCallBack.getTrackStatus(table.getStatus());
        trackCallBack.getTrackDistance(new DecimalFormat("######0.00").format(table.getDistance() / 1000));
        trackCallBack.onTrackSpeed(new DecimalFormat("######0.00").format(table.getSpeed()));
        trackCallBack.onTrackTime(IStringUtils.showTimeCount(table.getDuration()));
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

        if (trackCallBack != null)
            trackCallBack.onQueryTrackPointTables(tables);
    }

    public static class TrackBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackConstant.TRACK_REFRESH_VIEW_BROCAST_ACTION)) {

                if (trackCallBack == null)
                    return;

                /*Distance*/
                double distance = intent.getDoubleExtra(TrackConstant.TRACK_DISTANCE_KEY, 0);
                trackCallBack.getTrackDistance(new DecimalFormat("######0.00").format(distance / 1000));

                /*Speed*/
                float speed = intent.getFloatExtra(TrackConstant.TRACK_SPEED_KEY, 0);
                trackCallBack.onTrackSpeed(new DecimalFormat("######0.00").format(speed));

                /*LocationEntity*/
                LocationEntity locationEntity = (LocationEntity) intent.getSerializableExtra(TRACK_LOCATION_KEY);
                if (locationEntity != null) {
                    trackCallBack.onTrackGpsStatues(locationEntity.getGpsAccuracyStatus());
                    trackCallBack.onRefreshLocationPoint(locationEntity);
                }
            } else if (intent.getAction().equals(TrackConstant.TRACK_REFRESH_DURATION_BROCAST_ACTION)) {/*时长更新*/
                if (trackCallBack == null)
                    return;

                /*Duration*/
                long duration = intent.getLongExtra(TrackConstant.TRACK_DURATION_KEY, 0);
                int gpsStatus=intent.getIntExtra(TrackConstant.TRACK_GPSSTATUS_KEY, AMapLocation.GPS_ACCURACY_BAD);
                trackCallBack.onTrackTime(IStringUtils.showTimeCount(duration));
                trackCallBack.onTrackGpsStatues(gpsStatus);
            }
        }
    }
}
