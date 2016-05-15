package com.barbarossa.quotesapp.data;

import android.net.Uri;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;

/**
 * Created by Ioan on 11.05.2016.
 */
public interface QuotesContract extends ProviGenBaseContract{
    @Column(Type.TEXT)
    public static final String QUOTE_ID = "quote_id";

    @Column(Type.TEXT)
    public static final String QUOTE_TEXT = "quote_text";

    @Column(Type.TEXT)
    public static final String AUTHOR = "author";

    @Column(Type.TEXT)
    public static final String CATEGORY_NAME = "category_name";


    @ContentUri
    public static final Uri CONTENT_URI = Uri.parse("content://com.barbarossa.quotesapp/quotes");
}
