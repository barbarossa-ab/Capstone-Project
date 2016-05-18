package com.barbarossa.quotesapp.data;

import android.net.Uri;

import com.barbarossa.quotesapp.Utility;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;

/**
 * Created by Ioan on 11.05.2016.
 */
public interface QuotesContract extends ProviGenBaseContract{
    public static final String TABLE_NAME = "quotes";

    @Column(Type.TEXT)
    public static final String QUOTE_ID = "quote_id";

    @Column(Type.TEXT)
    public static final String QUOTE_TEXT = "quote_text";

    @Column(Type.TEXT)
    public static final String AUTHOR = "author";

    @Column(Type.INTEGER)
    public static final String TIMESTAMP = "timestamp";

    @ContentUri
    public static final Uri CONTENT_URI = Uri.parse("content://" + Utility.CONTENT_AUTHORITY + "/" + TABLE_NAME);
}
