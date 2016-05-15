package com.barbarossa.quotesapp.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ioan on 11.05.2016.
 */
public class Utility {
    public static final String BASE_URL = "http://quotes.rest/";
    public static final String API_KEY = "1h52AU4uTBa5GuMzXJMJugeF";
    public static final String API_KEY_HEADER = "X-Theysaidso-Api-Secret";
    public static final int QUOTES_PER_CATEG = 10;

    private static final String QUOTES_SHARED_PREF = "quotesapp.shared.prefs";
    private static final String PREF_LAST_UPDATE = "pref_last_update";

    public static long getLastUpdate(Context context) {
        SharedPreferences sp = context.getSharedPreferences(QUOTES_SHARED_PREF, Context.MODE_PRIVATE);
        return sp.getLong(PREF_LAST_UPDATE, 0);
    }

    public static void setLastUpdate(Context context, long lastUpdate) {
        SharedPreferences sp = context.getSharedPreferences(QUOTES_SHARED_PREF, Context.MODE_PRIVATE);
        sp.edit().putLong(PREF_LAST_UPDATE, lastUpdate).apply();
    }
}
