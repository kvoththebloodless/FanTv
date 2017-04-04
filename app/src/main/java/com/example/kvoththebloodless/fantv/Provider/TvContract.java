package com.example.kvoththebloodless.fantv.Provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TvContract {

    public static final String AUTHORITY =
            "com.example.kvoththebloodless.fantv";
    public static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    public static final String PATH_GENERAL_ALL = "general_all";
    public static final String PATH_GENERAL_SINGLE = "general_single";

    public static final String PATH_SEASONS_JOINED = "seasons_joined";
    public static final String PATH_SEASONS_ALONE = "seasons_alone";

    public static final String PATH_EPISODES = "episodes";
    //    public static final String PATH_CAST = "cast";
    //    public static final String PATH_CREDITS = "credits";
    public static final String DATABASE_NAME = "FanTv";


    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + DATABASE_NAME;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + DATABASE_NAME;

    public static class General implements BaseColumns {
        public static final Uri CONTENT_URI_ALL =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENERAL_ALL).build();

        public static final Uri CONTENT_URI_SINGLE = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENERAL_SINGLE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_GENERAL_ALL;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_GENERAL_SINGLE;

        public static final
        String TABLE_NAME = "General",
                TMDB_ID = "Tmdb_Id",
                SHOW_NAME = "Name",
                OVERVIEW = "Overview",
                GENRE = "Genre",
                TYPE = "type",
                ORIGIN = "Origin",
                STATUS = "Status",
                RUNTIME = "Runtime",
                VOTE_AVG = "Vote_Avg",
                BANNER = "Banner",
                POSTER = "Poster",
                BACKDROP = "Backdrop",
                AIRDATE = "Airdate",
                TVDB_ID = "Tvdb_Id",
                NO_SEASONS = "No_S0",
                NO_EP = "No_Ep",
                FAVOURITE = "Favourite";


        public static Uri uriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_ALL, id);

        }
    }

    public static class Seasons implements BaseColumns {
        public static final Uri CONTENT_URI_JOINED =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEASONS_JOINED).build();
        public static final Uri CONTENT_URI_ALONE =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEASONS_ALONE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_SEASONS_JOINED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_SEASONS_ALONE;

        public static final
        String TABLE_NAME = "Seasons",
                TMDB_ID = "Tmdb_Id",
                SEASON_ID = "Season_Id",
                AIRDATE = "S0_Airdate",
                SEASON_NO = "S0_NO",
                EP_COUNT = "Ep_Count";


        public static Uri uriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_ALONE, id);

        }
    }

    public static class Episodes implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EPISODES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_EPISODES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_EPISODES;

        public static final
        String TABLE_NAME = "Episodes",
                SEASON_ID = "Parent_Id",
                TMDB_ID = "Tmdb_Id",
                EP_NAME = "Ep_Name",
                STILLS = "Stills",
                AIRDATE = "Ep_Airdate",
                VIDEO_URL = "Video_Url",
                EP_ID = "Ep_Id",
                EP_NO = "Ep_No",
                FIRST = "first",
                WATCHED = "Watched";


        public static Uri uriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);

        }
    }

    //Future Scope

//    public static class Cast implements BaseColumns {
//        public static final Uri CONTENT_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAST).build();
//        public static final String CONTENT_TYPE =
//                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_CAST;
//        public static final String CONTENT_ITEM_TYPE =
//                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_CAST;
//
//        public static final
//        String TABLE_NAME = "Cast",
//                CREDITS_ID = "Credits_Id",
//                EP_ID="Ep_Id";
//
//
//
//        public static Uri uriWithId(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//
//        }
//    }
//
//    public static class Credits implements BaseColumns {
//        public static final Uri CONTENT_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CREDITS).build();
//
//
//        public static final
//        String TABLE_NAME = "Credits",
//                CREDITS_ID = "Credits_Id",
//                PROFILE_PATH="Profile_Path",
//                NAME="Name";
//
//
//
//        public static Uri uriWithId(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//
//        }
//    }
}
