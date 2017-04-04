package com.example.kvoththebloodless.fantv;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.kvoththebloodless.fantv.FanTvSync.FanTvSyncIntentService;
import com.example.kvoththebloodless.fantv.FanTvSync.FanTvUtils;


public class PoplarWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_DATA_UPDATED = "com.android.fantv.ACTION_DATA_UPDATED";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            Log.i("herewidgetrecieve", "hahaha");
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_view);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; ++i) {

            Intent in = new Intent(context, FanTvSyncIntentService.class);
            in.putExtra("tag", FanTvUtils.PERIODIC_SYNC);
            context.startService(in);

            Intent intent = new Intent(context, PopularWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_item_view);
            rv.setRemoteAdapter(R.id.stack_view, intent);

            rv.setEmptyView(R.id.stack_view, R.id.emptyview);

            Intent intentonclick = new Intent(context, DetailActivity.class);


            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentonclick,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.stack_view, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


}