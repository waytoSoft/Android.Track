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
public class TrackPointRemote implements TrackPointDataSource {

    private static TrackPointRemote install;

    public TrackPointRemote() {

    }

    public static TrackPointRemote newInstall() {
        if (install == null)
            install = new TrackPointRemote();

        return install;
    }

    @Override
    public void queryTrackPoint(long trackId, QueryTrackPointCallBack callBack) {
        try {
            List<TrackPointTable> tables = DataApplication.getInstance().getDaoSession().getTrackPointTableDao()
                    .queryBuilder()
                    .where(TrackPointTableDao.Properties.TarckTableId.eq(trackId))
                    .list();

            callBack.onQueyTrackPointSuccess(tables);
        } catch (Exception e) {
            callBack.onQueryTrackPointFailure("query TrackPointTable failure");
        }

    }
}
