package kr.brainylab.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;

public class SensorDataRepository {
    private SensorDataDao mSensorDataDao;

    public SensorDataRepository(Application application) {
        SensorDataRoomDatabase db = SensorDataRoomDatabase.getDatabase(application);
        mSensorDataDao = db.sensorDataDao();
    }


    public Flowable<List<SensorData>> getSensorDatas(String addr, long startTime, long endTime) {
        //Log.d("BrainyTemp", "get " + addr + ", " + startTime + ", " + endTime);

        return mSensorDataDao.getSensorDatas(addr, startTime, endTime);
    }

    public void insert(SensorData sensorData) {
        //Log.d("BrainyTemp", "insert " + sensorData.getAddr() + ", " + sensorData.getTime() + ", " + sensorData.getTemp() + ", " + sensorData.getHumi());
        SensorDataRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSensorDataDao.insert(sensorData);
        });
    }

    public void deleteSensorData(String addr) {
        //Log.d("BrainyTemp", "deleteSensorData " + addr);
        SensorDataRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSensorDataDao.deleteSensorData(addr);
        });
    }

    public void deleteSensorData(String addr, Date startTime, Date endTime) {
        SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Log.d("BrainyTemp", "deleteSensorData " + addr
                +", startTime: " + formattedDate.format(startTime)
                +", endTime: " + formattedDate.format(endTime));
        SensorDataRoomDatabase.databaseWriteExecutor.execute(() -> {
            mSensorDataDao.deleteSensorData(addr, startTime.getTime(), endTime.getTime());
        });
    }
}
