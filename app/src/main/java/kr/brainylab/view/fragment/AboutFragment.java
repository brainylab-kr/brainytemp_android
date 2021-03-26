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

import kr.brainylab.BuildConfig;
import kr.brainylab.R;
import kr.brainylab.databinding.FragmentAboutBinding;
import kr.brainylab.databinding.FragmentSettingBinding;

/**
 * About
 */
public class AboutFragment extends Fragment {

    View rootView;
    FragmentAboutBinding binding;


    public AboutFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
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
        String versionName = BuildConfig.VERSION_NAME;
        binding.tvVersion.setText(versionName);
    }
}
