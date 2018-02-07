package com.wayto.track.common;

/**
 * 常量类
 * <p>
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/24 10:18
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TrackConstant {
    public static final String TRACK_REFRESH_VIEW_BROCAST_ACTION = "com.wayto.track_refresh.view";
    public static final String TRACK_REFRESH_DURATION_BROCAST_ACTION = "com.wayto.track_refresh.duration";
    public static final String TRACK_REFRESH_LOCATION_BROCAST_ACTION="com.wayto.track_refresh.location";

    public static final String TRACK_DISTANCE_KEY = "track_distance";
    public static final String TRACK_SPEED_KEY = "track_speed";
    public static final String TRACK_DURATION_KEY = "track_duration";
    public static final String TRACK_LOCATION_KEY = "track_location";
    public static final String TRACK_GPSSTATUS_KEY = "track_gps_status";
    public static final String TRACK_ALARM_ACTION = "com.wayto.track.service.alarm";
    public static final String LAT="lat";
    public static final String LNG="lng";

    public static final int TRACK_PANEL_FRAGMENT = 1;
    public static final int TRACK_MAP_FRAGMENT = 2;

    /*状态标记*/
    public static final String LOCATION_STATUS_KEY = "Location_status_key";

    /*轨迹状态标记*/
    public static final String TRACK_STATUS_KEY="Track_status_key";

    /*足跡开始标记*/
    public static final int TRACK_START_FLAG = 0x10;

    /*足跡结束标记*/
    public static final int TRACK_STOP_FLAG = 0x20;

    /*足跡继续标记*/
    public static final int TRACK_CONTINUE_FLAG = 0x30;

    /*足跡注销标记*/
    public static final int TRACK_DESTROY_FLAG = 0x40;

    /*定位开始*/
    public static final int START_LOCATION_FLAG = 0x50;

    /*足迹采集标记*/
    public static final int TRACK_GATHER_FLAG=0x60;

    public static final String TRACK_ID_KEY = "track_Id_key";

    public static final int TRACK_START = 10;
    public static final int TRACK_STOP = 20;
    public static final int TRACK_CONTINUE = 30;
    public static final int TRACK_END = 40;
}
