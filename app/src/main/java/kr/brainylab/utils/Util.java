package kr.brainylab.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

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
import kr.brainylab.common.HttpService;
import kr.brainylab.model.AlarmListInfo;
import kr.brainylab.model.SensorListInfo;
import kr.brainylab.model.TempListInfo;
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
    public static void addSensor(Device info) {
        ArrayList<SensorListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorListInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<SensorListInfo>();
        }

        Map<Integer, Measurement> map = info.getMeasurements();
        double temperature = Double.valueOf(map.get(1).get().toString());
        SensorListInfo item = new SensorListInfo(info.getName(), info.getAddress(), getCurDate(), temperature, info.getRssi());
        list.add(item);
        BrainyTempApp.mPref.put(PREF_SENSOR_LIST, new Gson().toJson(list));

        //센서 닉네임 추가
        BrainyTempApp.setSensorName(info.getAddress(), info.getName());
        BrainyTempApp.setMaxTemp(info.getAddress(), 8.0);
        BrainyTempApp.setMinTemp(info.getAddress(), 2.0);
        BrainyTempApp.setDelayTime(info.getAddress(), 0);
    }

    /**
     * 센서 갱신
     */
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

        SensorListInfo dic = new SensorListInfo(info.getName(), info.getAddress(), getCurDate(), temperature, info.getRssi());
        list.add(index, dic);
        BrainyTempApp.mPref.put(PREF_SENSOR_LIST, new Gson().toJson(list));
    }

    /**
     * 센서 삭제
     */
    public static boolean deleteSensor(String idx) {
        ArrayList<SensorListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorListInfo>>() {
                }.getType());


        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            SensorListInfo info = list.get(i);
            if (info.getDevice().equals(idx)) {
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
        ArrayList<SensorListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorListInfo>>() {
                }.getType());
        if (list == null) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            SensorListInfo inf0 = list.get(i);
            if (inf0.getDevice().equals(idx))
                return true;
        }

        return false;
    }

    /**
     * 센서목록 얻기
     */
    public static ArrayList<SensorListInfo> getSensorList() {
        ArrayList<SensorListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(PREF_SENSOR_LIST, ""),
                new TypeToken<ArrayList<SensorListInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<SensorListInfo>();
        }

        return list;
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

        Intent sendIntent = new Intent(Common.ACT_ALARM_UPDATE);
        context.sendBroadcast(sendIntent);
    }

    /**
     * 온도 추가
     */
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
     * 온도 리스트 삭제
     */
    public static void deleteTemp(String idx) {
        ArrayList<TempListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(idx + "temp", ""),
                new TypeToken<ArrayList<TempListInfo>>() {
                }.getType());

        if (list == null) {
            return;
        }
        list.clear();
        BrainyTempApp.mPref.put(idx + "temp", new Gson().toJson(list));
    }

    //디바이스 온도 서버에 업로드
    public static void uploadTemp(Device device) {

        Log.d("BrainyTemp", "Util.java uploadTemp");

        Map<Integer, Measurement> map = device.getMeasurements();
        double temperature = Double.valueOf(map.get(1).get().toString());

        Log.d("BrainyTemp", "@@@@@@@@@@@@@@@@@ upload Temp " + device.getAddress() + ", " + map.get(1).get().toString());

        HttpService httpService = new HttpService(BrainyTempApp.getInstance());
        httpService.uploadTemp(device.getAddress(), temperature, new HttpService.ResponseListener() {
            @Override
            public void onResponseResult(Boolean bSuccess, String res) {
                closeProgress();
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
                                    Log.d("BrainyTemp", "@@@@@@@@@@@@@@@@@ upload Temp success ");
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

    /**
     * 저장하였던 온도리스트 얻기
     */
    public static ArrayList<TempListInfo> getSensorTempList(String device) {
        ArrayList<TempListInfo> list = new Gson().fromJson(BrainyTempApp.mPref.getValue(device + "temp", ""),
                new TypeToken<ArrayList<TempListInfo>>() {
                }.getType());
        if (list == null) {
            list = new ArrayList<TempListInfo>();
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

        int dealyTime = Integer.valueOf(BrainyTempApp.getDelayTime(device));
        if (list.size() >= dealyTime) {
            list.remove(0);
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
}
