package com.wayto.track.data;

import com.wayto.track.service.data.LocationEntity;
import com.wayto.track.storage.TrackPointTable;

import java.util.List;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/24 11:53
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public interface TrackContract {

    interface TrackPanelView {

        void onCountDownViewVisibility(int visibility);

        void showCountDownViewNumber(int number);

        void onTrackPanelActionLayoutVisibility(int visibility);

        void onTrackStartButtonVisibility(int visibility);

        void onTrackContinueButtonVisibility(int visibility);

        void onTrackStopButtonVisibility(int visibility);

        void onTrackEndButtonVisibility(int visibility);

        void resetView();

        void onShowTrackDistance(String distance);

        void onShowTrackTime(String time);

        void onShowTrackSpeed(String speed);

        void onShowTrackGpsStatues(int status);

    }

    interface TrackMapView{
        void queryTrackPointTables(List<TrackPointTable> trackPointTables);

        void refreshLocationPoint(LocationEntity locationEntity);
    }

    interface Present {
        void onCheckTrack(long trackId);

        void onStartTrackGather(boolean startTimer);

        void onStopTrackGather();

        void onContinueTrackGather();

        void onEndTrackGater();

        void onQueryTrackPointTables(long trackId);
    }
}
