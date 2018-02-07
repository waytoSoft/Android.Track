package com.wayto.track.data.source;

import android.content.Context;

import com.wayto.track.service.data.LocationEntity;
import com.wayto.track.storage.TrackPointTable;

import java.util.List;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/24 16:19
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public interface TrackDataSource {

    interface TrackCallBack {
        void getTrackDistance(String distance);

        void onTrackTime(String time);

        void onTrackSpeed(String speed);

        void onTrackGpsStatues(int status);

        void onQueryTrackPointTables(List<TrackPointTable> trackPointTables);

        void onDrawableTrackLine(double lat, double lng);

        void onRefreshLocation(double lat,double lng);

        void getTrackStatus(int status);
    }

    void onSetCallBack(TrackCallBack callBack);

    void onStartLocation(Context context);

    void onStartTrackGather(Context context);

    void onStopTrackGather(Context context);

    void onContinueTrackGather(Context context);

    void onEndTrackGater(Context context);

    void onQueryTrackPoint(Context context, long trackId);

    void onCheckTrack(long trackId);

    int queryTrackStatus(long trackId);

    long insterTrackTable();

    void instertTem(long trackId,int trackStatus);

    long queryTrackIdFromTem();

    int queryTrackStatusFromTem();

    void updateTem(long trackId,int trackStatus);

    void deleteTem();

    void onDestroy();
}
