package com.barbarossa.quotesapp.data;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

/**
 * Created by Ioan on 12.05.2016.
 */
public class QuotesLoader extends CursorLoader {

    public static QuotesLoader newQuotesForCategoryInstance(Context context, String category) {
        return new QuotesLoader(context, QuotesContract.CONTENT_URI,
                new String[] {category});
    }

    private QuotesLoader(Context context, Uri uri, String[] selArgs) {
//        context.getContentResolver().query(c);
        super(context, uri, Query.PROJECTION, QuotesContract.CATEGORY_NAME +"=?", selArgs, null);
    }

    public interface Query {
        String[] PROJECTION = {
                QuotesContract._ID,
                QuotesContract.QUOTE_ID,
                QuotesContract.QUOTE_TEXT,
                QuotesContract.AUTHOR,
        };

        int _ID = 0;
        int QUOTE_ID = 1;
        int QUOTE_TEXT = 2;
        int AUTHOR = 3;
    }
}
