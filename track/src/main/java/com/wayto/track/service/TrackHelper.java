package com.wayto.track.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.wayto.track.common.TrackConstant;
import com.wayto.track.provider.TrackData;
import com.wayto.track.storage.TrackTable;
import com.wayto.track.utils.IStringUtils;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/27 14:56
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackHelper {

    private ContentResolver contentResolver;

    private double mLastLng;
    private double mLastLat;
    private long mLastLocationTime;
    private double distance;
    private float speed;

    private Context context;

    private long trackId = -1;

    public TrackHelper(Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();
    }

    /**
     * 返回TrackId
     * <p>
     * author: hezhiWu
     * created at 2017/9/12 上午10:46
     */
    public long getTrackId() {
        Cursor cursor = contentResolver.query(TrackData.Tem.TRACK_TEM_URL, new String[]{TrackData.Tem.TemColumns.trackId}, null, null, null);

        if (cursor.moveToNext()) {
            trackId = cursor.getLong(cursor.getColumnIndex(TrackData.Tem.TemColumns.trackId));
        }

        return trackId;
    }

    /**
     * 获取采集状态
     * <p>
     * author: hezhiWu
     * created at 2018/2/7 11:14
     */
    public int getTrackStatusFromTem() {
        Cursor cursor = contentResolver.query(TrackData.Tem.TRACK_TEM_URL, new String[]{TrackData.Tem.TemColumns.trackStatus}, null, null, null);

        int trackStatus = -1;

        if (cursor.moveToNext()) {
            trackStatus = cursor.getInt(cursor.getColumnIndex(TrackData.Tem.TemColumns.trackStatus));
        }

        return trackStatus;
    }

    /**
     * 插入临时表
     * <p>
     * author: hezhiWu
     * created at 2018/2/6 14:44
     */
    private void instertTem(long trackId, int locationFlag, int trackStatus) {
        ContentValues values = new ContentValues();
        values.put(TrackData.Tem.TemColumns.trackId, trackId);
        values.put(TrackData.Tem.TemColumns.locationFlag, locationFlag);
        values.put(TrackData.Tem.TemColumns.trackStatus, trackStatus);

        contentResolver.insert(TrackData.Tem.TRACK_TEM_URL, values);
    }

    /**
     * 删除临时表
     * <p>
     * author: hezhiWu
     * created at 2018/2/7 13:48
     */
    public void deleteTem() {
        contentResolver.delete(TrackData.Tem.TRACK_TEM_URL, null, null);
    }

    /**
     * 插入Track
     * <p>
     * author: hezhiWu
     * created at 2017/11/28 15:34
     *
     * @param lng
     * @param lat
     */
    public long instertTrack(double lng, double lat) {
        ContentValues values = new ContentValues();
        values.put(TrackData.Track.TrackColumns.KEY, "");
        values.put(TrackData.Track.TrackColumns.START_TIME, System.currentTimeMillis());
        values.put(TrackData.Track.TrackColumns.FINISH_TIME, 0);
        values.put(TrackData.Track.TrackColumns.DURATION, 0);
        values.put(TrackData.Track.TrackColumns.START_LONGITUDE, lng);
        values.put(TrackData.Track.TrackColumns.FINISH_LONGITUDE, 0);
        values.put(TrackData.Track.TrackColumns.START_LATITUDE, lat);
        values.put(TrackData.Track.TrackColumns.FINISH_LATITUDE, 0);
        values.put(TrackData.Track.TrackColumns.DISTANCE, 0);
        values.put(TrackData.Track.TrackColumns.SPEED, 0);
        values.put(TrackData.Track.TrackColumns.STATUS, TrackConstant.TRACK_START);
        values.put(TrackData.Track.TrackColumns.REMARK, "");
        values.put(TrackData.Track.TrackColumns.REV_STR1, "");
        values.put(TrackData.Track.TrackColumns.REV_STR2, "");
        values.put(TrackData.Track.TrackColumns.REV_STR3, "");
        values.put(TrackData.Track.TrackColumns.REV_STR4, "");
        values.put(TrackData.Track.TrackColumns.REV_INT1, 0);
        values.put(TrackData.Track.TrackColumns.REV_INT2, 0);
        values.put(TrackData.Track.TrackColumns.REV_INT3, 0);
        values.put(TrackData.Track.TrackColumns.REV_INT4, 0);

        Uri uri = contentResolver.insert(TrackData.Track.TRACK_CONTENT_URI, values);
        long id = -1;
        if (uri != null && uri.getPathSegments() != null) {
            id = IStringUtils.toLong(uri.getPathSegments().get(1));

            instertTem(id, TrackConstant.TRACK_GATHER_FLAG, TrackConstant.TRACK_START_FLAG);
//            Toast.makeText(context, "id=" + id, Toast.LENGTH_LONG).show();
//
//            SharedPreferencesUtils.setValue(DataApplication.getInstance(), TrackConstant.TRACK_ID_KEY, id);
//
//            long trackId = IStringUtils.toLong(SharedPreferencesUtils.getValue(DataApplication.getInstance(), TrackConstant.TRACK_ID_KEY, 0L).toString());
//            Toast.makeText(context, "trackId=" + trackId, Toast.LENGTH_LONG).show();
        }

        mLastLng = lng;
        mLastLat = lat;
        mLastLocationTime = System.currentTimeMillis();

        return id;
    }

    /**
     * 更新Track表
     * <p>
     * author: hezhiWu
     * created at 2017/11/28 15:43
     *
     * @param trackId
     * @param startLat
     * @param startLng
     */
    public void updateTrack(long trackId, double startLng, double startLat) {
        ContentValues values = new ContentValues();
        values.put(TrackData.Track.TrackColumns.START_LONGITUDE, startLng);
        values.put(TrackData.Track.TrackColumns.START_LATITUDE, startLat);
        contentResolver.update(TrackData.Track.TRACK_CONTENT_URI, values, TrackData.Track.TrackColumns._ID + " = " + trackId, null);

        mLastLng = startLng;
        mLastLat = startLat;
    }

    /**
     * 更新Track表
     * <p>
     * author: hezhiWu
     * created at 2017/11/27 17:33
     *
     * @param trackId
     * @param location
     */
    public void updateTrackDistanceSpeed(long trackId, AMapLocation location) {
        if (location == null)
            return;

        /*处理系统回收APP,距离异常的情况*/
        if (mLastLng <= 0 || mLastLat <= 0) {
            mLastLng = location.getLongitude();
            mLastLat = location.getLatitude();
            return;
        }

        TrackTable table = queryTrack(trackId);

        if (table == null
                || table.getStatus() == TrackConstant.TRACK_STOP
                || table.getStatus() == TrackConstant.TRACK_END)
            return;

        /*更新距离*/
        distance = table.getDistance() + AMapUtils.calculateLineDistance(new LatLng(mLastLat, mLastLng),
                new LatLng(location.getLatitude(), location.getLongitude()));
        if (distance > 0)
            updateTrackDistance(trackId, distance);

        /*更新速度*/
        speed = (float) distance / table.getDuration();
        if (!Double.isNaN(speed))
            updateTrackSpeed(trackId, speed);


        /*插入轨迹点*/
        insterTrackPoint(trackId, location);
    }

    /**
     * 查询Track
     * <p>
     * author: hezhiWu
     * created at 2017/11/27 17:53
     *
     * @param trackId
     */
    private TrackTable queryTrack(long trackId) {
        TrackTable table = new TrackTable();

        Cursor cursor = contentResolver.query(TrackData.Track.TRACK_CONTENT_URI, null, TrackData.Track.TrackColumns._ID + " = " + trackId, null, null);
        while (cursor.moveToNext()) {
            table.setKey(cursor.getString(cursor.getColumnIndex(TrackData.Track.TrackColumns.KEY)));
            table.setStartTime(cursor.getLong(cursor.getColumnIndex(TrackData.Track.TrackColumns.START_TIME)));
            table.setFinishTime(cursor.getLong(cursor.getColumnIndex(TrackData.Track.TrackColumns.FINISH_TIME)));
            table.setDuration(cursor.getLong(cursor.getColumnIndex(TrackData.Track.TrackColumns.DURATION)));
            table.setStartLongitude(cursor.getDouble(cursor.getColumnIndex(TrackData.Track.TrackColumns.START_LONGITUDE)));
            table.setFinishLongitude(cursor.getDouble(cursor.getColumnIndex(TrackData.Track.TrackColumns.FINISH_LONGITUDE)));
            table.setStartLatitude(cursor.getDouble(cursor.getColumnIndex(TrackData.Track.TrackColumns.START_LATITUDE)));
            table.setFinishLatitude(cursor.getDouble(cursor.getColumnIndex(TrackData.Track.TrackColumns.FINISH_LATITUDE)));
            table.setDistance(cursor.getDouble(cursor.getColumnIndex(TrackData.Track.TrackColumns.DISTANCE)));
            table.setSpeed(cursor.getFloat(cursor.getColumnIndex(TrackData.Track.TrackColumns.SPEED)));
            table.setStatus(cursor.getInt(cursor.getColumnIndex(TrackData.Track.TrackColumns.STATUS)));
            table.setRemark(cursor.getString(cursor.getColumnIndex(TrackData.Track.TrackColumns.REMARK)));
            table.setRevStr1(cursor.getString(cursor.getColumnIndex(TrackData.Track.TrackColumns.REV_STR1)));
            table.setRevStr2(cursor.getString(cursor.getColumnIndex(TrackData.Track.TrackColumns.REV_STR2)));
            table.setRevStr3(cursor.getString(cursor.getColumnIndex(TrackData.Track.TrackColumns.REV_STR3)));
            table.setRevStr4(cursor.getString(cursor.getColumnIndex(TrackData.Track.TrackColumns.REV_STR4)));
            table.setRevInt1(cursor.getInt(cursor.getColumnIndex(TrackData.Track.TrackColumns.REV_INT1)));
            table.setRevInt2(cursor.getInt(cursor.getColumnIndex(TrackData.Track.TrackColumns.REV_INT2)));
            table.setRevInt3(cursor.getInt(cursor.getColumnIndex(TrackData.Track.TrackColumns.REV_INT3)));
            table.setRevInt4(cursor.getInt(cursor.getColumnIndex(TrackData.Track.TrackColumns.REV_INT4)));
        }
        cursor.close();

        return table;
    }

    /**
     * 更新状态
     * <p>
     * author: hezhiWu
     * created at 2017/11/27 16:45
     *
     * @param trackId
     * @param status
     */
    public void updateTrackStatus(long trackId, int status) {
        if (trackId <= 0)
            return;

        ContentValues values = new ContentValues();
        values.put(TrackData.Track.TrackColumns.STATUS, status);
        contentResolver.update(TrackData.Track.TRACK_CONTENT_URI, values, TrackData.Track.TrackColumns._ID + " = " + trackId, null);
    }

    /**
     * 更新距离
     * <p>
     * author: hezhiWu
     * created at 2017/11/27 17:31
     *
     * @param trackId
     * @param distance
     */
    private void updateTrackDistance(long trackId, double distance) {
        ContentValues values = new ContentValues();
        values.put(TrackData.Track.TrackColumns.DISTANCE, distance);
        contentResolver.update(TrackData.Track.TRACK_CONTENT_URI, values, TrackData.Track.TrackColumns._ID + " = " + trackId, null);
    }

    /**
     * 更新速度
     * <p>
     * author: hezhiWu
     * created at 2017/11/27 17:31
     */
    private void updateTrackSpeed(long trackId, float speed) {
        ContentValues values = new ContentValues();
        values.put(TrackData.Track.TrackColumns.SPEED, speed);
        contentResolver.update(TrackData.Track.TRACK_CONTENT_URI, values, TrackData.Track.TrackColumns._ID + " = " + trackId, null);
    }

    /**
     * 更新时长
     * <p>
     * author: hezhiWu
     * created at 2017/11/27 18:13
     *
     * @param trackId
     */
    public long updateTrackDuration(long trackId) {
        TrackTable table = queryTrack(trackId);
        if (table == null)
            return -1;

        if (table.getStatus() == TrackConstant.TRACK_STOP
                || table.getStatus() == TrackConstant.TRACK_END)
            return -1;

        long duration = 1000 + table.getDuration();

        ContentValues values = new ContentValues();
        values.put(TrackData.Track.TrackColumns.DURATION, duration);
        contentResolver.update(TrackData.Track.TRACK_CONTENT_URI, values, TrackData.Track.TrackColumns._ID + " = " + trackId, null);

        return duration;
    }

    /**
     * author: hezhiWu
     * created at 2017/11/27 16:04
     *
     * @param trackId
     * @param location
     */
    public void insterTrackPoint(long trackId, AMapLocation location) {
        ContentValues values = new ContentValues();
        values.put(TrackData.TrackPoint.TrackPointColums.TARCK_TABLE_ID, trackId);
        values.put(TrackData.TrackPoint.TrackPointColums.LONGITUDE, location.getLongitude());
        values.put(TrackData.TrackPoint.TrackPointColums.LATITUDE, location.getLatitude());
        values.put(TrackData.TrackPoint.TrackPointColums.ALTITUDE, location.getAltitude());
        values.put(TrackData.TrackPoint.TrackPointColums.SPEED, location.getSpeed());
        values.put(TrackData.TrackPoint.TrackPointColums.ACCURACY, location.getAccuracy());
        values.put(TrackData.TrackPoint.TrackPointColums.SOURCE, 0);
        values.put(TrackData.TrackPoint.TrackPointColums.PROVIDER, location.getProvider());
        values.put(TrackData.TrackPoint.TrackPointColums.ADDRESS, location.getAddress());
        values.put(TrackData.TrackPoint.TrackPointColums.SIGNAL, location.getGpsAccuracyStatus());
        values.put(TrackData.TrackPoint.TrackPointColums.LOCATION_TIME, System.currentTimeMillis());
        values.put(TrackData.TrackPoint.TrackPointColums.REV_STR1, "");
        values.put(TrackData.TrackPoint.TrackPointColums.REV_STR2, "");
        values.put(TrackData.TrackPoint.TrackPointColums.REV_STR3, "");
        values.put(TrackData.TrackPoint.TrackPointColums.REV_STR4, "");
        values.put(TrackData.TrackPoint.TrackPointColums.REV_INT1, 0);
        values.put(TrackData.TrackPoint.TrackPointColums.REV_INT2, 0);
        values.put(TrackData.TrackPoint.TrackPointColums.REV_INT3, 0);
        values.put(TrackData.TrackPoint.TrackPointColums.REV_INT4, 0);

        contentResolver.insert(TrackData.TrackPoint.TRACK_POINT_CONTENT_URI, values);

        mLastLng = location.getLongitude();
        mLastLat = location.getLatitude();
        mLastLocationTime = System.currentTimeMillis();
    }

    /**
     * 数据重置
     * <p>
     * author: hezhiWu
     * created at 2017/11/28 9:36
     */
    public void destoryData() {
        contentResolver = null;
        mLastLng = 0;
        mLastLat = 0;
        mLastLocationTime = 0;
        distance = 0;
        speed = 0;
    }

    /**
     * 采集暂停时间停止
     * <p>
     * author: hezhiWu
     * created at 2017/11/28 17:06
     */
    public void clearLocationTime() {
        mLastLocationTime = 0;
    }

    /**
     * 返回距离
     * <p>
     * author: hezhiWu
     * created at 2017/11/28 10:07
     */
    public double getDistance() {
        return distance;
    }

    /**
     * 返回速度
     * <p>
     * author: hezhiWu
     * created at 2017/11/28 10:07
     */
    public float getSpeed() {
        return speed;
    }
}
