package com.wayto.track.history.data.source;

import com.wayto.track.DataApplication;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.storage.TrackTable;
import com.wayto.track.storage.TrackTableDao;

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
            List<TrackTable> trackTables = DataApplication.getInstance().getDaoSession().getTrackTableDao()
                    .queryBuilder()
                    .where(TrackTableDao.Properties.Distance.gt(10))
                    .where(TrackTableDao.Properties.Status.eq(TrackConstant.TRACK_END))
                    .list();

            callBack.onQueryTrackHistorySuccess(trackTables);
        } catch (Exception e) {
            callBack.onQueryTrackHistoryFailure(100, "query TrackTable failure");
        }
    }
}
