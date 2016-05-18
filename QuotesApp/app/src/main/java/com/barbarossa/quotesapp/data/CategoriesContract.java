package com.barbarossa.quotesapp.data;

import android.net.Uri;

import com.barbarossa.quotesapp.Utility;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;

/**
 * Created by barbarossa on 17/05/16.
 */
public interface CategoriesContract extends ProviGenBaseContract {
    public static final String TABLE_NAME = "categories";

    @Column(Column.Type.TEXT)
    public static final String CATEGORY_NAME = "category_name";

    @ContentUri
    public static final Uri CONTENT_URI = Uri.parse("content://" + Utility.CONTENT_AUTHORITY + "/" + TABLE_NAME);

}
