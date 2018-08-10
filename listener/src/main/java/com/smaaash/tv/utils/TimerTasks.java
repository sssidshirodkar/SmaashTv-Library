package com.smaaash.tv.utils;

import java.util.TimerTask;

/**
 * Created by Siddhesh on 16-07-2018.
 */

public class TimerTasks extends TimerTask {

    public interface TaskListener {
        void performTask(String url);
    }

    private String url;
    private TaskListener listener;

    public TimerTasks(String url, TaskListener listener) {
        this.url = url;
        this.listener = listener;
    }

    @Override
    public void run() {
        listener.performTask(url);
    }

}
