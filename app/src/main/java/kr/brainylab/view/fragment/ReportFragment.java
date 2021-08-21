package kr.brainylab.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.common.HttpService;
import kr.brainylab.databinding.FragmentReportBinding;
import kr.brainylab.utils.Util;
import kr.brainylab.view.activity.DetailActivity;
import kr.brainylab.view.dailog.Calendar1Dialog;
import kr.brainylab.view.dailog.OneBtnDialog;

public class ReportFragment extends Fragment implements View.OnClickListener {

    View rootView;
    FragmentReportBinding binding;

    String startDate = "";
    String endDate = "";

    private int mType = 0;
    private int mForm = 0;

    public ReportFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_report, container, false);
        binding = DataBindingUtil.bind(rootView);
        loadLayout();
        changeType();
        changeForm();
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
        binding.rlyBackground.setOnClickListener(this);
        binding.edtStartDate.setOnClickListener(this);
        binding.edtEndDate.setOnClickListener(this);
        binding.llyType1.setOnClickListener(this);
        binding.llyType2.setOnClickListener(this);
        binding.llyType3.setOnClickListener(this);
        binding.llyForm1.setOnClickListener(this);
        binding.llyForm2.setOnClickListener(this);
        binding.tvOut.setOnClickListener(this);
    }

    /**
     * 형식 변경
     */
    private void changeType() {
        binding.ivType1.setBackground(getActivity().getDrawable(R.drawable.ic_option_off1));
        binding.ivType2.setBackground(getActivity().getDrawable(R.drawable.ic_option_off1));
        binding.ivType3.setBackground(getActivity().getDrawable(R.drawable.ic_option_off1));

        if (mType == 0) {
            binding.ivType1.setBackground(getActivity().getDrawable(R.drawable.ic_option_on1));
        } else if (mType == 1) {
            binding.ivType2.setBackground(getActivity().getDrawable(R.drawable.ic_option_on1));
        } else {
            binding.ivType3.setBackground(getActivity().getDrawable(R.drawable.ic_option_on1));
        }
    }

    /**
     * 양식 변경
     */
    private void changeForm() {
        binding.ivForm1.setBackground(getActivity().getDrawable(R.drawable.ic_option_off1));
        binding.ivForm2.setBackground(getActivity().getDrawable(R.drawable.ic_option_off1));

        if (mForm == 0) {
            binding.ivForm1.setBackground(getActivity().getDrawable(R.drawable.ic_option_on1));
        } else {
            binding.ivForm2.setBackground(getActivity().getDrawable(R.drawable.ic_option_on1));
        }

    }

    private void initData() {

        String address = BrainyTempApp.mPref.getValue(Common.PREF_REPORT_ADDRESS, "");

        if(address.length() != 0) {
            binding.edtEmail.setText(address);
        }

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        endDate = df.format(date);

        //cal.add(Calendar.MONTH, -1);
        //startDate = df.format(cal.getTime());
        startDate = endDate;

        binding.edtStartDate.setText(startDate);
        binding.edtEndDate.setText(endDate);
    }

    /**
     * Click Events
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_background:
                Util.hideKeyboard(binding.edtEmail);
                break;

            case R.id.tv_out: //리포트 전송
                String mail = binding.edtEmail.getText().toString();
                if(mail.length() == 0) {
                    showAlert(getResources().getString(R.string.input_mail));
                    break;
                }

                if(isValidEmail(mail) != true) {
                    showAlert(getResources().getString(R.string.not_vaild_mail));
                    break;
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                Date start = Calendar.getInstance().getTime();
                Date end = Calendar.getInstance().getTime();
                try {
                    start = dateFormat.parse(startDate);
                    end = dateFormat.parse(endDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long dateCounter = (end.getTime() - start.getTime()) / (24*60*60*1000);
                if(dateCounter >= 31) {
                    showAlert(getResources().getString(R.string.over_max_date));
                    break;
                }

                requestReport();
                break;
            case R.id.edt_start_date: //시작날짜

                Calendar1Dialog.init(getContext(), startDate, new Calendar1Dialog.OnClickListener() {
                    @Override
                    public void onConfirm(String content) {
                        startDate = content;
                        binding.edtStartDate.setText(startDate);
                    }
                }).show();
                break;
            case R.id.edt_end_date: //완료날짜

                Calendar1Dialog.init(getContext(), endDate, new Calendar1Dialog.OnClickListener() {
                    @Override
                    public void onConfirm(String content) {
                        endDate = content;
                        binding.edtEndDate.setText(endDate);
                    }
                }).show();
                break;
            case R.id.lly_type1:
                mType = 0;
                changeType();
                break;
            case R.id.lly_type2:
                mType = 1;
                changeType();
                break;
            case R.id.lly_type3:
                mType = 2;
                changeType();
                break;

            case R.id.lly_form1:
                mForm = 0;
                changeForm();
                break;

            case R.id.lly_form2:
                mForm = 1;
                changeForm();
                break;

        }
    }

    private HttpService httpService;
    public void requestReport() {
        String device = ((DetailActivity) getActivity()).deviceID;
        String name = Util.getSensorInfo(device).getName();
        String mail = binding.edtEmail.getText().toString();
        String start = startDate + " 00:00:00";
        String end = endDate + " 23:59:59";
        String reportType = "";
        if(Util.getSensorInfo(device).getType().equals(Common.SENSOR_TYPE_TH)) {
            reportType = "TH";
        }
        else {
            reportType = "T";
        }

        BrainyTempApp.mPref.put(Common.PREF_REPORT_ADDRESS, mail);

        httpService = new HttpService(BrainyTempApp.getInstance());
        httpService.requestReport(device, name, mail, start, end, reportType, new HttpService.ResponseListener() {
            @Override
            public void onResponseResult(Boolean bSuccess, String res) {
                if (bSuccess) {
                    Log.d("BrainyTemp", "onResponseResult:" + res);
                    try {
                        JSONObject jObj = new JSONObject(res);
                        String result = jObj.getString("ret");
                        if (result.equals("ok")) {
                            successUpload();
                            showAlert(getResources().getString(R.string.send_report));
                        }
                    } catch (JSONException e) {
                        Log.d("BrainyTemp", "err.. : " + e.toString());
                        showToast(BrainyTempApp.getInstance().getResources().getString(R.string.fail_report));
                    }
                } else {
                    Log.d("BrainyTemp", "err.. : " + BrainyTempApp.getInstance().getResources().getString(R.string.connect_fail));
                    showToast(BrainyTempApp.getInstance().getResources().getString(R.string.connect_fail));

                }
            }
        });
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

    private void showToast(String message) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void successUpload() {
        httpService = null;
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

}
