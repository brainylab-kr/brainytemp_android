package kr.brainylab.view.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;
import java.util.Calendar;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.adapter.RingtoneListAdapter;
import kr.brainylab.databinding.ActivityAlertBinding;
import kr.brainylab.model.RingtoneListInfo;
import kr.brainylab.utils.Util;

/**
 * 경보음
 */
public class AlertActivity extends BaseActivity implements View.OnClickListener {

    ActivityAlertBinding binding;

    private RingtoneListAdapter m_dadapter;
    private ArrayList<RingtoneListInfo> arrSensorList = new ArrayList<RingtoneListInfo>();
    MediaPlayer mPlayer;

    private static AlertActivity instance;

    public static AlertActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_alert, null, false);
        setContentView(binding.getRoot());

        instance = this;
        LoadLayout();

        Intent intent = getIntent();
        String device = intent.getStringExtra("device");
        String temp = intent.getStringExtra("temp");
        String humi = intent.getStringExtra("humi");

        setData(device, temp, humi);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void LoadLayout() {
        binding.tvAlertRemove.setOnClickListener(this);

        mPlayer = MediaPlayer.create(this, R.raw.alarm);
        mPlayer.setLooping(true);
        mPlayer.start();
    }

    public void setData(String device, String temp, String humi) {

        for(int i = 0; i < arrSensorList.size(); i++) {
            if(device == arrSensorList.get(i).getDevice()) {
                return;
            }
        }

        String name = BrainyTempApp.getSensorName(device);
        RingtoneListInfo info = new RingtoneListInfo(device, name, temp, humi, Util.getCurDate());

        arrSensorList.add(info);

        m_dadapter = new RingtoneListAdapter(this, arrSensorList);
        binding.lsvContent.setAdapter(m_dadapter);
        m_dadapter.notifyDataSetChanged();
    }

    private void finishAlert() {
        for (int i = 0; i < arrSensorList.size(); i++) {
            RingtoneListInfo info = arrSensorList.get(i);
            BrainyTempApp.setAlarmTime(info.getDevice(), "" + Calendar.getInstance().getTime().getTime());
        }

        instance = null;
        if (mPlayer.isPlaying())
            mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAlert();
    }

    /**
     * Click Events
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_alert_remove:
                finishAlert();
                finish();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
        }
    }
}