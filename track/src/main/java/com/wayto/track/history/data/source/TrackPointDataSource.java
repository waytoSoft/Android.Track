package com.wayto.track.history.data.source;

import com.wayto.track.storage.TrackPointTable;

import java.util.List;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2018/1/8 11:02
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public interface TrackPointDataSource {

    interface QueryTrackPointCallBack{
        void onQueyTrackPointSuccess(List<TrackPointTable> trackPoints);
        void onQueryTrackPointFailure(String msg);
    }

    void queryTrackPoint(long trackId, QueryTrackPointCallBack callBack);
}
