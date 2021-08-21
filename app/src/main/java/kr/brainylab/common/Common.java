package kr.brainylab.common;

import kr.brainylab.model.AlarmListInfo;

public class Common {
    public static final Boolean DEBUG_MODE = false;
    public static final String WEEKS[] = {"일","월","화", "수","목","금", "토"};

    public static final String ACT_SENSOR_RESCAN = "ACT_SENSOR_RESCAN";
    public static final String ACT_SENSOR_LIST_UPDATE = "ACT_SENSOR_UPDATE";
    public static final String ACT_SENSOR_VALUE_UPDATE = "ACT_SENSOR_VALUE_UPDATE";
    public static final String ACT_ALARM_UPDATE = "ACT_ALARM_UPDATE";
    public static final String ACT_ALARM_LIST_UPDATE = "ACT_ALARM_LIST_UPDATE";

    /**
     * 센서 타입
     */
    public static final String SENSOR_TYPE_T1 = "T1";
    public static final String SENSOR_TYPE_TH = "TH";

    /**
     * 이벤트 타입
     */
    public static final String EVENT_LOW_TEMP = "LOW_TEMP";
    public static final String EVENT_HIGH_TEMP = "HIGH_TEMP";
    public static final String EVENT_LOW_HUMI = "LOW_HUMI";
    public static final String EVENT_HIGH_HUMI = "HIGH_HUMI";
    public static final String EVENT_LOW_BT = "LOW_BT";
    public static final String EVENT_APP_ERR = "APP_ERR";
    public static final String EVENT_CONNECT_ERROR = "SS_OUT";
    public static final String EVENT_CHARGER_DISCONNECTED = "CHR_OUT";

    /**
     * 알림 타입
     */
    public static final String ALARM_CLOUD_SMS = "SMS";
    public static final String ALARM_ALIMTALK = "AlimTalk";

    /**
     * 약관 페이지
     */
    public static final String PAGE_POLICY = "POLICY";
    public static final String PAGE_OPEN_SOURCE = "source";
    public static final String PAGE_TEMRS = "temrs";

    /**
     * 편집을 위해 선택한 디바이스
     */
    public static String gSelDevice = "";

    /**
     * 알림 전화번호 선택 시
     */
    public static AlarmListInfo gAlarmInfo = null;

    public static final String PREF_schedule_time = "PREF_schedule_time";
    public static final String PREF_ALLOW_PERMISSION = "PREF_ALLOW_PERMISSION";
    public static final String PREF_LATITUDE = "PREF_LATITUDE";
    public static final String PREF_LONGITUDE = "PREF_LONGITUDE";
    public static final String PREF_SENSOR_LIST = "PREF_SENSOR_LIST";
    public static final String PREF_ALARM_LIST = "PREF_ALARM_LIST";
    public static final String PREF_ALARM_REPEAT_CYCLE = "PREF_ALARM_REPEAT_CYCLE"; //경보음 반복주기
    public static final String PREF_ALERT_REPEAT_CYCLE = "PREF_ALERT_REPEAT_CYCLE"; //알림 반복주기
    public static final String PREF_SENSING_REPEAT_CYCLE = "PREF_SENSING_REPEAT_CYCLE"; //알림 반복주기
    public static final String PREF_REPORT_ADDRESS = "PREF_REPORT_ADDRESS";
    public static final String PREF_DAILY_REPORT_ADDRESS = "PREF_DAILY_REPORT_ADDRESS";
    public static final String PREF_DAILY_REPORT = "PREF_DAILY_REPORT";
    public static final String PREF_WEEKLY_REPORT = "PREF_WEEKLY_REPORT";
    public static final String PREF_MONTHLY_REPORT = "PREF_MONTHLY_REPORT";

}
