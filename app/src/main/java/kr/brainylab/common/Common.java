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
     * 이벤트 타입
     */
    public static final String EVENT_LOW_TEMP = "LOW_TEMP";
    public static final String EVENT_HIGH_TEMP = "HIGH_TEMP";
    public static final String EVENT_LOW_BT = "LOW_BT";
    public static final String EVENT_APP_ERR = "APP_ERR";
    public static final String EVENT_ONNECT_ERROR = "SS_OUT";

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

}
