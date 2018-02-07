package com.wayto.track.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.wayto.track.common.TrackConstant;
import com.wayto.track.data.source.TrackDataSource;
import com.wayto.track.data.source.TrackRemote;
import com.wayto.track.service.data.LocationEntity;
import com.wayto.track.storage.TrackPointTable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/24 12:00
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
@SuppressLint("RestrictedApi")
public class TrackPresenter implements TrackContract.Presenter, TrackDataSource.TrackCallBack {

    private Activity mActivity;

    private TrackContract.TrackMainView mTrackMainView;
    private TrackContract.TrackPanelView mTrackPanelView;
    private TrackContract.TrackMapView mTrackMapView;
    private TrackRemote mTrackRemote;

    private Timer mTimer;
    private TrackPanelTask trackPanelTask;
    private int indext = 3;

    private double startPointLng, startPointLat;

    private int status = -1;

    public TrackPresenter(Activity activity, @NonNull TrackContract.TrackMainView trackMainView, @NonNull TrackContract.TrackPanelView trackPanelView, @NonNull TrackContract.TrackMapView trackMapView) {
        mActivity = checkNotNull(activity);
        mTrackMainView = checkNotNull(trackMainView, "trackMainView cannot be null");
        mTrackPanelView = checkNotNull(trackPanelView, "trackPanelView cannot be null");
        mTrackMapView = checkNotNull(trackMapView, "trackMapView cannot be null");

        mTrackPanelView.setmPresenter(this);
        mTrackMapView.setmPresenter(this);

        mTrackRemote = TrackRemote.newInstall();
        mTrackRemote.onSetCallBack(this);
    }

    @Override
    public void dettach() {
        mTrackPanelView = null;
        mTrackMapView = null;
    }

    @Override
    public void destroy() {
        mTrackRemote.onDestroy();
        mActivity = null;
        startPointLng = 0;
        startPointLat = 0;
    }

    @Override
    public void onSwitchFragment(int flag) {
        if (mTrackMainView == null)
            return;

        mTrackMainView.onSwitchFragment(flag);
    }

    @Override
    public void onStartLocation() {
        mTrackRemote.onStartLocation(mActivity);
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

        if (mActivity == null || mActivity.isFinishing())
            return;

        status = TrackConstant.TRACK_START;

        /*保存起点*/
        long trackId=mTrackRemote.insterTrackTable();

        /*保存临时点*/
        mTrackRemote.instertTem(trackId,status);

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

        if (mActivity == null || mActivity.isFinishing())
            return;

        status = TrackConstant.TRACK_CONTINUE;

        /*更新临时表状态*/
        long trackId=mTrackRemote.queryTrackIdFromTem();

        mTrackRemote.updateTem(trackId,status);

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

        if (mActivity == null || mActivity.isFinishing())
            return;

        status = TrackConstant.TRACK_STOP;

        /*更新临时表状态*/
        long trackId=mTrackRemote.queryTrackIdFromTem();

        mTrackRemote.updateTem(trackId,status);

        mTrackPanelView.onTrackStartButtonVisibility(View.GONE);
        mTrackPanelView.onTrackStopButtonVisibility(View.GONE);
        mTrackPanelView.onTrackContinueButtonVisibility(View.VISIBLE);
        mTrackPanelView.onTrackEndButtonVisibility(View.VISIBLE);

        mTrackRemote.onStopTrackGather(mActivity);

        //reset Data
        startPointLat = 0;
        startPointLng = 0;
    }

    @Override
    public void onEndTrackGater() {
        if (mTrackPanelView == null)
            return;

        if (mActivity == null || mActivity.isFinishing())
            return;

        status = TrackConstant.TRACK_END;

        /*删除临时表*/
//        mTrackRemote.deleteTem();
         /*更新临时表状态*/
        long trackId=mTrackRemote.queryTrackIdFromTem();

        mTrackRemote.updateTem(trackId,status);

        mTrackPanelView.onTrackStopButtonVisibility(View.GONE);
        mTrackPanelView.onTrackEndButtonVisibility(View.GONE);
        mTrackPanelView.onTrackContinueButtonVisibility(View.GONE);
        mTrackPanelView.onTrackStartButtonVisibility(View.VISIBLE);

        mTrackRemote.onEndTrackGater(mActivity);
        mTrackPanelView.resetView();

        if (mTrackMapView != null)
            mTrackMapView.removeMapView();
    }

    @Override
    public void onQueryTrackPointTables(long trackId) {
        if (mTrackRemote == null)
            return;

        if (mActivity == null || mActivity.isFinishing())
            return;

        mTrackRemote.onQueryTrackPoint(mActivity, trackId);
    }

    @Override
    public void getTrackDistance(String distance) {
        if (mTrackPanelView == null)
            return;

        if (mActivity == null || mActivity.isFinishing())
            return;

        mTrackPanelView.onShowTrackDistance(distance);
    }

    @Override
    public void onTrackTime(String time) {
        if (mTrackPanelView == null)
            return;

        if (mActivity == null || mActivity.isFinishing())
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

        if (trackPointTables != null && trackPointTables.size() > 0) {
            startPointLat = trackPointTables.get(0).getLatitude();
            startPointLng = trackPointTables.get(0).getLongitude();
        }

        if (startPointLat > 0 && startPointLng > 0) {
            mTrackMapView.drawableStartPoint(startPointLat, startPointLng);
        }

        mTrackMapView.queryTrackPointTables(trackPointTables);
    }

    @Override
    public void onDrawableTrackLine(double lat, double lng) {
        if (mTrackMapView == null)
            return;

        if (startPointLng == 0 && startPointLat == 0) {
            if (lat > 0 && lng > 0) {
                startPointLng = lng;
                startPointLat = lat;
            }

            if (status == TrackConstant.TRACK_START
                    && startPointLng > 0
                    && startPointLat > 0) {
                mTrackMapView.drawableStartPoint(startPointLat, startPointLng);
            }
        }

        mTrackMapView.drawableTrackLine(lat, lng);
    }

    @Override
    public void onRefreshLocation(double lat, double lng) {
        if (mTrackMapView == null)
            return;

        mTrackMapView.refreshLocation(lat, lng);
    }

    /**
     * 开始计时器
     * <p>
     * author: hezhiWu
     * created at 2017/9/10 上午11:23
     */
    private void startTimer() {
        if (mActivity == null || mActivity.isFinishing())
            return;

        if (mTimer == null)
            mTimer = new Timer();

        if (trackPanelTask == null) {
            trackPanelTask = new TrackPanelTask();

            if (mTrackPanelView != null) {
                mTrackPanelView.onCountDownViewVisibility(View.VISIBLE);
                mTrackPanelView.onTrackPanelActionLayoutVisibility(View.GONE);
            }
        }

        mTimer.schedule(trackPanelTask, 0, 1000);
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

    /**
     * Create TimerTask
     * <p>
     * author: hezhiWu
     * created at 2017/12/13 11:28
     */
    class TrackPanelTask extends TimerTask {
        @Override
        public void run() {
            if (indext == 0 && mTrackRemote != null) {
                mTrackRemote.onStartTrackGather(mActivity);
            }

            //关闭倒记时
            if (indext == 0) {
                cancelTimer();

                if (mTrackPanelView != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTrackPanelView.onTrackStartButtonVisibility(View.GONE);
                            mTrackPanelView.onTrackStopButtonVisibility(View.VISIBLE);
                            mTrackPanelView.onTrackContinueButtonVisibility(View.GONE);
                            mTrackPanelView.onTrackEndButtonVisibility(View.GONE);

                            mTrackPanelView.onCountDownViewVisibility(View.GONE);
                            mTrackPanelView.onTrackPanelActionLayoutVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTrackPanelView.showCountDownViewNumber(indext);
                }
            });

            indext--;
        }
    }
}
