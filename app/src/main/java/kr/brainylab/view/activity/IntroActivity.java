package kr.brainylab.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
/**
 * 인트로
 */
public class IntroActivity extends BaseActivity {

    AnimationDrawable introAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_intro);

        //ImageView introImage = (ImageView) findViewById(R.id.intro_ani);
        //introImage.setBackgroundResource(R.drawable.xml_img_logo_splash);
        //introAnimation = (AnimationDrawable) introImage.getBackground();

        if (!BrainyTempApp.getAllowPermission()) {
            checkPermission();
        } else {
            goNext();
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

    private void checkPermission() {
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                ) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1000);

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
                    String permission = permissions[i];
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