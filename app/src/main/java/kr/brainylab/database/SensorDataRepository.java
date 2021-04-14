package kr.brainylab.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SensorDataRepository {
    private SensorDataDao mSensorDataDao;

    public SensorDataRepository(Application application) {
        SensorDataRoomDatabase db = SensorDataRoomDatabase.getDatabase(application);
        mSensorDataDao = db.sensorDataDao();
    }

    LiveData<List<SensorData>> getSensorDatas(long startTime, long endTime) {
        return mSensorDataDao.getSensorDatas(startTime, endTime);
    }

    void insert(SensorData sensorData) {
        SensorDataRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSensorDataDao.insert(sensorData);
        });
    }
}
