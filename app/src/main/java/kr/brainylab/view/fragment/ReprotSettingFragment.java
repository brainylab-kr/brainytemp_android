/**
 * 홈
 */
package kr.brainylab.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.BuildConfig;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.databinding.FragmentReportSettingBinding;
import kr.brainylab.view.dailog.OneBtnDialog;

public class ReprotSettingFragment extends Fragment {

    View rootView;
    FragmentReportSettingBinding binding;


    public ReprotSettingFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_report_setting, container, false);
        binding = DataBindingUtil.bind(rootView);
        loadLayout();
        initData();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    private void loadLayout() {
        binding.llyDailyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean dailyReport = BrainyTempApp.mPref.getValue(Common.PREF_DAILY_REPORT, true);

                binding.ivDailyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_on_mtrl));
                binding.ivWeeklyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_off_mtrl));

                BrainyTempApp.mPref.put(Common.PREF_DAILY_REPORT, true);
                BrainyTempApp.mPref.put(Common.PREF_WEEKLY_REPORT, false);

                /*
                if (dailyReport) {
                    binding.ivDailyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_off_mtrl));
                } else {
                    binding.ivDailyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_on_mtrl));
                }
                BrainyTempApp.mPref.put(Common.PREF_DAILY_REPORT, !dailyReport);
                */
            }
        });

        binding.llyWeeklyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean weeklyReport = BrainyTempApp.mPref.getValue(Common.PREF_WEEKLY_REPORT, false);

                binding.ivDailyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_off_mtrl));
                binding.ivWeeklyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_on_mtrl));
                BrainyTempApp.mPref.put(Common.PREF_DAILY_REPORT, false);
                BrainyTempApp.mPref.put(Common.PREF_WEEKLY_REPORT, true);

                /*
                if (weeklyReport) {
                    binding.ivWeeklyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_off_mtrl));
                } else {
                    binding.ivWeeklyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_on_mtrl));
                }
                BrainyTempApp.mPref.put(Common.PREF_WEEKLY_REPORT, !weeklyReport);
                */
            }
        });

        binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = binding.edtEmail.getText().toString();
                if(address.length() == 0) {
                    showAlert(getResources().getString(R.string.input_mail));
                    return;
                }

                if(isValidEmail(address) != true) {
                    showAlert(getResources().getString(R.string.not_vaild_mail));
                    return;
                }

                BrainyTempApp.mPref.put(Common.PREF_DAILY_REPORT_ADDRESS, address);

                /*
                boolean dailyReport = BrainyTempApp.mPref.getValue(Common.PREF_DAILY_REPORT, false);
                BrainyTempApp.mPref.put(Common.PREF_DAILY_REPORT, !dailyReport);

                boolean weeklyReport = BrainyTempApp.mPref.getValue(Common.PREF_WEEKLY_REPORT, false);
                BrainyTempApp.mPref.put(Common.PREF_WEEKLY_REPORT, !weeklyReport);
                */
                showAlert(getResources().getString(R.string.save_complete));
            }
        });
    }

    private void initData() {

        String address = BrainyTempApp.mPref.getValue(Common.PREF_DAILY_REPORT_ADDRESS, "");

        if(address.length() != 0) {
            binding.edtEmail.setText(address);
        }

        boolean dailyReport = BrainyTempApp.mPref.getValue(Common.PREF_DAILY_REPORT, false);

        if (dailyReport) {
            binding.ivDailyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_on_mtrl));
        } else {
            binding.ivDailyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_off_mtrl));
        }

        boolean weeklyReport = BrainyTempApp.mPref.getValue(Common.PREF_WEEKLY_REPORT, false);

        if (weeklyReport) {
            binding.ivWeeklyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_on_mtrl));
        } else {
            binding.ivWeeklyReport.setBackground(getActivity().getDrawable(R.drawable.btn_radio_off_mtrl));
        }
    }

    public boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()) {
            err = true;
        }
        return err;
    }

    public void showAlert(String message) {

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                String button = getResources().getString(R.string.confirm);
                OneBtnDialog.init(getActivity(), message, button, new OneBtnDialog.OnClickListener() {
                    @Override
                    public void onConfirm() {
                    }
                }).show();
            }
        });
    }


}
