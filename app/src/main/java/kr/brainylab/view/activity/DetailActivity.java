package kr.brainylab.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import kr.brainylab.R;
import kr.brainylab.databinding.ActivityDetailBinding;
import kr.brainylab.view.fragment.ChartFragment;
import kr.brainylab.view.fragment.ReportFragment;
import kr.brainylab.view.fragment.SensorSettingFragment;

public class DetailActivity extends BaseActivity implements View.OnClickListener {

    private static DetailActivity instance;

    public static DetailActivity getInstance() {
        return instance;
    }

    ActivityDetailBinding binding;
    private static final int TAB_MENU_CNT = 3;

    public static final int PAGE_TAB_CHART = 0;
    public static final int PAGE_TAB_REPORT = 1;
    public static final int PAGE_TAB_SETTING = 2;

    int nTabIndex = PAGE_TAB_CHART;
    private SensorSettingFragment fragmentSetting = null;
    private ReportFragment fragmentReport = null;
    public static  ChartFragment fragmentChart = null;

    public static DetailPagerAdapter pagerAdapter;

    public static String deviceID = "";  //센서 식별자
    public double curTemp = 0;  //현재 온도
    public int curHumi = 0;  //현재 습도

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_detail, null, false);
        setContentView(binding.getRoot());

        instance = this;

        Intent intent = getIntent();
        deviceID = intent.getExtras().getString("device", "");
        curTemp = intent.getExtras().getDouble("temp");
        curHumi = intent.getExtras().getInt("humi");

        LoadLayout();
        initFragment();
        changeTab();
    }

    private void LoadLayout() {
        binding.rlyBack.setOnClickListener(this);
        binding.rlySetting.setOnClickListener(this);
        binding.rlyReport.setOnClickListener(this);
        binding.rlyChart.setOnClickListener(this);

        pagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());
        binding.vpFragment.setAdapter(pagerAdapter);
        binding.vpFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                nTabIndex = position;
                changeTab();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.vpFragment.setOffscreenPageLimit(TAB_MENU_CNT);
    }

    /**
     * 프래그먼트 생성
     */
    private void initFragment() {
        if (fragmentChart == null)
            fragmentChart = new ChartFragment();
        if (fragmentReport == null)
            fragmentReport = new ReportFragment();
        if (fragmentSetting == null)
            fragmentSetting = new SensorSettingFragment();
    }

    private void changeTab() {
        binding.ivAlarm.setAlpha(0.5f);
        binding.ivOut.setAlpha(0.5f);
        binding.ivChart.setAlpha(0.5f);

        if (nTabIndex == PAGE_TAB_CHART) {
            binding.ivAlarm.setAlpha(0.5f);
            binding.ivOut.setAlpha(0.5f);
            binding.ivChart.setAlpha(1.0f);
        }
        else if (nTabIndex == PAGE_TAB_REPORT) {
            binding.ivAlarm.setAlpha(0.5f);
            binding.ivOut.setAlpha(1.0f);
            binding.ivChart.setAlpha(0.5f);
        }
        else if (nTabIndex == PAGE_TAB_SETTING) {
            binding.ivAlarm.setAlpha(1.0f);
            binding.ivOut.setAlpha(0.5f);
            binding.ivChart.setAlpha(0.5f);
        }
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    /**
     * Click Events
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_back:
                finish();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
            case R.id.rly_setting:
                nTabIndex = PAGE_TAB_SETTING;
                changeTab();
                binding.vpFragment.setCurrentItem(nTabIndex);
                pagerAdapter.notifyDataSetChanged();
                break;
            case R.id.rly_report:
                nTabIndex = PAGE_TAB_REPORT;
                changeTab();
                binding.vpFragment.setCurrentItem(nTabIndex);
                pagerAdapter.notifyDataSetChanged();
                break;
            case R.id.rly_chart:
                nTabIndex = PAGE_TAB_CHART;
                changeTab();
                binding.vpFragment.setCurrentItem(nTabIndex);
                pagerAdapter.notifyDataSetChanged();
                break;
        }
    }

    private class DetailPagerAdapter extends FragmentStatePagerAdapter {

        public DetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case PAGE_TAB_CHART:
                    frag = fragmentChart;
                    break;
                case PAGE_TAB_REPORT:
                    frag = fragmentReport;
                    break;
                case PAGE_TAB_SETTING:
                    frag = fragmentSetting;
                    break;
            }
            return frag;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return TAB_MENU_CNT;
        }
    }
}