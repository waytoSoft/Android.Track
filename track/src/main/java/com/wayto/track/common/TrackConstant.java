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
    public static final String TRACK_REFRESH_VIEW_BROCAST_ACTION = "com.wayto.track_refresh.view.action";
    public static final String TRACK_REFRESH_DURATION_BROCAST_ACTION = "com.wayto.track_refresh.duration.action";
    public static final String TRACK_STATUS_BROCAST_ACTION = "com.wayto.track.action";
    public static final String TRACK_STATUS_KEY = "track_status";
    public static final String TRACK_DISTANCE_KEY = "track_distance";
    public static final String TRACK_SPEED_KEY = "track_speed";
    public static final String TRACK_DURATION_KEY = "track_duration";
    public static final String TRACK_LOCATION_KEY = "track_location";
    public static final String TRACK_ALARM_ACTION = "com.wayto.track.service.alarm";

    public static final int TRACK_PANEL_FRAGMENT = 1;
    public static final int TRACK_MAP_FRAGMENT = 2;

    /*切换面板模式Key*/
    public static final int TRACK_PANEL_MODE = 0x000088;

    /*切换地图模式Key*/
    public static final int TRACK_MAP_MODE = 0x000089;

    /*状态标记*/
    public static final String LOCATION_STATUS_KEY = "Location_status_key";

    /*定位启动标记*/
    public static final int LOCATION_START_FLAG = 0x10;

    /*定位结束标记*/
    public static final int LOCATION_STOP_FLAG = 0x20;

    /*定位继续标记*/
    public static final int LOCATION_CONTINUE_FLAG = 0x30;

    /*定位注销标记*/
    public static final int LOCATION_DESTROY_FLAG = 0x40;

    public static final String TRACK_ID_KEY = "track_Id_key";

    public static final int TRACK_START = 10;
    public static final int TRACK_STOP = 20;
    public static final int TRACK_CONTINUE = 30;
    public static final int TRACK_END = 40;
}
