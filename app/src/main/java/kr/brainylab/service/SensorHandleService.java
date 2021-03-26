package kr.brainylab.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.common.HttpService;
import kr.brainylab.model.AlarmListInfo;
import kr.brainylab.model.SensorListInfo;
import kr.brainylab.model.TempListInfo;
import kr.brainylab.utils.MyWorkWithData;
import kr.brainylab.utils.Util;
import kr.brainylab.view.activity.MainActivity;

import pl.efento.sdk.Efento;
import pl.efento.sdk.api.OnErrorCallback;
import pl.efento.sdk.api.connection.OnProgressCallback;
import pl.efento.sdk.api.measurement.Measurement;
import pl.efento.sdk.api.scan.Device;
import pl.efento.sdk.api.scan.OnScanResultCallback;
import pl.efento.sdk.api.scan.Scanner;

import static kr.brainylab.utils.PreferenceMgr.PREF_SENSOR_LIST;


public class SensorHandleService extends Service {
    public static Intent serviceIntent = null;

    Scanner scanner;

    private int mTimerCount = 0;
    private Timer mTimer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;
        initializeNotification();

        registerReceiver();
        loadService();
        return START_STICKY;
    }

    public void initializeNotification() {
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("Brainy-T");
        style.setBigContentTitle(null);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentText(null);
        builder.setContentTitle(null);
        builder.setOngoing(true);
        builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(false);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("1", "SensorHandleService", NotificationManager.IMPORTANCE_NONE));
        }
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    private void loadService() {
        WorkManager.getInstance().cancelAllWork();

        int sesingCycle = Integer.valueOf(BrainyTempApp.getSensingRepeatCycle ()) * 60;

        Data data = new Data.Builder()
                .putString("title", "BrainyT")
                .putString("text", "BrainyT")
                .build();

        OneTimeWorkRequest oneTimeWorkRequest =
                new OneTimeWorkRequest.Builder(MyWorkWithData.class)
                        .setInputData(data)
                        .setInitialDelay(sesingCycle, TimeUnit.SECONDS)
                        .build();

        WorkManager.getInstance().enqueue(oneTimeWorkRequest);
    }

    private void updateSensorValue() {
        Log.d("BrainyTemp", "SensorHandleService updateSensorValue()");
        if (scanner != null) {
            scanner.stop();
        }

        mTimer = new Timer();
        //Set the schedule function and rate
        mTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                mTimerCount++;
                if (mTimerCount == 15) {
                    mTimerCount = 0;
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                    if (scanner != null) {
                        scanner.stop();
                    }
                }
            }

        }, 0, 1000);

        scanner = Efento.scanner().setErrorCallback(null).build();
        scanner.scan(new OnScanResultCallback() {
            @Override
            public void onResult(@NonNull Device device) {
                if (Util.isExistSensor(device.getAddress())) {
                    Map<Integer, Measurement> map = device.getMeasurements();
                    double curTemp = Double.valueOf(map.get(1).get().toString());

                    Log.d("BrainyTemp", "read temp " + device.getAddress() + ",  " + map.get(1).get().toString());

                    updateSensor(device);    // 센서 정보 업데이트
                    addSensorTemp(device);    // 로컬DB에 온도 추가
                    uploadTemp(device);    //서버에 온도 전송

                    BrainyTempApp.setUpdateTime(device.getAddress(), "" + System.currentTimeMillis());

                    // 화면 온도 업데이트
                    JSONObject sendData = new JSONObject();
                    try {
                        sendData.put(device.getAddress(), curTemp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent screenUpdateIntent = new Intent(Common.ACT_SCREEN_UPDATE);
                    screenUpdateIntent.putExtra("data", sendData.toString());
                    LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(screenUpdateIntent);

                    double maxTemp = BrainyTempApp.getMaxTemp(device.getAddress());
                    double minTemp = BrainyTempApp.getMinTemp(device.getAddress());

                    int dealyTime = Integer.valueOf(BrainyTempApp.getDelayTime(device.getAddress()));

                    if (dealyTime > 0) {
                        long storeDealyTime = (long) Double.parseDouble(BrainyTempApp.getMeasureTime(device.getAddress()));
                        long currentDelayTime = System.currentTimeMillis();
                        int dicSec = (int) ((currentDelayTime - storeDealyTime) / 1000);
                        if (dicSec > 15) {
                            Util.addMeasureTemp(device.getAddress(), curTemp);
                        }
                    }

                    if (curTemp < minTemp || curTemp > maxTemp) {
                        if (curTemp < minTemp) {
                            prepareEventAlarm(device.getAddress(), Common.EVENT_LOW_TEMP, curTemp);
                        } else {
                            prepareEventAlarm(device.getAddress(), Common.EVENT_HIGH_TEMP, curTemp);
                        }
                    }

                    if (device.getBatteryStatus() == Device.BatteryStatus.LOW) {
                        //센서 배터리 부족 알림 준비
                        prepareEventAlarm(device.getAddress(), Common.EVENT_LOW_BT, curTemp);
                    }

                    if (device.getConnectivityStatus() != Device.ConnectivityStatus.CONNECTABLE) {
                        //센서 통신 오류 알림 준비
                        prepareEventAlarm(device.getAddress(), Common.EVENT_ONNECT_ERROR, curTemp);
                    }

                    Efento.connect(device.getAddress())
                        .setErrorCallback(new OnErrorCallback() {
                            @Override
                            public void onError(@NonNull Throwable throwable) {
                                //센서 s/w 오류 알림 준비
                                prepareEventAlarm(device.getAddress(), Common.EVENT_APP_ERR, curTemp);
                            }
                        })
                        .setProgressCallback(new OnProgressCallback() {
                            @Override
                            public void onProgress(int i) {

                            }
                        }
                    );
                }
            }
        });
    }

    public static void updateSensor(Device info) {
        Log.d("BrainyTemp", "updateSensor");
        long storeTime = (long) Double.parseDouble(BrainyTempApp.getUpdateTime(info.getAddress()));
        long currentTime = System.currentTimeMillis();
        int dicSec = (int) ((currentTime - storeTime) / 1000);

        //if (dicSec < Common.gSearchTime)
        //    return;

        ArrayList<SensorListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorListInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<SensorListInfo>();
        }

        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            SensorListInfo item = list.get(i);
            if (item.getDevice().equals(info.getAddress())) {
                index = i;
                break;
            }
        }

        if (index > -1) {
            list.remove(index);
        }

        if (index == -1) {
            index = 0;
        }

        Map<Integer, Measurement> map = info.getMeasurements();
        double temperature = Double.valueOf(map.get(1).get().toString());

        String date = "";

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        date = df.format(c);

        SensorListInfo dic = new SensorListInfo(info.getName(), info.getAddress(), date, temperature, info.getRssi());
        list.add(index, dic);
        BrainyTempApp.mPref.put(PREF_SENSOR_LIST, new Gson().toJson(list));
    }

    public static void uploadTemp(Device device) {

        Log.d("BrainyTemp", "uploadTemp");

        Map<Integer, Measurement> map = device.getMeasurements();
        double temperature = Double.valueOf(map.get(1).get().toString());

        Log.d("BrainyTemp", "################ upload Temp " + device.getAddress() + ", " + map.get(1).get().toString());

        HttpService httpService = new HttpService(BrainyTempApp.getInstance());
        httpService.uploadTemp(device.getAddress(), temperature, new HttpService.ResponseListener() {
            @Override
            public void onResponseResult(Boolean bSuccess, String res) {
                if (bSuccess) {
                    try {
                        JSONObject jObj = new JSONObject(res);
                        String result = jObj.getString("ret");
                        if (result.equals("ok")) {
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // 사용하고자 하는 코드
                                    Log.d("BrainyTemp", "################ upload Temp success ");
                                }
                            }, 0);
                        }
                    } catch (JSONException e) {
                        Log.d("BrainyTemp", "err.. : " + e.toString());
                    }
                } else {
                    Log.d("BrainyTemp", "err.. : " + BrainyTempApp.getInstance().getResources().getString(R.string.connect_fail));
                }
            }
        });
    }
    public static void addSensorTemp(Device info) {
        ArrayList<TempListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(info.getAddress() + "temp", ""),
                new TypeToken<ArrayList<TempListInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<TempListInfo>();
        }

        Map<Integer, Measurement> map = info.getMeasurements();
        double temperature = Double.valueOf(map.get(1).get().toString());

        TempListInfo dic = new TempListInfo(System.currentTimeMillis(), temperature);
        list.add(dic);
        BrainyTempApp.mPref.put(info.getAddress() + "temp", new Gson().toJson(list));
    }

    /**
     * 이벤트 알림
     */
    public void prepareEventAlarm(String device, String eventType, double curTemp) {
        Log.d("BrainyTemp", "prepareEventAlarm " + device + ",  " + eventType);

        int alertCycle = Integer.valueOf(BrainyTempApp.getAlarmRepeatCycle());
        if (alertCycle == 0) //설정페이지에서 설정한 알림 반복주기가 -1이면 알림 사용안함으로 본다.
            return;

        String key = device + eventType;
        long storeTime = (long) Double.parseDouble(BrainyTempApp.getAlertTime(key));
        long currentTime = System.currentTimeMillis();
        int dicSec = (int) ((currentTime - storeTime) / 1000);

        if (dicSec < alertCycle * 60) //알림 울리는 시간차가 알림반복주기시간보다 작으면 리턴
            return;

        BrainyTempApp.setAlertTime(key, "" + System.currentTimeMillis());

        showAlarm(device, curTemp);

        ArrayList<AlarmListInfo> list = Util.getAlarmList();

        //알림 반복주기에 따라 호출하고,  이미 알림반복서비스가 진행중인가를 정확히 체크
        for (int i = 0; i < list.size(); i++) {
            AlarmListInfo info = list.get(i);

            if ((eventType.equals(Common.EVENT_LOW_TEMP) && info.getTemp())
                    || (eventType.equals(Common.EVENT_HIGH_TEMP) && info.getTemp())
                    || (eventType.equals(Common.EVENT_LOW_BT) && info.getBattery())
                    || (eventType.equals(Common.EVENT_APP_ERR) && info.getError())
                    || (eventType.equals(Common.EVENT_ONNECT_ERROR) && info.getConnect())) {
                double maxTemp = BrainyTempApp.getMaxTemp(device);
                double minTemp = BrainyTempApp.getMinTemp(device);
                String deviceName = BrainyTempApp.getSensorName(device);
                reqEventAlarm(device, deviceName, eventType, info.getType(), info.getPhone(), curTemp, minTemp, maxTemp);
            }
        }
    }

    private void showAlarm(String device, double curTemp) {

        // 알람 화면 표시
        JSONObject sendData = new JSONObject();
        try {
            sendData.put("device", device);
            sendData.put("curTemp", curTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent screenUpdateIntent = new Intent(Common.ACT_ALARM_UPDATE);
        screenUpdateIntent.putExtra("data", sendData.toString());
        LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(screenUpdateIntent);

    }

    //서버에 이벤트 알림 업로드
    private void reqEventAlarm(String device, String deviceName, String eventType, String alarmType, String phone, double curTemp, double minTemp, double maxTemp) {
        HttpService httpService = new HttpService(this);
        httpService.eventAlarm(device, deviceName, eventType, alarmType, phone, curTemp, minTemp, maxTemp, new HttpService.ResponseListener() {
            @Override
            public void onResponseResult(Boolean bSuccess, String res) {
                if (bSuccess) {
                    try {

                        JSONObject jObj = new JSONObject(res);
                        String result = jObj.getString("ret");
                        if (result.equals("ok")) {
                            Log.d("BrainyTemp", "이벤트 알림 업로드 성공");
                        } else {
                            Log.d("BrainyTemp", "이벤트 알림 업로드 실패");
                        }

                    } catch (JSONException e) {
                        Log.d("BrainyTemp", e.toString());
                    }
                } else {
                    Log.d("BrainyTemp", "서버 연결이 실패했습니다. 네트워크 상태를 확인해주세요.");
                }
            }
        });
    }

    void registerReceiver() {
        IntentFilter f = new IntentFilter();
        f.addAction(Common.ACT_SENSOR_VALUE_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(f));
    }

    //boradcast receive
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Common.ACT_SENSOR_VALUE_UPDATE)) {
            Log.d("BrainyTemp", "SensorHandleService.java mBroadcastReceiver ACT_SENSOR_VALUE_UPDATE");
            updateSensorValue();
        }
        }
    };

}
