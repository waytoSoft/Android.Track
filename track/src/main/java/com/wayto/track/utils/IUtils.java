package com.wayto.track.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

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
    @Deprecated
    public static void setAlarm(Context context, AlarmManager aManager, String action, long triggerAtMillis) {
        Intent intent = new Intent(action);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        aManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), triggerAtMillis, pi);
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
    @Deprecated
    public static void cancelAlarm(Context context, AlarmManager aManager, String action) {
        Intent intent = new Intent(action);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        aManager.cancel(pi);
    }

    /**
     * 创建前台Notification
     * <p>
     * author: hezhiWu
     * created at 2017/3/15 10:26
     *
     * @param context
     * @param contentTitle
     * @param contentText
     * @param launcherRes
     * @param cls          跳转目标
     */
    public static Notification CreateForegroundNotification(Context context, String contentTitle, String contentText, int launcherRes, Class<?> cls) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Intent intent = new Intent(context.getApplicationContext(), cls);
        mBuilder.setContentTitle(contentTitle)
                .setContentText(contentText)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), launcherRes)) // 设置下拉列表中的图标(大图标)
                .setSmallIcon(launcherRes)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));//设置通知小ICON
        return mBuilder.build();
    }
}
