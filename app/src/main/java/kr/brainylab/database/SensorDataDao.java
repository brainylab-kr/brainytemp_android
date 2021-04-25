package kr.brainylab.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface SensorDataDao {

    @Query("SELECT * FROM sensordata_table ORDER BY id ASC")
    Flowable<List<SensorData>> getAllSensorDatas();

    @Query("SELECT * FROM sensordata_table WHERE addr = :addr AND time BETWEEN :startTime AND :endTime ORDER BY id ASC")
    Flowable<List<SensorData>> getSensorDatas(String addr, long startTime, long endTime);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SensorData sensorData);

    @Query("DELETE FROM sensordata_table")
    void deleteAll();

    @Query("DELETE FROM sensordata_table WHERE addr = :addr")
    void deleteSensorData(String addr);

    @Query("DELETE FROM sensordata_table WHERE addr = :addr AND time BETWEEN :startTime AND :endTime")
    void deleteSensorData(String addr, long startTime, long endTime);
}
