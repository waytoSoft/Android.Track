package com.wayto.track.storage;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 足迹点位表
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/21 11:46
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
@Entity
public class TrackPointTable {

    @Id(autoincrement = true)
    private Long id;

    /*关联足迹表ID*/
    @NotNull
    private long tarckTableId;

    /*经度*/
    @NotNull
    private double longitude;

    /*纬度*/
    @NotNull
    private double latitude;

    /*海拔*/
    private float altitude;

    /*速度*/
    private int speed;

    /*精度*/
    private double accuracy;

    /*来源*/
    private int source;

    /*定位提供程序*/
    private String provider;

    /*地址*/
    private String address;

    /*GPS信号强度*/
    private int signal;

    /*定位时间*/
    private long locationTime;

    /*预留String保留字段*/
    private String revStr1;
    private String revStr2;
    private String revStr3;
    private String revStr4;

    /*预留Int保留字段*/
    private int revInt1;
    private int revInt2;
    private int revInt3;
    private int revInt4;
    @Generated(hash = 1756899443)
    public TrackPointTable(Long id, long tarckTableId, double longitude,
            double latitude, float altitude, int speed, double accuracy, int source,
            String provider, String address, int signal, long locationTime,
            String revStr1, String revStr2, String revStr3, String revStr4,
            int revInt1, int revInt2, int revInt3, int revInt4) {
        this.id = id;
        this.tarckTableId = tarckTableId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.speed = speed;
        this.accuracy = accuracy;
        this.source = source;
        this.provider = provider;
        this.address = address;
        this.signal = signal;
        this.locationTime = locationTime;
        this.revStr1 = revStr1;
        this.revStr2 = revStr2;
        this.revStr3 = revStr3;
        this.revStr4 = revStr4;
        this.revInt1 = revInt1;
        this.revInt2 = revInt2;
        this.revInt3 = revInt3;
        this.revInt4 = revInt4;
    }
    @Generated(hash = 1457219545)
    public TrackPointTable() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getTarckTableId() {
        return this.tarckTableId;
    }
    public void setTarckTableId(long tarckTableId) {
        this.tarckTableId = tarckTableId;
    }
    public double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public float getAltitude() {
        return this.altitude;
    }
    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }
    public int getSpeed() {
        return this.speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public double getAccuracy() {
        return this.accuracy;
    }
    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
    public int getSource() {
        return this.source;
    }
    public void setSource(int source) {
        this.source = source;
    }
    public String getProvider() {
        return this.provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public int getSignal() {
        return this.signal;
    }
    public void setSignal(int signal) {
        this.signal = signal;
    }
    public long getLocationTime() {
        return this.locationTime;
    }
    public void setLocationTime(long locationTime) {
        this.locationTime = locationTime;
    }
    public String getRevStr1() {
        return this.revStr1;
    }
    public void setRevStr1(String revStr1) {
        this.revStr1 = revStr1;
    }
    public String getRevStr2() {
        return this.revStr2;
    }
    public void setRevStr2(String revStr2) {
        this.revStr2 = revStr2;
    }
    public String getRevStr3() {
        return this.revStr3;
    }
    public void setRevStr3(String revStr3) {
        this.revStr3 = revStr3;
    }
    public String getRevStr4() {
        return this.revStr4;
    }
    public void setRevStr4(String revStr4) {
        this.revStr4 = revStr4;
    }
    public int getRevInt1() {
        return this.revInt1;
    }
    public void setRevInt1(int revInt1) {
        this.revInt1 = revInt1;
    }
    public int getRevInt2() {
        return this.revInt2;
    }
    public void setRevInt2(int revInt2) {
        this.revInt2 = revInt2;
    }
    public int getRevInt3() {
        return this.revInt3;
    }
    public void setRevInt3(int revInt3) {
        this.revInt3 = revInt3;
    }
    public int getRevInt4() {
        return this.revInt4;
    }
    public void setRevInt4(int revInt4) {
        this.revInt4 = revInt4;
    }
}
