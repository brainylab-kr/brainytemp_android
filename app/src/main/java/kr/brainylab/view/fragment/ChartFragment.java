/**
 * 홈
 */
package kr.brainylab.view.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.databinding.FragmentChartBinding;
import kr.brainylab.model.TempListInfo;
import kr.brainylab.utils.Util;
import kr.brainylab.view.activity.DetailActivity;

/**
 * 센서 상세에서 그래프
 */

public class ChartFragment extends Fragment {

    View rootView;
    FragmentChartBinding binding;
    private Timer mTimer;
    private ArrayList<TempListInfo> arrTempList = new ArrayList<TempListInfo>();

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
        //loadLayout();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            String device = ((DetailActivity) getActivity()).deviceID;
            double temp = ((DetailActivity) getActivity()).curTemp;
            double maxTemp = BrainyTempApp.getMaxTemp(device);
            double minTemp = BrainyTempApp.getMinTemp(device);

            if(temp > maxTemp || temp < minTemp) {
                binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_c2185b));
            }
            else {
                binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_171717));
            }
            binding.tvSensorName.setText(BrainyTempApp.getSensorName(device));

            if(temp > maxTemp || temp < minTemp) {
                binding.tvCurTemp.setTextColor(getResources().getColor(R.color.color_c2185b));
            }
            else {
                binding.tvCurTemp.setTextColor(getResources().getColor(R.color.color_171717));
            }
            binding.tvCurTemp.setText(String.valueOf(temp) + "°C");

            //binding.tvMaxTemp.setText(String.valueOf(maxTemp) + "°C");
            //binding.tvMinTemp.setText(String.valueOf(minTemp) + "°C");

            startTimer();
        }
        else {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    //현재 온도 업데이트
    public void updateCurrentTemp(double temp) {
        binding.tvCurTemp.setText(String.valueOf(temp) + "°C");
    }

    private void startTimer() {
        Log.d("BrainyTemp", "startTimer");
        mTimer = new Timer();
        //Set the schedule function and rate
        mTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadLayout();
                    }
                });
            }

        }, 0, 1000 * 60);
    }

    private void loadLayout() {

        Log.d("BrainyTemp", "loadLayout");
        if (arrTempList.size() > 0) {
            arrTempList.clear();
        }

        String device = ((DetailActivity) getActivity()).deviceID;

        ArrayList<TempListInfo> lists = Util.getSensorTempList(device);

        long currentTime = System.currentTimeMillis();
        //현재 시간으로부터 24시간데이터만 얻기

        for (int i = 0; i < lists.size(); i++) {
            TempListInfo info = lists.get(i);
            int dicSec = (int) ((currentTime - info.getTime()) / 1000);
            if (dicSec < 86400)
                arrTempList.add(info);
        }
        drawChart(device);
    }

    private void drawChart(String device) {
        float maxTemp = (float) (BrainyTempApp.getMaxTemp(device));
        float minTemp = (float) (BrainyTempApp.getMinTemp(device));


        double curTemp = 0;
        if(arrTempList.size() > 0) {
            curTemp = arrTempList.get(arrTempList.size() - 1).getTemp();
        }

        if(curTemp > maxTemp || curTemp < minTemp) {
            binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_c2185b));
        }
        else {
            binding.tvSensorName.setTextColor(getResources().getColor(R.color.color_171717));
        }
        binding.tvSensorName.setText(BrainyTempApp.getSensorName(device));

        if(curTemp > maxTemp || curTemp < minTemp) {
            binding.tvCurTemp.setTextColor(getResources().getColor(R.color.color_c2185b));
        }
        else {
            binding.tvCurTemp.setTextColor(getResources().getColor(R.color.color_171717));
        }
        binding.tvCurTemp.setText(String.valueOf(curTemp) + "°C");

        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Entry> minValues = new ArrayList<>();
        ArrayList<Entry> maxValues = new ArrayList<>();
        for (int i = 0; i < arrTempList.size(); i++) {
            TempListInfo info = arrTempList.get(i);
            double temp = info.getTemp();
            long time = info.getTime();
            float val = (float) (temp);
            values.add(new Entry(i, val));

            if (val > maxTemp)
                maxTemp = val;

            if (val < minTemp)
                minTemp = val;

            maxValues.add(new Entry(i, (float) (BrainyTempApp.getMaxTemp(device))));
            minValues.add(new Entry(i, (float) (BrainyTempApp.getMinTemp(device))));
        }

        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = binding.lineChart.getAxisLeft();
        yAxis.setAxisMaximum(maxTemp + 5);
        yAxis.setAxisMinimum(minTemp - 5);
        binding.lineChart.getAxisRight().setEnabled(false);
        binding.lineChart.getDescription().setText("");
        binding.lineChart.setBackgroundColor(Color.TRANSPARENT); // 그래프 배경 색 설정
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
        binding.lineChart.setData(data);
        data.notifyDataChanged();
        binding.lineChart.notifyDataSetChanged();

        binding.lineChart.invalidate();
    }
}
