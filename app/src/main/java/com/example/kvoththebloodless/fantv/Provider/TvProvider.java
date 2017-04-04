package com.example.kvoththebloodless.fantv.Provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


public class TvProvider extends ContentProvider {
    static final int GEN_INDI_SHOW = 100;//item
    static final int GEN_ALL_SHOW = 101;//dir
    static final int EP_S0 = 102;//item
    static final int EPISODES = 103;
    static final int SEASONS = 104;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();
    private static DatabaseHelper dh;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(TvContract.AUTHORITY,
                TvContract.PATH_GENERAL_ALL,
                GEN_ALL_SHOW);
        URI_MATCHER.addURI(TvContract.AUTHORITY,
                TvContract.PATH_SEASONS_JOINED,
                EP_S0);
        URI_MATCHER.addURI(TvContract.AUTHORITY,
                TvContract.PATH_GENERAL_SINGLE,
                GEN_INDI_SHOW);
        URI_MATCHER.addURI(TvContract.AUTHORITY,
                TvContract.PATH_SEASONS_ALONE,
                SEASONS);
        URI_MATCHER.addURI(TvContract.AUTHORITY,
                TvContract.PATH_EPISODES,
                EPISODES);
        return URI_MATCHER;
    }

    @Override
    public boolean onCreate() {
        dh = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (URI_MATCHER.match(uri)) {

            case GEN_ALL_SHOW: {
                retCursor = dh.returnGenTvInfo(projection, selection, selectionArgs, sortOrder);
                break;
            }

            case GEN_INDI_SHOW: {
                retCursor = dh.returnGenTvInfo(projection, selection, selectionArgs, sortOrder);
                break;
            }

            case EP_S0: {

                retCursor = dh.returnSeasonEpisode(projection, selection, selectionArgs, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case GEN_ALL_SHOW:
                return TvContract.General.CONTENT_TYPE;
            case GEN_INDI_SHOW:
                return TvContract.General.CONTENT_ITEM_TYPE;
            case EP_S0:
                return TvContract.Seasons.CONTENT_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = dh.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        Uri returnUri;

        switch (match) {
            case GEN_ALL_SHOW: {

                long _id = db.insertWithOnConflict(TvContract.General.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (_id >= 0)
                    returnUri = TvContract.General.uriWithId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SEASONS: {

                long _id = db.insertWithOnConflict(TvContract.Seasons.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (_id >= 0)
                    returnUri = TvContract.Seasons.uriWithId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case EPISODES: {

                long _id = db.insertWithOnConflict(TvContract.Episodes.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (_id >= 0)
                    returnUri = TvContract.Episodes.uriWithId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dh.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsdeleted;

        switch (match) {

            case GEN_ALL_SHOW:
                rowsdeleted = db.delete(TvContract.General.TABLE_NAME, selection, selectionArgs);
                break;
            case SEASONS:
                rowsdeleted = db.delete(TvContract.Seasons.TABLE_NAME, selection, selectionArgs);
                break;
            case EPISODES:
                rowsdeleted = db.delete(TvContract.Episodes.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsdeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsdeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dh.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsupdated;

        switch (match) {

            case GEN_ALL_SHOW:
                rowsupdated = db.update(TvContract.General.TABLE_NAME, values, selection, selectionArgs);
                break;
            case EPISODES:
                rowsupdated = db.update(TvContract.Episodes.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsupdated != 0) {
            if (match == EPISODES)
                getContext().getContentResolver().notifyChange(TvContract.Seasons.CONTENT_URI_JOINED, null);
            else
                getContext().getContentResolver().notifyChange(uri, null);

            Log.i("notify", "plesss " + uri);
        }
        return rowsupdated;
    }
}