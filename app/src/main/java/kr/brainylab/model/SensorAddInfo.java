package kr.brainylab.model;

/**
 * 센서 추가
 */
public class SensorAddInfo {
    private String mDevice;
    private int mRssi;

    public SensorAddInfo(String device, int rssi) {
        mDevice = device;
        mRssi = rssi;
    }

    public String getDevice() {
        return mDevice;
    }

    public int getRssi() {
        return mRssi;
    }

}
