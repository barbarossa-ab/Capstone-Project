package com.barbarossa.quotesapp.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created by Ioan on 12.05.2016.
 */
public class CategoriesLoader extends CursorLoader {

    public static CategoriesLoader newCategoriesInstance(Context context) {
        return new CategoriesLoader(context, CategoriesContract.CONTENT_URI);
    }

    private CategoriesLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, null);
    }

    public interface Query {
        String[] PROJECTION = {
                CategoriesContract._ID,
                CategoriesContract.CATEGORY_NAME
        };

        int _ID = 0;
        int CATEGORY_NAME = 1;
    }
}
