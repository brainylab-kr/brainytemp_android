package kr.brainylab.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SensorDataDao {

    @Query("SELECT * FROM sensordata_table ORDER BY id ASC")
    LiveData<List<SensorData>> getAllSensorDatas();

    @Query("SELECT * FROM sensordata_table WHERE time BETWEEN :startTime AND :endTime ORDER BY id ASC")
    LiveData<List<SensorData>> getSensorDatas(long startTime, long endTime);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SensorData sensorData);

    @Query("DELETE FROM sensordata_table")
    void deleteAll();
}
