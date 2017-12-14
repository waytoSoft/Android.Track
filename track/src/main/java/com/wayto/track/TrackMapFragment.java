package com.wayto.track;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapFragment;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.data.TrackContract;
import com.wayto.track.service.data.LocationEntity;
import com.wayto.track.storage.TrackPointTable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * 足迹地图模式
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/22 15:43
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
@SuppressLint("RestrictedApi")
public class TrackMapFragment extends MapFragment implements TrackContract.TrackMapView {
    private final String TAG = getClass().getSimpleName();

    @BindView(R.id.Track_MapView)
    MapView TrackMapView;
    Unbinder unbinder;

    private AMap mAMap;
    private MyLocationStyle locationStyle;

    //实线集合
    private List<PolylineOptions> mTrackSolidArrowLineList = new ArrayList<>();

    //虚线集合
    private List<PolylineOptions> mTrackDotLineList = new ArrayList<>();

    //临时点集合
    private List<LatLng> tempLatLngs = new ArrayList<>();

    //最后一个点
    private LatLng lastLatLng;

    private long trackId;

    private TrackContract.Presenter mPresenter;

    private Thread mThreed;

    public TrackMapFragment() {

    }

    public static TrackMapFragment newInstance() {
        return new TrackMapFragment();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        trackId = getArguments().getLong("trackId", 0);
    }

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

        mThreed = new Thread(new Runnable() {
            @Override
            public void run() {
                mPresenter.onQueryTrackPointTables(trackId);
            }
        });
        mThreed.start();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

        mPresenter.dettach();

        if (mThreed != null) {
            mThreed.interrupt();
        }
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
        mAMap.moveCamera(new CameraUpdateFactory().zoomTo(16));
    }

    @Override
    public void setmPresenter(TrackContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void queryTrackPointTables(List<TrackPointTable> trackPointTables) {
        if (trackPointTables == null || trackPointTables.size() == 0)
            return;

        lastLatLng = new LatLng(trackPointTables.get(trackPointTables.size() - 1).getLatitude(),
                trackPointTables.get(trackPointTables.size() - 1).getLongitude());

        createPolyline(trackPointTables);

        /*绘制实线*/
        for (PolylineOptions polyline : mTrackSolidArrowLineList) {
            addPolyLine(polyline);
        }

        /*绘制虚线*/
        for (PolylineOptions polyline : mTrackDotLineList) {
            addPolyLine(polyline);
        }

        Log.d(TAG, "trackTable size=" + trackPointTables.size());
    }

    @Override
    public void refreshLocationPoint(LocationEntity locationEntity) {
        if (lastLatLng == null) {
            lastLatLng = new LatLng(locationEntity.getLatitude(), locationEntity.getLongitude());
        } else {
            LatLng latLng = new LatLng(locationEntity.getLatitude(), locationEntity.getLongitude());

            tempLatLngs.add(lastLatLng);
            tempLatLngs.add(latLng);

            String tempColor = "#66FFFF";
            double distance = AMapUtils.calculateLineDistance(lastLatLng, latLng);
            if (distance > 1000) {
                tempColor = "#CCCCCC";
            }

            addPolyLine(createPolyLineOption(tempLatLngs, 10, Color.parseColor(tempColor)));
        }
    }

    @Override
    public void drawableStartPoint(double lat, double lng) {
        drawablePointMarker(lng, lat, R.mipmap.icon_track_start);
    }

    @Override
    public void removeMapView() {
        mAMap.clear();
    }

    @OnClick({R.id.Track_Map_back_Layout, R.id.Track_Map_Location_Layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Track_Map_back_Layout:
                mPresenter.onSwitchFragment(TrackConstant.TRACK_PANEL_FRAGMENT);
                break;
            case R.id.Track_Map_Location_Layout:
                break;
        }
    }

    /**
     * 创建点
     * <p>
     * author: hezhiWu
     * created at 2017/12/13 16:18
     */
    private void drawablePointMarker(double lng, double lat, int iconResId) {
        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(iconResId);
        markerOptions.position(latLng).icon(bitmapDescriptor);
        markerOptions.setFlat(true);//平贴地图设置为 true，面对镜头设置为 false
        markerOptions.draggable(false);//默认设置Marker不可拖动
        mAMap.addMarker(markerOptions);
    }

    /**
     * 创建线
     * <p>
     * author: hezhiWu
     * created at 2017/12/13 16:21
     */
    private void createPolyline(List<TrackPointTable> trackPointTables) {
        LatLng lastPoint = null;
         /*实线点集合*/
        List<LatLng> solidPoints = new ArrayList<>();
        /*虚线点集合*/
        List<LatLng> dotPoints = new ArrayList<>();

        for (TrackPointTable table : trackPointTables) {
            LatLng point = new LatLng(table.getLatitude(), table.getLongitude());

            if (lastPoint == null) {
                solidPoints.add(point);
            } else {
                double distance = AMapUtils.calculateLineDistance(lastPoint, point);
                if (distance > 1000) {

                    if (dotPoints.size() <= 0) {
                        dotPoints.add(lastPoint);
                    }

                    dotPoints.add(point);

                    PolylineOptions polylineOptions = createPolyLineOption(dotPoints, 10, Color.parseColor("#CCCCCC"));
                    mTrackDotLineList.add(polylineOptions);
                    solidPoints.clear();

                } else {
                    if (solidPoints.size() <= 0) {
                        solidPoints.add(lastPoint);
                    }

                    solidPoints.add(point);

                    PolylineOptions polylineOptions = createPolyLineOption(solidPoints, 10, Color.parseColor("#66FFFF"));
                    mTrackSolidArrowLineList.add(polylineOptions);
                    solidPoints.clear();
                }
            }

            lastPoint = point;
        }
    }

    /**
     * r
     * 创建线段
     * <p>
     * author: hezhiWu
     * created at 2017/12/13 16:22
     */
    public PolylineOptions createPolyLineOption(@NonNull List<LatLng> latLngs, int width, @ColorInt int color) {
        checkNotNull(latLngs, "latLngs cannot be null");

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngs)
                .width(width)
                .color(color);

        return polylineOptions;
    }

    /**
     * 添加线段
     * <p>
     * author: hezhiWu
     * created at 2017/12/13 16:49
     */
    public Polyline addPolyLine(@NonNull PolylineOptions polylineOptions) {
        Polyline polyline = mAMap.addPolyline(polylineOptions);
        return polyline;
    }
}
