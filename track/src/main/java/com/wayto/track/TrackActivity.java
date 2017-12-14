package com.wayto.track;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.wayto.track.common.SharedPreferencesUtils;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.data.TrackContract;
import com.wayto.track.data.TrackPresenter;
import com.wayto.track.storage.TrackTable;
import com.wayto.track.storage.TrackTableDao;
import com.wayto.track.utils.IStringUtils;

import java.util.List;

/**
 * 轨迹采集
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/22 15:39
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackActivity extends AppCompatActivity implements TrackContract.TrackMainView {
    private String TAG = getClass().getSimpleName();

    private TrackPanelFragment mTrackPanelFragment;
    private TrackMapFragment mTrackMapFragment;

    private TrackPresenter trackPresent;

    private int fragmentFlag = TrackConstant.TRACK_PANEL_FRAGMENT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wayto_track);

        /*获取TrackId,判断当前记录状态, 若状态结束，TrackId默认为0*/
        long trackId = IStringUtils.toLong(SharedPreferencesUtils.getValue(this, TrackConstant.TRACK_ID_KEY, "").toString());
        Log.d(TAG, "trackId=" + trackId);

        List<TrackTable> tables = DataApplication.getInstance().getDaoSession().getTrackTableDao()
                .queryBuilder()
                .where(TrackTableDao.Properties.Id.eq(trackId))
                .list();

        int status = -1;
        if (tables != null && tables.size() > 0) {
            TrackTable table = tables.get(0);
            status = table.getStatus();
            if (table.getStatus() == TrackConstant.TRACK_END) {
                trackId = 0;
            }
        }

        Bundle bundle = new Bundle();
        bundle.putLong("trackId", trackId);
        bundle.putInt("status", status);

        //Create the Fragment
        mTrackPanelFragment = TrackPanelFragment.newInstance();
        mTrackPanelFragment.setArguments(bundle);

        mTrackMapFragment = TrackMapFragment.newInstance();
        mTrackMapFragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .add(R.id.Track_FragmeLayout, mTrackPanelFragment)
                .add(R.id.Track_FragmeLayout, mTrackMapFragment)
                .hide(mTrackMapFragment)
                .show(mTrackPanelFragment)
                .commitAllowingStateLoss();

        //Create the Presenter
        trackPresent = new TrackPresenter(this, this, mTrackPanelFragment, mTrackMapFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        trackPresent.destroy();
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

    @Override
    public void onSwitchFragment(int flag) {
        fragmentFlag = flag;
        if (flag == TrackConstant.TRACK_PANEL_FRAGMENT) {
            switchFragment(mTrackPanelFragment, mTrackMapFragment);
        } else if (flag == TrackConstant.TRACK_MAP_FRAGMENT) {
            switchFragment(mTrackMapFragment, mTrackPanelFragment);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (fragmentFlag == TrackConstant.TRACK_MAP_FRAGMENT) {
                switchFragment(mTrackPanelFragment, mTrackMapFragment);
                fragmentFlag=TrackConstant.TRACK_PANEL_FRAGMENT;

                return true;
            } else
                this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
