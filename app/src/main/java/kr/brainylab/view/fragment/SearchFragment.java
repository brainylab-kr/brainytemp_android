/**
 * 홈
 */
package kr.brainylab.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.brainylab.R;
import kr.brainylab.adapter.SensorAddAdapter;
import kr.brainylab.common.HttpService;
import kr.brainylab.databinding.FragmentSearchBinding;
import kr.brainylab.model.SensorAddInfo;
import kr.brainylab.model.SensorInfo;
import kr.brainylab.utils.Util;
import kr.brainylab.view.dailog.OneBtnDialog;
import pl.efento.sdk.Efento;
import pl.efento.sdk.api.scan.Device;
import pl.efento.sdk.api.scan.OnScanResultCallback;
import pl.efento.sdk.api.scan.Scanner;

public class SearchFragment extends Fragment {

    View rootView;
    FragmentSearchBinding binding;
    Scanner scanner;
    private SensorAddAdapter m_dadapter;
    private ArrayList<SensorAddInfo> arrSensorList = new ArrayList<SensorAddInfo>();
    private ArrayList<Device> arrSearchList = new ArrayList<Device>();
    private static ProgressDialog _progressDlg = null;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStop() {
        super.onStop();
        Util.closeProgress();
        scanner.stop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
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
        Util.showProgress(getContext(), true);
        checkPage();

        m_dadapter = new SensorAddAdapter(getActivity(), this, arrSensorList);
        binding.lsvContent.setAdapter(m_dadapter);

        scanner = Efento.scanner().setErrorCallback(null).build();
        scanner.scan(new OnScanResultCallback() {
            @Override
            public void onResult(@NonNull Device device) {
                if (!isExistDevice(device.getAddress())) {
                    SensorAddInfo item = new SensorAddInfo(device.getAddress(), device.getRssi());
                    arrSensorList.add(item);
                    arrSearchList.add(device);
                    m_dadapter.notifyDataSetChanged();
                    checkPage();
                }
            }
        });
    }

    /**
     * 이미 디바이스가 등록되어있는지 체크
     *
     * @return
     */
    private boolean isExistDevice(String device) {

        if (Util.isExistSensor(device)) { //이미 등록된 센서인지 체크
            return true;
        }

        for (int i = 0; i < arrSensorList.size(); i++) { //이미 검색된 센서인지 체크
            SensorAddInfo info = arrSensorList.get(i);
            if (info.getDevice().equals(device)) {
                return true;
            }
        }
        return false;
    }

    private void checkPage() {
        if (arrSensorList.size() > 0) {
            Util.closeProgress();
            binding.tvSearch.setVisibility(View.GONE);
            binding.lsvContent.setVisibility(View.VISIBLE);
        } else {
            binding.tvSearch.setVisibility(View.VISIBLE);
            binding.lsvContent.setVisibility(View.GONE);
        }
    }

    //센서 추가
    public void addSensor(final int postion) {

        ArrayList<SensorInfo> sensorList = Util.getSensorList();
        if(sensorList.size() >= 3) {
            showMaxSensor();
            return;
        }

        Device device = arrSearchList.get(postion);
        reqDeviceAuth(device);
    }

    private void showMaxSensor() {
        String content = getResources().getString(R.string.max_sensor_number);
        String button = getResources().getString(R.string.confirm);

        OneBtnDialog.init(getActivity(), content, button, new OneBtnDialog.OnClickListener() {
            @Override
            public void onConfirm() {
            }
        }).show();
    }

    public static void showProgress(Context _context, boolean cancelable) {

        if (_progressDlg != null)
            return;

        try {
            _progressDlg = new ProgressDialog(_context, R.style.MyDialogTheme);
            _progressDlg.setIndeterminate(true);

            Drawable drawable = new ProgressBar(_context).getIndeterminateDrawable().mutate();
            drawable.setColorFilter(ContextCompat.getColor(_context, R.color.color_2c8aaa),
                    PorterDuff.Mode.SRC_IN);
            _progressDlg.setIndeterminateDrawable(drawable);


            _progressDlg.setCancelable(cancelable);
            _progressDlg
                    .setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            _progressDlg.show();

        } catch (Exception e) {
        }
    }

    public static void closeProgress() {
        try {
            if ((_progressDlg != null) && _progressDlg.isShowing()) {
                _progressDlg.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            _progressDlg = null;
        }
    }

    /**
     * network function
     */
    //디바이스 인증
    private void reqDeviceAuth(Device device) {

        showProgress(getActivity(), false);
        HttpService httpService = new HttpService(getActivity());
        httpService.deviceAuth(device.getAddress(), new HttpService.ResponseListener() {
            @Override
            public void onResponseResult(Boolean bSuccess, String res) {
                closeProgress();
                if (bSuccess) {
                    try {

                        JSONObject jObj = new JSONObject(res);
                        String result = jObj.getString("ret");
                        if (result.equals("ok")) {
                            Util.addSensor(device);

                            m_dadapter.notifyDataSetChanged();

                            int index = -1;
                            for (int i = 0; i < arrSensorList.size(); i++) {
                                SensorAddInfo item = arrSensorList.get(i);
                                if (item.getDevice().equals(device.getAddress())) {
                                    index = i;
                                    break;
                                }
                            }

                            if (index == -1) {
                                return;
                            }
                            arrSensorList.remove(index);
                            arrSearchList.remove(index);
                            m_dadapter.notifyDataSetChanged();

                            Util.showToast(getActivity(), getActivity().getString(R.string.add_sensor_success));
                        } else {
                            JSONObject jObjError = new JSONObject("error");
                            String code = jObjError.getString("code");
                            if (code.equals("30")) {
                                Util.showToast(getActivity(), getActivity().getString(R.string.not_find_device));
                                return;
                            }
                            Util.showToast(getActivity(), getActivity().getString(R.string.device_auth_fail));
                        }

                    } catch (JSONException e) {
                        Log.d("BrainyTemp", "err.. : " + e.toString() +", " + res);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Util.showToast(getActivity(), e.getMessage());
                            }
                        });
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), getResources().getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}
