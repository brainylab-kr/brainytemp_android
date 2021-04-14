package kr.brainylab.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sensordata_table")
public class SensorData {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    private int id;

    @ColumnInfo(name = "time")
    @NonNull
    private long mTime;

    @ColumnInfo(name = "addr")
    @NonNull
    private String mAddr;

    @ColumnInfo(name = "temp")
    @NonNull
    private double mTemp;

    @ColumnInfo(name = "humi")
    @NonNull
    private int mHumi;

    @ColumnInfo(name = "rssi")
    @NonNull
    private int mRssi;

    public SensorData(@NonNull String addr, @NonNull long time, @NonNull double temp, @NonNull int humi, @NonNull int rssi) {
        this.mAddr = addr;
        this.mTime = time;
        this.mTemp = temp;
        this.mHumi = humi;
        this.mRssi = rssi;
    }

    @NonNull
    public String getAddr() {
        return mAddr;
    }

    public void setAddr(@NonNull String addr) {
        this.mAddr = addr;
    }

    @NonNull
    public long getTime() {
        return mTime;
    }

    public void setTime(@NonNull long time) {
        this.mTime = time;
    }

    @NonNull
    public double getTemp() {
        return mTemp;
    }

    public void setTemp(@NonNull double temp) {
        this.mTemp = temp;
    }

    @NonNull
    public int getHumi() {
        return mHumi;
    }

    public void setHumi(@NonNull int humi) {
        this.mHumi = humi;
    }

    @NonNull
    public int getRssi() {
        return mRssi;
    }

    public void setRssi(@NonNull int rssi) {
        this.mRssi = rssi;
    }

}
