/**
 * 홈
 */
package kr.brainylab.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.databinding.FragmentAlarmSettingBinding;
import kr.brainylab.view.dailog.DeviceAlarmRepeatDialog;
import kr.brainylab.view.dailog.SMSAlertRepeatDialog;
import kr.brainylab.view.dailog.SensingRepeatDialog;

/**
 * 경보음 및 알림
 */
public class AlarmSettingFragment extends Fragment {

    View rootView;
    FragmentAlarmSettingBinding binding;


    public AlarmSettingFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_alarm_setting, container, false);
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
        binding.rlySensing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SensingRepeatDialog.init(getActivity(), new SensingRepeatDialog.OnClickListener() {
                    @Override
                    public void onConfirm(int minute) {
                        BrainyTempApp.setSensingRepeatCycle(minute);
                    }
                }).show();
            }
        });

        // SMS 알림
        binding.rlySmsAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMSAlertRepeatDialog.init(getActivity(), new SMSAlertRepeatDialog.OnClickListener() {
                    @Override
                    public void onConfirm(int minute) {
                        BrainyTempApp.setAlertRepeatCycle(minute);
                    }
                }).show();
            }
        });

        // 경보음
        binding.rlyDeviceAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceAlarmRepeatDialog.init(getActivity(), new DeviceAlarmRepeatDialog.OnClickListener() {
                    @Override
                    public void onConfirm(int minute) {
                        BrainyTempApp.setAlarmRepeatCycle(minute);
                    }
                }).show();
            }
        });
    }
}
