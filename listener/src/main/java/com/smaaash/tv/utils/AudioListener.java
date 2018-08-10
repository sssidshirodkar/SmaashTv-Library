package com.smaaash.tv.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.IACRCloudListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import models.acrResponse;

import static com.smaaash.tv.utils.SmashTv.TAG;

public class AudioListener extends Observable implements IACRCloudListener {

    public interface AudioDetection {
        void onDetect(acrResponse s);
    }

    private AudioDetection detection;
    private String path;
    private ACRCloudConfig mConfig;
    private ACRCloudClient mClient;
    private boolean initState;
    private acrResponse mResponse;
    private ObjectMapper mapper = new ObjectMapper();
    private boolean mProcessing = false;
    private long startTime = 0;
    private long stopTime = 0;
    String Tag = "TESTING";

    private long defaultDelay = TimeUnit.SECONDS.toMillis(10);
    private long maxWatchingDelay = TimeUnit.SECONDS.toMillis(30);
    private long maxNotWatchingDelay = TimeUnit.MINUTES.toMillis(2);
    private long delay = TimeUnit.SECONDS.toMillis(5);

    void setDetection(AudioDetection detection) {
        this.detection = detection;
    }

    public AudioListener(Context ctx) {

        path = Environment.getExternalStorageDirectory().toString()
                + "/cloud/model";
        File file = new File(path);
        if (!file.exists()) {
            Boolean isPathCreated = file.mkdirs();
        }

        mConfig = new ACRCloudConfig();
        mConfig.acrcloudListener = this;
        mConfig.host = "identify-ap-southeast-1.acrcloud.com";
        mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
        mConfig.accessKey = "567834a9c9debe9951bd00d953cd3bfc";
        mConfig.accessSecret = "8dSNPMUrNDFTlSNgQ3J8Qf8fI39gShh1s7NEDymy";
        mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTP; // PROTOCOL_HTTPS
        mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
        mConfig.context = ctx;
        mClient = new ACRCloudClient();
        initState = mClient.initWithConfig(mConfig);
        if (initState) {
            mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
        }
        start();
    }

    public void start() {
        if (!initState) {
            return;
        }
        if (!mProcessing) {
            mProcessing = mClient != null && mClient.startRecognize();
            startTime = System.currentTimeMillis();
        }
    }

    public void stop() {
        if (mProcessing && mClient != null) {
            mClient.stopRecordToRecognize();
        }
        mProcessing = false;

        stopTime = System.currentTimeMillis();
    }

    public void destroy() {
        if (mProcessing && mClient != null) {
            mClient.stopRecordToRecognize();
        }
        mProcessing = false;
        mClient = null;

        stopTime = System.currentTimeMillis();
    }

    protected void cancel() {
        if (mProcessing && mClient != null) {
            mProcessing = false;
            mClient.cancel();
        }
    }

    private boolean isWatching = false;
    private boolean watcher = false;

    @Override
    public void onResult(String s) {
        Log.v(TAG , s);
        Log.v(TAG , "ACR RESULT" + new Date().toString());

        try {
            mResponse = mapper.readValue(s, acrResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (detection != null && mResponse != null && mResponse.getStatus().getCode() == 0) {
            isWatching = true;
            detection.onDetect(mResponse);
        } else {
            isWatching = false;
        }

        cancel();

        if (watcher == isWatching) {
            if (isWatching) {
                // got response
                if (delay >= maxWatchingDelay)
                    delay = maxWatchingDelay;
                else
                    delay = delay + TimeUnit.SECONDS.toMillis(5);
            } else {
                // got no response
                if (delay >= maxNotWatchingDelay)
                    delay = maxNotWatchingDelay;
                else
                    delay = delay + TimeUnit.SECONDS.toMillis(5);
            }
        } else {
            delay = defaultDelay;
        }

        Log.v(TAG, "DELAYING @@@@@ " + new Date().toString());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.v(TAG, "STARTING @@@@@ " + new Date().toString());
                start();
            }
        }, delay);
        watcher = isWatching;

    }

    @Override
    public void onVolumeChanged(double v) {
//        Log.d("Is processing?", ""+mProcessing);
    }

    public static final int SUCCESS = 0;
    public static final int NO_RESULT = 1001;
    public static final int JSON_ERROR = 2002;
    public static final int ENGINE_TYPE_ERROR = 2006;
    public static final int HTTP_ERROR = 3000;
    public static final int GEN_FP_ERROR = 2004;
    public static final int HTTP_ERROR_TIMEOUT = 2005;
    public static final int RECORD_ERROR = 2000;
    public static final int INIT_ERROR = 2001;
    public static final int RESAMPLE_ERROR = 2008;
    public static final int NO_INIT_ERROR = 2003;
    public static final int UNKNOW_ERROR = 2010;

}
