package com.wayto.track.service;

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
import com.wayto.track.R;
import com.wayto.track.TrackActivity;
import com.wayto.track.common.SharedPreferencesUtils;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.service.data.LocationEntity;
import com.wayto.track.utils.IUtils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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

    private Timer mTimer;
    private TrackTimerTask mTrackTimerTask;

    private int trackState = -1;

    private int gpsStatus = -1;

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
//        int flag = Integer.parseInt(SharedPreferencesUtils.getValue(this, TrackConstant.LOCATION_STATUS_KEY, -1).toString());
//        /*判断启动定位 or 开始采集路径*/
//        if (flag == TrackConstant.START_LOCATION_FLAG) {
//
//            startLocation();
//        } else if (flag == TrackConstant.TRACK_GATHER_FLAG) {
//            trackState = Integer.parseInt(SharedPreferencesUtils.getValue(this, TrackConstant.TRACK_STATUS_KEY, -1).toString());
//
//            paresTrackIntent(trackState);
//        }

        startLocation();

        paresTrackIntent();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 解析测试Intent
     * <p>
     * author: hezhiWu
     * created at 2018/2/5 16:29
     */
    private void paresTrackIntent() {
        if (mTrackHelper == null)
            mTrackHelper = new TrackHelper(this);

        trackState = mTrackHelper.getTrackStatusFromTem();

        try {
            if (trackState == TrackConstant.TRACK_START) {/*开始*/
                trackId = mTrackHelper.getTrackId();

                //2、启动定位
                startLocation();

                //3、开户定时提醒
                startTimer();

                /*开启前台服务*/
                startForeground(17, IUtils.CreateForegroundNotification(getApplicationContext(), "轨迹", "路径采集中...", R.mipmap.ic_launcher_round, TrackActivity.class));

            } else if (trackState == TrackConstant.TRACK_STOP) {/*停止*/
                mTrackHelper.updateTrackStatus(mTrackHelper.getTrackId(), TrackConstant.TRACK_STOP);
                mTrackHelper.clearLocationTime();

                /*关闭定时提醒*/
                cancelTimer();

                /*开启前台服务*/
                startForeground(17, IUtils.CreateForegroundNotification(getApplicationContext(), "轨迹", "路径采集停止", R.mipmap.ic_launcher_round, TrackActivity.class));
            } else if (trackState == TrackConstant.TRACK_CONTINUE) {/*继续*/
                mTrackHelper.updateTrackStatus(mTrackHelper.getTrackId(), TrackConstant.TRACK_CONTINUE);

                trackId = mTrackHelper.getTrackId();

                startLocation();

                startTimer();

                /*开启前台服务*/
                startForeground(17, IUtils.CreateForegroundNotification(getApplicationContext(), "轨迹", "路径采集中", R.mipmap.ic_launcher_round, TrackActivity.class));
            } else if (trackState == TrackConstant.TRACK_END) {/*结束*/
                mTrackHelper.updateTrackStatus(mTrackHelper.getTrackId(), TrackConstant.TRACK_END);

                mTrackHelper.deleteTem();

                teml = 1;

                cancelTimer();

                /*注销数据*/
                destoryTrackData();

                /*退出前台服务*/
                stopForeground(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "ServiceGather Exception", Toast.LENGTH_SHORT).show();
        }
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

    /**
     * 启动定时器
     * <p>
     * author: hezhiWu
     * created at 2017/12/13 12:02
     */
    private void startTimer() {
        cancelTimer();

        if (mTimer == null)
            mTimer = new Timer();

        if (mTrackTimerTask == null)
            mTrackTimerTask = new TrackTimerTask();

        mTimer.schedule(mTrackTimerTask, new Date(), 1000);
    }

    /**
     * 取消定时器
     * <p>
     * author: hezhiWu
     * created at 2017/12/13 12:02
     */
    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTrackTimerTask != null) {
            mTrackTimerTask.cancel();
            mTrackTimerTask = null;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

            gpsStatus = aMapLocation.getGpsAccuracyStatus();

            recordTrack(aMapLocation);
        }
    }

    private int teml = 1;

    /**
     * 记录足迹采集
     * <p>
     * author: hezhiWu
     * created at 2017/11/27 17:07
     */
    private void recordTrack(AMapLocation location) {
        if (location == null)
            return;

        //TODO 测试数据
        if (trackState == TrackConstant.TRACK_START || trackState == TrackConstant.TRACK_CONTINUE) {
            location.setLatitude(location.getLatitude() + 0.00004 * teml);
            location.setLongitude(location.getLongitude() + 0.00005 * teml);
            teml++;
        }

        //更新前台地图当前位置
        sendLocationData(this, location.getLatitude(), location.getLongitude());

        //判断采集状态是否为开始 or 断续
        if (trackState != TrackConstant.TRACK_CONTINUE &&
                trackState != TrackConstant.TRACK_START) {

            return;
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

        /*更新Track表*/
        mTrackHelper.updateTrackDistanceSpeed(trackId, location);
    }

    /**
     * 更新实时位置
     * <p>
     * author: hezhiWu
     * created at 2018/1/17 16:30
     */
    public void sendLocationData(Context context, double lat, double lng) {
        Intent intent = new Intent(TrackConstant.TRACK_REFRESH_LOCATION_BROCAST_ACTION);
        intent.putExtra(TrackConstant.LAT, lat);
        intent.putExtra(TrackConstant.LNG, lng);
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

    /**
     * 定时任务接收器，对于有些手机产商修改AlarmManager定时任务延后的原因，不建议使用AlarmManager做定时任务
     * <p>
     * author: hezhiWu
     * created at 2017/12/13 11:51
     */
    @Deprecated
    public static class TrackAlarReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackConstant.TRACK_ALARM_ACTION)) {
                if (mTrackHelper == null)
                    mTrackHelper = new TrackHelper(context);

                //更新时长
                long duration = mTrackHelper.updateTrackDuration(trackId);

                Intent intent1 = new Intent(TrackConstant.TRACK_REFRESH_DURATION_BROCAST_ACTION);
                intent1.putExtra(TrackConstant.TRACK_DURATION_KEY, duration);
                context.sendBroadcast(intent1);
            }
        }
    }

    /**
     * Create the TimerTask
     * <p>
     * author: hezhiWu
     * created at 2017/12/13 11:58
     */
    class TrackTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mTrackHelper == null)
                mTrackHelper = new TrackHelper(getApplicationContext());

            //更新时长
            long duration = mTrackHelper.updateTrackDuration(trackId);

            Intent intent1 = new Intent(TrackConstant.TRACK_REFRESH_DURATION_BROCAST_ACTION);
            intent1.putExtra(TrackConstant.TRACK_DURATION_KEY, duration);
            intent1.putExtra(TrackConstant.TRACK_GPSSTATUS_KEY, gpsStatus);
            intent1.putExtra(TrackConstant.TRACK_SPEED_KEY, mTrackHelper.getSpeed());
            intent1.putExtra(TrackConstant.TRACK_DISTANCE_KEY, mTrackHelper.getDistance());
            intent1.putExtra(TrackConstant.LAT, mLastLat);
            intent1.putExtra(TrackConstant.LNG, mLastLng);
            getApplication().sendBroadcast(intent1);
        }
    }
}
