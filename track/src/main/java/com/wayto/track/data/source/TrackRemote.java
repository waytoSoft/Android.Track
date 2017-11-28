package com.wayto.track.data.source;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wayto.track.DataApplication;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.service.data.LocationEntity;
import com.wayto.track.utils.IStringUtils;

import java.text.DecimalFormat;

import static com.wayto.track.common.TrackConstant.TRACK_DISTANCE_KEY;
import static com.wayto.track.common.TrackConstant.TRACK_LOCATION_KEY;
import static com.wayto.track.common.TrackConstant.TRACK_SPEED_KEY;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/24 16:22
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackRemote implements TrackDataSource {

    private static TrackCallBack trackPanelCallBack;

    public TrackRemote(TrackCallBack callBack) {
        this.trackPanelCallBack = callBack;
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

    public static class TrackBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackConstant.TRACK_REFRESH_VIEW_BROCAST_ACTION)) {

                if (trackPanelCallBack == null)
                    return;

                double distance = intent.getDoubleExtra(TrackConstant.TRACK_DISTANCE_KEY, 0);
                trackPanelCallBack.getTrackDistance(new DecimalFormat("######0.00").format(distance / 1000));

                float speed = intent.getFloatExtra(TrackConstant.TRACK_SPEED_KEY, 0);
                trackPanelCallBack.onTrackSpeed(new DecimalFormat("######0.00").format(speed));


                LocationEntity locationEntity = (LocationEntity) intent.getSerializableExtra(TRACK_LOCATION_KEY);
                if (locationEntity != null) {
                    trackPanelCallBack.onTrackGpsStatues(locationEntity.getGpsAccuracyStatus());
                }
            }else if (intent.getAction().equals(TrackConstant.TRACK_REFRESH_DURATION_BROCAST_ACTION)){
                if (trackPanelCallBack == null)
                    return;

                long duration = intent.getLongExtra(TrackConstant.TRACK_DURATION_KEY, 0);
                trackPanelCallBack.onTrackTime(IStringUtils.showTimeCount(duration));
            }
        }
    }
}
