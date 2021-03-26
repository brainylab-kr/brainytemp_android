package kr.brainylab.model;

/**
 * 로컬에 저장하는 온도 리스트
 */
public class TempListInfo {
    private long mTime; //시간
    private double mTemp; //온도

    public TempListInfo(long time, double temp) {
        mTime = time;
        mTemp = temp;
    }

    public long getTime() {
        return mTime;
    }

    public double getTemp() {
        return mTemp;
    }

}
