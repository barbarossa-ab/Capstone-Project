package com.barbarossa.quotesapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.barbarossa.quotesapp.R;
import com.barbarossa.quotesapp.data.CategoriesContract;
import com.barbarossa.quotesapp.data.QuotesCategoriesContract;
import com.barbarossa.quotesapp.data.QuotesContract;
import com.barbarossa.quotesapp.data.QuotesProvider;
import com.barbarossa.quotesapp.model.EndpointInterface;
import com.barbarossa.quotesapp.model.QuoteResponse;
import com.barbarossa.quotesapp.Utility;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
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
            String catName = category.toLowerCase();

            long catId = QuotesProvider.getCategoryIdByName(getContext(), catName);

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

//                            String[] selArgs = {category};
//                            getContext().getContentResolver().delete(
//                                    QuotesContract.CONTENT_URI,
//                                    QuotesContract.CATEGORY_NAME + "=?",
//                                    selArgs);

                            Utility.setLastUpdate(getContext(), System.currentTimeMillis());
                        }

                        QuoteResponse q = response.body();

                        ContentValues vals = new ContentValues();
                        vals.put(QuotesContract.QUOTE_TEXT, q.getContents().getQuote());
                        vals.put(QuotesContract.AUTHOR, q.getContents().getAuthor());
                        vals.put(QuotesContract.QUOTE_ID, q.getContents().getId());

                        if(QuotesProvider.getQuoteIdByApiId(getContext(), q.getContents().getId()) == -1) {
                            Uri uri = getContext().getContentResolver().insert(QuotesContract.CONTENT_URI, vals);
                            long quoteId = Long.valueOf(uri.getLastPathSegment());

                            vals = new ContentValues();
                            vals.put(QuotesCategoriesContract.QUOTE_ID, quoteId);
                            vals.put(QuotesCategoriesContract.CATEGORY_ID, catId);
                            getContext().getContentResolver().insert(QuotesCategoriesContract.CONTENT_URI, vals);
                        }
                    }
                } catch (IOException e) {
                }
            }

//            getContext().getContentResolver().bulkInsert(QuotesContract.CONTENT_URI, valsVector);
        }

        String[] PROJECTION = {
                QuotesContract.TABLE_NAME + "." + QuotesContract._ID,
                QuotesContract.TABLE_NAME + "." + QuotesContract.QUOTE_ID,
                QuotesContract.TABLE_NAME + "." + QuotesContract.QUOTE_TEXT,
                QuotesContract.TABLE_NAME + "." + QuotesContract.AUTHOR,
                QuotesCategoriesContract.TABLE_NAME + "." + QuotesCategoriesContract.QUOTE_ID,
                QuotesCategoriesContract.TABLE_NAME + "." + QuotesCategoriesContract.CATEGORY_ID,
                CategoriesContract.TABLE_NAME + "." + CategoriesContract._ID,
                CategoriesContract.TABLE_NAME + "." + CategoriesContract.CATEGORY_NAME,

        };


        Cursor c = getContext().getContentResolver().query(
                QuotesProvider.buildQuotesByCategotyUri("love"),
                PROJECTION,
                null,
                null,
                null
        );

        if(c.moveToFirst()) {
            Log.e("dump-cursor", DatabaseUtils.dumpCursorToString(c));
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