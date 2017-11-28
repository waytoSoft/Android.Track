package com.wayto.track.data.source;

import android.content.Context;

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

        void queryTrackPointTables(List<TrackPointTable> trackPointTables);
    }

    void onStartTrackGather(Context context);

    void onStopTrackGather(Context context);

    void onContinueTrackGather(Context context);

    void onEndTrackGater(Context context);
}
