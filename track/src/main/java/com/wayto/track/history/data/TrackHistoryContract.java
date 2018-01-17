package com.wayto.track.history.data;

import com.wayto.track.data.BasePresenter;
import com.wayto.track.storage.TrackTable;

import java.util.List;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2018/1/8 11:07
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public interface TrackHistoryContract {

    interface QueryTrackHistoryView{
        void onQueryTrackSuccess(List<TrackTable> trackTables);

        void onQueryTrackMoreSuccess(List<TrackTable> trackTables);

        void onQueryTrackFailure(String msg);

        void onQueryEmpty();

        void onCloseDownRefresh();

        void onClosePullRefresh();

        void onLoadMoreComplete();
    }

    interface Presenter extends BasePresenter{
        void queryTrack(int index,int pageSize);
    }
}
