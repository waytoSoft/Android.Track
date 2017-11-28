package com.wayto.track;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.wayto.track.common.NoticeEvent;
import com.wayto.track.common.TrackConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 轨迹采集
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/22 15:39
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackActivity extends AppCompatActivity {

    private Fragment mTrackPanelFragment, mTrackMapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wayto_track);
        EventBus.getDefault().register(this);

        mTrackPanelFragment = new TrackPanelFragment();
        mTrackMapFragment = new TrackMapFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.Track_FragmeLayout, mTrackPanelFragment)
                .add(R.id.Track_FragmeLayout, mTrackMapFragment)
                .hide(mTrackMapFragment)
                .show(mTrackPanelFragment)
                .commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 界面切换
     * <p>
     * author: hezhiWu
     * created at 2017/11/24 10:35
     */
    private void switchFragment(Fragment showFragment, Fragment hideFragment) {
        getFragmentManager().beginTransaction()
                .show(showFragment)
                .hide(hideFragment)
                .commitAllowingStateLoss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoticeEventBus(NoticeEvent event) {
        if (event.getWhat() == TrackConstant.TRACK_MAP_MODE) {
            switchFragment(mTrackMapFragment, mTrackPanelFragment);
        } else if (event.getWhat() == TrackConstant.TRACK_PANEL_MODE) {
            switchFragment(mTrackPanelFragment, mTrackMapFragment);
        }
    }
}
