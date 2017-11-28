package com.wayto.track.storage;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 足迹表
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/21 11:37
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
@Entity
public class TrackTable {

    @Id(autoincrement = true)
    private Long id;

    /*标记不同系统及用户调用数据唯一性*/
    private String key;

    /*开始时间点*/
    private long startTime;

    /*结束时间点*/
    private long finishTime;

    /*时长*/
    private long duration;

    /*开始时经度*/
    private double startLongitude;

    /*结束时经度*/
    private double finishLongitude;

    /*开始时纬度*/
    private double startLatitude;

    /*结束时纬度*/
    private double finishLatitude;

    /*总距离*/
    private double distance;

    /*均速*/
    private float speed;

    /*状态,记录是否已上传服务器*/
    private int status;

    /*备注*/
    private String remark;

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
    @Generated(hash = 744524142)
    public TrackTable(Long id, String key, long startTime, long finishTime,
            long duration, double startLongitude, double finishLongitude,
            double startLatitude, double finishLatitude, double distance,
            float speed, int status, String remark, String revStr1, String revStr2,
            String revStr3, String revStr4, int revInt1, int revInt2, int revInt3,
            int revInt4) {
        this.id = id;
        this.key = key;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.duration = duration;
        this.startLongitude = startLongitude;
        this.finishLongitude = finishLongitude;
        this.startLatitude = startLatitude;
        this.finishLatitude = finishLatitude;
        this.distance = distance;
        this.speed = speed;
        this.status = status;
        this.remark = remark;
        this.revStr1 = revStr1;
        this.revStr2 = revStr2;
        this.revStr3 = revStr3;
        this.revStr4 = revStr4;
        this.revInt1 = revInt1;
        this.revInt2 = revInt2;
        this.revInt3 = revInt3;
        this.revInt4 = revInt4;
    }
    @Generated(hash = 1021608512)
    public TrackTable() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getKey() {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public long getStartTime() {
        return this.startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public long getFinishTime() {
        return this.finishTime;
    }
    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }
    public double getStartLongitude() {
        return this.startLongitude;
    }
    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }
    public double getFinishLongitude() {
        return this.finishLongitude;
    }
    public void setFinishLongitude(double finishLongitude) {
        this.finishLongitude = finishLongitude;
    }
    public double getStartLatitude() {
        return this.startLatitude;
    }
    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }
    public double getFinishLatitude() {
        return this.finishLatitude;
    }
    public void setFinishLatitude(double finishLatitude) {
        this.finishLatitude = finishLatitude;
    }
    public double getDistance() {
        return this.distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public float getSpeed() {
        return this.speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
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
    public long getDuration() {
        return this.duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }
}
