package com.example.kvoththebloodless.fantv;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.kvoththebloodless.fantv.Provider.TvContract;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class PopularWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static int mCount;
    private Context mContext;
    private int mAppWidgetId;
    private Cursor cursor;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        /*G: we retrieve the id for the widget, invalid appwidget id is a constant that defines a value
        that the widgetmanager will never send under normal circumstances.*/
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {

        cursor = mContext.getContentResolver().query(TvContract.General.CONTENT_URI_ALL, new String[]{TvContract.General.TMDB_ID, TvContract.General.SHOW_NAME, TvContract.General.POSTER},
                null, null, null);
        /*G: I didn't put our data retrieval code here cause
        1)I'm not sure how much time it'll take and this function exists for around 20 sec
        2) for constant data update we have to put the data in the onDatasetChange() which will make sure that before
        the content is rendered in the widget it is updated*/
    }

    public void onDestroy() {

    }

    public int getCount() {
        return mCount;
    }

    public RemoteViews getViewAt(int position) {


        final RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);


        if (cursor == null || cursor.getCount() == 0) {
            rv.setEmptyView(R.id.stack_view, R.id.emptyview);
            return null;
        }

        cursor.moveToPosition(position);

        Intent detail = new Intent();


        rv.setTextViewText(R.id.widget_showname, cursor.getString(cursor.getColumnIndex(TvContract.General.SHOW_NAME)));


        try {
            Bitmap b =
                    Picasso
                            .with(mContext)
                            .load("https://image.tmdb.org/t/p/w300/" +
                                    cursor.getString(
                                            cursor.getColumnIndex(TvContract.General.POSTER))).get();
            rv.setImageViewBitmap(R.id.widget_poster, b);
            rv.setContentDescription(R.id.widget_poster, cursor.getString(cursor.getColumnIndex(TvContract.General.SHOW_NAME)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        cursor.moveToPosition(position);
        int temp = cursor.getInt(cursor.getColumnIndex(TvContract.General.TMDB_ID));
        detail.putExtra("tmdbid", temp);
        detail.putExtra("isitvisible", false);
        rv.setOnClickFillInIntent(R.id.widget_poster, detail);
        return rv;
    }

    public RemoteViews getLoadingView() {

        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();


        cursor = mContext.getContentResolver().query(TvContract.General.CONTENT_URI_ALL, new String[]{TvContract.General.SHOW_NAME, TvContract.General.POSTER, TvContract.General.TMDB_ID},
                null, null, null);
        Binder.restoreCallingIdentity(identityToken);


        if (cursor != null && cursor.getCount() != 0) {
            mCount = cursor.getCount();
        }
    }
}