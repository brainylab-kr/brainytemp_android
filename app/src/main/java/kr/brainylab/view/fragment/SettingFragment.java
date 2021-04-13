/**
 * 홈
 */
package kr.brainylab.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.brainylab.R;
import kr.brainylab.databinding.FragmentSettingBinding;
import kr.brainylab.view.activity.MainActivity;

import static kr.brainylab.view.activity.MainActivity.PAGE_ABOUT;
import static kr.brainylab.view.activity.MainActivity.PAGE_REPEAT_SETTING;
import static kr.brainylab.view.activity.MainActivity.PAGE_INFO;
import static kr.brainylab.view.activity.MainActivity.PAGE_SEARCH;

public class SettingFragment extends Fragment {

    View rootView;
    FragmentSettingBinding binding;

    public SettingFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);
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
        binding.rlyRepeatSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.nTabIndex = PAGE_REPEAT_SETTING;
                mainActivity.changePage();
            }
        });

        binding.rlyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.nTabIndex = PAGE_INFO;
                mainActivity.changePage();
            }
        });

        binding.rlyAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.nTabIndex = PAGE_ABOUT;
                mainActivity.changePage();
            }
        });
        binding.rlyHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://brainylab.kr/"));
                startActivity(intent);
            }
        });
    }
}
