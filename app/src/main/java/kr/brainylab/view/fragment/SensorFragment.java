/**
 * 홈
 */
package kr.brainylab.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

import kr.brainylab.R;
import kr.brainylab.adapter.SensorListAdapter;
import kr.brainylab.common.Common;
import kr.brainylab.databinding.FragmentSensorBinding;
import kr.brainylab.model.SensorInfo;
import kr.brainylab.utils.Util;
import kr.brainylab.view.activity.MainActivity;

import static kr.brainylab.view.activity.MainActivity.PAGE_SEARCH;

public class SensorFragment extends Fragment {

    View rootView;
    FragmentSensorBinding binding;

    private SensorListAdapter m_dadapter;
    private ArrayList<SensorInfo> arrSensorList = new ArrayList<SensorInfo>();

    public SensorFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_sensor, container, false);
        binding = DataBindingUtil.bind(rootView);
        loadLayout();
        loadData();

        registerReceiver();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    private void loadLayout() {

        binding.rlyNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.nTabIndex = PAGE_SEARCH;
                mainActivity.changePage();
            }
        });
    }

    private void loadData() {
        arrSensorList = Util.getSensorList();
        m_dadapter = new SensorListAdapter(getActivity(), this, arrSensorList);
        binding.lsvContent.setAdapter(m_dadapter);
        m_dadapter.notifyDataSetChanged();

        if (arrSensorList.size() > 0) {
            binding.rlyNoData.setVisibility(View.GONE);
            binding.lsvContent.setVisibility(View.VISIBLE);
        } else {
            binding.rlyNoData.setVisibility(View.VISIBLE);
            binding.lsvContent.setVisibility(View.GONE);
        }
    }

    void registerReceiver() {
        IntentFilter f = new IntentFilter();
        f.addAction(Common.ACT_SENSOR_VALUE_UPDATE);
        f.addAction(Common.ACT_SENSOR_LIST_UPDATE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, new IntentFilter(f));
    }

    //boradcast receive
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Common.ACT_SENSOR_VALUE_UPDATE)) {
                loadData();
            }
            else if(action.equals(Common.ACT_SENSOR_LIST_UPDATE)) {
                loadData();
            }
        }
    };
}
