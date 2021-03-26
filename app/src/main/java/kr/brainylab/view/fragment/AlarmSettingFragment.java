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
import kr.brainylab.view.dailog.AlarmRepeatDialog;
import kr.brainylab.view.dailog.AlertRepeatDialog;

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
        binding.rlyAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertRepeatDialog.init(getActivity(), new AlertRepeatDialog.OnClickListener() {
                    @Override
                    public void onConfirm(int minute) {
                        BrainyTempApp.setAlertRepeatCycle(minute);
                    }
                }).show();
            }
        });

        binding.rlyAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmRepeatDialog.init(getActivity(), new AlarmRepeatDialog.OnClickListener() {
                    @Override
                    public void onConfirm(int minute) {
                        BrainyTempApp.setAlarmRepeatCycle(minute);
                    }
                }).show();
            }
        });
    }
}
