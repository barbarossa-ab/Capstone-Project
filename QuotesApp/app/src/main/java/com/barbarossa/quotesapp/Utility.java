package com.barbarossa.quotesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by Ioan on 11.05.2016.
 */
public class Utility {
    public static final String CONTENT_AUTHORITY = "com.barbarossa.quotesapp";

    public static final String BASE_URL = "http://quotes.rest/";
    public static final String API_KEY = "1h52AU4uTBa5GuMzXJMJugeF";
    public static final String API_KEY_HEADER = "X-Theysaidso-Api-Secret";
    public static final int QUOTES_PER_CATEG = 2;

    private static final String QUOTES_SHARED_PREF = "quotesapp.shared.prefs";
    private static final String PREF_LAST_UPDATE = "pref_last_update";

    public static final String QUOTE_KEY = "QUOTE_KEY";
    public static final String FAV_QUOTES_UPDATED = "com.barbarossa.quotesapp.FAV_QUOTES_UPDATED";

    public static long getLastUpdate(Context context) {
        SharedPreferences sp = context.getSharedPreferences(QUOTES_SHARED_PREF, Context.MODE_PRIVATE);
        return sp.getLong(PREF_LAST_UPDATE, 0);
    }

    public static void setLastUpdate(Context context, long lastUpdate) {
        SharedPreferences sp = context.getSharedPreferences(QUOTES_SHARED_PREF, Context.MODE_PRIVATE);
        sp.edit().putLong(PREF_LAST_UPDATE, lastUpdate).apply();
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void setupAdView(AdView adView) {
//        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("B3869BFE6E72DC5498FF0741DC0C5AC2")
                .build();

//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//            }
//
//            @Override
//            public void onAdClosed() {
//                Toast.makeText(getContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(getContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                Toast.makeText(getContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//            }
//        });

        adView.loadAd(adRequest);
    }

}
