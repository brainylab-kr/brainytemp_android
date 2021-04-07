package kr.brainylab.view.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.databinding.FragmentLimitBinding;
import kr.brainylab.model.SensorInfo;
import kr.brainylab.utils.Util;
import kr.brainylab.view.activity.DetailActivity;
import kr.brainylab.view.dailog.DelayTimeDialog;
import kr.brainylab.view.dailog.HumiLimitDialog;
import kr.brainylab.view.dailog.TempLimitDialog;

/**
 * 상,하한값 설정
 */
public class LimitSettingFragment extends Fragment implements View.OnClickListener {

    View rootView;
    FragmentLimitBinding binding;

    private boolean bDelay = false;
    String device = "";

    public LimitSettingFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_limit, container, false);
        binding = DataBindingUtil.bind(rootView);
        loadLayout();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    private void loadLayout() {
        device = ((DetailActivity) getActivity()).deviceID;

        binding.rlyTempMax.setOnClickListener(this);
        binding.rlyTempMin.setOnClickListener(this);
        binding.rlyHumiMax.setOnClickListener(this);
        binding.rlyHumiMin.setOnClickListener(this);
        binding.llyAlarmDelay.setOnClickListener(this);
        binding.llyDelayTime.setOnClickListener(this);

        double maxTemp = BrainyTempApp.getMaxTemp(device);
        double minTemp = BrainyTempApp.getMinTemp(device);
        binding.tvTempMax.setText(String.valueOf(maxTemp) + "°C");
        binding.tvTempMin.setText(String.valueOf(minTemp) + "°C");

        SensorInfo sensor = Util.getSensorInfo(device);

        if(sensor.getType().equals(Common.SENSOR_TYPE_TH)) {
            binding.rlyHumi.setVisibility(View.VISIBLE);
            int maxHumi = BrainyTempApp.getMaxHumi(device);
            int minHumi = BrainyTempApp.getMinHumi(device);
            binding.tvHumiMax.setText(String.valueOf(maxHumi) + "%");
            binding.tvHumiMin.setText(String.valueOf(minHumi) + "%");
        }
        else{
            binding.rlyHumi.setVisibility(View.GONE);
        }

        int delayTime = BrainyTempApp.getDelayTime(device);
        bDelay = delayTime != 0;
        showDelaytime();
    }

    private void showDelaytime() {
        int delayTime = BrainyTempApp.getDelayTime(device);
        if (bDelay) {
            binding.ivCheck.setBackground(getActivity().getDrawable(R.drawable.ic_checkbox_on));
            binding.llyDelayTime.setVisibility(View.VISIBLE);
        } else {
            binding.ivCheck.setBackground(getActivity().getDrawable(R.drawable.ic_checkbox_off));
            binding.llyDelayTime.setVisibility(View.GONE);
        }
        binding.tvDelayTime.setText(String.valueOf(delayTime));
    }

    /**
     * Click Events
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_temp_max:
                TempLimitDialog.init(getActivity(), 0, device, new TempLimitDialog.OnClickListener() {
                    @Override
                    public void onConfirm(double temp) {
                        Util.deleteMeasureTemp(device);
                        BrainyTempApp.setMaxTemp(device, temp);
                        binding.tvTempMax.setText(String.valueOf(temp) + "°C");
                        Intent sendIntent = new Intent(Common.ACT_SENSOR_LIST_UPDATE);
                        LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(sendIntent);
                    }
                }).show();
                break;
            case R.id.rly_temp_min:
                TempLimitDialog.init(getActivity(), 1, device, new TempLimitDialog.OnClickListener() {
                    @Override
                    public void onConfirm(double temp) {
                        Util.deleteMeasureTemp(device);
                        BrainyTempApp.setMinTemp(device, temp);
                        binding.tvTempMin.setText(String.valueOf(temp) + "°C");
                        Intent sendIntent = new Intent(Common.ACT_SENSOR_LIST_UPDATE);
                        LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(sendIntent);
                    }
                }).show();
                break;
            case R.id.rly_humi_max:
                HumiLimitDialog.init(getActivity(), 0, device, new HumiLimitDialog.OnClickListener() {
                    @Override
                    public void onConfirm(int humi) {
                        Util.deleteMeasureHumi(device);
                        BrainyTempApp.setMaxHumi(device, humi);
                        binding.tvHumiMax.setText(String.valueOf(humi) + "%");
                        Intent sendIntent = new Intent(Common.ACT_SENSOR_LIST_UPDATE);
                        LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(sendIntent);
                    }
                }).show();
                break;
            case R.id.rly_humi_min:
                HumiLimitDialog.init(getActivity(), 1, device, new HumiLimitDialog.OnClickListener() {
                    @Override
                    public void onConfirm(int humi) {
                        Util.deleteMeasureHumi(device);
                        BrainyTempApp.setMinHumi(device, humi);
                        binding.tvHumiMin.setText(String.valueOf(humi) + "%");
                        Intent sendIntent = new Intent(Common.ACT_SENSOR_LIST_UPDATE);
                        LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(sendIntent);
                    }
                }).show();
                break;
            case R.id.lly_alarm_delay:
                bDelay = !bDelay;
                if (bDelay) {
                    BrainyTempApp.setDelayTime(device, 5);
                    showDelaytime();
                } else {
                    BrainyTempApp.setDelayTime(device, 0);
                    showDelaytime();
                }
                Util.deleteMeasureTemp(device);
                break;
            case R.id.lly_delay_time:
                DelayTimeDialog.init(getActivity(), device, new DelayTimeDialog.OnClickListener() {
                    @Override
                    public void onConfirm(int minute) {
                        Util.deleteMeasureTemp(device);
                        BrainyTempApp.setDelayTime(device, minute);
                        showDelaytime();
                    }
                }).show();
                break;

        }
    }
}
