package com.smaaash.tv.aws;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
//import com.amazonaws.regions.Regions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.UUID;

import static com.smaaash.tv.utils.SmashTv.TAG;

/**
 * Created by Siddhesh on 07-08-2018.
 */

public class PubSubHelper {

    public static int CONNECTING = 0;
    public static int CONNECTED = 1;
    public static int RECONNECTING = 2;
    public static int DISCONNECTED = 3;
    public static int ERROR = 4;

    public interface Communicator {
        void onMessageReceived(String message);

        void onConnected(PubSubHelper helper);
    }

    private static final String LOG_TAG = PubSubHelper.class.getCanonicalName();

    // IoT endpoint
    // AWS Iot CLI describe-endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com
    private final String CUSTOMER_SPECIFIC_ENDPOINT = "a2pki6xnyg76l2.iot.ap-south-1.amazonaws.com";
    // Region of AWS IoT
//    private final Regions MY_REGION = Regions.AP_SOUTHEAST_1;
    // Filename of KeyStore file on the filesystem
    private final String KEYSTORE_NAME = "iot_keystore2";
    // Password for the private key in the KeyStore
    private final String KEYSTORE_PASSWORD = "password2";
    // Certificate and key aliases in the KeyStore
    private final String CERTIFICATE_ID = "default2";

    private AWSIotMqttManager mqttManager;
    private String clientId;
    private String keystorePath;
    private String keystoreName;
    private String keystorePassword;

    private KeyStore clientKeyStore = null;
    private String certificateId;

    private StringBuilder keyBuf = new StringBuilder();
    private StringBuilder certBuf = new StringBuilder();
    private InputStream someKey, someCert;
    private Context context;
    private Communicator communicator;
    private int connectionStatus;
    private String mTopic;

    public int getConnectionStatus() {
        return connectionStatus;
    }

    public PubSubHelper(Context context, Communicator communicator) {
        this.context = context;
        this.communicator = communicator;
        keystorePath = context.getFilesDir().getPath();
        clientId = UUID.randomUUID().toString();

        prepareAssets();
        prepareConnection();

    }

    private void prepareAssets() {
        try {
            someKey = context.getAssets().open("private.key");
            BufferedReader insr =
                    new BufferedReader(new InputStreamReader(someKey, "UTF-8"));
            String str;
            str = insr.readLine();
            while (str != null) {
                keyBuf.append(str + "\n");
                str = insr.readLine();
            }
            insr.close();

        } catch (IOException e) {
            Log.v(TAG , "Error reading private key from assets ");
            e.printStackTrace();
        }

        try {
            someCert = context.getAssets().open("certificate.crt");
            BufferedReader insr =
                    new BufferedReader(new InputStreamReader(someCert, "UTF-8"));
            String str;
            str = insr.readLine();
            while (str != null) {
                certBuf.append(str + "\n");
                str = insr.readLine();
            }
            insr.close();
        } catch (IOException e) {
            Log.v(TAG, "Error reading private key from assets ");
            e.printStackTrace();
        }
    }

    private void prepareConnection() {
        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        // Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
        // MQTT pings every 10 seconds.
        mqttManager.setKeepAlive(10);

        keystoreName = KEYSTORE_NAME;
        keystorePassword = KEYSTORE_PASSWORD;
        certificateId = CERTIFICATE_ID;

        // To load cert/key from keystore on filesystem
        try {
            if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
                        keystoreName, keystorePassword)) {
                    Log.i(LOG_TAG, "Certificate " + certificateId
                            + " found in keystore - using for MQTT.");
                    // load keystore from file into memory to pass on connection
                    clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                            keystorePath, keystoreName, keystorePassword);
                } else {
                    Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
                }
            } else {
                Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
        }

        if (clientKeyStore == null) {
            Log.i(LOG_TAG, "Cert/key was not found in keystore - creating new key and certificate.");

            AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
                    certBuf.toString(),
                    keyBuf.toString(),
                    keystorePath, keystoreName, keystorePassword);

            // load keystore from file into memory to pass on
            // connection
            clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                    keystorePath, keystoreName, keystorePassword);
        }

    }

    public void startConnection() {
        try {
            mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status, final Throwable throwable) {
                    Log.v(TAG, "Status = " + String.valueOf(status));

                    if (status == AWSIotMqttClientStatus.Connecting) {
                        connectionStatus = CONNECTING;

                    } else if (status == AWSIotMqttClientStatus.Connected) {
                        connectionStatus = CONNECTED;
                        if (communicator != null) {
                            communicator.onConnected(PubSubHelper.this);
                        }
                    } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                        if (throwable != null) {
                            Log.e(LOG_TAG, "Connection error.", throwable);
                        }
                        connectionStatus = RECONNECTING;
                    } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                        if (throwable != null) {
                            Log.e(LOG_TAG, "Connection error.", throwable);
                        }
                        connectionStatus = DISCONNECTED;
                    } else {
                        connectionStatus = DISCONNECTED;
                    }
                }
            });
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Connection error.", e);
            connectionStatus = ERROR;
        }
    }

    public void endConnection() {
        try {
            mqttManager.disconnect();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Disconnect error.", e);
        }
    }

    public void subscribe(String topic) {
        Log.v(TAG + "MQTT SUBSCRIBE", "Old : " + mTopic + " NEW : " + topic);
        if (mTopic == null)
            return;

        if (mTopic.equals(topic)) {
            return;
        }
        Log.v(TAG, "topic = " + topic);

        // unSubscribing previous channel
        unSubscribe();

        mTopic = topic;
        actualSubscription(mTopic);
    }

    public void globalSubscribe(String topic) {
        if (topic == null)
            return;
        Log.v(TAG, "global topic = " + topic);
        actualSubscription(topic);
    }


    private void actualSubscription(String topic) {
        try {
            mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            try {
                                String message = new String(data, "UTF-8");
                                Log.v(TAG, "Message arrived:");
                                Log.v(TAG, "   Topic: " + topic);
                                Log.v(TAG, " Message: " + message);

                                if (communicator != null) {
                                    communicator.onMessageReceived(message);
                                }

                            } catch (UnsupportedEncodingException e) {
                                Log.e(TAG, "Message encoding error.", e);
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
    }

    public void unSubscribe() {
        if (mTopic == null)
            return;

        Log.v(TAG, "topic = " + mTopic);

        try {
            mqttManager.unsubscribeTopic(mTopic);
        } catch (Exception e) {
            Log.e(LOG_TAG, "UnSubscription error.", e);
        }
    }

    public void publishMessage(String topic, String msg) {
        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }

}
