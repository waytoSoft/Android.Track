package com.wayto.track.history.data.source;

import com.wayto.track.DataApplication;
import com.wayto.track.storage.TrackPointTable;
import com.wayto.track.storage.TrackPointTableDao;
import com.wayto.track.storage.TrackTable;

import java.util.List;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2018/1/8 11:06
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackHistoryRemote implements TrackHistoryDataSource {

    private static TrackHistoryRemote install;

    public TrackHistoryRemote() {

    }

    public static TrackHistoryRemote newInstall() {
        if (install == null)
            install = new TrackHistoryRemote();

        return install;
    }

    @Override
    public void queryTrackHistory(int index, int pageSize, QueryTrackHistoryCallBack callBack) {
        try {
            List<TrackTable> trackTables = DataApplication.getInstance().getDaoSession().getTrackTableDao().loadAll();
            callBack.onQueryTrackHistorySuccess(trackTables);
        } catch (Exception e) {
            callBack.onQueryTrackHistoryFailure(100, "query TrackTable failure");
        }
    }
}
