package kr.brainylab.model;

import android.util.Log;

import java.util.Date;
import java.util.Set;

import pl.efento.sdk.api.scan.Device;
import pl.efento.sdk.api.scan.SoftwareVersion;

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
    private Device.BatteryStatus mBatteryStatus;
    private Date mCalibrationDate;
    private int mCounter;
    private Device.EncryptionStatus mEncryptionStatus;
    private int mPeriod;
    private Set<Device.Feature> mFeatures;
    private Device.ConnectivityStatus mConnectivityStatus;
    private SoftwareVersion mSoftwareVersion;

    public SensorInfo(String type, String name, String address, String date, double temp, int humi,
                      int rssi, Device.BatteryStatus batteryStatus, Date calibrationDate, int counter,
                      Device.EncryptionStatus encryptionStatus, int period, Set<Device.Feature> features,
                      Device.ConnectivityStatus connectivityStatus, SoftwareVersion softwareVersion) {
        mType = type;
        mName = name;
        mAddress = address;
        mDate = date;
        mTemp = temp;
        mHumi = humi;
        mRssi = rssi;
        mBatteryStatus = batteryStatus;
        mCalibrationDate = calibrationDate;
        mCounter = counter;
        mEncryptionStatus = encryptionStatus;
        mPeriod = period;
        mFeatures = features;
        mConnectivityStatus = connectivityStatus;
        mSoftwareVersion = softwareVersion;
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

    public Date getCalibrationDate() {
        return mCalibrationDate;
    }

    public Device.BatteryStatus getBatteryStatus() {
        return mBatteryStatus;
    }

    public int getCounter() {
        return mCounter;
    }

    public Device.EncryptionStatus getEncryptionStatus() {
        return mEncryptionStatus;
    }

    public int getPeriod() {
        return mPeriod;
    }

    public Set<Device.Feature> getFeatures() {
        return mFeatures;
    }

    public SoftwareVersion getSoftwareVersion() {
        return mSoftwareVersion;
    }

    public Device.ConnectivityStatus getConnectivityStatus() {
        return mConnectivityStatus;
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

    public void setCalibrationDate(Date calibrationDate) {
        this.mCalibrationDate = calibrationDate;
    }

    public void setBatteryStatus(Device.BatteryStatus batteryStatus) {
        this.mBatteryStatus = batteryStatus;
    }

    public void setCounter(int counter) {
        this.mCounter = counter;
    }

    public void setEncryptionStatus(Device.EncryptionStatus encryptionStatus) {
        mEncryptionStatus = encryptionStatus;
    }

    public void setPeriod(int period) {
        this.mPeriod = period;
    }

    public void setFeatures(Set<Device.Feature> features) {
        this.mFeatures = features;
    }

    public void setSoftwareVersion(SoftwareVersion softwareVersion) {
        this.mSoftwareVersion = softwareVersion;
    }

    public void setConnectivityStatus(Device.ConnectivityStatus connectivityStatus) {
        this.mConnectivityStatus = connectivityStatus;
    }

}
