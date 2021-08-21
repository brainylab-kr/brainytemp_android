package kr.brainylab.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.common.HttpService;
import kr.brainylab.model.SensorInfo;
import kr.brainylab.utils.Util;

public class AlarmReceiver extends BroadcastReceiver {

    private HttpService httpService;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String address = BrainyTempApp.getDailyReportAddress();
        boolean dailyReport = BrainyTempApp.getDailyReport();
        boolean weeklyReport = BrainyTempApp.getWeeklyReport();

        String startDate = "";
        String endDate = "";

        Log.d("BrainyTemp", "dailyReport: " + dailyReport);
        Log.d("BrainyTemp", "weeklyReport: " + weeklyReport);
        if(dailyReport == true) {

            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            calendar.setTime(date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            calendar.add(Calendar.DATE, -1);

            startDate = dateFormat.format(calendar.getTime()) + " 00:00:00";
            endDate = dateFormat.format(calendar.getTime()) + " 23:59:59";;

            Log.d("BrainyTemp", "Daily Report startDate: " + startDate);
            Log.d("BrainyTemp", "Daily Report endDate: " + endDate);

            ArrayList<SensorInfo> sensorList = Util.getSensorList();

            if(sensorList.size() <= 0) {
                return;
            }

            for(int i = 0; i < sensorList.size(); i++) {
                SensorInfo sensor = sensorList.get(i);
                String reportType = "T";
                if(sensor.getType().equals(Common.SENSOR_TYPE_TH)) {
                    reportType = "TH";
                }
                else {
                    reportType = "T";
                }

                httpService = new HttpService(BrainyTempApp.getInstance());
                httpService.requestReport(sensor.getAddress(), sensor.getName(), address, startDate, endDate, reportType, new HttpService.ResponseListener() {
                    @Override
                    public void onResponseResult(Boolean bSuccess, String res) {
                        if (bSuccess) {
                            Log.d("BrainyTemp", "onResponseResult:" + res);
                            try {
                                JSONObject jObj = new JSONObject(res);
                                String result = jObj.getString("ret");
                                if (result.equals("ok")) {
                                    successUpload();
                                    Log.d("BrainyTemp", BrainyTempApp.getInstance().getResources().getString(R.string.send_report));
                                    //Toast.makeText(context, BrainyTempApp.getInstance().getResources().getString(R.string.send_report), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.d("BrainyTemp", "err.. : " + e.toString());
                                //Toast.makeText(context, BrainyTempApp.getInstance().getResources().getString(R.string.fail_report), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("BrainyTemp", "err.. : " + BrainyTempApp.getInstance().getResources().getString(R.string.connect_fail));
                            //Toast.makeText(context, BrainyTempApp.getInstance().getResources().getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        if(weeklyReport == true) {

            Calendar calendar = Calendar.getInstance();
            int weekday = calendar.get(Calendar.DAY_OF_WEEK);

            if(weekday != Calendar.MONDAY) {
                return;
            }

            Date date = calendar.getTime();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            endDate = dateFormat.format(calendar.getTime()) + " 23:59:59";;


            date = calendar.getTime();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -7);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            startDate = dateFormat.format(calendar.getTime()) + " 00:00:00";

            Log.d("BrainyTemp", "Weekly Report startDate: " + startDate);
            Log.d("BrainyTemp", "Weekly Report endDate: " + endDate);

            ArrayList<SensorInfo> sensorList = Util.getSensorList();

            if(sensorList.size() <= 0) {
                return;
            }

            for(int i = 0; i < sensorList.size(); i++) {
                SensorInfo sensor = sensorList.get(i);
                String reportType = "T";
                if(sensor.getType().equals(Common.SENSOR_TYPE_TH)) {
                    reportType = "TH";
                }
                else {
                    reportType = "T";
                }

                httpService = new HttpService(BrainyTempApp.getInstance());
                httpService.requestReport(sensor.getAddress(), sensor.getName(), address, startDate, endDate, reportType, new HttpService.ResponseListener() {
                    @Override
                    public void onResponseResult(Boolean bSuccess, String res) {
                        if (bSuccess) {
                            Log.d("BrainyTemp", "onResponseResult:" + res);
                            try {
                                JSONObject jObj = new JSONObject(res);
                                String result = jObj.getString("ret");
                                if (result.equals("ok")) {
                                    successUpload();
                                    Log.d("BrainyTemp", BrainyTempApp.getInstance().getResources().getString(R.string.send_report));
                                    //Toast.makeText(context, BrainyTempApp.getInstance().getResources().getString(R.string.send_report), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.d("BrainyTemp", "err.. : " + e.toString());
                                //Toast.makeText(context, BrainyTempApp.getInstance().getResources().getString(R.string.fail_report), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("BrainyTemp", "err.. : " + BrainyTempApp.getInstance().getResources().getString(R.string.connect_fail));
                            //Toast.makeText(context, BrainyTempApp.getInstance().getResources().getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    }

    private void successUpload() {
        httpService = null;
    }
}