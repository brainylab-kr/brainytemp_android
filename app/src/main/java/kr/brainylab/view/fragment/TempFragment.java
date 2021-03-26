/**
 * 홈
 */
package kr.brainylab.view.fragment;

import android.content.Intent;
import android.os.Build;
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
import kr.brainylab.databinding.FragmentTempBinding;
import kr.brainylab.utils.Util;
import kr.brainylab.view.activity.DetailActivity;
import kr.brainylab.view.dailog.DelayTimeDialog;
import kr.brainylab.view.dailog.TempLimitDialog;

/**
 * 센서 상세에서 온도
 */
public class TempFragment extends Fragment implements View.OnClickListener {

    View rootView;
    FragmentTempBinding binding;

    private boolean bDelay = false;
    String device = "";

    public TempFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_temp, container, false);
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

        binding.rlyMax.setOnClickListener(this);
        binding.rlyMin.setOnClickListener(this);
        binding.llyAlarmDelay.setOnClickListener(this);
        binding.llyDelayTime.setOnClickListener(this);

        double maxTemp = BrainyTempApp.getMaxTemp(device);
        double minTemp = BrainyTempApp.getMinTemp(device);
        int delayTime = BrainyTempApp.getDelayTime(device);
        binding.tvMax.setText(String.valueOf(maxTemp) + "°C");
        binding.tvMin.setText(String.valueOf(minTemp) + "°C");

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
            case R.id.rly_max:
                TempLimitDialog.init(getActivity(), 0, device, new TempLimitDialog.OnClickListener() {
                    @Override
                    public void onConfirm(double temp) {
                        Util.deleteMeasureTemp(device);
                        BrainyTempApp.setMaxTemp(device, temp);
                        binding.tvMax.setText(String.valueOf(temp) + "°C");
                        Intent sendIntent = new Intent(Common.ACT_SENSOR_UPDATE);
                        LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(sendIntent);
                    }
                }).show();
                break;
            case R.id.rly_min:
                TempLimitDialog.init(getActivity(), 1, device, new TempLimitDialog.OnClickListener() {
                    @Override
                    public void onConfirm(double temp) {
                        Util.deleteMeasureTemp(device);
                        BrainyTempApp.setMinTemp(device, temp);
                        binding.tvMin.setText(String.valueOf(temp) + "°C");
                        Intent sendIntent = new Intent(Common.ACT_SENSOR_UPDATE);
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
