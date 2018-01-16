package com.wayto.track.history.data;

import com.wayto.track.data.BasePresenter;
import com.wayto.track.storage.TrackPointTable;
import com.wayto.track.storage.TrackTable;

import java.util.List;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2018/1/8 11:07
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public interface TrackPointContract {

    interface QueryTrackPointViwe{
        void queryTrackPointTablesSuccess(List<TrackPointTable> trackPointTables);

        void queryTrackPointTablesFailure(String msg);

        void drawableStartPoint(double lat, double lng);

        void drawableEndPoint(double lat, double lng);
    }

    interface Presenter extends BasePresenter{

        void queryTrackPoint(long trackId);
    }
}
