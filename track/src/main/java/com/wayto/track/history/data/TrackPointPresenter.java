package com.wayto.track.history.data;

import android.annotation.SuppressLint;

import com.wayto.track.history.data.source.TrackPointDataSource;
import com.wayto.track.history.data.source.TrackPointRemote;
import com.wayto.track.storage.TrackPointTable;

import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2018/1/8 11:14
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
@SuppressLint("RestrictedApi")
public class TrackPointPresenter implements TrackPointContract.Presenter {

    private TrackPointContract.QueryTrackPointViwe queryTrackPointViwe;

    public TrackPointPresenter(TrackPointContract.QueryTrackPointViwe queryTrackPointViwe) {
        this.queryTrackPointViwe = checkNotNull(queryTrackPointViwe, "QueryTrackHistoryView be not null");
    }


    @Override
    public void dettach() {
        queryTrackPointViwe = null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void queryTrackPoint(long trackId) {
        TrackPointRemote.newInstall().queryTrackPoint(trackId, new TrackPointDataSource.QueryTrackPointCallBack() {
            @Override
            public void onQueyTrackPointSuccess(List<TrackPointTable> trackPoints) {
                if (queryTrackPointViwe == null)
                    return;

                if (trackPoints != null && trackPoints.size() > 0) {
                    queryTrackPointViwe.drawableStartPoint(trackPoints.get(0).getLatitude(), trackPoints.get(0).getLongitude());
                    queryTrackPointViwe.drawableEndPoint(trackPoints.get(trackPoints.size() - 1).getLatitude(), trackPoints.get(trackPoints.size() - 1).getLongitude());
                    queryTrackPointViwe.queryTrackPointTablesSuccess(trackPoints);
                } else {
                    queryTrackPointViwe.queryTrackPointTablesFailure("暂无数据");
                }
            }

            @Override
            public void onQueryTrackPointFailure(String msg) {
                if (queryTrackPointViwe != null)
                    queryTrackPointViwe.queryTrackPointTablesFailure(msg);
            }
        });
    }
}
