package com.wayto.track.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.wayto.track.common.SharedPreferencesUtils;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.service.data.LocationEntity;
import com.wayto.track.utils.IUtils;

/**
 * 定位服务[独立进程]
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/24 10:53
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class LocationService extends Service implements AMapLocationListener {
    private final String TAG = "LocationService";

    /*定位Client*/
    private AMapLocationClient mLocationClient;

    /*默认定位间隙-1秒*/
    private long defalut_location_Interval = 1 * 1000;

    private static TrackHelper mTrackHelper;
    private static long trackId = -1;
    private double mLastLng;
    private double mLastLat;
    private double mDistance;
    private boolean isFirstLocation = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTrackHelper = new TrackHelper(this);

        initLocationClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            int trackState = Integer.parseInt(SharedPreferencesUtils.getValue(this, TrackConstant.LOCATION_STATUS_KEY, -1).toString());
            if (trackState == TrackConstant.LOCATION_START_FLAG) {/*开始*/
                if (mTrackHelper == null)
                    mTrackHelper = new TrackHelper(this);

                //1、创建一条足迹记录[解决定位延时，计算时间不准备问题]
                if (trackId <= 0) {
                    trackId = mTrackHelper.instertTrack(0, 0);

                    isFirstLocation = true;
                }

                //2、启动定位
                startLocation();

                //3、开户定时提醒
                IUtils.setAlarm(this, (AlarmManager) getSystemService(ALARM_SERVICE), TrackConstant.TRACK_ALARM_ACTION, 1000);

            } else if (trackState == TrackConstant.LOCATION_STOP_FLAG) {/*停止*/
                stopLocation();

                if (mTrackHelper != null) {
                    mTrackHelper.updateTrackStatus(trackId, TrackConstant.TRACK_STOP);
                    mTrackHelper.clearLocationTime();
                }

                /*关闭定时提醒*/
                IUtils.cancelAlarm(this, (AlarmManager) getSystemService(ALARM_SERVICE), TrackConstant.TRACK_ALARM_ACTION);

            } else if (trackState == TrackConstant.LOCATION_CONTINUE_FLAG) {/*继续*/
                startLocation();

                if (mTrackHelper != null) {
                    mTrackHelper.updateTrackStatus(trackId, TrackConstant.TRACK_CONTINUE);
                }

                /*开启定时提醒*/
                IUtils.setAlarm(this, (AlarmManager) getSystemService(ALARM_SERVICE), TrackConstant.TRACK_ALARM_ACTION, 1000);

            } else if (trackState == TrackConstant.LOCATION_DESTROY_FLAG) {/*结束*/
                destroyLocation();

                if (mTrackHelper != null) {
                    mTrackHelper.updateTrackStatus(trackId, TrackConstant.TRACK_END);
                }

                /*关闭定时提醒*/
                IUtils.cancelAlarm(this, (AlarmManager) getSystemService(ALARM_SERVICE), TrackConstant.TRACK_ALARM_ACTION);

                /*注销数据*/
                destoryTrackData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "ServiceGather Exception", Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化LocationClient
     * <p>
     * author: hezhiWu
     * created at 2017/11/24 10:58
     */
    private void initLocationClient() {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getApplicationContext());

            AMapLocationClientOption locationClientOption = new AMapLocationClientOption();

            /*定位模式-高精度*/
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            /*是否返回地址信息*/
            locationClientOption.setNeedAddress(true);

            /*是否使用设备传感器 */
            locationClientOption.setSensorEnable(true);

            /*发起定位请求的时间间隔*/
            locationClientOption.setInterval(defalut_location_Interval);

            mLocationClient.setLocationOption(locationClientOption);
            mLocationClient.setLocationListener(this);
        }
    }

    /**
     * 启动定位
     * <p>
     * author: hezhiWu
     * created at 2017/9/1 10:49
     */
    private void startLocation() {
        if (mLocationClient == null) {
            initLocationClient();
        }

        if (!mLocationClient.isStarted()) {
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     * <p>
     * author: hezhiWu
     * created at 2017/9/1 10:49
     */
    private void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient = null;
        }
    }

    /**
     * 注销定位
     * <p>
     * author: hezhiWu
     * created at 2017/11/24 11:44
     */
    private void destroyLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    /**
     * 注销数据
     * <p>
     * author: hezhiWu
     * created at 2017/11/28 9:32
     */
    private void destoryTrackData() {
        if (mTrackHelper != null)
            mTrackHelper.destoryData();

        trackId = -1;
        mTrackHelper = null;
        mLastLng = 0;
        mLastLat = 0;
        mDistance = 0;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            recordTrack(aMapLocation);
        }
    }

    /**
     * 记录足迹采集
     * <p>
     * author: hezhiWu
     * created at 2017/11/27 17:07
     */
    private void recordTrack(AMapLocation location) {
        if (mTrackHelper == null) {
            mTrackHelper = new TrackHelper(this);
        }

        if (location == null)
            return;

        /*处理第一次定位更新起始经纬度*/
        if (isFirstLocation) {
            mTrackHelper.updateTrack(trackId, location.getLongitude(), location.getLatitude());
            isFirstLocation = false;
        }

        //1、定位点与上次的距离大于0
        if (mLastLng > 0 && mLastLng > 0) {
            mDistance = AMapUtils.calculateLineDistance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(mLastLat, mLastLng));
        }
        if (mLastLat > 0 && mLastLng > 0 && mDistance <= 0) {
            return;
        }
        mLastLng = location.getLongitude();
        mLastLat = location.getLatitude();

        //TODO 2、过虑漂移点

        //TODO 3、传感器辅助过虑漂移点

        if (trackId == -1) {
            /*新增Track*/
            trackId = mTrackHelper.instertTrack(location);
        } else {
            /*更新Track表*/
            mTrackHelper.updateTrack(trackId, location);
        }

        sendTrackData(this, location);
    }

    /**
     * 更新UI
     * <p>
     * author: hezhiWu
     * created at 2017/11/28 10:10
     */
    public void sendTrackData(Context context, AMapLocation location) {
        if (mTrackHelper == null)
            mTrackHelper = new TrackHelper(context);

        Intent intent = new Intent(TrackConstant.TRACK_REFRESH_VIEW_BROCAST_ACTION);
        intent.putExtra(TrackConstant.TRACK_DISTANCE_KEY, mTrackHelper.getDistance());
        intent.putExtra(TrackConstant.TRACK_SPEED_KEY, mTrackHelper.getSpeed());
        intent.putExtra(TrackConstant.TRACK_DURATION_KEY, mTrackHelper.getDuration());
        intent.putExtra(TrackConstant.TRACK_LOCATION_KEY, createLocationEntity(location));
        context.sendBroadcast(intent);
    }

    /**
     * 创建Location实体
     * <p>
     * author: hezhiWu
     * created at 2017/9/10 上午11:56
     */
    protected LocationEntity createLocationEntity(AMapLocation location) {
        if (location == null)
            return null;

        LocationEntity aMapLocation = new LocationEntity();

        aMapLocation.setAltitude(location.getAltitude());
        aMapLocation.setSpeed(location.getSpeed());
        aMapLocation.setBearing(location.getBearing());
        aMapLocation.setCitycode(location.getCityCode());
        aMapLocation.setAdcode(location.getAdCode());
        aMapLocation.setCountry(location.getCountry());
        aMapLocation.setProvince(location.getProvince());
        aMapLocation.setCity(location.getCity());
        aMapLocation.setDistrict(location.getDistrict());
        aMapLocation.setRoad(location.getRoad());
        aMapLocation.setStreet(location.getStreet());
        aMapLocation.setNumber(location.getStreetNum());
        aMapLocation.setPoiname(location.getPoiName());
        aMapLocation.setLocationType(location.getLocationType());
        aMapLocation.setLocationDetail(location.getLocationDetail());
        aMapLocation.setAoiname(location.getAoiName());
        aMapLocation.setAddress(location.getAddress());
        aMapLocation.setTime(location.getTime());
        aMapLocation.setProvider(location.getProvider());
        aMapLocation.setLongitude(location.getLongitude());
        aMapLocation.setLatitude(location.getLatitude());
        aMapLocation.setAccuracy(location.getAccuracy());
        aMapLocation.setGpsAccuracyStatus(location.getGpsAccuracyStatus());

        return aMapLocation;
    }

    public static class TrackAlarReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackConstant.TRACK_ALARM_ACTION)) {
                if (mTrackHelper == null)
                    mTrackHelper = new TrackHelper(context);

                Log.d("LocationService", "alar");

                mTrackHelper.updateTrackDuration(trackId);

                Intent intent1 = new Intent(TrackConstant.TRACK_REFRESH_DURATION_BROCAST_ACTION);
                intent1.putExtra(TrackConstant.TRACK_DURATION_KEY, mTrackHelper.getDuration());
                context.sendBroadcast(intent1);
            }
        }
    }
}
