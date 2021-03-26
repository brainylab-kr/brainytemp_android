/**
 * 홈
 */
package kr.brainylab.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.databinding.FragmentInfoBinding;
import kr.brainylab.databinding.FragmentSettingBinding;
import kr.brainylab.view.activity.TermsActivity;

/**
 * 정보
 */
public class InfomarionFragment extends Fragment implements View.OnClickListener {

    View rootView;
    FragmentInfoBinding binding;


    public InfomarionFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_info, container, false);
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
        binding.rlyPolicy.setOnClickListener(this);
        binding.rlyOpenSource.setOnClickListener(this);
        binding.rlyTerms.setOnClickListener(this);
    }

    /**
     * Click Events
     */
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rly_policy:
                intent = new Intent(getActivity(), TermsActivity.class);
                intent.putExtra("page", Common.PAGE_POLICY);
                getActivity().startActivity(intent);
                break;

            case R.id.rly_open_source:
                intent = new Intent(getActivity(), TermsActivity.class);
                intent.putExtra("page", Common.PAGE_OPEN_SOURCE);
                getActivity().startActivity(intent);
                break;
            case R.id.rly_terms:
                intent = new Intent(getActivity(), TermsActivity.class);
                intent.putExtra("page", Common.PAGE_TEMRS);
                getActivity().startActivity(intent);
                break;

        }
    }
}
