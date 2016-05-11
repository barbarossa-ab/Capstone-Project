package com.barbarossa.quotesapp.data;

/**
 * Created by Ioan on 11.05.2016.
 */

import android.net.Uri;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;

public interface CategoriesContract extends ProviGenBaseContract {
    @Column(Column.Type.TEXT)
    public static final String CATEGORY_NAME = "category_name";

    @ContentUri
    public static final Uri CONTENT_URI = Uri.parse("content://com.barbarossa.quotesapp/categories");
}
