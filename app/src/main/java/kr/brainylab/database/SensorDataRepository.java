package kr.brainylab.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SensorDataRepository {
    private SensorDataDao mSensorDataDao;

    public SensorDataRepository(Application application) {
        SensorDataRoomDatabase db = SensorDataRoomDatabase.getDatabase(application);
        mSensorDataDao = db.sensorDataDao();
    }

    List<SensorData> sensorDatas;

    public List<SensorData> getSensorDatas(String addr, long startTime, long endTime) {
        Log.d("BrainyTemp", "get " + addr + ", " + startTime + ", " + endTime);

        SensorDataRoomDatabase.databaseReadExecutor.execute(() -> {
            sensorDatas = mSensorDataDao.getSensorDatas(addr, startTime, endTime);

            for(int i = 0; i < sensorDatas.size(); i++) {

                Log.d("BrainyTemp",sensorDatas.get(i).getTime() + ", " + sensorDatas.get(i).getTemp() +", " + sensorDatas.get(i).getHumi());
            }
        });

        return sensorDatas;
    }

    public void insert(SensorData sensorData) {
        Log.d("BrainyTemp", "insert " + sensorData.getAddr() + ", " + sensorData.getTime() + ", " + sensorData.getTemp() + ", " + sensorData.getHumi());
        SensorDataRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSensorDataDao.insert(sensorData);
        });
    }

    public void deleteSensorData(String addr) {
        Log.d("BrainyTemp", "deleteSensorData " + addr);
        mSensorDataDao.deleteSensorData(addr);
    }
}
