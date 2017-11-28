package com.wayto.track.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference 工具类
 * <p>
 * author: hezhiWu <wuhezhi007@gmail.com>
 * version: V1.0
 * created at 2017/3/17 11:51
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class SharedPreferencesUtils {
    private static final String NAME = "Track";

    /**
     * 设置SharePreference文件中的字段的值
     * <p>
     * author: hezhiWu
     * created at 2017/3/17 11:52
     *
     * @param ctx   上下文
     * @param key   字段
     * @param value 值
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("WorldWriteableFiles")
    public static boolean setValue(Context ctx, String key, Object value) {
        boolean status = false;
        SharedPreferences spf = null;
        try {
            spf = ctx.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
            String type = value.getClass().getSimpleName();// 获取数据类型
            SharedPreferences.Editor editor = spf.edit();
            if (spf != null) {
                if ("String".equals(type)) {
                    editor.putString(key, (String) value);
                } else if ("Integer".equals(type)) {
                    editor.putInt(key, (Integer) value);
                } else if ("Boolean".equals(type)) {
                    editor.putBoolean(key, (Boolean) value);
                } else if ("Long".equals(type)) {
                    editor.putLong(key, (Long) value);
                } else if ("Float".equals(type)) {
                    editor.putFloat(key, (Float) value);
                }
                status = editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }


    /**
     * 获得SharePreference的值
     * <p>
     * author: hezhiWu
     * created at 2017/3/17 11:52
     *
     * @param ctx      上下文
     * @param key      字段
     * @param defValue 默认值
     * @return 获得对应key的值
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("WorldWriteableFiles")
    public static Object getValue(Context ctx, String key, Object defValue) {
        SharedPreferences spf = null;
        try {
            spf = ctx.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
            String type = defValue.getClass().getSimpleName();// 获取数据类型
            if (spf != null) {
                if (type.equals("String")) {
                    return spf.getString(key, defValue.toString());
                } else if (type.equals("Integer")) {
                    return spf.getInt(key, (Integer) defValue);
                } else if (type.equals("Boolean")) {
                    return spf.getBoolean(key, (Boolean) defValue);
                } else if (type.equals("Long")) {
                    return spf.getLong(key, (Long) defValue);
                } else if (type.equals("Float")) {
                    return spf.getFloat(key, (Float) defValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }
}
