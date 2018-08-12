package com.smaaash.tv.utils;

public interface SmaaashTvListener {
    void onUrlReceivedEvent(String url);

    void onResponseReceived(String response);
}