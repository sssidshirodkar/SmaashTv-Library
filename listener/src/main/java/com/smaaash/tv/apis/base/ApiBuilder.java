package com.smaaash.tv.apis.base;

public class ApiBuilder {

    public static String getBaseUrl() {
        return "https://smaaash.in/";
    }

    public static String getEpisodeDetail(String channelId, String userID, String timeStamp) {
        return "http://api.smaaash.com/episodes/currentepisodes?"
                + "channel_id=" + channelId
                + "&user_id=" + userID
//                + "&timestamp=2018-07-18T17:29:00.000Z"
//                +"&timestamp=" + timeStamp
        ;
    }
}