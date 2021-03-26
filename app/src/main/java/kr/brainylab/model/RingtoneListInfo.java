package kr.brainylab.model;

/**
 * 경보음 리스트
 */
public class RingtoneListInfo {
    private String mDevice; //센서 아이디
    private String mName; //센서 이름
    private String mTemp; //현재 온도
    private String mTime; //현재 시간

    public RingtoneListInfo(String device, String name, String temp, String time) {
        mDevice = device;
        mName = name;
        mTemp = temp;
        mTime = time;
    }

    public String getDevice() {
        return mDevice;
    }

    public String getName() {
        return mName;
    }

    public String getTemp() {
        return mTemp;
    }

    public String getTime() {
        return mTime;
    }
}
