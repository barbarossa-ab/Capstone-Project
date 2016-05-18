package com.barbarossa.quotesapp.data;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

/**
 * Created by Ioan on 12.05.2016.
 */
public class QuotesLoader extends CursorLoader {

    public static QuotesLoader newQuotesForCategoryInstance(Context context, String category) {
        return new QuotesLoader(
                context,
                QuotesProvider.buildQuotesByCategotyUri(category),
//                QuotesContract.CATEGORY_NAME + "=?",
                null,
                null);
    }

    public static QuotesLoader newQuoteByIdInstance(Context context, long id) {
        return new QuotesLoader(
                context,
                QuotesContract.CONTENT_URI,
                QuotesContract._ID + "=?",
                new String[] {String.valueOf(id)});
    }

    private QuotesLoader(Context context, Uri uri, String selString, String[] selArgs) {
        super(context, uri, Query.PROJECTION, selString, selArgs, null);
    }


    public interface Query {
        String[] PROJECTION = {
                QuotesContract.TABLE_NAME + "." + QuotesContract._ID,
                QuotesContract.TABLE_NAME + "." + QuotesContract.QUOTE_ID,
                QuotesContract.TABLE_NAME + "." + QuotesContract.QUOTE_TEXT,
                QuotesContract.TABLE_NAME + "." + QuotesContract.AUTHOR,
        };

        int _ID = 0;
        int QUOTE_ID = 1;
        int QUOTE_TEXT = 2;
        int AUTHOR = 3;
    }
}
