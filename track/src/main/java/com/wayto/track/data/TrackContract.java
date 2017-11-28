package com.wayto.track.data;

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

    interface Present {
        void onStartTrackGather();

        void onStopTrackGather();

        void onContinueTrackGather();

        void onEndTrackGater();
    }
}
