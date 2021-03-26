package kr.brainylab.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class PreferenceMgr {

    /**
     * 유저정보
     */
    public static final String PREF_schedule_time = "PREF_schedule_time";
    public static final String PREF_ALLOW_PERMISSION = "PREF_ALLOW_PERMISSION";
    public static final String PREF_LATITUDE = "PREF_LATITUDE";
    public static final String PREF_LONGITUDE = "PREF_LONGITUDE";
    public static final String PREF_SENSOR_LIST = "PREF_SENSOR_LIST";
    public static final String PREF_ALARM_LIST = "PREF_ALARM_LIST";
    public static final String PREF_ALARM_REPEAT_CYCLE = "PREF_ALARM_REPEAT_CYCLE"; //경보음 반복주기
    public static final String PREF_ALERT_REPEAT_CYCLE = "PREF_ALERT_REPEAT_CYCLE"; //알림 반복주기
    public static final String PREF_SENSING_REPEAT_CYCLE = "PREF_SENSING_REPEAT_CYCLE"; //알림 반복주기

    //센서, 온도 갱신 시간 차
    public static final String PREF_UPDATE_SENSOR_TIME = "PREF_UPDATE_SENSOR_TIME";

    private final String PREF_NAME = kr.brainylab.utils.PreferenceMgr.class.getCanonicalName();

    private static Context mContext;
    private int mMode = Activity.MODE_PRIVATE;

    public PreferenceMgr(Context c) {
        mContext = c;
    }


    public void put(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, mMode);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, Boolean value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, mMode);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, mMode);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void put(String key, float value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, mMode);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public String getValue(String key, String defValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, mMode);
        try {
            return pref.getString(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public int getValue(String key, int defValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, mMode);
        try {
            return pref.getInt(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public Boolean getValue(String key, Boolean defValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, mMode);
        try {
            return pref.getBoolean(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public void put(String key, String value, int mode) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, mode);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getValue(String key, String defValue, int mode) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, mode);
        try {
            return pref.getString(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public float getValue(String key, float defValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, mMode);
        try {
            return pref.getFloat(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }
}