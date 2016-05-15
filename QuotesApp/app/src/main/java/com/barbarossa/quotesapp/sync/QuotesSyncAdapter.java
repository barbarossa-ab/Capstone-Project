package com.barbarossa.quotesapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;

import com.barbarossa.quotesapp.R;
import com.barbarossa.quotesapp.data.QuotesContract;
import com.barbarossa.quotesapp.model.EndpointInterface;
import com.barbarossa.quotesapp.model.QuoteResponse;
import com.barbarossa.quotesapp.model.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class QuotesSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = QuotesSyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED =
            "com.barbarossa.quotesapp.app.ACTION_DATA_UPDATED";

    // 24 hrs
    public static final int SYNC_INTERVAL = 60 * 60 * 24;
    // 20 mins
    public static final int SYNC_FLEXTIME = 60 * 20;

    // 12 hrs
    public static final int MIN_REFRESH_INTERVAL = 60 * 60 * 12 * 1000;

    public QuotesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        // Fix multiple updates at app start
        if((System.currentTimeMillis() - Utility.getLastUpdate(getContext())) < MIN_REFRESH_INTERVAL ) {
            return;
        }

        final String BASE_URL = Utility.BASE_URL;
        final String API_KEY = Utility.API_KEY;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EndpointInterface apiService = retrofit.create(EndpointInterface.class);

        String[] categories = getContext().getResources().getStringArray(R.array.categories_array);

        for(String category : categories) {
//            ContentValues[] valsVector = new ContentValues[Utility.QUOTES_PER_CATEG];
            boolean firstResponse = true;

            for(int quoteIndex = 0 ; quoteIndex < Utility.QUOTES_PER_CATEG ; quoteIndex++) {
                Call<QuoteResponse> quoteResponseCall =
                        apiService.getQuoteForCategoryResponse(API_KEY,
                                category.toLowerCase());
                try{
                    Response<QuoteResponse> response = quoteResponseCall.execute();

                    if(response.isSuccessful()) {
                        if(firstResponse) {
                            firstResponse = false;

                            String[] selArgs = {category};
                            getContext().getContentResolver().delete(
                                    QuotesContract.CONTENT_URI,
                                    QuotesContract.CATEGORY_NAME + "=?",
                                    selArgs);

                            Utility.setLastUpdate(getContext(), System.currentTimeMillis());
                        }

                        QuoteResponse q = response.body();

                        ContentValues vals = new ContentValues();
                        vals.put(QuotesContract.QUOTE_TEXT, q.getContents().getQuote());
                        vals.put(QuotesContract.AUTHOR, q.getContents().getAuthor());
                        vals.put(QuotesContract.QUOTE_ID, q.getContents().getId());
                        vals.put(QuotesContract.CATEGORY_NAME, category);

                        getContext().getContentResolver().insert(QuotesContract.CONTENT_URI, vals);

//                        valsVector[quoteIndex] = vals;
                    }
                } catch (IOException e) {
                }
            }

//            getContext().getContentResolver().bulkInsert(QuotesContract.CONTENT_URI, valsVector);
        }

    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        QuotesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


}