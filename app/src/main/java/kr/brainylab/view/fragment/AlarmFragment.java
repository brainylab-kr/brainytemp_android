/**
 * 홈
 */
package kr.brainylab.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import kr.brainylab.R;
import kr.brainylab.adapter.AlarmListAdapter;
import kr.brainylab.common.Common;
import kr.brainylab.databinding.FragmentAlarmBinding;
import kr.brainylab.model.AlarmListInfo;
import kr.brainylab.utils.Util;
import kr.brainylab.view.dailog.ReceiveUserDialog;

public class AlarmFragment extends Fragment {

    View rootView;
    FragmentAlarmBinding binding;

    private AlarmListAdapter m_dadapter;
    private ArrayList<AlarmListInfo> arrAlarmList = new ArrayList<AlarmListInfo>();

    public int mSelIndex = -1;

    public AlarmFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_alarm, container, false);
        binding = DataBindingUtil.bind(rootView);
        loadLayout();
        loadData();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    private void loadLayout() {
        IntentFilter f = new IntentFilter();
        f.addAction(Common.ACT_ALARM_UPDATE);
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(f));

        binding.rlyNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReceiveUserDialog.init(getActivity(), null, new ReceiveUserDialog.OnClickListener() {
                    @Override
                    public void onConfirm(String type, String content) {
                        AlarmListInfo info = new AlarmListInfo(content, type, true, true, true, true);
                        if (Util.addAlarm(info)) {
                            loadData();
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
                }).show();
            }
        });

        binding.ivAlarmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReceiveUserDialog.init(getActivity(), null, new ReceiveUserDialog.OnClickListener() {
                    @Override
                    public void onConfirm(String type, String content) {
                        AlarmListInfo info = new AlarmListInfo(content, type, true, true, true, true);
                        if (Util.addAlarm(info)) {
                            loadData();
                        }
                    }

                    @Override
                    public void onCancel() {
                    }

                }).show();
            }
        });
    }

    private void loadData() {
        arrAlarmList = Util.getAlarmList();
        m_dadapter = new AlarmListAdapter(getActivity(), this, arrAlarmList);
        binding.lsvContent.setAdapter(m_dadapter);
        m_dadapter.notifyDataSetChanged();

        if (arrAlarmList.size() > 0) {
            binding.rlyContent.setVisibility(View.VISIBLE);
            binding.rlyNoData.setVisibility(View.GONE);
        } else {
            binding.rlyContent.setVisibility(View.GONE);
            binding.rlyNoData.setVisibility(View.VISIBLE);
        }
    }

    //boradcast receive
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Common.ACT_ALARM_UPDATE)) {
                loadData();
            }
        }
    };
}
