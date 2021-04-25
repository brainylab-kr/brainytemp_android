package kr.brainylab;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

import kr.brainylab.common.Common;
import kr.brainylab.utils.PreferenceMgr;
import pl.efento.sdk.Efento;

public class BrainyTempApp extends Application {

    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

    private static BrainyTempApp _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Efento.with(this);
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        _instance = this;

        mPref = new PreferenceMgr(getApplicationContext());

    }


    public static synchronized BrainyTempApp getInstance() {
        return _instance;
    }


    //내부 저장
    public static PreferenceMgr mPref;

    /************************************** 아래는 유저 정보**********************************************************/

    public static void setScheduleTime(String value) {
        mPref.put(Common.PREF_schedule_time, value);
    }

    public static String getScheduleTime() {
        return mPref.getValue(Common.PREF_schedule_time, "5");
    }
    /**
     * 퍼미션 허용하기
     */
    public static void setAllowPermission() {
        mPref.put(Common.PREF_ALLOW_PERMISSION, true);
    }

    /**
     * 퍼미션 허용하기
     */
    public static boolean getAllowPermission() {
        return mPref.getValue(Common.PREF_ALLOW_PERMISSION, false);
    }

    /**
     * 유저 위도
     */
    public static void setLatitude(float value) {
        mPref.put(Common.PREF_LATITUDE, value);
    }

    /**
     * 유저 위도
     */
    public static float getLatitude() {
        return mPref.getValue(Common.PREF_LATITUDE, 0.0f);
    }

    /**
     * 유저 경도
     */
    public static void setLongitude(float value) {
        mPref.put(Common.PREF_LONGITUDE, value);
    }

    /**
     * 유저 경도
     */
    public static float getLongitude() {
        return mPref.getValue(Common.PREF_LONGITUDE, 0.0f);
    }

    /**
     * 센서 닉네임
     */
    public static void setSensorName(String key, String value) {
        mPref.put(key + "name", value);
    }

    /**
     * 센서 닉네임
     */
    public static String getSensorName(String key) {
        return mPref.getValue(key + "name", "BrainyT");
    }

    /**
     * 센서 온도 최대 값
     */
    public static void setMaxTemp(String key, double value) {
        mPref.put(key + "maxTemp", (float) value);
    }

    /**
     * 센서 온도 최대 값
     */
    public static double getMaxTemp(String key) {
        return mPref.getValue(key + "maxTemp", 30.0f);
    }

    /**
     * 센서 온도 최소 값
     */
    public static void setMinTemp(String key, double value) {
        mPref.put(key + "minTemp", (float) value);
    }

    /**
     * 센서 온도 최소 값
     */
    public static double getMinTemp(String key) {
        return mPref.getValue(key + "minTemp", -10.0f);
    }

    /**
     * 센서 습도 최대 값
     */
    public static void setMaxHumi(String key, int value) {
        mPref.put(key + "maxHumi", (int) value);
    }

    /**
     * 센서 습도 최대 값
     */
    public static int getMaxHumi(String key) {
        return mPref.getValue(key + "maxHumi", 80);
    }

    /**
     * 센서 습도 최소 값
     */
    public static void setMinHumi(String key, int value) {
        mPref.put(key + "minHumi", (int) value);
    }

    /**
     * 센서 습도 최소 값
     */
    public static int getMinHumi(String key) {
        return mPref.getValue(key + "minHumi", 20);
    }

    /**
     * 측정 시간
     */
    public static void setMeasureTime(String key, String value) {
        mPref.put(key + "measuretime", value);
    }

    /**
     * 측정 시간
     */
    public static String getMeasureTime(String key) {
        return mPref.getValue(key + "measuretime", "0");
    }

    /**
     * 경보음 반복 주기
     */
    public static void setAlarmRepeatCycle(int value) {
        mPref.put(Common.PREF_ALARM_REPEAT_CYCLE, value);
    }

    /**
     * 경보음 반복 주기
     */
    public static int getAlarmRepeatCycle() {
        return mPref.getValue(Common.PREF_ALARM_REPEAT_CYCLE, 5);
    }

    /**
     * SMS 알림 반복 주기
     */
    public static void setAlertRepeatCycle(int value) {
        mPref.put(Common.PREF_ALERT_REPEAT_CYCLE, value);
    }

    /**
     * SMS 알림 반복 주기
     */
    public static int getAlertRepeatCycle() {
        return mPref.getValue(Common.PREF_ALERT_REPEAT_CYCLE, 15);
    }

    /**
     * 센싱 반복 주기
     */
    public static void setSensingRepeatCycle(int value) {
        mPref.put(Common.PREF_SENSING_REPEAT_CYCLE, value);
    }

    /**
     * 센싱 반복 주기
     */
    public static int getSensingRepeatCycle() {
        return mPref.getValue(Common.PREF_SENSING_REPEAT_CYCLE, 5);
    }


    /**
     * 센서, 온도 갱신 시간
     */
    public static void setUpdateTime(String key, String value) {
        mPref.put(key + "updatetime", value);
    }

    /**
     * 센서, 온도 갱신 시간
     */
    public static String getUpdateTime(String key) {
        return mPref.getValue(key + "updatetime", "0");
    }

    /**
     * 마지막으로 울린 alert time
     */
    public static void setAlertTime(String key, String value) {
        mPref.put(key + "alerttime", value);
    }

    /**
     * 마지막으로 울린 alert time
     */
    public static String getAlertTime(String key) {
        return mPref.getValue(key + "alerttime", "5");
    }

    /**
     * 마지막으로 울린 경보음 time
     */
    public static void setAlarmTime(String key, String value) {
        mPref.put(key +  "alarmtime", value);
    }

    /**
     * 마지막으로 울린 경보음 time
     */
    public static String getAlarmTime(String key) {
        return mPref.getValue(key + "alarmtime", "5");
    }
}