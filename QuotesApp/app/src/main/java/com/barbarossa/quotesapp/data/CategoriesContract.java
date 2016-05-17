package com.barbarossa.quotesapp.data;

import android.net.Uri;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;

/**
 * Created by barbarossa on 17/05/16.
 */
public interface CategoriesContract extends ProviGenBaseContract {
    @Column(Column.Type.TEXT)
    public static final String CATEGORY_NAME = "category_name";

    @ContentUri
    public static final Uri CONTENT_URI = Uri.parse("content://com.barbarossa.quotesapp/categories");

}
