package com.wayto.track;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapFragment;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.wayto.track.common.NoticeEvent;
import com.wayto.track.common.TrackConstant;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 足迹地图模式
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/22 15:43
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackMapFragment extends MapFragment {

    @BindView(R.id.Track_MapView)
    MapView TrackMapView;
    Unbinder unbinder;

    private AMap mAMap;
    private MyLocationStyle locationStyle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_map, null);
        unbinder = ButterKnife.bind(this, rootView);
        TrackMapView.onCreate(savedInstanceState);

        if (mAMap == null) {
            mAMap = TrackMapView.getMap();
        }

        initMapView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        TrackMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        TrackMapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        TrackMapView.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        TrackMapView.onSaveInstanceState(outState);
    }

    /**
     * 初始化地图
     * <p>
     * author: hezhiWu
     * created at 2017/11/24 11:36
     */
    private void initMapView() {
        /*隐藏放大缩小按钮*/
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.getUiSettings().setRotateGesturesEnabled(false);
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);

        mAMap.setMyLocationEnabled(true);
        mAMap.setMapType(AMap.MAP_TYPE_NIGHT);

        locationStyle = new MyLocationStyle();
        locationStyle.showMyLocation(true);
        locationStyle.radiusFillColor(Color.parseColor("#00000000"));
        locationStyle.strokeColor(Color.parseColor("#00000000"));

        mAMap.setMyLocationStyle(locationStyle);

    }

    @OnClick({R.id.Track_Map_back_Layout, R.id.Track_Map_Location_Layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Track_Map_back_Layout:
                NoticeEvent event = new NoticeEvent();
                event.setWhat(TrackConstant.TRACK_PANEL_MODE);
                EventBus.getDefault().post(event);
                break;
            case R.id.Track_Map_Location_Layout:
                break;
        }
    }
}
