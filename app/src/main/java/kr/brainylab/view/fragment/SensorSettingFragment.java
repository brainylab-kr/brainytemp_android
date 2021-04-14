package kr.brainylab.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.databinding.FragmentSensorSettingBinding;
import kr.brainylab.model.SensorInfo;
import kr.brainylab.utils.Util;
import kr.brainylab.view.activity.DetailActivity;
import kr.brainylab.view.dailog.HumiLimitDialog;
import kr.brainylab.view.dailog.TempLimitDialog;
import pl.efento.sdk.api.scan.Device;

public class SensorSettingFragment extends Fragment implements View.OnClickListener {

    View rootView;
    FragmentSensorSettingBinding binding;

    private boolean bDelay = false;
    String device = "";

    public SensorSettingFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_sensor_setting, container, false);
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

        if(sensor.getBatteryStatus() == Device.BatteryStatus.LOW) {
            binding.tvBatteryStatus.setTextColor(Color.RED);
            binding.tvBatteryStatus.setText("낮음");
        }
        else {
            binding.tvBatteryStatus.setTextColor(getResources().getColor(R.color.color_171717));
            binding.tvBatteryStatus.setText("정상");
        }

        binding.tvFirmwareVersion.setText(sensor.getSoftwareVersion().toString());
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
                        BrainyTempApp.setMinHumi(device, humi);
                        binding.tvHumiMin.setText(String.valueOf(humi) + "%");
                        Intent sendIntent = new Intent(Common.ACT_SENSOR_LIST_UPDATE);
                        LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(sendIntent);
                    }
                }).show();
                break;
        }
    }
}
