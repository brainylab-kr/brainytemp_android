package kr.brainylab.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.PermissionChecker;


public class BaseActivity extends AppCompatActivity {

    private AppCompatDialog loadingDlg = null;
    private static ProgressDialog _progressDlg = null;
    private IntentFilter intentFilter;
    private WindowManager.LayoutParams params;

    BroadcastReceiver powerConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                Toast.makeText(context, "충전기가 연결되었습니다", Toast.LENGTH_LONG).show();
                params.screenBrightness = 1f;
                getWindow().setAttributes(params);

            }else if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                Toast.makeText(context, "충전기가 해제되었습니다", Toast.LENGTH_LONG).show();
                params.screenBrightness = 0f;
                getWindow().setAttributes(params);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        params = getWindow().getAttributes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(powerConnectionReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(powerConnectionReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public boolean checkPermission(Context context, String... permission) {

        boolean nr = true;

        for (int i = 0; i < permission.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                nr = context.checkSelfPermission(permission[i])
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                nr = PermissionChecker.checkSelfPermission(context, permission[i])
                        == PermissionChecker.PERMISSION_GRANTED;
            }

            if (!nr) {
                break;
            }
        }
        return nr;
    }



}
