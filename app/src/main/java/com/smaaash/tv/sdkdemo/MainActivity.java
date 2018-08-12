package com.smaaash.tv.sdkdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.smaaash.tv.utils.SmaaashTvListener;
import com.smaaash.tv.utils.SmashTv;

public class MainActivity extends AppCompatActivity {

    private TextView mData;
    private WebView web_view;
    SmashTv smashTv;
    String Tag = "TESTING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mData = (TextView) findViewById(R.id.textView);
        web_view = (WebView) findViewById(R.id.web_view);

//        smashTv = new SmashTv(this,"5b3491bcda8ac8794788ef21", web_view);

        smashTv = new SmashTv(this, "5b3491bcda8ac8794788ef21", new SmaaashTvListener() {
            @Override
            public void onUrlReceivedEvent(String url) {
                Log.v(Tag, " URL RECEIVED ==>" + url);
            }

            @Override
            public void onResponseReceived(String response) {
                Log.v(Tag, " RESPONSE RECEIVED ==>" + response);
            }
        });

//        smashTv = new SmashTv(this, "5b3491bcda8ac8794788ef21");
        smashTv.startWorking();
    }

    @Override
    protected void onDestroy() {
        smashTv.destroyEverything();
        super.onDestroy();
    }
}