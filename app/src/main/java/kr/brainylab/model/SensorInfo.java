package kr.brainylab.model;

import android.util.Log;

/**
 * 센서 정보
 */
public class SensorInfo {
    private String mType;
    private String mName;
    private String mAddress;
    private String mDate;
    private double mTemp;
    private int mHumi;
    private int mRssi;

    public SensorInfo(String type, String name, String address, String date, double temp, int humi, int rssi) {
        mType = type;
        mName = name;
        mAddress = address;
        mDate = date;
        mTemp = temp;
        mHumi = humi;
        mRssi = rssi;

    }


    public String getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getDate() {
        return mDate;
    }

    public double getTemp() {
        return mTemp;
    }

    public int getHumi() {
        return mHumi;
    }

    public int getRssi() {
        return mRssi;
    }

    public void setType(String type) {
        mType = type;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setTemp(double temp) {
        mTemp = temp;
    }

    public void setHumi(int humi) {
        mHumi = humi;
    }

    public void setRssi(int rssi) {
        mRssi = rssi;
    }
}
