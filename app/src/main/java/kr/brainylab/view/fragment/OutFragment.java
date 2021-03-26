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

import kr.brainylab.R;
import kr.brainylab.databinding.FragmentOutBinding;
import kr.brainylab.utils.Util;
import kr.brainylab.view.dailog.Calendar1Dialog;
import kr.brainylab.view.dailog.NameEditDialog;
import kr.brainylab.view.dailog.TempLimitDialog;

/**
 * 센서 상세에서 내보내기
 */
public class OutFragment extends Fragment implements View.OnClickListener {

    View rootView;
    FragmentOutBinding binding;

    private int mType = 0;
    private int mForm = 0;

    public OutFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_out, container, false);
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
        String startDate = "2021-01-02";
        String endDate = "2021-02-28";
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

            case R.id.tv_out: //내보내기

                break;
            case R.id.edt_start_date: //시작날짜

                String startDate = "2021-01-02";

                Calendar1Dialog.init(getContext(), startDate, new Calendar1Dialog.OnClickListener() {
                    @Override
                    public void onConfirm(String content) {

                    }
                }).show();
                break;
            case R.id.edt_end_date: //완료날짜
                String endDate = "2021-02-28";
                Calendar1Dialog.init(getContext(), endDate, new Calendar1Dialog.OnClickListener() {
                    @Override
                    public void onConfirm(String content) {

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
}
