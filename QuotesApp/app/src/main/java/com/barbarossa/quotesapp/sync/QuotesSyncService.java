package com.barbarossa.quotesapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Ioan on 12.05.2016.
 */
public class QuotesSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static QuotesSyncAdapter sQuotesSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("QuotesSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sQuotesSyncAdapter == null) {
                sQuotesSyncAdapter = new QuotesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sQuotesSyncAdapter.getSyncAdapterBinder();
    }
}