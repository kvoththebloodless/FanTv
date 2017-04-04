package com.example.kvoththebloodless.fantv.Misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;


public class Utility {
    public static final String TMDB_API_KEY = "d0fa8157cea46d952cb9578970c82542";
    public static final String TVDB_API_KEY = "4B040528A7E82B83";

    public static boolean isConnected(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) (mContext.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isconnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isconnected;
    }

    public static void editPref(Context c, int key, Object value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor se = sp.edit();


        if (value instanceof Integer) {
            se.putInt(c.getString(key), (Integer) value);
        } else if (value instanceof String) {
            se.putString(c.getString(key), (String) value);
        } else if (value instanceof Boolean) {
            se.putBoolean(c.getString(key), (Boolean) value);
        }

        se.apply();
    }

    public static boolean checkForString(Context c, int key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getBoolean(c.getString(key), false);
    }
}
