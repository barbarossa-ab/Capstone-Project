package com.barbarossa.quotesapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.barbarossa.quotesapp.R;
import com.barbarossa.quotesapp.Utility;
import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.helper.TableBuilder;
import com.tjeannin.provigen.helper.TableUpdater;
import com.tjeannin.provigen.model.Constraint;
import com.tjeannin.provigen.model.Contract;

/**
 * Created by Ioan on 11.05.2016.
 */
public class QuotesProvider extends ProviGenProvider {
    private static Class[] contracts = new Class[]{
            QuotesContract.class,
            CategoriesContract.class,
            QuotesCategoriesContract.class
    };

    public static final String QUOTES_BY_CATEG_PATH = "quotes_by_categ";
    public static final Uri CONTENT_BY_CATEG_URI = Uri.parse("content://"
            + Utility.CONTENT_AUTHORITY
            + "/"
            + QUOTES_BY_CATEG_PATH );

    private static final SQLiteQueryBuilder sQuotesByCategoryQueryBuilder;

    private static final String sQuotesByCategorySelection =
            CategoriesContract.TABLE_NAME+
                    "." + CategoriesContract.CATEGORY_NAME + " = ? ";
    static{
        sQuotesByCategoryQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sQuotesByCategoryQueryBuilder.setTables(
                        QuotesContract.TABLE_NAME
                        + " INNER JOIN "
                        + QuotesCategoriesContract.TABLE_NAME
                        + " ON "
                        + QuotesContract.TABLE_NAME + "." + QuotesContract._ID
                        + " = "
                        + QuotesCategoriesContract.TABLE_NAME + "." + QuotesCategoriesContract.QUOTE_ID
                        + " INNER JOIN "
                        + CategoriesContract.TABLE_NAME
                        + " ON "
                        + QuotesCategoriesContract.TABLE_NAME + "." + QuotesCategoriesContract.CATEGORY_ID
                        + " = "
                        + CategoriesContract.TABLE_NAME + "." + CategoriesContract._ID
        );
    }



    private SQLiteOpenHelper mOpenHelper;
    private UriMatcher uriMatcher;
    private int QUOTES_MATCH = 101;

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Utility.CONTENT_AUTHORITY, QUOTES_BY_CATEG_PATH + "/*", QUOTES_MATCH);

        return super.onCreate();
    }

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        mOpenHelper = new SQLiteOpenHelper(getContext(), "quotes.db", null, 1) {

            @Override
            public void onCreate(SQLiteDatabase database) {
                // Automatically creates table and needed columns.
                new TableBuilder(QuotesContract.class)
                        .addConstraint(QuotesContract.QUOTE_ID, Constraint.UNIQUE, Constraint.OnConflict.ABORT)
                        .createTable(database);

                new TableBuilder(CategoriesContract.class)
                        .addConstraint(CategoriesContract.CATEGORY_NAME, Constraint.UNIQUE, Constraint.OnConflict.ABORT)
                        .createTable(database);

                new TableBuilder(QuotesCategoriesContract.class)
                        .createTable(database);

                String[] categories = getContext().getResources().getStringArray(R.array.categories_array);

                ContentValues[] cvVector = new ContentValues[categories.length];
                for(String category : categories) {
                    ContentValues initialValues = new ContentValues();
                    initialValues.put(CategoriesContract.CATEGORY_NAME, category.toLowerCase());

                    database.insert(CategoriesContract.TABLE_NAME, null, initialValues);
                }

                ContentValues initialValues = new ContentValues();
                initialValues.put(CategoriesContract.CATEGORY_NAME,
                        getContext().getResources().getString(R.string.categ_favourites));

                database.insert(CategoriesContract.TABLE_NAME, null, initialValues);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                // Automatically adds new columns.
                TableUpdater.addMissingColumns(db, QuotesContract.class);

                // Anything else related to database upgrade should be done here.
            }

        };

        return mOpenHelper;
    }

    @Override
    public Class[] contractClasses() {
        return contracts;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = uriMatcher.match(uri);

        if(match == QUOTES_MATCH) {
            Log.e("quotes-provider", "QUOTES_MATCH");
            String category = uri.getLastPathSegment();

            String sel = sQuotesByCategorySelection;
            String[] selArgs = {category};

            return sQuotesByCategoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    sel,
                    selArgs,
                    null,
                    null,
                    sortOrder
            );

        } else {
            return super.query(uri, projection, selection, selectionArgs, sortOrder);
        }
    }

    public static Uri buildQuotesByCategotyUri(String category) {
        return CONTENT_BY_CATEG_URI.buildUpon().appendPath(category).build();
    }

    public static long getQuoteIdByApiId(Context context, String quoteId) {
        String[] proj = {QuotesContract._ID};
        String[] selArgs = {quoteId};

        Cursor categoryCursor = context.getContentResolver().query(
                QuotesContract.CONTENT_URI,
                proj,
                QuotesContract.QUOTE_ID + "=?",
                selArgs,
                null
        );

        if(categoryCursor.moveToFirst()) {
            return categoryCursor.getLong(0);
        }

        return -1;
    }

    public static long getCategoryIdByName(Context context, String categoryName) {
        String[] proj = {CategoriesContract._ID};
        String[] selArgs = {categoryName};

        Cursor categoryCursor = context.getContentResolver().query(
                CategoriesContract.CONTENT_URI,
                proj,
                CategoriesContract.CATEGORY_NAME + "=?",
                selArgs,
                null
        );

        if(categoryCursor.moveToFirst()) {
            return categoryCursor.getLong(0);
        }

        return -1;
    }

}
