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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
