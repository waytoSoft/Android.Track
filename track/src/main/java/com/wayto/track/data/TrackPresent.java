package com.wayto.track.data;

import android.app.Activity;
import android.view.View;

import com.wayto.track.common.TrackConstant;
import com.wayto.track.data.source.TrackDataSource;
import com.wayto.track.data.source.TrackRemote;
import com.wayto.track.service.data.LocationEntity;
import com.wayto.track.storage.TrackPointTable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/24 12:00
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackPresent implements TrackContract.Present, TrackDataSource.TrackCallBack {

    private Activity mActivity;
    private TrackContract.TrackPanelView mTrackPanelView;
    private TrackContract.TrackMapView mTrackMapView;
    private TrackRemote mTrackRemote;

    private Timer mTimer;
    private TrackPanelTask trackPanelTask;
    private int indext = 3;

    public TrackPresent(Activity activity, TrackContract.TrackPanelView trackPanelView) {
        this.mActivity = activity;
        this.mTrackPanelView = trackPanelView;
        this.mTrackRemote = new TrackRemote(this);
    }

    public TrackPresent(Activity activity, TrackContract.TrackMapView trackMapView) {
        this.mActivity = activity;
        this.mTrackMapView = trackMapView;
        if (mTrackMapView == null)
            this.mTrackRemote = new TrackRemote(this);
    }

    @Override
    public void onCheckTrack(long trackId) {
        if (mTrackRemote != null)
            mTrackRemote.onCheckTrack(trackId);
    }

    @Override
    public void getTrackStatus(int status) {
        if (mTrackPanelView == null)
            return;

        if (status == TrackConstant.TRACK_START) {
            mTrackPanelView.onTrackStartButtonVisibility(View.GONE);
            mTrackPanelView.onTrackStopButtonVisibility(View.VISIBLE);
            mTrackPanelView.onTrackContinueButtonVisibility(View.GONE);
            mTrackPanelView.onTrackEndButtonVisibility(View.GONE);
        } else if (status == TrackConstant.TRACK_STOP) {
            mTrackPanelView.onTrackStartButtonVisibility(View.GONE);
            mTrackPanelView.onTrackStopButtonVisibility(View.GONE);
            mTrackPanelView.onTrackContinueButtonVisibility(View.VISIBLE);
            mTrackPanelView.onTrackEndButtonVisibility(View.VISIBLE);
        } else if (status == TrackConstant.TRACK_CONTINUE) {
            mTrackPanelView.onTrackStartButtonVisibility(View.GONE);
            mTrackPanelView.onTrackStopButtonVisibility(View.VISIBLE);
            mTrackPanelView.onTrackContinueButtonVisibility(View.GONE);
            mTrackPanelView.onTrackEndButtonVisibility(View.GONE);
        } else if (status == TrackConstant.TRACK_END) {
            mTrackPanelView.onTrackStartButtonVisibility(View.VISIBLE);
            mTrackPanelView.onTrackStopButtonVisibility(View.GONE);
            mTrackPanelView.onTrackContinueButtonVisibility(View.GONE);
            mTrackPanelView.onTrackEndButtonVisibility(View.GONE);
        }
    }

    @Override
    public void onStartTrackGather(boolean startTimer) {
        if (mTrackPanelView == null)
            return;

        if (startTimer) {
            startTimer();
        } else {
            mTrackPanelView.onTrackStartButtonVisibility(View.GONE);
            mTrackPanelView.onTrackStopButtonVisibility(View.VISIBLE);
            mTrackPanelView.onTrackContinueButtonVisibility(View.GONE);
            mTrackPanelView.onTrackEndButtonVisibility(View.GONE);

            mTrackRemote.onStartTrackGather(mActivity);
        }
    }

    @Override
    public void onContinueTrackGather() {
        if (mTrackPanelView == null)
            return;

        mTrackPanelView.onTrackStartButtonVisibility(View.GONE);
        mTrackPanelView.onTrackContinueButtonVisibility(View.GONE);
        mTrackPanelView.onTrackEndButtonVisibility(View.GONE);
        mTrackPanelView.onTrackStopButtonVisibility(View.VISIBLE);

        mTrackRemote.onContinueTrackGather(mActivity);
    }

    @Override
    public void onStopTrackGather() {
        if (mTrackPanelView == null)
            return;

        mTrackPanelView.onTrackStartButtonVisibility(View.GONE);
        mTrackPanelView.onTrackStopButtonVisibility(View.GONE);
        mTrackPanelView.onTrackContinueButtonVisibility(View.VISIBLE);
        mTrackPanelView.onTrackEndButtonVisibility(View.VISIBLE);

        mTrackRemote.onStopTrackGather(mActivity);
    }

    @Override
    public void onEndTrackGater() {
        if (mTrackPanelView == null)
            return;

        mTrackPanelView.onTrackStopButtonVisibility(View.GONE);
        mTrackPanelView.onTrackEndButtonVisibility(View.GONE);
        mTrackPanelView.onTrackContinueButtonVisibility(View.GONE);
        mTrackPanelView.onTrackStartButtonVisibility(View.VISIBLE);

        mTrackRemote.onEndTrackGater(mActivity);
        mTrackPanelView.resetView();
    }

    @Override
    public void onQueryTrackPointTables(long trackId) {
        if (mTrackRemote == null)
            return;

        mTrackRemote.onQueryTrackPoint(mActivity, trackId);
    }

    @Override
    public void getTrackDistance(String distance) {
        if (mTrackPanelView == null)
            return;

        mTrackPanelView.onShowTrackDistance(distance);
    }

    @Override
    public void onTrackTime(String time) {
        if (mTrackPanelView == null)
            return;

        mTrackPanelView.onShowTrackTime(time);
    }

    @Override
    public void onTrackSpeed(String speed) {
        if (mTrackPanelView == null)
            return;

        mTrackPanelView.onShowTrackSpeed(speed);
    }

    @Override
    public void onTrackGpsStatues(int status) {
        if (mTrackPanelView == null)
            return;

        mTrackPanelView.onShowTrackGpsStatues(status);
    }

    @Override
    public void onQueryTrackPointTables(List<TrackPointTable> trackPointTables) {
        if (mTrackMapView == null)
            return;

        mTrackMapView.queryTrackPointTables(trackPointTables);
    }

    @Override
    public void onRefreshLocationPoint(LocationEntity entity) {
        if (mTrackMapView == null)
            return;

        mTrackMapView.refreshLocationPoint(entity);
    }

    /**
     * 开始计时器
     * <p>
     * author: hezhiWu
     * created at 2017/9/10 上午11:23
     */
    private void startTimer() {
        if (mTimer == null)
            mTimer = new Timer();

        if (trackPanelTask == null) {
            trackPanelTask = new TrackPanelTask();

            if (mTrackPanelView != null) {
                mTrackPanelView.onCountDownViewVisibility(View.VISIBLE);
                mTrackPanelView.onTrackPanelActionLayoutVisibility(View.GONE);
            }

            mTimer.schedule(trackPanelTask, 0, 1000);
        }
    }

    /**
     * 取消定时器
     * <p>
     * author: hezhiWu
     * created at 2017/9/10 上午11:27
     */
    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (trackPanelTask != null) {
            trackPanelTask.cancel();
            trackPanelTask = null;
        }
        indext = 3;
    }


    class TrackPanelTask extends TimerTask {
        @Override
        public void run() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (indext == 0) {
                        cancelTimer();

                        if (mTrackPanelView != null) {
                            mTrackPanelView.onTrackStartButtonVisibility(View.GONE);
                            mTrackPanelView.onTrackStopButtonVisibility(View.VISIBLE);
                            mTrackPanelView.onTrackContinueButtonVisibility(View.GONE);
                            mTrackPanelView.onTrackEndButtonVisibility(View.GONE);

                            mTrackPanelView.onCountDownViewVisibility(View.GONE);
                            mTrackPanelView.onTrackPanelActionLayoutVisibility(View.VISIBLE);
                        }
                    }

                    /*提前1秒调用,解决定时延迟问题*/
                    if (indext == 1 && mTrackRemote != null) {
                        mTrackRemote.onStartTrackGather(mActivity);
                    }

                    mTrackPanelView.showCountDownViewNumber(indext);
                    indext--;
                }
            });
        }
    }
}
