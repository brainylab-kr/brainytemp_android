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
import android.os.IBinder;
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
import kr.brainylab.database.SensorData;
import kr.brainylab.database.SensorDataRepository;
import kr.brainylab.model.AlarmListInfo;
import kr.brainylab.model.SensorInfo;
import kr.brainylab.model.ValueListInfo;
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
import pl.efento.sdk.api.scan.SoftwareVersion;

import static kr.brainylab.utils.PreferenceMgr.PREF_SENSOR_LIST;


public class SensorHandleService extends Service {
    public static Intent serviceIntent = null;

    Scanner scanner;

    private int mTimerCount = 0;
    private Timer mTimer;

    private SensorDataRepository mRepository;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mRepository = new SensorDataRepository(getApplication());
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
        updateSensorValue();

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

        ArrayList<SensorInfo> currentSensingList = new ArrayList<SensorInfo>();

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
                    ArrayList<SensorInfo> existedSensor = Util.getSensorList();

                    for(int i = 0; i < existedSensor.size(); i++) {
                        SensorInfo sensorInfo = existedSensor.get(i);

                        boolean isExisted = false;
                        for (int j = 0; j < currentSensingList.size(); j++) {
                            SensorInfo currentSenser = currentSensingList.get(j);
                            if (currentSenser != null && sensorInfo.getAddress().equals(currentSenser.getAddress())) {
                                isExisted = true;
                            }
                        }

                        if(isExisted == false) {
                            sensorInfo.setRssi(-100);
                            updateSensor(sensorInfo);
                            addSensorValue(sensorInfo);
                            uploadData(sensorInfo);

                            Intent screenUpdateIntent = new Intent(Common.ACT_SENSOR_VALUE_UPDATE);
                            LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(screenUpdateIntent);
                        }
                    }

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
                    SensorInfo sensor = Util.getSensorInfo(device.getAddress());

                    Map<Integer, Measurement> map = device.getMeasurements();

                    double curTemp = 0;
                    int curHumi = 0;

                    if(map.get(1) != null && map.get(1).isValid()) {
                        curTemp = Double.valueOf(map.get(1).get().toString());
                    }

                    if((sensor.getType().equals(Common.SENSOR_TYPE_TH))
                            && (map.get(2) != null && map.get(2).isValid())) {
                        curHumi = Integer.valueOf(map.get(2).get().toString());
                    }

                    updateSensor(device);    // 센서 정보 업데이트
                    addSensorValue(device);    // 로컬DB에 온도 추가
                    uploadData(device);    //서버에 온도 전송

                    BrainyTempApp.setUpdateTime(device.getAddress(), "" + System.currentTimeMillis());

                    Intent screenUpdateIntent = new Intent(Common.ACT_SENSOR_VALUE_UPDATE);
                    LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(screenUpdateIntent);

                    double maxTemp = BrainyTempApp.getMaxTemp(device.getAddress());
                    double minTemp = BrainyTempApp.getMinTemp(device.getAddress());

                    if (curTemp < minTemp || curTemp > maxTemp) {
                        if (curTemp < minTemp) {
                            prepareEventAlarm(device.getAddress(), Common.EVENT_LOW_TEMP, curTemp);
                        } else {
                            prepareEventAlarm(device.getAddress(), Common.EVENT_HIGH_TEMP, curTemp);
                        }

                        showAlarm(device.getAddress(), curTemp, curHumi);
                    }

                    int maxHumi = BrainyTempApp.getMaxHumi(device.getAddress());
                    int minHumi = BrainyTempApp.getMinHumi(device.getAddress());

                    if (sensor.getType().equals(Common.SENSOR_TYPE_TH) && (curHumi < minHumi || curHumi > maxHumi)) {
                        if (curHumi < minHumi) {
                            prepareEventAlarm(device.getAddress(), Common.EVENT_LOW_HUMI, curHumi);
                        } else {
                            prepareEventAlarm(device.getAddress(), Common.EVENT_HIGH_HUMI, curHumi);
                        }

                        showAlarm(device.getAddress(), curTemp, curHumi);
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
                                prepareEventAlarm(device.getAddress(), Common.EVENT_APP_ERR, 0);
                            }
                        })
                        .setProgressCallback(new OnProgressCallback() {
                            @Override
                            public void onProgress(int i) {
                            }
                        }
                    );

                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    currentSensingList.add(new SensorInfo(
                            sensor.getType(),
                            device.getName(),
                            device.getAddress(),
                            df.format(c),
                            curTemp,
                            curHumi,
                            device.getRssi(),
                            device.getBatteryStatus(),
                            device.getCalibrationDate(),
                            device.getCounter(),
                            device.getEncryptionStatus(),
                            device.getPeriod(),
                            device.getFeatures(),
                            device.getConnectivityStatus(),
                            device.getSoftwareVersion()
                    ));

                    if(currentSensingList.size() ==  Util.getSensorList().size()){
                        currentSensingList.clear();

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
            }
        });
    }

    public void updateSensor(Device device) {
        long storeTime = (long) Double.parseDouble(BrainyTempApp.getUpdateTime(device.getAddress()));
        long currentTime = System.currentTimeMillis();
        int dicSec = (int) ((currentTime - storeTime) / 1000);

        ArrayList<SensorInfo> sensorList = Util.getSensorList();
        SensorInfo sensorInfo = Util.getSensorInfo(device.getAddress());
        int index = Util.getSensorIndex(device.getAddress());

        if(sensorInfo == null || index < 0) {
            return;
        }

        Map<Integer, Measurement> map = device.getMeasurements();
        double curTemp = 0;

        if(map.get(1) != null && map.get(1).isValid()) {
            curTemp = Double.valueOf(map.get(1).get().toString());
        }

        int curHumi = 0;
        if(sensorInfo.getType().equals(Common.SENSOR_TYPE_TH)
            && map.get(2) != null && map.get(2).isValid()) {
            curHumi = Integer.valueOf(map.get(2).get().toString());
        }

        String date = "";
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        date = df.format(c);

        sensorList.remove(index);
        SensorInfo dic = new SensorInfo(sensorInfo.getType(), device.getName(), device.getAddress(), date, curTemp, curHumi, device.getRssi(),
                device.getBatteryStatus(), device.getCalibrationDate(), device.getCounter(), device.getEncryptionStatus(), device.getPeriod(),
                device.getFeatures(), device.getConnectivityStatus(), device.getSoftwareVersion());
        sensorList.add(index, dic);
        BrainyTempApp.mPref.put(PREF_SENSOR_LIST, new Gson().toJson(sensorList));
    }

    public void updateSensor(SensorInfo sensorInfo) {
        long storeTime = (long) Double.parseDouble(BrainyTempApp.getUpdateTime(sensorInfo.getAddress()));
        long currentTime = System.currentTimeMillis();
        int dicSec = (int) ((currentTime - storeTime) / 1000);

        //if (dicSec < Common.gSearchTime)
        //    return;

        ArrayList<SensorInfo> sensorList = Util.getSensorList();
        int index = Util.getSensorIndex(sensorInfo.getAddress());

        if(index < 0) {
            return;
        }

        String date = "";
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        date = df.format(c);

        sensorList.remove(index);
        sensorList.add(index, sensorInfo);
        BrainyTempApp.mPref.put(PREF_SENSOR_LIST, new Gson().toJson(sensorList));
    }

    HttpService httpService;

    public void uploadData(Device device) {
        SensorInfo sensorInfo = Util.getSensorInfo(device.getAddress());

        Map<Integer, Measurement> map = device.getMeasurements();
        double temperature = Double.valueOf(map.get(1).get().toString());

        int humidity = 0;
        if(sensorInfo.getType().equals(Common.SENSOR_TYPE_TH)
                && map.get(2) != null && map.get(2).isValid()) {
            humidity = Integer.valueOf(map.get(2).get().toString());
        }

        int rssi = device.getRssi();

        httpService = new HttpService(BrainyTempApp.getInstance());
        httpService.uploadData(device.getAddress(), temperature, humidity, rssi, new HttpService.ResponseListener() {
            @Override
            public void onResponseResult(Boolean bSuccess, String res) {
                if (bSuccess) {
                    try {
                        JSONObject jObj = new JSONObject(res);
                        String result = jObj.getString("ret");
                        if (result.equals("ok")) {
                            successUpload(device.getAddress());
                            /*
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // 사용하고자 하는 코드

                                }
                            }, 0);
                            */
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

    public void uploadData(SensorInfo sensorInfo) {
        httpService = new HttpService(BrainyTempApp.getInstance());
        httpService.uploadData(sensorInfo.getAddress(), sensorInfo.getTemp(), sensorInfo.getHumi(), sensorInfo.getRssi(), new HttpService.ResponseListener() {
            @Override
            public void onResponseResult(Boolean bSuccess, String res) {
                if (bSuccess) {
                    try {
                        JSONObject jObj = new JSONObject(res);
                        String result = jObj.getString("ret");
                        if (result.equals("ok")) {
                            successUpload(sensorInfo.getAddress());
                            /*
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                }
                            }, 0);

                             */
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
    private void successUpload(String address) {
        httpService = null;
    }

    public void addSensorValue(Device info) {

        ArrayList<ValueListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(info.getAddress() + "sensorValue", ""),
                new TypeToken<ArrayList<ValueListInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<ValueListInfo>();
        }

        Map<Integer, Measurement> map = info.getMeasurements();
        double temperature = Double.valueOf(map.get(1).get().toString());

        SensorInfo sensorInfo = Util.getSensorInfo(info.getAddress());
        int humidity = 0;
        if(sensorInfo.getType().equals(Common.SENSOR_TYPE_TH)
                && map.get(2) != null && map.get(2).isValid()) {
            humidity = Integer.valueOf(map.get(2).get().toString());
        }

        ValueListInfo dic = new ValueListInfo(System.currentTimeMillis(), temperature, humidity);
        list.add(dic);
        BrainyTempApp.mPref.put(info.getAddress() + "sensorValue", new Gson().toJson(list));

    }

    public void addSensorValue(SensorInfo sensorInfo) {
        ArrayList<ValueListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(sensorInfo.getAddress() + "sensorValue", ""),
                new TypeToken<ArrayList<ValueListInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<ValueListInfo>();
        }

        ValueListInfo dic = new ValueListInfo(System.currentTimeMillis(), sensorInfo.getTemp(), sensorInfo.getHumi());
        list.add(dic);
        BrainyTempApp.mPref.put(sensorInfo.getAddress() + "sensorValue", new Gson().toJson(list));


        //public SensorData(@NonNull String addr, @NonNull long time, @NonNull double temp, @NonNull int humi, @NonNull int rssi)
        SensorData sensorData = new SensorData(sensorInfo.getAddress(), System.currentTimeMillis(), sensorInfo.getTemp(), sensorInfo.getHumi(), sensorInfo.getRssi());
        mRepository.insert(sensorData);


    }

    /**
     * 이벤트 SMS 알림
     */
    public void prepareEventAlarm(String device, String eventType, double curVal) {

        int alertCycle = Integer.valueOf(BrainyTempApp.getAlertRepeatCycle());
        if (alertCycle == 0) //설정페이지에서 설정한 알림 반복주기가 -1이면 알림 사용안함으로 본다.
            return;

        String key = device + eventType;
        long storeTime = (long) Double.parseDouble(BrainyTempApp.getAlertTime(key));
        long currentTime = System.currentTimeMillis();
        int dicSec = (int) ((currentTime - storeTime) / 1000);

        if (dicSec < (alertCycle * 60)-30) //알림 울리는 시간차가 알림반복주기시간보다 작으면 리턴
            return;

        BrainyTempApp.setAlertTime(key, "" + System.currentTimeMillis());

        ArrayList<AlarmListInfo> list = Util.getAlarmList();

        //알림 반복주기에 따라 호출하고,  이미 알림반복서비스가 진행중인가를 정확히 체크
        for (int i = 0; i < list.size(); i++) {
            AlarmListInfo info = list.get(i);

            if ((eventType.equals(Common.EVENT_LOW_TEMP) && info.getTemp())
                || (eventType.equals(Common.EVENT_HIGH_TEMP) && info.getTemp())) {
                double maxTemp = BrainyTempApp.getMaxTemp(device);
                double minTemp = BrainyTempApp.getMinTemp(device);
                String deviceName = BrainyTempApp.getSensorName(device);
                reqEventAlarm(device, deviceName, eventType, info.getType(), info.getPhone(), curVal, minTemp, maxTemp);
            }
            else if ((eventType.equals(Common.EVENT_LOW_HUMI) && info.getHumi())
                || (eventType.equals(Common.EVENT_HIGH_HUMI) && info.getHumi())) {
                int maxHumi = BrainyTempApp.getMaxHumi(device);
                int minHumi = BrainyTempApp.getMinHumi(device);
                String deviceName = BrainyTempApp.getSensorName(device);
                reqEventAlarm(device, deviceName, eventType, info.getType(), info.getPhone(), curVal, maxHumi, minHumi);
            }
            else if (eventType.equals(Common.EVENT_LOW_BT) && info.getBattery()
                || eventType.equals(Common.EVENT_APP_ERR) && info.getError()
                || eventType.equals(Common.EVENT_ONNECT_ERROR) && info.getConnect()) {
                String deviceName = BrainyTempApp.getSensorName(device);
                reqEventAlarm(device, deviceName, eventType, info.getType(), info.getPhone(), 0, 0, 0);
            }
        }
    }

    private void showAlarm(String device, double curTemp, int curHumi) {

        // 알람 화면 표시
        JSONObject sendData = new JSONObject();
        try {
            sendData.put("device", device);
            sendData.put("curTemp", curTemp);
            sendData.put("curHumi", curHumi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent screenUpdateIntent = new Intent(Common.ACT_ALARM_UPDATE);
        screenUpdateIntent.putExtra("data", sendData.toString());
        LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(screenUpdateIntent);

    }

    //서버에 이벤트 알림 업로드
    private void reqEventAlarm(String device, String deviceName, String eventType, String alarmType, String phone, double curVal, double minVal, double maxVal) {

        Log.d("BrainyTemp", "Send SMS Alert: " + device);
        HttpService httpService = new HttpService(this);
        httpService.eventAlarm(device, deviceName, eventType, alarmType, phone, curVal, minVal, maxVal, new HttpService.ResponseListener() {
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
        f.addAction(Common.ACT_SENSOR_RESCAN);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(f));
    }

    //boradcast receive
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Common.ACT_SENSOR_RESCAN)) {
            updateSensorValue();
        }
        }
    };

}
