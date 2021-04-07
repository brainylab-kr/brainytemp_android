package kr.brainylab.common;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


public class HttpService {
    public static final MediaType CONTENT_TYPE
            = MediaType.parse("application/json; charset=utf-8");
    public static String HTP_DOMAIN() {
        return "https://brainytemp.appspot.com/t1/";
    }

    private Context mContext;
    private OkHttpClient client;

    public interface ResponseListener {
        void onResponseResult(Boolean bSuccess, String res);
    }

    public HttpService(Context context) {

        mContext = context;
        client = new OkHttpClient();

    }

    private void postenc(String url, RequestBody body, ResponseListener resListner) {
        post(url, body, resListner);
    }

    private void post(String url2, RequestBody params, final ResponseListener resListner) {
        String url = getAbsoluteUrl(url2);
        Request request = new Request.Builder()
                .url(url)
                .post(params)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                if (mContext instanceof Activity) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resListner.onResponseResult(false, e.toString());
                            //loadingDlg.dismiss();
                        }
                    });
                } else {
                    resListner.onResponseResult(false, e.toString());
                    //loadingDlg.dismiss();
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                final String sres = response.body().string();
                if (mContext instanceof Activity) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resListner.onResponseResult(true, sres);
                            //loadingDlg.dismiss();
                        }
                    });
                } else {
                    resListner.onResponseResult(true, sres);

                    //loadingDlg.dismiss();
                }
            }
        });
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return HTP_DOMAIN() + relativeUrl;
    }

    public static boolean isBase64(String s) {
        return (s.length() % 4 == 0) && s.matches("^[A-Za-z0-9+/]+[=]{0,2}$");
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            copy.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    /**
     * 디바이스 인증
     */
    public void deviceAuth(String device, ResponseListener resListner) {
        String url = "authsensor";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mac", device);
        RequestBody body = RequestBody.create(CONTENT_TYPE, new Gson().toJson(hashMap)); // new
        postenc(url, body, resListner);
    }

    public void uploadData(String device, double temp, int humi, int rssi, ResponseListener resListner) {
        String url = "temp";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mac", device);
        hashMap.put("val", String.valueOf(temp));
        hashMap.put("hmd", String.valueOf(humi));
        hashMap.put("rssi", String.valueOf(rssi));

        String postBody = new Gson().toJson(hashMap);
        RequestBody body = RequestBody.create(CONTENT_TYPE, postBody);

        Log.d("BrainyTemp", "POST body: " + postBody);

        postenc(url, body, resListner);
    }

    /**
     * 이벤트 알림
     */
    public void eventAlarm(String device, String deviceName, String type, String alarmType, String phone, double curVal, double lowVal, double highVal, ResponseListener resListner) {
        String url = "event";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mac", device);
        hashMap.put("nm", deviceName);
        hashMap.put("evTp", type);
        hashMap.put("met", alarmType);
        hashMap.put("toPhNo", phone);
        hashMap.put("curVal", String.valueOf(curVal));
        hashMap.put("lowVal", String.valueOf(lowVal));
        hashMap.put("highVal", String.valueOf(highVal));

        RequestBody body = RequestBody.create(CONTENT_TYPE, new Gson().toJson(hashMap)); // new
        postenc(url, body, resListner);
    }
}





