package kr.brainylab.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.database.SensorData;
import kr.brainylab.database.SensorDataRepository;
import kr.brainylab.databinding.FragmentChartBinding;
import kr.brainylab.model.SensorInfo;
import kr.brainylab.model.ValueListInfo;
import kr.brainylab.utils.Util;
import kr.brainylab.view.activity.DetailActivity;

public class ChartFragment extends Fragment {

    View rootView;
    FragmentChartBinding binding;

    private boolean isStarted = false;
    private Timer mTimer;
    private ArrayList<ValueListInfo> arrDataList = new ArrayList<ValueListInfo>();

    SensorDataRepository mRepository;
    CompositeDisposable mDisposables;
    Disposable mDisposable;

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_chart, container, false);
        binding = DataBindingUtil.bind(rootView);

        loadCurrentData();
        startTimer();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isStarted == true) {
            startTimer();
        }
        else {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }

            if(mDisposables != null) {
                mDisposables.clear();
            }
            isStarted = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if(mDisposables != null) {
            mDisposables.clear();
        }
        isStarted = false;
    }

    private void loadCurrentData() {
        String device = ((DetailActivity) getActivity()).deviceID;

        //Log.d("BrainyTemp", "draw chart: " + device);

        SensorInfo sensorInfo = Util.getSensorInfo(device);

        double temp = sensorInfo.getTemp();
        double maxTemp = BrainyTempApp.getMaxTemp(device);
        double minTemp = BrainyTempApp.getMinTemp(device);
        int humi = 0;
        int maxHumi = 0;
        int minHumi = 0;

        if(sensorInfo.getType().equals(Common.SENSOR_TYPE_TH)) {
            humi = sensorInfo.getHumi();
            maxHumi = BrainyTempApp.getMaxHumi(device);
            minHumi = BrainyTempApp.getMinHumi(device);
        }
        else{
            binding.tvCurHumi.setVisibility(View.GONE);
            binding.humiLineChart.setVisibility(View.GONE);
        }

        if(sensorInfo.getIsDisconnected() == true) {
            binding.tvSensorName.setTextColor(getResources().getColor(R.color.gray));
        }
        else {
            if ((temp > maxTemp || temp < minTemp)
                    || (sensorInfo.getType().equals(Common.SENSOR_TYPE_TH) && (humi > maxHumi || humi < minHumi))) {
                binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_c2185b));
            } else {
                binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_171717));
            }
        }
        binding.tvSensorName.setText(BrainyTempApp.getSensorName(device));

        if(sensorInfo.getIsDisconnected() == true) {
            binding.tvCurTemp.setTextColor(getResources().getColor(R.color.gray));
        }
        else {
            if (temp > maxTemp || temp < minTemp) {
                binding.tvCurTemp.setTextColor(getResources().getColor(R.color.color_c2185b));
            } else {
                binding.tvCurTemp.setTextColor(getResources().getColor(R.color.color_171717));
            }
        }

        binding.tvCurTemp.setText(String.valueOf(temp) + "°C");

        if(sensorInfo.getIsDisconnected() == true) {
            binding.tvCurHumi.setTextColor(getResources().getColor(R.color.gray));
        }
        else {
            if (sensorInfo.getType().equals(Common.SENSOR_TYPE_TH)) {
                if (humi > maxHumi || humi < minHumi) {
                    binding.tvCurHumi.setTextColor(getResources().getColor(R.color.color_c2185b));
                } else {
                    binding.tvCurHumi.setTextColor(getResources().getColor(R.color.color_171717));
                }
                binding.tvCurHumi.setText(String.valueOf(humi) + "%");
            }
        }

        binding.tvSensorAddress.setText(sensorInfo.getAddress());
        binding.tvSensingTime.setText(sensorInfo.getDate());

    }

    private void startTimer() {
        mDisposables = new CompositeDisposable();

        mTimer = new Timer();
        //Set the schedule function and rate
        mTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                ((DetailActivity) getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadLayout();
                    }
                });
            }

        }, 0, 1000 * 60);

        isStarted = true;
    }

    private void loadLayout() {
        if (arrDataList.size() > 0) {
            arrDataList.clear();
        }

        String device = ((DetailActivity) getActivity()).deviceID;

        long currentTime = Calendar.getInstance().getTime().getTime();
        mRepository = new SensorDataRepository(getActivity().getApplication());
        mDisposable = mRepository.getSensorDatas(device, currentTime - 86400000, currentTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sensorDatas -> drawChart(sensorDatas),
                        throwable -> Log.e("BrainyTemp", "SensorData read Failed!", throwable));

        mDisposables.add(mDisposable);
    }

    private void drawChart(List<SensorData> sensorDatas) {

        if(sensorDatas.size() == 0) {
            return;
        }

        SensorData sensor = sensorDatas.get(sensorDatas.size()-1);

        SensorInfo sensorInfo = Util.getSensorInfo(sensor.getAddr());

        //Log.d("BrainyTemp", "draw chart: " + sensor.getAddr() + ", " + sensorInfo.getDate());

        double temp = sensorInfo.getTemp();
        double maxTemp = BrainyTempApp.getMaxTemp(sensor.getAddr());
        double minTemp = BrainyTempApp.getMinTemp(sensor.getAddr());
        int humi = 0;
        int maxHumi = 0;
        int minHumi = 0;

        if(sensorInfo.getType().equals(Common.SENSOR_TYPE_TH)) {
            humi = sensorInfo.getHumi();
            maxHumi = BrainyTempApp.getMaxHumi(sensor.getAddr());
            minHumi = BrainyTempApp.getMinHumi(sensor.getAddr());
        }
        else{
            binding.tvCurHumi.setVisibility(View.GONE);
            binding.humiLineChart.setVisibility(View.GONE);
        }

        if(sensorInfo.getIsDisconnected() == true) {
            binding.tvSensorName.setTextColor(getResources().getColor(R.color.gray));
        }
        else {
            if ((temp > maxTemp || temp < minTemp)
                    || (sensorInfo.getType().equals(Common.SENSOR_TYPE_TH) && (humi > maxHumi || humi < minHumi))) {
                binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_c2185b));
            } else {
                binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_171717));
            }
        }

        binding.tvSensorName.setText(BrainyTempApp.getSensorName(sensor.getAddr()));

        if(sensorInfo.getIsDisconnected() == true) {
            binding.tvCurTemp.setTextColor(getResources().getColor(R.color.gray));
        }
        else {
            if (temp > maxTemp || temp < minTemp) {
                binding.tvCurTemp.setTextColor(getResources().getColor(R.color.color_c2185b));
            } else {
                binding.tvCurTemp.setTextColor(getResources().getColor(R.color.color_171717));
            }
        }
        binding.tvCurTemp.setText(String.valueOf(temp) + "°C");

        if(sensorInfo.getIsDisconnected() == true) {
            binding.tvCurHumi.setTextColor(getResources().getColor(R.color.gray));
        }
        else {
            if (sensorInfo.getType().equals(Common.SENSOR_TYPE_TH)) {
                if (humi > maxHumi || humi < minHumi) {
                    binding.tvCurTemp.setTextColor(getResources().getColor(R.color.color_c2185b));
                } else {
                    binding.tvCurHumi.setTextColor(getResources().getColor(R.color.color_171717));
                }
                binding.tvCurHumi.setText(String.valueOf(humi) + "%");
            }
        }

        binding.tvSensorAddress.setText(sensorInfo.getAddress());
        binding.tvSensingTime.setText(sensorInfo.getDate());

        if(arrDataList.size() >= sensorDatas.size()) {
            return;
        }

        for(int i = 0; i < sensorDatas.size(); i++) {
           // Log.d("BrainyTemp", i + ":" + sensorDatas.get(i).getAddr() + ", " + sensorDatas.get(i).getTemp() + ", " + sensorDatas.get(i).getHumi());
            ValueListInfo value = new ValueListInfo(sensorDatas.get(i).getTime(), sensorDatas.get(i).getTemp(), sensorDatas.get(i).getHumi());
            arrDataList.add(value);
        }

        drawTempChart(sensor.getAddr());

        if(Util.getSensorInfo(sensor.getAddr()).getType().equals(Common.SENSOR_TYPE_TH)) {
            drawHumiChart(sensor.getAddr());
        }
    }

    private void drawTempChart(String device) {
        float maxTemp = (float) (BrainyTempApp.getMaxTemp(device));
        float minTemp = (float) (BrainyTempApp.getMinTemp(device));

        double curTemp = 0;
        if(arrDataList.size() > 0) {
            curTemp = arrDataList.get(arrDataList.size() - 1).getTemp();
        }

        SensorInfo sensorInfo = Util.getSensorInfo(device);

        if(sensorInfo.getIsDisconnected() == true) {
            binding.tvSensorName.setTextColor(getResources().getColor(R.color.gray));
        }
        else {
            if (curTemp > maxTemp || curTemp < minTemp) {
                binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_c2185b));
            } else {
                binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_171717));
            }
        }
        binding.tvSensorName.setText(BrainyTempApp.getSensorName(device));

        if(sensorInfo.getIsDisconnected() == true) {
            binding.tvCurTemp.setTextColor(getResources().getColor(R.color.gray));
        }
        else {
            if (curTemp > maxTemp || curTemp < minTemp) {
                binding.tvCurTemp.setTextColor(getResources().getColor(R.color.color_c2185b));
            } else {
                binding.tvCurTemp.setTextColor(getResources().getColor(R.color.color_171717));
            }
        }
        binding.tvCurTemp.setText(String.valueOf(curTemp) + "°C");

        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Long> times = new ArrayList<>();
        ArrayList<Entry> minValues = new ArrayList<>();
        ArrayList<Entry> maxValues = new ArrayList<>();
        for (int i = 0; i < arrDataList.size(); i++) {
            ValueListInfo info = arrDataList.get(i);
            double temp = info.getTemp();
            long time = info.getTime();
            float val = (float) (temp);
            values.add(new Entry(i, val));
            times.add(time);

            if (val > maxTemp)
                maxTemp = val;

            if (val < minTemp)
                minTemp = val;

            maxValues.add(new Entry(i, (float) (BrainyTempApp.getMaxTemp(device))));
            minValues.add(new Entry(i, (float) (BrainyTempApp.getMinTemp(device))));
        }

        XAxis xAxis = binding.tempLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new XAxisValueFormatter(times));
        xAxis.setDrawGridLines(false);

        YAxis yAxis = binding.tempLineChart.getAxisLeft();
        yAxis.setAxisMaximum(maxTemp + 5);
        yAxis.setAxisMinimum(minTemp - 5);
        binding.tempLineChart.getAxisRight().setEnabled(false);
        binding.tempLineChart.getDescription().setText("");
        binding.tempLineChart.setBackgroundColor(Color.TRANSPARENT); // 그래프 배경 색 설정
        yAxis.setValueFormatter(new LargeValueFormatter());

        LineDataSet set1 = new LineDataSet(values, "온도");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        /* black lines and points */
        set1.setColor(Color.BLACK); // 차트의 선 색 설정
        set1.setCircleColor(Color.BLACK); // 차트의 points 점 색 설정
        set1.setDrawCircles(false);
        set1.setValueTextSize(0);

        set1.setDrawFilled(false); // 차트 아래 fill(채우기) 설정
        set1.setFillColor(Color.BLACK); // 차트 아래 채우기 색 설정

        ////////////////////////////////////////////////////////
        //max line
        LineDataSet set2 = new LineDataSet(maxValues, "상한값");
        dataSets.add(set2); // add the data sets

        // black lines and points
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.TRANSPARENT);
        set2.setValueTextColor(Color.WHITE);
        set2.setDrawCircles(false);

        set2.setDrawFilled(false); // 차트 아래 fill(채우기) 설정
        set2.setFillColor(Color.RED); // 차트 아래 채우기 색 설정
        ////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////
        //min line
        LineDataSet set3 = new LineDataSet(minValues, "하한값");
        dataSets.add(set3); // add the data sets

        // black lines and points
        set3.setColor(Color.BLUE);
        set3.setCircleColor(Color.TRANSPARENT);
        set3.setValueTextColor(Color.WHITE);
        set3.setDrawCircles(false);

        set3.setDrawFilled(false); // 차트 아래 fill(채우기) 설정
        set3.setFillColor(Color.BLUE); // 차트 아래 채우기 색 설정
        ////////////////////////////////////////////////////////

        LineData data = new LineData(dataSets);
        binding.tempLineChart.setData(data);
        data.notifyDataChanged();
        binding.tempLineChart.notifyDataSetChanged();

        binding.tempLineChart.invalidate();
    }


    private void drawHumiChart(String device) {
        int maxHumi = (int) (BrainyTempApp.getMaxHumi(device));
        int minHumi = (int) (BrainyTempApp.getMinHumi(device));

        int curHumi = 0;
        if(arrDataList.size() > 0) {
            curHumi = arrDataList.get(arrDataList.size() - 1).getHumi();
        }

        SensorInfo sensorInfo = Util.getSensorInfo(device);
        if(sensorInfo.getIsDisconnected() == true) {
            binding.tvSensorName.setTextColor(getResources().getColor(R.color.gray));
        }
        else {
            if (curHumi > maxHumi || curHumi < minHumi) {
                binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_c2185b));
            } else {
                binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_171717));
            }
        }
        binding.tvSensorName.setText(BrainyTempApp.getSensorName(device));
        if(sensorInfo.getIsDisconnected() == true) {
            binding.tvCurHumi.setTextColor(getResources().getColor(R.color.gray));
        }
        else {
            if (curHumi > maxHumi || curHumi < minHumi) {
                binding.tvCurHumi.setTextColor(getResources().getColor(R.color.color_c2185b));
            } else {
                binding.tvCurHumi.setTextColor(getResources().getColor(R.color.color_171717));
            }
        }
        binding.tvCurHumi.setText(String.valueOf(curHumi) + "%");

        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Long> times = new ArrayList<>();
        ArrayList<Entry> minValues = new ArrayList<>();
        ArrayList<Entry> maxValues = new ArrayList<>();
        for (int i = 0; i < arrDataList.size(); i++) {
            ValueListInfo info = arrDataList.get(i);
            int humi = info.getHumi();
            long time = info.getTime();

            values.add(new Entry(i, humi));
            times.add(time);

            if (humi > maxHumi)
                maxHumi = humi;

            if (humi < minHumi)
                minHumi = humi;

            maxValues.add(new Entry(i, (int) (BrainyTempApp.getMaxHumi(device))));
            minValues.add(new Entry(i, (int) (BrainyTempApp.getMinHumi(device))));
        }

        XAxis xAxis = binding.humiLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new XAxisValueFormatter(times));
        xAxis.setDrawGridLines(false);

        YAxis yAxis = binding.humiLineChart.getAxisLeft();
        yAxis.setAxisMaximum(maxHumi + 5);
        yAxis.setAxisMinimum(minHumi - 5);
        binding.humiLineChart.getAxisRight().setEnabled(false);
        binding.humiLineChart.getDescription().setText("");
        binding.humiLineChart.setBackgroundColor(Color.TRANSPARENT); // 그래프 배경 색 설정
        yAxis.setValueFormatter(new LargeValueFormatter());

        LineDataSet set1 = new LineDataSet(values, "습도");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        /* black lines and points */
        set1.setColor(Color.BLACK); // 차트의 선 색 설정
        set1.setCircleColor(Color.BLACK); // 차트의 points 점 색 설정
        set1.setDrawCircles(false);
        set1.setValueTextSize(0);

        set1.setDrawFilled(false); // 차트 아래 fill(채우기) 설정
        set1.setFillColor(Color.BLACK); // 차트 아래 채우기 색 설정

        ////////////////////////////////////////////////////////
        //max line
        LineDataSet set2 = new LineDataSet(maxValues, "상한값");
        dataSets.add(set2); // add the data sets

        // black lines and points
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.TRANSPARENT);
        set2.setValueTextColor(Color.WHITE);
        set2.setDrawCircles(false);

        set2.setDrawFilled(false); // 차트 아래 fill(채우기) 설정
        set2.setFillColor(Color.RED); // 차트 아래 채우기 색 설정
        ////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////
        //min line
        LineDataSet set3 = new LineDataSet(minValues, "하한값");
        dataSets.add(set3); // add the data sets

        // black lines and points
        set3.setColor(Color.BLUE);
        set3.setCircleColor(Color.TRANSPARENT);
        set3.setValueTextColor(Color.WHITE);
        set3.setDrawCircles(false);

        set3.setDrawFilled(false); // 차트 아래 fill(채우기) 설정
        set3.setFillColor(Color.BLUE); // 차트 아래 채우기 색 설정
        ////////////////////////////////////////////////////////

        LineData data = new LineData(dataSets);
        binding.humiLineChart.setData(data);
        data.notifyDataChanged();
        binding.humiLineChart.notifyDataSetChanged();

        binding.humiLineChart.invalidate();
    }
}

class XAxisValueFormatter  extends ValueFormatter {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private ArrayList<Long> dateList;

    public XAxisValueFormatter (ArrayList<Long> dateList) {
        this.dateList = dateList;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        int axisValue = (int) value;
        if (axisValue >= 0 && axisValue < dateList.size()) {
            return dateFormat.format(dateList.get(axisValue));
        } else {
            return "";
        }

    }
}
