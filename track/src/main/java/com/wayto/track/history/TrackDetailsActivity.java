package com.wayto.track.history;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.wayto.track.R;
import com.wayto.track.history.data.TrackPointContract;
import com.wayto.track.history.data.TrackPointPresenter;
import com.wayto.track.storage.TrackPointTable;
import com.wayto.track.widget.SwipeBackLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * 足迹详情
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2018/1/8 17:01
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
@SuppressLint("RestrictedApi")
public class TrackDetailsActivity extends AppCompatActivity implements TrackPointContract.QueryTrackPointViwe {

    @BindView(R.id.TrackDetails_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.TrackDetails_MapView)
    MapView mMapView;

    private ActionBar mActionBar;

    private SwipeBackLayout swipeBackLayout;

    private AMap mAMap;

    private TrackPointPresenter mPresenter;

    //实线集合
    private List<PolylineOptions> mTrackSolidArrowLineList = new ArrayList<>();

    //虚线集合
    private List<PolylineOptions> mTrackDotLineList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);
        ButterKnife.bind(this);

        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();

        swipeBackLayout = new SwipeBackLayout(this);
        swipeBackLayout.replaceLayer(this);

        //init Toolbar
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initMapView();

        mPresenter = new TrackPointPresenter(this);
        mPresenter.queryTrackPoint(getIntent().getLongExtra("id", 0));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.slide_out_right);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 初始化地图
     * <p>
     * author: hezhiWu
     * created at 2018/1/9 15:56
     */
    private void initMapView() {
        /*隐藏放大缩小按钮*/
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.getUiSettings().setRotateGesturesEnabled(false);
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);

//        mAMap.setMapType(AMap.MAP_TYPE_NIGHT);
    }

    @Override
    public void queryTrackPointTablesSuccess(List<TrackPointTable> trackPointTables) {
        int size = trackPointTables.size();
        LatLng marker = new LatLng(trackPointTables.get(size / 2).getLatitude(), trackPointTables.get(size / 2).getLongitude());
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(marker));
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        createPolyline(trackPointTables);

        /*绘制实线*/
        for (PolylineOptions polyline : mTrackSolidArrowLineList) {
            addPolyLine(polyline);
        }

        /*绘制虚线*/
        for (PolylineOptions polyline : mTrackDotLineList) {
            addPolyLine(polyline);
        }
    }

    @Override
    public void queryTrackPointTablesFailure(String msg) {

    }

    @Override
    public void drawableEndPoint(double lat, double lng) {
        drawablePointMarker(lng, lat, R.mipmap.icon_track_end);
    }

    @Override
    public void drawableStartPoint(double lat, double lng) {
        drawablePointMarker(lng, lat, R.mipmap.icon_track_start);
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

                    PolylineOptions polylineOptions = createPolyLineOption(solidPoints, 10, Color.parseColor("#00BFFF"));
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
