package kr.brainylab.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.view.activity.AlertActivity;

public class MyWorkWithData extends Worker {

    private static final String TAB = MyWorkWithData.class.getSimpleName();
    static final String EXTRA_TITLE = "title";
    static final String EXTRA_TEXT = "text";
    static final String EXTRA_OUTPUT_MESSAGE = "output_message";

    private Context context = null;

    public MyWorkWithData(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = context;
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        long storeTime = (long) Double.parseDouble(BrainyTempApp.getScheduleTime());
        long currentTime = System.currentTimeMillis();
        int dicSec = (int) ((currentTime - storeTime) / 1000);
        int sesingCycle = Integer.valueOf(BrainyTempApp.getSensingRepeatCycle ()) * 60;

        BrainyTempApp.setScheduleTime("" + System.currentTimeMillis());

        Intent sendIntent = new Intent(Common.ACT_SENSOR_RESCAN);
        LocalBroadcastManager.getInstance(context).sendBroadcast(sendIntent);

        // Sending Data to MainActivity.
        Data data = new Data.Builder()
                .putString("title", "BrainyT")
                .putString("text", "BrainyT")
                .build();

        WorkManager.getInstance().cancelAllWork();
        OneTimeWorkRequest oneTimeWorkRequest =
                new OneTimeWorkRequest.Builder(MyWorkWithData.class)
                        .setInputData(data)
                        .setInitialDelay(sesingCycle, TimeUnit.SECONDS)
                        .build();

        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest);

        return Result.success();
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notification.build());
    }

}
