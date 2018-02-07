package com.wayto.track.storage;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 临时表，记录trackId、定位标记、采集状态
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2018/2/6 14:12
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
@Entity
public class TemTable {

    @Id(autoincrement = true)
    private Long id;

    private long trackId;

    private int locationFlag;

    private int trackStatus;

    @Generated(hash = 1890062402)
    public TemTable(Long id, long trackId, int locationFlag, int trackStatus) {
        this.id = id;
        this.trackId = trackId;
        this.locationFlag = locationFlag;
        this.trackStatus = trackStatus;
    }

    @Generated(hash = 537178103)
    public TemTable() {
    }

    public long getTrackId() {
        return this.trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public int getLocationFlag() {
        return this.locationFlag;
    }

    public void setLocationFlag(int locationFlag) {
        this.locationFlag = locationFlag;
    }

    public int getTrackStatus() {
        return this.trackStatus;
    }

    public void setTrackStatus(int trackStatus) {
        this.trackStatus = trackStatus;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
