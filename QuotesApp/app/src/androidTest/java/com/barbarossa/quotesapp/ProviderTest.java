package com.barbarossa.quotesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.barbarossa.quotesapp.data.CategoriesContract;
import com.barbarossa.quotesapp.data.QuotesContract;

import junit.framework.TestCase;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Ioan on 11.05.2016.
 */
public class ProviderTest extends AndroidTestCase{

    public void testProvider() {

        ContentValues testValues = new ContentValues();
        testValues.put(QuotesContract.AUTHOR, "NutuCamataru");
        testValues.put(QuotesContract.QUOTE_ID, String.valueOf(new Random().nextInt()));
        testValues.put(QuotesContract.QUOTE_TEXT, "Ma doare in pula");

        mContext.getContentResolver().delete(QuotesContract.CONTENT_URI, null, null);
        mContext.getContentResolver().insert(QuotesContract.CONTENT_URI, testValues);

        Cursor cursor = mContext.getContentResolver().query(
                QuotesContract.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        validateCursor("testInsert. Error validating Quotes.",
                cursor, testValues);

        ContentValues categValues = new ContentValues();
        categValues.put(CategoriesContract.CATEGORY_NAME, "love");

        mContext.getContentResolver().delete(CategoriesContract.CONTENT_URI, null, null);
        mContext.getContentResolver().insert(CategoriesContract.CONTENT_URI, categValues);

        Cursor ccursor = mContext.getContentResolver().query(
                CategoriesContract.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        validateCursor("testInsert. Error validating Quotes.",
                ccursor, categValues);

    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);

            String expectedValue = entry.getValue().toString();
//            String entryValue = valueCursor.getString(idx);
//            boolean equalse = expectedValue.equals(entryValue);

            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

}
