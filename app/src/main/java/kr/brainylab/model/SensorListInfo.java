package kr.brainylab.model;

import java.util.Date;

/**
 * 센서 리스트
 */
public class SensorListInfo {
    private String mName;
    private String mDevice;
    private String mDate;
    private double mTemp;
    private int mRssi;

    public SensorListInfo(String name, String device, String date, double temp, int rssi) {
        mName = name;
        mDevice = device;
        mDate = date;
        mTemp = temp;
        mRssi = rssi;
    }


    public String getName() {
        return mName;
    }

    public String getDevice() {
        return mDevice;
    }

    public String getDate() {
        return mDate;
    }

    public double getTemp() {
        return mTemp;
    }

    public int getRssi() {
        return mRssi;
    }
}
