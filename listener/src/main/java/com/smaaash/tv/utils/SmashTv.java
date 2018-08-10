package com.smaaash.tv.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.smaaash.tv.apis.EpisodeInteractor;
import com.smaaash.tv.apis.PhoneStateInteractor;
import com.smaaash.tv.apis.base.Error;
import com.smaaash.tv.aws.PubSubHelper;
import com.smaaash.tv.background.Callback;
import com.smaaash.tv.background.ExecutorModule;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import models.CustomFile;
import models.CustomStream;
import models.EpisodeDetailResponse;
import models.Screen;
import models.acrResponse;
import models.mqttResponse;

public class SmashTv implements TimerTasks.TaskListener, PubSubHelper.Communicator {

    private final int MY_PERMISSIONS_RECORD_AUDIO = 111;
    private AudioListener mAudioListener;
    private WebView webView;
    private SmaaashTvListener listener;
    //    private MqttAndroidClient mClient;
//    private boolean isBrokerConnected;
//    private String mTopic;
    private String channelId;
    private String userId;
    private Activity context;
    private String defaultUrl = "http://api.smaaash.com/quiz/index.html?action=0";
    private Timer timer = new Timer();
    private PubSubHelper helper;

    public static String TAG = SmashTv.class.getSimpleName();

    @Override
    public void performTask(final String url) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (webView != null) {
                    webView.loadUrl(url);
                    Log.v(TAG + "WEBVIEW", "LOADING...@@@@@@@@@@@");
                    Log.v(TAG + "webview : " + url, new Date().toString());
                }
            }
        });
    }

    @Override
    public void onMessageReceived(String message) {
        Log.v(TAG , "Message arrived from topic");
        mqttResponse response = null;
        try {
            response = new ObjectMapper().readValue(message.toString(), mqttResponse.class);
            webView.loadUrl(response.getData());

            if (listener != null) {
                listener.onUrlReceivedEvent(response.getData());
            }
            performTask(response.getData());
        } catch (IOException e) {
            Log.v(TAG + "ERROR in arrived msg", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(PubSubHelper helper) {
        helper.globalSubscribe("global/" + userId);
    }

    public interface SmaaashTvListener {
        void onUrlReceivedEvent(String url);

        void onResponseReceived(String response);
    }

//    public SmashTv(Activity context, String userId, WebView yourWebView, String defaultUrl) {
//        this(context, userId, yourWebView);
//        this.defaultUrl = defaultUrl;
//        webView.loadUrl(defaultUrl);
//    }

    public SmashTv(Activity context, String userId) {
        this.userId = userId;
        this.context = context;
    }

    public SmashTv(Activity context, String userId, WebView yourWebView) {
        this.userId = userId;
        webView = yourWebView;
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("Error", description);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
        }
        webView.getSettings().setBlockNetworkLoads(false);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(0);
        }
        webView.loadUrl(defaultUrl);
//        webView.setWebViewClient(new WebViewClient());
        this.context = context;

        helper = new PubSubHelper(context, this);
    }

    public SmashTv(Activity context, String userId, SmaaashTvListener smaaashTvListener) {
        this.userId = userId;
        listener = smaaashTvListener;
        this.context = context;
        helper = new PubSubHelper(context, this);
    }

    public void startWorking() {
//        doConnect();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            processWorking();
        } else {
            Log.v(TAG , "Need audio record permission");
            requestAudioPermissions();
        }
    }

    private void processWorking() {
        if (helper != null) helper.startConnection();
        if (Utils.isNewUser(context)) {
            uploadPhoneState(true, null);
        }
        startAudioListening();
    }

    //Requesting run-time permissions
    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(context, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            //Go ahead with recording audio now
            processWorking();
        }
    }

    public void destroyEverything() {
        if (mAudioListener != null) {
            mAudioListener.cancel();
            mAudioListener.destroy();
        }
        if (helper != null) helper.endConnection();
//        if (mClient != null && mClient.isConnected()) try {
//            mClient.disconnect();
//            mClient.unregisterResources();
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
        webView = null;
        listener = null;
    }

    int offSet = 0;

    private void startAudioListening() {
        Log.v(TAG + "ACROP", "STARTED");
        mAudioListener = new AudioListener(context);
        mAudioListener.setDetection(new AudioListener.AudioDetection() {
            @Override
            public void onDetect(final acrResponse audio) {
                Log.v(TAG + "ACROP", audio.toString());

                if (!(webView == null && listener == null)) {

                    if (audio.getStatus().getCode() != null) {
                        CustomStream customStream = null;
                        if (audio.getMetadata().getCustomStreams() != null)
                            customStream = audio.getMetadata().getCustomStreams().get(0);
                        CustomFile customFile = null;
                        if (audio.getMetadata().getCustomFiles() != null)
                            customFile = audio.getMetadata().getCustomFiles().get(0);

                        if (customFile == null && customStream == null)
                            return;

                        int score = (customFile == null) ? customStream.getScore() : customFile.getScore();
                        String temp_channelId = (customFile == null) ? customStream.getChannelId() : customFile.getChannelId();


                        if (customFile != null)
                            offSet = customFile.getPlayOffsetMs() / 1000;


                        if (score > 50 && !temp_channelId.equals(channelId)) {
                            // IF SCORE IS > 50 and is not (old channel or null)
                            // Hit api and schedule urls to web view
                            channelId = temp_channelId;

                            EpisodeInteractor interactor = new EpisodeInteractor();
                            interactor.setChannelId(channelId);
                            interactor.setUserId(userId);
                            final CustomFile finalCustomFile = customFile;
                            interactor.setCallback(new Callback<EpisodeDetailResponse>() {
                                @Override
                                public void onSuccess(EpisodeDetailResponse callback) {

                                    if (listener != null)
                                        listener.onResponseReceived(new Gson().toJson(callback));

                                    Log.v(TAG + "RESPONSE", callback.toString());
                                    long lastScreen = 0;
                                    if (callback.getEpisodes().size() > 0) {
                                        List<Screen> screens = callback.getEpisodes().get(0).getScreens();
                                        for (Screen screen : screens) {
                                            if (finalCustomFile != null) {
                                                if (offSet < (screen.getStartTime() * 60) + screen.getStartTimeSeconds()) {

                                                    // if video pointer < scheduled time then schedule
                                                    Log.v(TAG + "SCHEDULING", screen.getUrl());
                                                    scheduleTasksCustomFile((screen.getStartTime() * 60) + screen.getStartTimeSeconds() - offSet,
                                                            screen.getUrl()
                                                                    + "&episode_id=" + callback.getEpisodes().get(0).getId()
                                                                    + "&screen_id=" + screen.getId());

                                                    // last screen ?
                                                    if (((screen.getStartTime() * 60) + screen.getStartTimeSeconds() - offSet) > lastScreen) {
                                                        lastScreen = (screen.getStartTime() * 60) + screen.getStartTimeSeconds() - offSet;
                                                    }
                                                }
                                            } else {
                                                scheduleTasks(callback.getEpisodes().get(0).getStartTime(),
                                                        (screen.getStartTime() * 60) + screen.getStartTimeSeconds(),
                                                        screen.getUrl()
                                                                + "&episode_id=" + callback.getEpisodes().get(0).getId()
                                                                + "&screen_id=" + screen.getId());
                                                // last screen ?
                                                if (((screen.getStartTime() * 60) + screen.getStartTimeSeconds()) > lastScreen) {
                                                    lastScreen = (screen.getStartTime() * 60) + screen.getStartTimeSeconds();
                                                }
                                            }

                                            if (lastScreen > 0) {
                                                Timer cleaner = new Timer();
                                                cleaner.schedule(new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        removeTasks();
                                                        channelId = null;
                                                        Log.v(TAG + "CLEANER", "resetting");
                                                    }
                                                }, lastScreen + 100);
                                            }
                                        }
                                        // also try to subscribe to the channel
                                        // subscribeTo(callback.getEpisodes().get(0).getChannel());
//                                    subscribeTo(userId + "/" + channelId);
                                        if (helper != null && helper.getConnectionStatus() == PubSubHelper.CONNECTED) {
                                            helper.subscribe("channel/" + channelId + "/" + userId);
                                        }
                                    }
                                }

                                @Override
                                public void onError(Error callback) {

                                }
                            });
                            ExecutorModule.provideExecutor().runOnBackground(interactor);

                        }
                    } else {
//                    unsubscribe();
                        if (helper != null) helper.unSubscribe();
                    }
                }
                uploadPhoneState(false, new Gson().toJson(audio));
            }
        });
//        mAudioListener.start();
    }

    private void scheduleTasksCustomFile(int seconds, String url) {
        timer.schedule(new TimerTasks(url, this), convertTime(Utils.getCurrentISOTime(), seconds));
    }

    private void scheduleTasks(String dtStart, int seconds, String url) {
//        String dtStart = "2018-07-16T20:38:37Z";

        Date date = convertTime(dtStart, seconds);
        if (new Date().after(date)) {
            date = convertTime(Utils.getCurrentISOTime(), seconds);
        }
        timer.schedule(new TimerTasks(url, this), date);
    }

    private void removeTasks() {
        timer.cancel();
    }

    private Date convertTime(String dtStart, int seconds) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");/*:ss'Z'*/
        try {
            date = format.parse(dtStart);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.SECOND, seconds);

            date = calendar.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    private void uploadPhoneState(final boolean fromNewUser, String acr_data) {
        PhoneStateInteractor interactor = new PhoneStateInteractor();
        if (!fromNewUser) {
            interactor.setAcr_data(acr_data);
        }
        interactor.setContext(context);
        interactor.setCallback(new Callback() {
            @Override
            public void onSuccess(Object callback) {
                Log.v(TAG + "phone_upload", "success");
                if (fromNewUser) {
                    Utils.userIsOldNow(context);
                }
            }

            @Override
            public void onError(Error callback) {
                Log.v(TAG + "phone_upload", "error");
            }
        });
        ExecutorModule.provideExecutor().runOnBackground(interactor);
    }


//    private void doConnect() {
//        Log.d(TAG, "doConnect()");
//        IMqttToken token;
//        MqttConnectOptions options = new MqttConnectOptions();
//        options.setKeepAliveInterval(60);//seconds
//        options.setCleanSession(true);
//        options.setAutomaticReconnect(true);
//
//        try {
////            String deviceId = MqttClient.generateClientId();
//            String deviceId = UUID.randomUUID().toString();
//            mClient = new MqttAndroidClient(context, "tcp://13.127.45.200:1883", deviceId);
//            token = mClient.connect(options);
////            token.waitForCompletion(3500);
//            token.setActionCallback(new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    Log.d("MQTT", "onSuccess");
//                    isBrokerConnected = true;
//                    subscribeGlobal("global");
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    Log.i("SmaaashTv", "Failed to connect smaaash messaging backend ");
//                    Log.i("SmaaashTv", exception.toString());
//                    exception.printStackTrace();
//                }
//            });
//            mClient.setCallback(new MqttEventCallback());
//        } catch (MqttSecurityException e) {
//            e.printStackTrace();
//            Log.i("SmaaashTv", e.toString());
//        } catch (MqttException e) {
//            Log.i("SmaaashTv", e.toString());
//            switch (e.getReasonCode()) {
//                case MqttException.REASON_CODE_BROKER_UNAVAILABLE:
//                case MqttException.REASON_CODE_CLIENT_TIMEOUT:
//                case MqttException.REASON_CODE_CONNECTION_LOST:
//                case MqttException.REASON_CODE_SERVER_CONNECT_ERROR:
//                    Log.v(TAG, "c" + e.getMessage());
//                    e.printStackTrace();
//                    break;
//                case MqttException.REASON_CODE_FAILED_AUTHENTICATION:
//                    Log.e(TAG, "b" + e.getMessage());
//                    break;
//                default:
//                    Log.e(TAG, "a" + e.getMessage());
//            }
//        }
//    }

//    public void unsubscribe() {
//        try {
//            IMqttToken unsub = mClient.unsubscribe(mTopic);
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }

//    private void subscribeGlobal(String channel) {
//        try {
//            if (mClient.isConnected()) {
//                IMqttToken subscriptionToken = mClient.subscribe(channel, 1);
////                    subscriptionToken.waitForCompletion(5000);
//            }
//        } catch (MqttException e) {
//            Log.i("SmaaashTv", e.toString());
//            e.printStackTrace();
//        }
//    }

//    public void subscribeTo(String channel) {
//        Log.d("MQTT SUBSCRIBE", "Old : " + mTopic + " NEW : " + channel);
//        if (mTopic != null && mTopic.equals(channel)) {
//            return;
//        }
//
//        // unSubscribing previous channel
//        if (mTopic != null)
//            unsubscribe();
//
//        mTopic = channel;
//        if (true) {
//            try {
//                if (mClient.isConnected()) {
//                    IMqttToken subscriptionToken = mClient.subscribe(mTopic, 1);
////                    subscriptionToken.waitForCompletion(5000);
//                }
//            } catch (MqttException e) {
//                Log.i("SmaaashTv", e.toString());
//                e.printStackTrace();
//            }
//        } else {
//            Log.i("SmaaashTv", "smaaash messaging backend is not connected");
//            Log.i("SmaaashTv", "smaaash messaging backend is connected " + mClient.isConnected());
//        }
//    }

//    private class MqttEventCallback implements MqttCallback {
//
//        @Override
//        public void connectionLost(Throwable arg0) {
//            Log.i("SmaaashTv", arg0.toString());
//        }
//
//        @Override
//        public void deliveryComplete(IMqttDeliveryToken arg0) {
//            Log.i("SmaaashTv", arg0.toString());
//        }
//
//        @Override
//        @SuppressLint("NewApi")
//        public void messageArrived(String topic, final MqttMessage message) throws Exception {
//            Log.i(TAG, "Message arrived from topic" + topic);
//            mqttResponse response = new ObjectMapper().readValue(message.toString(), mqttResponse.class);
//            webView.loadUrl(response.getData());
//            if (listener != null) {
//                listener.onUrlReceivedEvent(response.getData());
//            }
//            performTask(response.getData());
//        }
//    }

}
