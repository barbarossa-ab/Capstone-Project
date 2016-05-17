package com.barbarossa.quotesapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.helper.TableBuilder;
import com.tjeannin.provigen.helper.TableUpdater;
import com.tjeannin.provigen.model.Constraint;

/**
 * Created by Ioan on 11.05.2016.
 */
public class QuotesProvider extends ProviGenProvider {
    private static Class[] contracts = new Class[]{QuotesContract.class};
    SQLiteOpenHelper mOpenHelper;

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        mOpenHelper = new SQLiteOpenHelper(getContext(), "quotes.db", null, 1) {

            @Override
            public void onCreate(SQLiteDatabase database) {
                // Automatically creates table and needed columns.
                new TableBuilder(QuotesContract.class)
//                        .addConstraint(QuotesContract.QUOTE_ID, Constraint.UNIQUE, Constraint.OnConflict.ABORT)
                        .createTable(database);
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

//    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        super.query(uri, projection, selection, selectionArgs, sortOrder);
//    }
}
