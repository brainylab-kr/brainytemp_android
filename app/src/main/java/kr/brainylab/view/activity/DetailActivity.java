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
import kr.brainylab.view.fragment.SensorSettingFragment;

/**
 * 센서 상세
 */
public class DetailActivity extends BaseActivity implements View.OnClickListener {

    private static DetailActivity instance;

    public static DetailActivity getInstance() {
        return instance;
    }

    ActivityDetailBinding binding;
    private static final int TAB_MENU_CNT = 2;

    public static final int PAGE_TAB_ALARM = 0;
    //    public static final int PAGE_TAB_OUT = 1;
    public static final int PAGE_TAB_CHART = 1;

    int nTabIndex = PAGE_TAB_ALARM;
    private SensorSettingFragment fragmentTemp = null;
    //    private OutFragment fragmentOut = null;
    public static  ChartFragment fragmentchart = null;

    public static DetailPagerAdapter pagerAdapter;

    public static String deviceID = "";  //센서 식별자
    public double curTemp = 0;  //현재 온도
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

        LoadLayout();
        initFragment();
        changeTab();
    }

    private void LoadLayout() {
        binding.rlyBack.setOnClickListener(this);
        binding.rlyAlarm.setOnClickListener(this);
        binding.rlyOut.setOnClickListener(this);
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
        if (fragmentTemp == null)
            fragmentTemp = new SensorSettingFragment();
//        if (fragmentOut == null)
//            fragmentOut = new OutFragment();
        if (fragmentchart == null)
            fragmentchart = new ChartFragment();
    }

    private void changeTab() {
        binding.ivAlarm.setAlpha(0.5f);
        binding.ivOut.setAlpha(0.5f);
        binding.ivChart.setAlpha(0.5f);

        if (nTabIndex == PAGE_TAB_ALARM) {
            binding.ivAlarm.setAlpha(1.0f);
        } else {
            binding.ivChart.setAlpha(1.0f);
        }
    }

    public static void updateTemperature(double curTemp){
        if (fragmentchart != null) {
            fragmentchart.updateCurrentTemp(curTemp);
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
            case R.id.rly_alarm:
                nTabIndex = PAGE_TAB_ALARM;
                changeTab();
                binding.vpFragment.setCurrentItem(nTabIndex);
                pagerAdapter.notifyDataSetChanged();
                break;
//            case R.id.rly_out:
//                nTabIndex = PAGE_TAB_OUT;
//                changeTab();
//                binding.vpFragment.setCurrentItem(nTabIndex);
//                pagerAdapter.notifyDataSetChanged();
//                break;
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
                case PAGE_TAB_ALARM:
                    frag = fragmentTemp;
                    break;
//                case PAGE_TAB_OUT:
//                    frag = fragmentOut;
//                    break;
                case PAGE_TAB_CHART:
                    frag = fragmentchart;
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