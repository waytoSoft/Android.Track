package com.wayto.track.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/28 14:01
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class IUtils {

    /**
     * 设置提醒
     * <p>
     * author: hezhiWu
     * created at 2017/11/28 14:06
     *
     * @param context
     * @param aManager
     * @param action
     * @param triggerAtMillis
     */
    public static void setAlarm(Context context, AlarmManager aManager, String action, long triggerAtMillis) {
        Intent intent = new Intent(action);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        aManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),triggerAtMillis, pi);
    }


    /**
     * 取消提醒
     * <p>
     * author: hezhiWu
     * created at 2017/11/28 14:07
     *
     * @param context
     * @param aManager
     * @param action
     */
    public static void cancelAlarm(Context context, AlarmManager aManager, String action) {
        Intent intent = new Intent(action);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        aManager.cancel(pi);
    }
}
