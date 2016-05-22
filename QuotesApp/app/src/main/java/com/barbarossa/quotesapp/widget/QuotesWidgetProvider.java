package com.barbarossa.quotesapp.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.barbarossa.quotesapp.DetailActivity;
import com.barbarossa.quotesapp.MainActivity;
import com.barbarossa.quotesapp.R;
import com.barbarossa.quotesapp.Utility;
import com.barbarossa.quotesapp.data.QuotesLoader;

/**
 * Created by Ioan on 22.05.2016.
 */
public class QuotesWidgetProvider extends AppWidgetProvider {
    private static final String QUOTE_CLICK_ACTION = "com.barbarossa.quotesapp.QUOTE_CLICK_ACTION";

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (Utility.FAV_QUOTES_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_quotes_list);
        } else if(QUOTE_CLICK_ACTION.equals(intent.getAction())) {

            long quoteId = intent.getLongExtra(Utility.QUOTE_KEY, 0);
            Intent detailIntent = new Intent(context, DetailActivity.class);
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            detailIntent.putExtra(Utility.QUOTE_KEY, quoteId);

            context.startActivity(detailIntent);
        }
    }


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // update each of the widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_fav_quotes);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }

            Intent clickQuoteIntent = new Intent(context, QuotesWidgetProvider.class);
            clickQuoteIntent.setAction(QuotesWidgetProvider.QUOTE_CLICK_ACTION);
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickQuoteIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_quotes_list, clickPendingIntent);

            views.setEmptyView(R.id.widget_quotes_list, R.id.widget_no_quotes_text);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                views.setContentDescription(R.id.widget_matches_list, context.getString(R.string.today_matches_title));
//            }

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }



        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_quotes_list,
                new Intent(context, QuotesRemoteViewsService.class));
    }

    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_quotes_list,
                new Intent(context, QuotesRemoteViewsService.class));
    }

}
