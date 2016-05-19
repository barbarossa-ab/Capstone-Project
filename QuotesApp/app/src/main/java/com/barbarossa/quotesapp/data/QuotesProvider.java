package com.barbarossa.quotesapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
//    public static final String QUOTES_BY_CATEG_OLDER_THAN_PATH = "quotes_by_categ_older_than";

    public static final Uri CONTENT_BY_CATEG_URI = Uri.parse("content://"
            + Utility.CONTENT_AUTHORITY
            + "/"
            + QUOTES_BY_CATEG_PATH );

//    public static final Uri CONTENT_BY_CATEG_OLDER_THAN_URI = Uri.parse("content://"
//            + Utility.CONTENT_AUTHORITY
//            + "/"
//            + QUOTES_BY_CATEG_OLDER_THAN_PATH );


    private static final SQLiteQueryBuilder sQuotesByCategoryQueryBuilder;

    private static final String sQuotesByCategorySelection =
            CategoriesContract.TABLE_NAME+
                    "." + CategoriesContract.CATEGORY_NAME + " = ? ";

    private static final String sQuotesByCategoryAfterTimestampSelection =
            CategoriesContract.TABLE_NAME + "." + CategoriesContract.CATEGORY_NAME + " = ? AND " +
                    QuotesContract.TABLE_NAME + "." + QuotesContract.TIMESTAMP + " >= ?" ;


    static {
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

    private final static int QUOTES_BY_CATEG_MATCH = 101;
    private final static int QUOTES_BY_CATEG_OLDER_THAN_MATCH = 102;

    private SQLiteOpenHelper mOpenHelper;
    private UriMatcher uriMatcher;


    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Utility.CONTENT_AUTHORITY,
                QUOTES_BY_CATEG_PATH + "/*",
                QUOTES_BY_CATEG_MATCH);

        uriMatcher.addURI(Utility.CONTENT_AUTHORITY,
                QUOTES_BY_CATEG_PATH + "/*/#",
                QUOTES_BY_CATEG_OLDER_THAN_MATCH);

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

                for(String category : categories) {
                    ContentValues initialValues = new ContentValues();
                    initialValues.put(CategoriesContract.CATEGORY_NAME, category.toLowerCase());

                    database.insert(CategoriesContract.TABLE_NAME, null, initialValues);
                }

                ContentValues initialValues = new ContentValues();
                initialValues.put(CategoriesContract.CATEGORY_NAME,
                        getContext().getResources().getString(R.string.categ_favourites).toLowerCase());

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

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch(uriMatcher.match(uri)) {
            case QUOTES_BY_CATEG_MATCH:
                String cat1 = uri.getPathSegments().get(1);
                String sel = sQuotesByCategorySelection;
                String[] selArgs = {cat1};

                Cursor c1 =  sQuotesByCategoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sel,
                        selArgs,
                        null,
                        null,
                        sortOrder
                );

//                Log.e("dump-cursor 1", DatabaseUtils.dumpCursorToString(c1));
                c1.setNotificationUri(getContext().getContentResolver(), uri);
                return c1;


            case QUOTES_BY_CATEG_OLDER_THAN_MATCH:
                String cat2 = uri.getPathSegments().get(1);
                String startTime = uri.getPathSegments().get(2);
                String sel2 = sQuotesByCategoryAfterTimestampSelection;
                String[] selArgs2 = {cat2, startTime};

//            {
//                String sel3 = sQuotesByCategorySelection;
//                String[] selArgs3 = {cat2};
//
//                Cursor c3 =  sQuotesByCategoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
//                        projection,
//                        sel3,
//                        selArgs3,
//                        null,
//                        null,
//                        sortOrder
//                );
//
//                Log.e("dump-cursor 3", "startTime = " + startTime);
//                Log.e("dump-cursor 3", DatabaseUtils.dumpCursorToString(c3));
//            }

                Cursor c2 =  sQuotesByCategoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sel2,
                        selArgs2,
                        null,
                        null,
                        sortOrder
                );

//                Log.e("dump-cursor 2", DatabaseUtils.dumpCursorToString(c2));
                c2.setNotificationUri(getContext().getContentResolver(), uri);
                return c2;

            default:
                return super.query(uri, projection, selection, selectionArgs, sortOrder);
        }
    }

    public static Uri buildQuotesByCategoryUri(String category) {
        return CONTENT_BY_CATEG_URI.buildUpon().appendPath(category).build();
    }

    public static Uri buildQuotesByCategoryAfterTimestampUri(String category, long timeStamp) {
        return CONTENT_BY_CATEG_URI.buildUpon()
                .appendPath(category)
                .appendPath(String.valueOf(timeStamp))
                .build();
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

    public static long getQuoteCategoryPair(Context context, long quoteId, long catId) {
        String[] proj = {
                QuotesCategoriesContract._ID,
                QuotesCategoriesContract.QUOTE_ID,
                QuotesCategoriesContract.CATEGORY_ID};

        String[] selArgs = {String.valueOf(quoteId), String.valueOf(catId)};

        Cursor cursor = context.getContentResolver().query(
                QuotesCategoriesContract.CONTENT_URI,
                proj,
                QuotesCategoriesContract.QUOTE_ID + "=? AND " + QuotesCategoriesContract.CATEGORY_ID + "=?",
                selArgs,
                null
        );

        if(cursor.moveToFirst()) {
            return cursor.getLong(0);
        }

        return -1;
    }


    public static long insertQuote(Context context, String quoteApiId, String quote, String author) {
        if(getQuoteIdByApiId(context, quoteApiId) == -1) {
            ContentValues vals = new ContentValues();
            vals.put(QuotesContract.QUOTE_TEXT, quote);
            vals.put(QuotesContract.AUTHOR, author);
            vals.put(QuotesContract.QUOTE_ID, quoteApiId);
            vals.put(QuotesContract.TIMESTAMP, System.currentTimeMillis());

            Uri uri = context.getContentResolver().insert(QuotesContract.CONTENT_URI, vals);
            return Long.valueOf(uri.getLastPathSegment());
        }

        return -1;
    }

    public static long insertQuoteCategoryPair(Context context, long quoteId, long catId) {
        if(getQuoteCategoryPair(context, quoteId, catId) == -1) {
            ContentValues vals = new ContentValues();
            vals.put(QuotesCategoriesContract.QUOTE_ID, quoteId);
            vals.put(QuotesCategoriesContract.CATEGORY_ID, catId);

            Uri uri = context.getContentResolver().insert(QuotesCategoriesContract.CONTENT_URI, vals);
            return Long.valueOf(uri.getLastPathSegment());
        }

        return -1;
    }



}
