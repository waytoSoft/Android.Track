package com.wayto.track.history.data.source;

import com.wayto.track.provider.TrackData;
import com.wayto.track.storage.TrackPointTable;
import com.wayto.track.storage.TrackTable;

import java.util.List;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2018/1/8 11:02
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public interface TrackHistoryDataSource {

    interface QueryTrackHistoryCallBack{
        void onQueryTrackHistorySuccess(List<TrackTable> trackTables);
        void onQueryTrackHistoryFailure(int code,String msg);
    }

    void queryTrackHistory(int index,int pageSize,QueryTrackHistoryCallBack callBack);
}
