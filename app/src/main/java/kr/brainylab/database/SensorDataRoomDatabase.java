package kr.brainylab.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {SensorData.class}, version = 1, exportSchema = false)
abstract class SensorDataRoomDatabase extends RoomDatabase {

    public abstract SensorDataDao sensorDataDao();

    private static volatile SensorDataRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static SensorDataRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SensorDataRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SensorDataRoomDatabase.class, "sensordata_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                /*
                SensorDataDao dao = INSTANCE.sensorDataDao();
                dao.deleteAll();

                SensorData sensorData = new SensorData("");
                dao.insert(sensorData);
                sensorData = new SensorData("World");
                dao.insert(sensorData);
                 */
            });
        }
    };
}
