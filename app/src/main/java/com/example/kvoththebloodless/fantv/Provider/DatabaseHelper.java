package com.example.kvoththebloodless.fantv.Provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int database_version = 3;

    public String CREATE_QUERY1 = "CREATE TABLE " + TvContract.General.TABLE_NAME + "(" +
            TvContract.General.TMDB_ID + " INTEGER PRIMARY KEY, " +
            TvContract.General.SHOW_NAME + " TEXT, " +
            TvContract.General.OVERVIEW + " TEXT, " +
            TvContract.General.GENRE + " TEXT, " +
            TvContract.General.TYPE + " TEXT, " +
            TvContract.General.ORIGIN + " TEXT, " +
            TvContract.General.STATUS + " TEXT, " +
            TvContract.General.RUNTIME + " TEXT, " +
            TvContract.General.VOTE_AVG + " TEXT, " +
            TvContract.General.BANNER + " TEXT, " +
            TvContract.General.POSTER + " TEXT, " +
            TvContract.General.BACKDROP + " TEXT, " +
            TvContract.General.AIRDATE + " TEXT, " +
            TvContract.General.TVDB_ID + " INTEGER, " +
            TvContract.General.NO_SEASONS + " TEXT, " +
            TvContract.General.NO_EP + " TEXT, " +
            TvContract.General.FAVOURITE + " TEXT DEFAULT 'no'); ";

    public String CREATE_QUERY2 = "CREATE TABLE " + TvContract.Seasons.TABLE_NAME + "(" +
            TvContract.Seasons.TMDB_ID + " INTEGER, " +
            TvContract.Seasons.SEASON_ID + " INTEGER PRIMARY KEY, " +
            TvContract.Seasons.AIRDATE + " TEXT, " +
            TvContract.Seasons.SEASON_NO + " INTEGER, " +
            TvContract.Seasons.EP_COUNT + " INTEGER);";


    public String CREATE_QUERY3 = "CREATE TABLE " + TvContract.Episodes.TABLE_NAME + "("
            + TvContract.Episodes.SEASON_ID + " INTEGER, " +
            TvContract.Episodes.TMDB_ID + " INTEGER, " +
            TvContract.Episodes.EP_NAME + " TEXT, " +
            TvContract.Episodes.STILLS + " TEXT, " +
            TvContract.Episodes.AIRDATE + " TEXT, " +
            TvContract.Episodes.VIDEO_URL + " TEXT, " +
            TvContract.Episodes.EP_ID + " INTEGER PRIMARY KEY, "
            + TvContract.Episodes.EP_NO + " INTEGER, " +
            TvContract.Episodes.FIRST + " INTEGER, " +
            TvContract.Episodes.WATCHED + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, TvContract.DATABASE_NAME, null, database_version);
        Log.d("Database operations", "Database created");

    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {

        sdb.execSQL(CREATE_QUERY1);
        sdb.execSQL(CREATE_QUERY2);
        sdb.execSQL(CREATE_QUERY3);

        Log.d("Database operations", "Tables created");

    }

    public Cursor returnGenTvInfo(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TvContract.General.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public Cursor returnSeasonEpisode(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append(TvContract.Seasons.TABLE_NAME);
        sb.append(" INNER JOIN ");
        sb.append(TvContract.Episodes.TABLE_NAME);
        sb.append(" ON (");
        sb.append(TvContract.Seasons.TABLE_NAME + "." + TvContract.Seasons.SEASON_ID);
        sb.append(" = ");
        sb.append(TvContract.Episodes.TABLE_NAME + "." + TvContract.Episodes.SEASON_ID);
        sb.append(")");
        queryBuilder.setTables(sb.toString());

        Log.i("episodessss", "yes");
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);
    }


    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }


}