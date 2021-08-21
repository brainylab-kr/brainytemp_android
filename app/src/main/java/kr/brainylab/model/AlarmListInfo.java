package kr.brainylab.model;

/**
 * 센서 리스트
 */
public class AlarmListInfo {
    private String mPhone; //전화번호
    private String mType; //{SMS, AlimTalk}

    private boolean mTemp; //온도이상 알림
    private boolean mHumi; //습도이상 알림
    private boolean mBattery; //배터리 부족알림
    private boolean mConnect; //센서 연결없음 알림
    private boolean mError; //S/W 오류알림
    private boolean mDisconnectCharger;

    public AlarmListInfo(String phone, String type, boolean temp, boolean humi, boolean battery, boolean connect, boolean error, boolean disconnectCharger) {
        mPhone = phone;
        mType = type;
        mTemp = temp;
        mHumi = humi;
        mBattery = battery;
        mConnect = connect;
        mError = error;
        mDisconnectCharger = disconnectCharger;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getType() {
        return mType;
    }

    public boolean getTemp() {
        return mTemp;
    }

    public boolean getHumi() {
        return mHumi;
    }

    public boolean getBattery() {
        return mBattery;
    }

    public boolean getConnect() {
        return mConnect;
    }

    public boolean getError() {
        return mError;
    }

    public boolean getDisconnectCharger() {
        return mDisconnectCharger;
    }


    public void setTemp(boolean status) {
        mTemp = status;
    }

    public void setHumi(boolean status) {
        mHumi = status;
    }

    public void setBattery(boolean status) {
        mBattery = status;
    }

    public void setConnect(boolean status) {
        mConnect = status;
    }

    public void setError(boolean status) {
        mError = status;
    }

    public void setDisconnectCharger(boolean status) {
        mDisconnectCharger = status;
    }

}
