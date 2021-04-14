package kr.brainylab.view.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.brainylab.BuildConfig;
import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.database.SensorDataRepository;
import kr.brainylab.model.AlarmListInfo;
import kr.brainylab.service.SensorHandleService;
import kr.brainylab.utils.GPSTracker;
import kr.brainylab.utils.Util;
import kr.brainylab.view.dailog.NameEditDialog;
import kr.brainylab.view.dailog.ReceiveUserDialog;
import kr.brainylab.view.dailog.SensorDelDialog;
import kr.brainylab.view.dailog.TwoBtnDialog;
import kr.brainylab.view.fragment.AlarmFragment;
import kr.brainylab.view.fragment.SensorFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final int PAGE_SENSOR = 0;  //센서
    public static final int PAGE_SEARCH = 1;  //검색
    public static final int PAGE_ALARM = 2;  //알림
    public static final int PAGE_SETTING = 3;  //설정
    public static final int PAGE_REPEAT_SETTING = 4;  //경보음 및 알림
    public static final int PAGE_INFO = 5;  //정보
    public static final int PAGE_ABOUT = 6;  //About

    private DrawerLayout drawerLayout;
    private View drawerView;
    private RelativeLayout rlyContent;

    private boolean bFinish = false;
    public boolean bEdit = false; //센서 편집

    public int nTabIndex = PAGE_SENSOR;

    private RelativeLayout rlyDelete, rlyEdit;
    private RelativeLayout rlySensor, rlySearch, rlyAlarm, rlySetting;
    private TextView txvSensor, txvSearch, txvAlarm, txvSetting, txvTitle, tvVersion;
    private ImageView ivSensor, ivSearch, ivAlarm, ivSetting, ivMenu;

    GPSTracker gpsTracker;

    Intent foregroundServiceIntent;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main);

        instance = this;

        gpsTracker = new GPSTracker(this);
        LoadLayout();
        changePage();
        loadSensorHandleService();
        registerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        GPSTracker gpsTracker = new GPSTracker(this);
        if (!isGpsEnable()) {
            showLocationSettingsAlert();
        } else if (!isBluetoothEnable()) {
            showBluetoothSettingsAlert();
        }
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
        finishApplication();
        if (null != foregroundServiceIntent) {
            stopService(foregroundServiceIntent);
            foregroundServiceIntent = null;
        }
    }

    private void LoadLayout() {
        findViewById(R.id.rly_menu).setOnClickListener(this);
        txvTitle = (TextView) findViewById(R.id.tv_title);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        rlyDelete = (RelativeLayout) findViewById(R.id.rly_delete);
        rlyDelete.setOnClickListener(this);
        rlyEdit = (RelativeLayout) findViewById(R.id.rly_edit);
        rlyEdit.setOnClickListener(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer);  //slidding menu
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        rlyContent = (RelativeLayout) findViewById(R.id.rly_content);

        rlySensor = (RelativeLayout) findViewById(R.id.rly_sensor);
        rlySensor.setOnClickListener(this);
        rlySearch = (RelativeLayout) findViewById(R.id.rly_search);
        rlySearch.setOnClickListener(this);
        rlyAlarm = (RelativeLayout) findViewById(R.id.rly_alarm);
        rlyAlarm.setOnClickListener(this);
        rlySetting = (RelativeLayout) findViewById(R.id.rly_setting);
        rlySetting.setOnClickListener(this);

        txvSensor = (TextView) findViewById(R.id.tv_sensor);
        txvSearch = (TextView) findViewById(R.id.tv_search);
        txvAlarm = (TextView) findViewById(R.id.tv_alarm);
        txvSetting = (TextView) findViewById(R.id.tv_setting);
        tvVersion = (TextView) findViewById(R.id.tv_version);

        ivSensor = (ImageView) findViewById(R.id.iv_sensor);
        ivSearch = (ImageView) findViewById(R.id.iv_search);
        ivAlarm = (ImageView) findViewById(R.id.iv_alarm);
        ivSetting = (ImageView) findViewById(R.id.iv_setting);

        String versionName = BuildConfig.VERSION_NAME;
        tvVersion.setText(versionName);
        changeTitle();
    }

    public void changeTitle() {
        if (nTabIndex == PAGE_SENSOR) {
            if (bEdit) {
                ivMenu.setBackground(getDrawable(R.drawable.vd_back_white));
                rlyDelete.setVisibility(View.VISIBLE);
                rlyEdit.setVisibility(View.VISIBLE);
                txvTitle.setText(getResources().getString(R.string.sensor_edit));
            } else {
                ivMenu.setBackground(getDrawable(R.drawable.vd_menu_white));
                rlyDelete.setVisibility(View.GONE);
                rlyEdit.setVisibility(View.GONE);
                txvTitle.setText(getResources().getString(R.string.sensor));
            }
        } else if (nTabIndex == PAGE_SEARCH) {
            txvTitle.setText(getResources().getString(R.string.search));
        } else if (nTabIndex == PAGE_ALARM) {
            if (bEdit) {
                ivMenu.setBackground(getDrawable(R.drawable.vd_back_white));
                rlyDelete.setVisibility(View.VISIBLE);
                rlyEdit.setVisibility(View.VISIBLE);
                txvTitle.setText(getResources().getString(R.string.alarm_edit));
            } else {
                ivMenu.setBackground(getDrawable(R.drawable.vd_menu_white));
                rlyDelete.setVisibility(View.GONE);
                rlyEdit.setVisibility(View.GONE);
                txvTitle.setText(getResources().getString(R.string.alarm));
            }

        } else if (nTabIndex == PAGE_SETTING) {
            txvTitle.setText(getResources().getString(R.string.setting));
        }

    }

    public void changePage() {

        bEdit = false;
        changeTitle();

        if ((nTabIndex == PAGE_SENSOR || nTabIndex == PAGE_SEARCH || nTabIndex == PAGE_ALARM || nTabIndex == PAGE_SETTING)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rlySensor.setBackgroundColor(getColor(R.color.white));
                rlySearch.setBackgroundColor(getColor(R.color.white));
                rlyAlarm.setBackgroundColor(getColor(R.color.white));
                rlySetting.setBackgroundColor(getColor(R.color.white));

                txvSensor.setTextColor(getColor(R.color.color_171717));
                txvSearch.setTextColor(getColor(R.color.color_171717));
                txvAlarm.setTextColor(getColor(R.color.color_171717));
                txvSetting.setTextColor(getColor(R.color.color_171717));

                ivSensor.setBackground(getDrawable(R.drawable.vd_signal_wifi_menu));
                ivSearch.setBackground(getDrawable(R.drawable.vd_search_menu));
                ivAlarm.setBackground(getDrawable(R.drawable.vd_email_menu));
                ivSetting.setBackground(getDrawable(R.drawable.vd_settings_menu));
            }
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_main);

        if (nTabIndex == PAGE_SENSOR) {
            txvTitle.setText(getResources().getString(R.string.sensor));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rlySensor.setBackgroundColor(getColor(R.color.color_156aee));
                txvSensor.setTextColor(getColor(R.color.white));
                ivSensor.setBackground(getDrawable(R.drawable.vd_signal_wifi_menu_white));
            }

            navController.navigate(R.id.sensorFragment);
        } else if (nTabIndex == PAGE_SEARCH) {
            txvTitle.setText(getResources().getString(R.string.search));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rlySearch.setBackgroundColor(getColor(R.color.color_156aee));
                txvSearch.setTextColor(getColor(R.color.white));
                ivSearch.setBackground(getDrawable(R.drawable.vd_search_menu_white));
            }

            navController.navigate(R.id.searchFragment);
        } else if (nTabIndex == PAGE_ALARM) {
            txvTitle.setText(getResources().getString(R.string.alarm));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rlyAlarm.setBackgroundColor(getColor(R.color.color_156aee));
                txvAlarm.setTextColor(getColor(R.color.white));
                ivAlarm.setBackground(getDrawable(R.drawable.vd_email_white));
            }

            navController.navigate(R.id.alarmFragment);
        } else if (nTabIndex == PAGE_SETTING) {
            txvTitle.setText(getResources().getString(R.string.setting));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rlySetting.setBackgroundColor(getColor(R.color.color_156aee));
                txvSetting.setTextColor(getColor(R.color.white));
                ivSetting.setBackground(getDrawable(R.drawable.vd_settings_menu_white));
            }

            navController.navigate(R.id.settingFragment);
        } else if (nTabIndex == PAGE_REPEAT_SETTING) {
            txvTitle.setText(getResources().getString(R.string.setting));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rlySetting.setBackgroundColor(getColor(R.color.color_156aee));
                txvSetting.setTextColor(getColor(R.color.white));
                ivSetting.setBackground(getDrawable(R.drawable.vd_settings_menu_white));
            }

            navController.navigate(R.id.alarmrsettingFragment);
        } else if (nTabIndex == PAGE_INFO) {
            txvTitle.setText(getResources().getString(R.string.setting));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rlySetting.setBackgroundColor(getColor(R.color.color_156aee));
                txvSetting.setTextColor(getColor(R.color.white));
                ivSetting.setBackground(getDrawable(R.drawable.vd_settings_menu_white));
            }

            navController.navigate(R.id.infomationFragment);
        } else if (nTabIndex == PAGE_ABOUT) {
            txvTitle.setText(getResources().getString(R.string.setting));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rlySetting.setBackgroundColor(getColor(R.color.color_156aee));
                txvSetting.setTextColor(getColor(R.color.white));
                ivSetting.setBackground(getDrawable(R.drawable.vd_settings_menu_white));
            }

            navController.navigate(R.id.aboutFragment);
        }
    }

    //위치서비스가 커져있는지 체크
    private boolean isGpsEnable() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            return false;
        }
        return true;
    }

    //위치서비스가 커져있는지 체크
    private boolean isBluetoothEnable() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return false;
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            return false;
        } else {
            // Bluetooth is enabled
            return true;
        }
    }

    /**
     * 위치서비스 유도 팝업
     */
    public void showLocationSettingsAlert() {
        String content = getResources().getString(R.string.gps_off);
        String left = getResources().getString(R.string.cancel);
        String right = getResources().getString(R.string.setting);
        TwoBtnDialog.init(this, content, left, right, new TwoBtnDialog.OnClickListener() {
            @Override
            public void onConfirm() {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                finish();
            }
        }).show();
    }

    /**
     * 블루투스 유도 팝업
     */
    public void showBluetoothSettingsAlert() {
        String content = getResources().getString(R.string.bluetooth_off);
        String left = getResources().getString(R.string.cancel);
        String right = getResources().getString(R.string.setting);
        TwoBtnDialog.init(this, content, left, right, new TwoBtnDialog.OnClickListener() {
            @Override
            public void onConfirm() {
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                finish();
            }
        }).show();
    }

    private void loadSensorHandleService() {
        if (null == SensorHandleService.serviceIntent) {
            foregroundServiceIntent = new Intent(this, SensorHandleService.class);
            startService(foregroundServiceIntent);
        } else {
            foregroundServiceIntent = SensorHandleService.serviceIntent;
        }
    }

    /**
     * Click Events
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_menu:
                if (bEdit) {
                    bEdit = false;
                    changeTitle();
                    return;
                }
                drawerLayout.openDrawer(drawerView);
                break;
            case R.id.rly_sensor:
                drawerLayout.closeDrawer(drawerView);

                nTabIndex = PAGE_SENSOR;
                changePage();
                break;
            case R.id.rly_search:
                drawerLayout.closeDrawer(drawerView);

                nTabIndex = PAGE_SEARCH;
                changePage();
                break;
            case R.id.rly_alarm:
                drawerLayout.closeDrawer(drawerView);

                nTabIndex = PAGE_ALARM;
                changePage();
                break;
            case R.id.rly_setting:
                drawerLayout.closeDrawer(drawerView);

                nTabIndex = PAGE_SETTING;
                changePage();
                break;
            case R.id.rly_edit: //센서 편집, 알림 편집
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_main);
                Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                if (fragment != null && fragment instanceof AlarmFragment) {
                    ReceiveUserDialog.init(this, Common.gAlarmInfo, new ReceiveUserDialog.OnClickListener() {
                        @Override
                        public void onConfirm(String type, String content) {
                            bEdit = false;
                            changeTitle();
                            AlarmListInfo item = new AlarmListInfo(content, type, Common.gAlarmInfo.getTemp(), Common.gAlarmInfo.getHumi(), Common.gAlarmInfo.getBattery(), Common.gAlarmInfo.getConnect(), Common.gAlarmInfo.getError());
                            Util.updateAlarm(MainActivity.this, Common.gAlarmInfo.getPhone(), item);
                        }

                        @Override
                        public void onCancel() {
                            bEdit = false;
                            changeTitle();
                        }

                    }).show();
                } else {
                    String name = BrainyTempApp.getSensorName(Common.gSelDevice);
                    NameEditDialog.init(this, name, new NameEditDialog.OnClickListener() {
                        @Override
                        public void onConfirm(String content) {
                            bEdit = false;
                            changeTitle();

                            BrainyTempApp.setSensorName(Common.gSelDevice, content);

                            Intent sendIntent = new Intent(Common.ACT_SENSOR_LIST_UPDATE);
                            LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(sendIntent);
                        }
                    }).show();
                }
                break;
            case R.id.rly_delete://센서 삭제

                NavHostFragment navHostFragment1 = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_main);
                Fragment fragment1 = navHostFragment1.getChildFragmentManager().getFragments().get(0);
                if (fragment1 != null && fragment1 instanceof AlarmFragment) {
                    bEdit = false;
                    changeTitle();

                    if (Common.gAlarmInfo == null) {
                        return;
                    }
                    if (Util.deleteAlarm(Common.gAlarmInfo.getPhone())) {
                        Intent sendIntent = new Intent(Common.ACT_ALARM_LIST_UPDATE);
                        sendBroadcast(sendIntent);
                    }

                } else {
                    SensorDelDialog.init(this, new SensorDelDialog.OnClickListener() {
                        @Override
                        public void onConfirm() {
                            bEdit = false;
                            changeTitle();

                            if (Common.gSelDevice.equals("")) {
                                return;
                            }
                            if (Util.deleteSensor(Common.gSelDevice)) {
                                SensorDataRepository repository = new SensorDataRepository(getApplication());
                                repository.deleteSensorData(Common.gSelDevice);

                                Intent sendIntent = new Intent(Common.ACT_SENSOR_LIST_UPDATE);
                                LocalBroadcastManager.getInstance(BrainyTempApp.getInstance()).sendBroadcast(sendIntent);
                            }
                        }

                        @Override
                        public void onCancel() {
                            bEdit = false;
                            changeTitle();
                        }
                    }).show();
                }
                break;
        }
    }

    /**
     * BACK key 핸들러
     */
    Handler m_hndBackKey = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0)
                bFinish = false;
        }
    };

    @Override
    public void onBackPressed() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_main);
        Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (fragment != null && fragment instanceof SensorFragment) {

            if (bEdit) { //편집상태라면 원래상태로 .....
                bEdit = false;
                changeTitle();
                return;
            }

            if (!bFinish) {
                bFinish = true;
                Toast.makeText(this, R.string.app_finish_message, Toast.LENGTH_SHORT).show();
                m_hndBackKey.sendEmptyMessageDelayed(0, 2000);
            } else {
                // 앱을 종료한다.
                finishApplication();
            }
            return;
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 온도 경보음 울리기
     */
    public void showAlarm(String device, double curTemp, int curHumi) {

        int alarmCycle = Integer.valueOf(BrainyTempApp.getAlarmRepeatCycle ());

        if (alarmCycle == 0) { //설정페이지에서 설정한 경보음 반복주기가 0이면 알림 사용안함으로 본다.
            return;
        }

        long storeTime = (long) Double.parseDouble(BrainyTempApp.getAlarmTime(device));
        long currentTime = System.currentTimeMillis();
        int dicSec = (int) ((currentTime - storeTime) / 1000);


        if (dicSec < ((alarmCycle * 60)-30)) { //경보음 울리는 시간차가 알림반복주기시간보다 작으면 리턴(30초 여유시간을 둔다)
            return;
        }

        if (AlertActivity.getInstance() != null) {
            AlertActivity.getInstance().setData(device, String.valueOf(curTemp), String.valueOf(curHumi));
            return;
        }

        Intent intent = new Intent(this, AlertActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("device", device);
        intent.putExtra("temp", String.valueOf(curTemp));
        intent.putExtra("humi", String.valueOf(curHumi));

        startActivity(intent.setAction(Intent.ACTION_MAIN));
    }

    private void finishApplication() {
        WorkManager.getInstance().cancelAllWork();
        moveTaskToBack(true);
        finishAndRemoveTask();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    void registerReceiver() {
        IntentFilter f = new IntentFilter();
        f.addAction(Common.ACT_ALARM_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(f));
    }

    //boradcast receive
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Common.ACT_ALARM_UPDATE)) {

                String data = intent.getStringExtra("data");

                try {
                    JSONObject jsonData = new JSONObject(data);

                    String device = (String)jsonData.get("device");
                    double curTemp = (double)jsonData.getDouble("curTemp");
                    int curHumi = (int)jsonData.getDouble("curHumi");
                    showAlarm(device, curTemp, curHumi);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

}