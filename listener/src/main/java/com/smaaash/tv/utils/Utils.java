package com.smaaash.tv.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Siddhesh on 19-07-2018.
 */

public class Utils {

    public String fromISO8601UTC(String timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        Date date = null;
        try {
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = (Date) formatter.parse(timestamp);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            return dateFormat.format(date);
        } else
            return timestamp;
    }

    public static String getCurrentISOTime() {
        TimeZone tz = TimeZone.getDefault();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //'Z'   Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(new Date());
    }


    private static String PREF = "Pref";

    public static boolean isNewUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF, 0);
        return preferences.getBoolean("isNew", true);
    }

    public static void userIsOldNow(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF, 0);
        preferences.edit().putBoolean("isNew", false).apply();
    }

}
