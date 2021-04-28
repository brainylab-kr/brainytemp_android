package kr.brainylab.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
/**
 * 인트로
 */
public class IntroActivity extends BaseActivity {

    private static final int WRITE_SETTINGS_PERMISSION_REQUEST_CODE = 0x1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_intro);

        //ImageView introImage = (ImageView) findViewById(R.id.intro_ani);
        //introImage.setBackgroundResource(R.drawable.xml_img_logo_splash);
        //introAnimation = (AnimationDrawable) introImage.getBackground();

        if(checkSystemPermission()) {
            if (!BrainyTempApp.getAllowPermission()) {
                checkPermission();
            } else {
                goNext();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //introAnimation.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBattery();
        //introAnimation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean permission;
        if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permission = Settings.System.canWrite(this);
            } else {
                permission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
            }

            if (!permission) {
                Toast.makeText(getApplicationContext(), "시스템 설정(밝기 조절 세팅, 화면 회전)을 변경을 위한 권한이 없어서 앱을 종료하였습니다", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                if (!BrainyTempApp.getAllowPermission()) {
                    checkPermission();
                } else {
                    goNext();
                }
            }
        }
    }

    private void goNext() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                goMain();

            }
        }, 2000);
    }

    private void goMain() {
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
        finish();
    }

    private boolean checkSystemPermission() {
        boolean permission;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(getApplicationContext());
        } else {
            permission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }

        if (!permission) {
            Toast.makeText(getApplicationContext(), "시스템 설정(밝기 조절 세팅, 화면 회전)을 변경하기 위해서 시스템 변경할 수 있는 권한이 필요합니다." +
                    "\n잠시 후에 시스템 설정 변경 창으로 이동합니다. 권한을 [허용]해주세요.", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, WRITE_SETTINGS_PERMISSION_REQUEST_CODE);
                    } else {
                        ActivityCompat.requestPermissions(IntroActivity.this, new String[]{Manifest.permission.WRITE_SETTINGS}, WRITE_SETTINGS_PERMISSION_REQUEST_CODE);
                    }
                }
            }, 3500);

            return false;
        }
        return true;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
            ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1000);
            }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            }

        } else {
            BrainyTempApp.setAllowPermission();
            goNext();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000:
                Boolean isAllowed = true;
                for (int i = 0; i < permissions.length; i++) {
                    int grantResult = grantResults[i];

                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isAllowed = false;
                        break;
                    }
                }

                if (!isAllowed) {
                    Toast.makeText(IntroActivity.this, getString(R.string.str_permission_denied), Toast.LENGTH_SHORT).show();
                } else {
                    BrainyTempApp.setAllowPermission();
                    goNext();
                }
                break;
        }
    }

    void checkBattery() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if(pm.isIgnoringBatteryOptimizations(this.getPackageName()) == false) {
                Intent intent  = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:"+ this.getPackageName()));
                this.startActivity(intent);
            }
        }
    }
}