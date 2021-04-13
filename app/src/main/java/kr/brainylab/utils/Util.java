package kr.brainylab.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.model.AlarmListInfo;
import kr.brainylab.model.SensorInfo;
import kr.brainylab.model.ValueListInfo;
import pl.efento.sdk.api.measurement.Measurement;
import pl.efento.sdk.api.scan.Device;

import static kr.brainylab.utils.PreferenceMgr.PREF_ALARM_LIST;
import static kr.brainylab.utils.PreferenceMgr.PREF_SENSOR_LIST;

public class Util {
    private static ProgressDialog _progressDlg = null;

    public static void showToast(Context p_context, int strId) {
        String msg = p_context.getString(strId);
        showToast(p_context, msg);
    }

    public static void showToast(Context p_context, String msg) {
        Toast.makeText(p_context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Close keyboard
     */
    public static void hideKeyboard(EditText edit) {
        InputMethodManager imm = (InputMethodManager) edit.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    //pattern ==> "yyyyMMddHHmmss"
    public static String getNowDateTime(String pattern) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String getLocalProfilePath(Context context, String id) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        String state = Environment.getExternalStorageState();
        String tempFolderPath;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            tempFolderPath = Environment.getExternalStorageDirectory().getPath() + "/lvc/";
        } else {
            tempFolderPath = context.getApplicationInfo().dataDir;
        }

        File tempFolder = new File(tempFolderPath);
        if (!tempFolder.exists())
            tempFolder.mkdirs();

        String profileFilePath = tempFolderPath + id + ".jpg";

        return profileFilePath;
    }

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void showProgress(Context _context, boolean cancelable) {

        if (_progressDlg != null)
            return;

        try {
            _progressDlg = new ProgressDialog(_context, R.style.MyDialogTheme);
            _progressDlg.setIndeterminate(true);

            Drawable drawable = new ProgressBar(_context).getIndeterminateDrawable().mutate();
            drawable.setColorFilter(ContextCompat.getColor(_context, R.color.color_2c8aaa),
                    PorterDuff.Mode.SRC_IN);
            _progressDlg.setIndeterminateDrawable(drawable);


            _progressDlg.setCancelable(cancelable);
            _progressDlg
                    .setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            _progressDlg.show();

        } catch (Exception e) {
        }
    }

    public static void closeProgress() {
        try {
            if ((_progressDlg != null) && _progressDlg.isShowing()) {
                _progressDlg.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            _progressDlg = null;
        }
    }

    public static String getCurDate() {
        String result = "";

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        result = df.format(c);

        return result;
    }

    /**
     * 센서 추가
     */
    public static void addSensor(Device device) {
        ArrayList<SensorInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<SensorInfo>();
        }
        String type = "";

        Map<Integer, Measurement> map = device.getMeasurements();
        double temperature = Double.valueOf(map.get(1).get().toString());

        int humidity = 0;
        if(map.get(2) != null && map.get(2).isValid()) {
            humidity = Integer.valueOf(map.get(2).get().toString());
            type = Common.SENSOR_TYPE_TH;
        }
        else {
            type = Common.SENSOR_TYPE_T1;
        }

        SensorInfo item = new SensorInfo(type, device.getName(), device.getAddress(), getCurDate(), temperature, humidity, device.getRssi(),
                device.getBatteryStatus(), device.getCalibrationDate(), device.getCounter(), device.getEncryptionStatus(), device.getPeriod(),
                device.getFeatures(), device.getConnectivityStatus(), device.getSoftwareVersion());
        list.add(item);
        BrainyTempApp.mPref.put(PREF_SENSOR_LIST, new Gson().toJson(list));

        BrainyTempApp.setSensorName(device.getAddress(), device.getName());
        BrainyTempApp.setMaxTemp(device.getAddress(), 8.0);
        BrainyTempApp.setMinTemp(device.getAddress(), 2.0);
        BrainyTempApp.setMaxHumi(device.getAddress(), 80);
        BrainyTempApp.setMinHumi(device.getAddress(), 20);
        BrainyTempApp.setDelayTime(device.getAddress(), 0);
    }

    /**
     * 센서 삭제
     */
    public static boolean deleteSensor(String idx) {
        ArrayList<SensorInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorInfo>>() {
                }.getType());


        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            SensorInfo info = list.get(i);
            if (info.getAddress().equals(idx)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        }
        list.remove(index);
        BrainyTempApp.mPref.put(PREF_SENSOR_LIST, new Gson().toJson(list));
        return true;
    }

    /**
     * 등록된 센서인지 체크
     */
    public static boolean isExistSensor(String idx) {
        ArrayList<SensorInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorInfo>>() {
                }.getType());
        if (list == null) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            SensorInfo info = list.get(i);
            if (info.getAddress().equals(idx))
                return true;
        }

        return false;
    }

    /**
     * 센서목록 얻기
     */
    public static ArrayList<SensorInfo> getSensorList() {
        ArrayList<SensorInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<SensorInfo>();
        }

        return list;
    }

    public static SensorInfo getSensorInfo(String address) {
        ArrayList<SensorInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorInfo>>() {
                }.getType());

        if (list == null) {
            return null;
        }

        for(int i = 0; i < list.size(); i++) {
            SensorInfo sensor = list.get(i);
            if(sensor.getAddress().equals(address)) {
                return sensor;
            }
        }
        return null;
    }

    public static SensorInfo getSensorInfo(int index) {
        ArrayList<SensorInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorInfo>>() {
                }.getType());

        return list.get(index);
    }

    public static int getSensorIndex(String address) {

        ArrayList<SensorInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<SensorInfo>();
        }

        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            SensorInfo sensorInfo = list.get(i);
            if (sensorInfo.getAddress().equals(address)) {
                index = i;
                break;
            }
        }

        return index;
    }

    /**
     * 알림 추가
     */
    public static boolean addAlarm(AlarmListInfo info) {
        ArrayList<AlarmListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_ALARM_LIST, ""),
                new TypeToken<ArrayList<AlarmListInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<AlarmListInfo>();
        }

        list.add(info);
        BrainyTempApp.mPref.put(PREF_ALARM_LIST, new Gson().toJson(list));
        return true;
    }

    /**
     * 알림 삭제
     */
    public static boolean deleteAlarm(String phone) {
        ArrayList<AlarmListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_ALARM_LIST, ""),
                new TypeToken<ArrayList<AlarmListInfo>>() {
                }.getType());


        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            AlarmListInfo info = list.get(i);
            if (info.getPhone().equals(phone)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        }
        list.remove(index);
        BrainyTempApp.mPref.put(PREF_ALARM_LIST, new Gson().toJson(list));
        return true;
    }

    /**
     * 등록된 전화번호인지 체크
     */
    public static boolean isExistAlarm(String phone) {
        ArrayList<AlarmListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_ALARM_LIST, ""),
                new TypeToken<ArrayList<AlarmListInfo>>() {
                }.getType());
        if (list == null) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            AlarmListInfo inf0 = list.get(i);
            if (inf0.getPhone().equals(phone))
                return true;
        }

        return false;
    }

    /**
     * 알림목록 얻기
     */
    public static ArrayList<AlarmListInfo> getAlarmList() {
        ArrayList<AlarmListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_ALARM_LIST, ""),
                new TypeToken<ArrayList<AlarmListInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<AlarmListInfo>();
        }

        return list;
    }

    /**
     * 등록된 알림 변경
     */
    public static void updateAlarm(Context context, String oldPhone, AlarmListInfo info) {

        ArrayList<AlarmListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_ALARM_LIST, ""),
                new TypeToken<ArrayList<AlarmListInfo>>() {
                }.getType());

        if (list == null) {
            return;
        }

        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            AlarmListInfo item = list.get(i);
            if (item.getPhone().equals(oldPhone)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return;
        }
        list.remove(index);
        list.add(index, info);
        BrainyTempApp.mPref.put(PREF_ALARM_LIST, new Gson().toJson(list));

        Intent sendIntent = new Intent(Common.ACT_ALARM_LIST_UPDATE);
        context.sendBroadcast(sendIntent);
    }

    /**
     * 온도 리스트 삭제
     */
    public static void deleteSensorValue(String idx) {
        ArrayList<ValueListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(idx + "sensorValue", ""),
                new TypeToken<ArrayList<ValueListInfo>>() {
                }.getType());

        if (list == null) {
            return;
        }
        list.clear();
        BrainyTempApp.mPref.put(idx + "temp", new Gson().toJson(list));
    }

    /**
     * 저장하였던 온도리스트 얻기
     */
    public static ArrayList<ValueListInfo> getSensorValueList(String device) {
        ArrayList<ValueListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(device + "sensorValue", ""),
                new TypeToken<ArrayList<ValueListInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<ValueListInfo>();
        }

        return list;
    }

    /**
     * 현재 온도 측정 리스트
     */
    public static ArrayList<String> getMeasureList(String device) {
        ArrayList<String> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(device + "templist", ""),
                new TypeToken<ArrayList<String>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<String>();
        }

        return list;
    }

    /**
     * 측정 온도 추가
     */
    public static void addMeasureTemp(String device, double temp) {
        ArrayList<String> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(device + "templist", ""),
                new TypeToken<ArrayList<String>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<String>();
        }

        list.add(String.valueOf(temp));
        BrainyTempApp.mPref.put(device + "templist", new Gson().toJson(list));

        BrainyTempApp.setMeasureTime(device, "" + System.currentTimeMillis());
    }

    /**
     * 측정 온도 삭제
     */
    public static void deleteMeasureTemp(String device) {
        ArrayList<String> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(device + "templist", ""),
                new TypeToken<ArrayList<String>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<String>();
        }

        list.clear();
        BrainyTempApp.mPref.put(device + "templist", new Gson().toJson(list));
    }

    /**
     * 습도 리스트 삭제
     */
    public static void deleteHumi(String idx) {
        ArrayList<ValueListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(idx + "humi", ""),
                new TypeToken<ArrayList<ValueListInfo>>() {
                }.getType());

        if (list == null) {
            return;
        }
        list.clear();
        BrainyTempApp.mPref.put(idx + "humi", new Gson().toJson(list));
    }

    /**
     * 저장하였던 습도리스트 얻기
     */
    public static ArrayList<ValueListInfo> getSensorHumiList(String device) {
        ArrayList<ValueListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(device + "humi", ""),
                new TypeToken<ArrayList<ValueListInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<ValueListInfo>();
        }

        return list;
    }

    /**
     * 현재 습도 측정 리스트
     */
    public static ArrayList<String> getHumiMeasureList(String device) {
        ArrayList<String> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(device + "humilist", ""),
                new TypeToken<ArrayList<String>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<String>();
        }

        return list;
    }

    /**
     * 측정 습도 추가
     */
    public static void addMeasureHumi(String device, int humi) {

        ArrayList<String> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(device + "humilist", ""),
                new TypeToken<ArrayList<String>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<String>();
        }

        list.add(String.valueOf(humi));
        BrainyTempApp.mPref.put(device + "templist", new Gson().toJson(list));

        BrainyTempApp.setMeasureTime(device, "" + System.currentTimeMillis());
    }

    /**
     * 측정 습도 삭제
     */
    public static void deleteMeasureHumi(String device) {
        ArrayList<String> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(device + "humilist", ""),
                new TypeToken<ArrayList<String>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<String>();
        }

        list.clear();
        BrainyTempApp.mPref.put(device + "humilist", new Gson().toJson(list));
    }
}
