package com.smaaash.tv.apis;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import com.smaaash.tv.apis.base.AnyError;
import com.smaaash.tv.apis.base.ApiService;
import com.smaaash.tv.apis.base.Error;
import com.smaaash.tv.apis.base.RestClient;
import com.smaaash.tv.background.Callback;
import com.smaaash.tv.background.Interactor;
import com.smaaash.tv.utils.BuildConfig;
import com.smaaash.tv.utils.Installation;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Siddhesh on 18-07-2018.
 */

public class PhoneStateInteractor implements Interactor {

    private Callback callback;
    private String acr_data;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setAcr_data(String acr_data) {
        this.acr_data = acr_data;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            RestClient restClient = new RestClient(ApiService.class);
            ApiService apiService = (ApiService) restClient.getApiService2();


            JSONObject data = new JSONObject();
            data.put("package_name", context.getPackageName());
            data.put("timestamp", System.currentTimeMillis());
            data.put("device_model", Build.MODEL);
            data.put("device_manufacturer", Build.MANUFACTURER);
            data.put("os_version", System.getProperty("os.version"));
            data.put("api_level", Build.VERSION.SDK);
            data.put("device", Build.DEVICE);
            data.put("device_product", Build.PRODUCT);
            data.put("version_release", Build.VERSION.RELEASE);
            data.put("brand", Build.BRAND);
            data.put("cpu_abi", Build.CPU_ABI);
            data.put("cpu_abi2", Build.CPU_ABI2);
            data.put("display", Build.DISPLAY);
            data.put("fingerprint", Build.FINGERPRINT);
            data.put("hardware", Build.HARDWARE);
            data.put("build_id", Build.ID);
            data.put("unique_id", Installation.id(context));
            data.put("android_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            data.put("mac_id", wm.getConnectionInfo().getMacAddress());
            data.put("user", Build.USER);
            data.put("sdk_version_code", BuildConfig.VERSION_CODE);
            data.put("sdk_version_name", BuildConfig.VERSION_NAME);
            if (acr_data != null)
                data.put("acr_data", new JSONObject(acr_data));

//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("DATA", data);

            Call<ResponseBody> call = apiService.uploadPhoneState(data);

            Response<ResponseBody> response = call.execute();
            if (response != null && response.isSuccessful()) {
                onSuccess(response.body());
            } else {
                onError(new AnyError("ERROR", 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(Object object) {
        callback.onSuccess(object);
    }

    @Override
    public void onError(Error object) {
        callback.onError(object);
    }
}
