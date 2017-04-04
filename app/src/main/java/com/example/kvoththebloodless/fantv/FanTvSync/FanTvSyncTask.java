/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kvoththebloodless.fantv.FanTvSync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.kvoththebloodless.fantv.Misc.Utility;
import com.example.kvoththebloodless.fantv.Provider.TvContract;
import com.example.kvoththebloodless.fantv.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.GET;


public class FanTvSyncTask {
    public static Context c;
    public static String POPULAR_URL;
    public static String TV_DETAILS;
    public static String TVDB;
    public static String TV_CHANGES;
    public static String SEASON_CHANGES;
    public static String EPISODE_CHANGES;
    public static String VIDEO_URL1, IMG_URL1;
    static String TVDB_TOKEN;

    synchronized public static void startSync(final Context context, int TAG) {
        c = context;
        POPULAR_URL = c.getString(R.string.popular_url, Utility.TMDB_API_KEY);
        switch (TAG) {
            case FanTvUtils.FIRST_SYNC:


            case FanTvUtils.PERIODIC_SYNC:
                getTvdbToken();
                pullPreList(POPULAR_URL, c.getString(R.string.Popular_TAG));


                if (TAG == FanTvUtils.FIRST_SYNC)
                    break;


                final Cursor cursor = context.getContentResolver().query(TvContract.General.CONTENT_URI_ALL,
                        new String[]{TvContract.General.TMDB_ID}, TvContract.General.FAVOURITE + "= 'yes'", null, null);
                if (cursor != null && cursor.getCount() != 0) {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        int id = cursor.getInt(cursor.getColumnIndex(TvContract.General.TMDB_ID));
                        pullTvChanges(id);
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }


                } else
                    cursor.close();

                break;
            case FanTvUtils.NEWSFEED:
                pullNewsfeed();
                break;

        }

    }

    public static void getTvdbToken() {

        JSONObject js = new JSONObject();

        try {

            js.put("apikey", Utility.TVDB_API_KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReqtoken = new JsonObjectRequest(
                Request.Method.POST, c.getString(R.string.tvdb_login), js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            TVDB_TOKEN = response.getString("token");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");

                return headers;
            }
        };

        VolleySingleton.getInstance(c).addToRequestQueue(jsonObjReqtoken);
    }

    public static void pullPreList(final String URL, final String TAG) {
        JsonObjectRequest prelistreq = new JsonObjectRequest(GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    JSONArray jarr = response.getJSONArray(c.getString(R.string.KEY_RESULT));
                    if (!(jarr.length() > 0))
                        return;
                    for (int i = 0; i < jarr.length(); i++) {
                        JSONObject show = jarr.getJSONObject(i);
                        pullDetail(show.getInt(c.getString(R.string.tmdb_id)), TAG);
                    }
                } catch (Exception e) {
                    Log.e(URL, "" + e);
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("prepulllist", error + "");
            }
        });
        VolleySingleton.getInstance(c).addToRequestQueue(prelistreq);


    }

    static String resolveGenre(JSONArray jsonArray) throws JSONException {
        String genre = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            genre = genre + jsonArray.getJSONObject(i).getString("name") + " | ";
        }
        return genre;
    }

    public static void pullDetail(final int id, final String TAG) {


        JsonObjectRequest pulldetreq = new JsonObjectRequest(GET, c.getString(R.string.Popular_Detailed, id, Utility.TMDB_API_KEY), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ContentValues cv = new ContentValues();
                try {
                    cv.put(TvContract.General.TMDB_ID, response.getInt(c.getString(R.string.id)));
                    cv.put(TvContract.General.SHOW_NAME, response.getString("original_name"));
                    cv.put(TvContract.General.OVERVIEW, response.getString("overview"));
                    cv.put(TvContract.General.GENRE, resolveGenre(response.getJSONArray("genres")));
                    cv.put(TvContract.General.TYPE, TAG);
                    cv.put(TvContract.General.ORIGIN, response.getJSONArray("origin_country").join(" | "));
                    cv.put(TvContract.General.STATUS, response.getString("status"));
                    cv.put(TvContract.General.RUNTIME, response.getJSONArray("episode_run_time").join(","));
                    cv.put(TvContract.General.VOTE_AVG, response.getString("vote_average"));
                    cv.put(TvContract.General.BANNER, "soon");
                    cv.put(TvContract.General.POSTER, response.getString("poster_path"));
                    cv.put(TvContract.General.BACKDROP, response.getString("backdrop_path"));
                    cv.put(TvContract.General.AIRDATE, response.getString("first_air_date"));
                    cv.put(TvContract.General.TVDB_ID, -50);
                    cv.put(TvContract.General.NO_SEASONS, response.getString("number_of_seasons"));
                    cv.put(TvContract.General.NO_EP, response.getString("number_of_episodes"));
                    final Cursor cursor = c.getContentResolver().query(TvContract.General.CONTENT_URI_ALL,
                            new String[]{TvContract.General.FAVOURITE}, TvContract.General.TMDB_ID + "=" + id, null, null);
                    if (cursor != null && cursor.getCount() != 0)
                        cv.put(TvContract.General.FAVOURITE, cursor.getString(cursor.getColumnIndex(TvContract.General.FAVOURITE)));
                    else
                        cv.put(TvContract.General.FAVOURITE, "no");

                    c.getContentResolver().insert(TvContract.General.CONTENT_URI_ALL, cv);
                    pullTvdbId(id);


                } catch (Exception e) {
                    Log.e("pullpredet", e + "");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("pullpredet", error + "");
            }
        });
        VolleySingleton.getInstance(c).addToRequestQueue(pulldetreq);

    }


    public static ContentValues pullLatestList() {
        return null;
    }

    public static Cursor pullSearchList() {
        return null;
    }

    public static void pullTvChanges(final int id) {

        final Cursor cursor = c.getContentResolver().query(TvContract.General.CONTENT_URI_SINGLE,
                new String[]{TvContract.General.NO_SEASONS}, TvContract.General.TMDB_ID + "=" + id, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return;
        }

        cursor.moveToFirst();
        int nos = cursor.getInt(cursor.getColumnIndex(TvContract.General.NO_SEASONS));

        cursor.close();


        for (int i = 0; i < nos; i++) {
            //add nos to the url

            VolleySingleton.getInstance(c).addToRequestQueue(new JsonObjectRequest(
                    Request.Method.GET,
                    c.getString(R.string.url_tmdb) + id + "/season/" +
                            (i + 1) + "?api_key=" + Utility.TMDB_API_KEY + "&language=en-US", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ContentValues cv = new ContentValues();

                            try {
                                cv.put(TvContract.Seasons.TMDB_ID, id);
                                cv.put(TvContract.Seasons.SEASON_ID, response.getString("id"));
                                cv.put(TvContract.Seasons.AIRDATE, response.getString("air_date"));
                                cv.put(TvContract.Seasons.SEASON_NO, response.getInt("season_number"));
                                cv.put(TvContract.Seasons.EP_COUNT, response.getJSONArray("episodes").length());

                                c.getContentResolver().insert(TvContract.Seasons.CONTENT_URI_ALONE, cv);

                                pushEpInfo(response, id, response.getInt("season_number"));
                            } catch (Exception e) {

                            }


                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("tvchanges", "Error: " + error.getMessage());

                }
            }));


        }

    }

    static long formatToSec(String airdate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date d = null;

            d = formatter.parse(airdate);


            Calendar thatDay = Calendar.getInstance();
            thatDay.setTime(d);

            return thatDay.getTimeInMillis();
        } catch (Exception e) {
            return -999;
        }

    }

    public static void pushEpInfo(final JSONObject response, final int id, final int s0no) {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    final JSONArray jarr = response.getJSONArray("episodes");
                    int i = jarr.length();
                    for (int j = 0; j < i; j++) {
                        ContentValues cv = new ContentValues();
                        final JSONObject ep = jarr.getJSONObject(j);
                        cv.put(TvContract.Episodes.SEASON_ID, response.getString("id"));
                        cv.put(TvContract.Episodes.TMDB_ID, id);
                        cv.put(TvContract.Episodes.EP_NAME, ep.getString("name"));
                        cv.put(TvContract.Episodes.STILLS, ep.getString("still_path"));
                        cv.put(TvContract.Episodes.AIRDATE, ep.getString("air_date"));
                        cv.put(TvContract.Episodes.VIDEO_URL, "soon");
                        cv.put(TvContract.Episodes.EP_ID, ep.getInt("id"));
                        cv.put(TvContract.Episodes.EP_NO, ep.getInt("episode_number"));
                        cv.put(TvContract.Episodes.FIRST, formatToSec(ep.getString("air_date")));
                        cv.put(TvContract.Episodes.WATCHED, "no");
                        Uri id1 = c.getContentResolver().insert(TvContract.Episodes.CONTENT_URI, cv);
                    }

                } catch (Exception e) {
                }
                return null;
            }
        }.execute();

    }


    public static void pullVideo(int tvid, int s0no, int epno, final int epid) {
        JsonObjectRequest vidreq = new JsonObjectRequest(
                Request.Method.GET, c.getString(R.string.url_tmdb) + tvid + "/season/" + s0no + "/episode/" + epno + "/videos?api_key=d0fa8157cea46d952cb9578970c82542&language=en-US"//add tvid s0no and epno
                , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ContentValues cv = new ContentValues();
                        try {
                            JSONArray jarr = response.getJSONArray("results");

                            if (jarr.length() > 0) {
                                cv.put(TvContract.Episodes.VIDEO_URL, jarr.getJSONObject(0).getString("key"));
                                c.getContentResolver().update(TvContract.Episodes.CONTENT_URI, cv, TvContract.Episodes.EP_ID
                                        + "=" + epid, null);
                            }

                        } catch (Exception e) {
                            Log.e("videopull", "" + e);
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("videopull", "Error: " + error.getMessage());

            }
        });
        VolleySingleton.getInstance(c).addToRequestQueue(vidreq);
    }


    public static void pullImages(int tvid, int s0no, int epno, final int epid) {
        JsonObjectRequest jsonObjReqim = new JsonObjectRequest(
                Request.Method.GET, "https://api.themoviedb.org/3/tv/" + tvid + "/season/" + s0no + "/episode/" + epno + "/images?api_key=d0fa8157cea46d952cb9578970c82542"//add tvid s0no and epno
                , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ContentValues cv = new ContentValues();
                        try {
                            JSONArray jarr = response.getJSONArray("stills");

                            if (jarr.length() > 0) {
                                cv.put(TvContract.Episodes.STILLS, jarr.getJSONObject(0).getString("file_path"));
                                c.getContentResolver().update(TvContract.Episodes.CONTENT_URI, cv, TvContract.Episodes.EP_ID
                                        + "=" + epid, null);
                            }

                        } catch (Exception e) {
                            Log.e("imgpull", "" + e);
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("imgpull", "Error: " + error.getMessage());

            }
        });
        VolleySingleton.getInstance(c).addToRequestQueue(jsonObjReqim);


    }


    public static void checkPullBanner(final int tvdbid, final int tmdbid) {
        if (TVDB_TOKEN == null) {
            getTvdbToken();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (TVDB_TOKEN == null)
                        return;
                    pullBanner(tvdbid, tmdbid);
                }
            }, 5000);
        } else
            pullBanner(tvdbid, tmdbid);


    }

    public static Cursor pullNewsfeed() {
        return null;
    }


    public static void pullTvdbId(final int id) {
        JsonObjectRequest tvdbidreq = new JsonObjectRequest(GET, c.getString(R.string.url_tmdb) + id + "/external_ids?api_key=" + Utility.TMDB_API_KEY + "&language=en-US", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ContentValues cv = new ContentValues();
                try {
                    final int tvdb = response.getInt("tvdb_id");
                    cv.put(TvContract.General.TVDB_ID, tvdb);
                    c.getContentResolver().update(TvContract.General.CONTENT_URI_ALL, cv, TvContract.General.TMDB_ID + "=" + id, null);
                    checkPullBanner(tvdb, id);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("tvdbidpull", "" + error.getMessage());
            }
        });
        VolleySingleton.getInstance(c).addToRequestQueue(tvdbidreq);
    }

    public static void pullBanner(int tvdbid, final int tmdbid) {


        JsonObjectRequest bannerreq = new JsonObjectRequest(
                Request.Method.GET, "https://api.thetvdb.com/series/" + tvdbid, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ContentValues cv = new ContentValues();
                        try {

                            cv.put(TvContract.General.BANNER, response.getJSONObject("data").getString("banner"));
                            c.getContentResolver().update(TvContract.General.CONTENT_URI_ALL, cv, TvContract.General.TMDB_ID + "=" + tmdbid, null);

                        } catch (Exception e) {
                            Log.e("TVDBURL", "" + e);
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tvdbpull", "Error: " + error.getMessage());

            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + TVDB_TOKEN);
                return headers;
            }
        };
        VolleySingleton.getInstance(c).addToRequestQueue(bannerreq);
    }

}







