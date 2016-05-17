package com.barbarossa.quotesapp.data;

import android.net.Uri;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;

/**
 * Created by barbarossa on 17/05/16.
 */
public interface QuotesCategoriesContract extends ProviGenBaseContract {
    @Column(Column.Type.INTEGER)
    public static final String QUOTE_ID = "quote_id";

    @Column(Column.Type.INTEGER)
    public static final String CATEGORY_ID = "category_id";

    @ContentUri
    public static final Uri CONTENT_URI = Uri.parse("content://com.barbarossa.quotesapp/quotescategories");

}
