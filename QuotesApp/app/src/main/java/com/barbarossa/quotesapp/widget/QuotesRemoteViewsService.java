package com.barbarossa.quotesapp.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.barbarossa.quotesapp.R;
import com.barbarossa.quotesapp.Utility;
import com.barbarossa.quotesapp.data.QuotesLoader;
import com.barbarossa.quotesapp.data.QuotesProvider;

/**
 * Created by Ioan on 22.05.2016.
 */
public class QuotesRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                Uri favQuotesUri = QuotesProvider
                        .buildQuotesByCategoryUri(
                                getString(R.string.categ_favourites).toLowerCase()
                        );

                data = getContentResolver().query(
                        favQuotesUri,
                        QuotesLoader.Query.PROJECTION,
                        null,
                        null,
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_quote_item);

                views.setTextViewText(R.id.quote_text, data.getString(QuotesLoader.Query.QUOTE_TEXT));
                views.setTextViewText(R.id.author_text, data.getString(QuotesLoader.Query.AUTHOR));

                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(Utility.QUOTE_KEY, data.getLong(QuotesLoader.Query._ID));
                views.setOnClickFillInIntent(R.id.quote_item_container, fillInIntent);

//              views.setContentDescription(R.id.matchInfo, matchInfo);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_quote_item);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

        };
    }
}
