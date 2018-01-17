package com.wayto.track.history.data;

import android.annotation.SuppressLint;

import com.wayto.track.history.data.source.TrackHistoryDataSource;
import com.wayto.track.history.data.source.TrackHistoryRemote;
import com.wayto.track.storage.TrackTable;

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
public class TrackHistoryPresenter implements TrackHistoryContract.Presenter {

    private TrackHistoryContract.QueryTrackHistoryView queryTrackHistoryView;

    public TrackHistoryPresenter(TrackHistoryContract.QueryTrackHistoryView queryTrackHistoryView) {
        this.queryTrackHistoryView = checkNotNull(queryTrackHistoryView, "QueryTrackHistoryView be not null");
    }

    @Override
    public void dettach() {
        queryTrackHistoryView = null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void queryTrack(final int index, int pageSize) {
        TrackHistoryRemote.newInstall().queryTrackHistory(index, pageSize, new TrackHistoryDataSource.QueryTrackHistoryCallBack() {
            @Override
            public void onQueryTrackHistorySuccess(List<TrackTable> trackTables) {
                if (queryTrackHistoryView == null)
                    return;

                if (index == 0) {
                    if (trackTables != null && trackTables.size() > 0)
                        queryTrackHistoryView.onQueryTrackSuccess(trackTables);
                    else
                        queryTrackHistoryView.onQueryEmpty();

                    queryTrackHistoryView.onCloseDownRefresh();
                } else {
                    if (trackTables != null && trackTables.size() > 0)
                        queryTrackHistoryView.onQueryTrackMoreSuccess(trackTables);
                    else
                        queryTrackHistoryView.onLoadMoreComplete();

                    queryTrackHistoryView.onClosePullRefresh();
                }
            }

            @Override
            public void onQueryTrackHistoryFailure(int code, String msg) {
                if (index == 0) {
                    queryTrackHistoryView.onQueryEmpty();
                    queryTrackHistoryView.onCloseDownRefresh();
                } else {
                    queryTrackHistoryView.onLoadMoreComplete();
                    queryTrackHistoryView.onClosePullRefresh();
                }
            }
        });
    }
}
