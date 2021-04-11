package kr.brainylab.model;

/**
 * 로컬에 저장하는 온도 리스트
 */
public class ValueListInfo {
    private long mTime; //시간
    private double mTemp; //온도
    private int mHumi; //습도

    public ValueListInfo(long time, double temp, int humi) {
        mTime = time;
        mTemp = temp;
        mHumi = humi;
    }

    public long getTime() {
        return mTime;
    }

    public double getTemp() {
        return mTemp;
    }

    public int getHumi() {
        return mHumi;
    }
}
