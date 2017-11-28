package com.wayto.track.data;

import android.app.Activity;
import android.view.View;

import com.wayto.track.data.source.TrackDataSource;
import com.wayto.track.data.source.TrackRemote;
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

    private Activity mContext;
    private TrackContract.TrackPanelView mTrackPanelView;
    private TrackRemote mTrackRemote;

    private Timer mTimer;
    private TrackPanelTask trackPanelTask;
    private int indext = 3;

    public TrackPresent(Activity context, TrackContract.TrackPanelView trackPanelView) {
        this.mContext = context;
        this.mTrackPanelView = trackPanelView;
        this.mTrackRemote = new TrackRemote(this);
    }

    @Override
    public void onStartTrackGather() {
        if (mTrackPanelView == null)
            return;

        startTimer();
    }

    @Override
    public void onContinueTrackGather() {
        if (mTrackPanelView == null)
            return;

        mTrackPanelView.onTrackContinueButtonVisibility(View.GONE);
        mTrackPanelView.onTrackEndButtonVisibility(View.GONE);
        mTrackPanelView.onTrackStopButtonVisibility(View.VISIBLE);

        mTrackRemote.onContinueTrackGather(mContext);
    }

    @Override
    public void onStopTrackGather() {
        if (mTrackPanelView == null)
            return;

        mTrackPanelView.onTrackStopButtonVisibility(View.GONE);
        mTrackPanelView.onTrackContinueButtonVisibility(View.VISIBLE);
        mTrackPanelView.onTrackEndButtonVisibility(View.VISIBLE);

        mTrackRemote.onStopTrackGather(mContext);
    }

    @Override
    public void onEndTrackGater() {
        if (mTrackPanelView == null)
            return;

        mTrackPanelView.onTrackEndButtonVisibility(View.GONE);
        mTrackPanelView.onTrackContinueButtonVisibility(View.GONE);
        mTrackPanelView.onTrackStartButtonVisibility(View.VISIBLE);

        mTrackRemote.onEndTrackGater(mContext);
        mTrackPanelView.resetView();
    }

    @Override
    public void getTrackDistance(String distance) {
        mTrackPanelView.onShowTrackDistance(distance);
    }

    @Override
    public void onTrackTime(String time) {
        mTrackPanelView.onShowTrackTime(time);
    }

    @Override
    public void onTrackSpeed(String speed) {
        mTrackPanelView.onShowTrackSpeed(speed);
    }

    @Override
    public void onTrackGpsStatues(int status) {
        mTrackPanelView.onShowTrackGpsStatues(status);
    }

    @Override
    public void queryTrackPointTables(List<TrackPointTable> trackPointTables) {

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
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (indext == 0) {
                        cancelTimer();

                        if (mTrackPanelView != null) {
                            mTrackPanelView.onTrackStartButtonVisibility(View.GONE);
                            mTrackPanelView.onTrackStopButtonVisibility(View.VISIBLE);

                            mTrackPanelView.onCountDownViewVisibility(View.GONE);
                            mTrackPanelView.onTrackPanelActionLayoutVisibility(View.VISIBLE);
                        }
                    }

                    /*提前1秒调用,解决定时延迟问题*/
                    if (indext == 1 && mTrackRemote != null) {
                        mTrackRemote.onStartTrackGather(mContext);
                    }

                    mTrackPanelView.showCountDownViewNumber(indext);
                    indext--;
                }
            });
        }
    }
}
