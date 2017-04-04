package com.example.kvoththebloodless.fantv;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.kvoththebloodless.fantv.FanTvSync.FanTvSyncTask;
import com.example.kvoththebloodless.fantv.FanTvSync.FanTvUtils;
import com.example.kvoththebloodless.fantv.Misc.Utility;
import com.example.kvoththebloodless.fantv.Provider.TvContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.tooly)
    Toolbar tooly;
    @BindView(R.id.popularlist)
    RecyclerView recycler;
    AppCompatSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_tracking);
        ButterKnife.bind(this);
        setSupportActionBar(tooly);
        getSupportLoaderManager().initLoader(0, null, this);
        FanTvUtils.initialize(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(null);


        new Thread() {
            @Override
            public void run() {
                FanTvSyncTask.c = getApplicationContext();
                FanTvSyncTask.pullTvChanges(1622);
                super.run();
            }
        }.start();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_popular, menu);
        spinner = (android.support.v7.widget.AppCompatSpinner) (menu.findItem(R.id.spinner).getActionView());
        ArrayAdapter<CharSequence> SpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Spinner_choice, android.R.layout.simple_list_item_1);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(SpinnerAdapter);
        Log.i("onCreateOptionsMenu ", "working");

        MenuItem mn = menu.findItem(R.id.menu_account);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        Recycler.setAdapter(null);
//
//
        switch (
                i
                ) {
            case 0:
                if (getSupportLoaderManager().getLoader(1) != null)
                    getSupportLoaderManager().destroyLoader(1);

                getSupportLoaderManager().initLoader(0, null, this);


                break;
            case 1:
                if (getSupportLoaderManager().getLoader(0) != null)
                    getSupportLoaderManager().destroyLoader(0);

                getSupportLoaderManager().initLoader(1, null, this);
                break;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new CursorLoader(this, TvContract.General.CONTENT_URI_ALL,
                        null,
                        TvContract.General.TYPE + "='popular'",
                        null,
                        null);

            case 1:
                return new CursorLoader(this, TvContract.General.CONTENT_URI_ALL,
                        null,
                        TvContract.General.TYPE + "='toprated'",
                        null,
                        null);

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        switch (loader.getId()) {
            case 1:
                if (data == null || data.getCount() == 0) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... no) {
                            FanTvSyncTask.c = getApplicationContext();
                            FanTvSyncTask.pullPreList("https://api.themoviedb.org/3/tv/top_rated?api_key=" + Utility.TMDB_API_KEY + "&language=en-US&page=1", "toprated");
//                                 Toast.makeText(context,"pulling from server",Toast.LENGTH_SHORT).show();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_SHORT).show();
                        }
                    }.execute();
                }
            case 0:
        }
        recycler.setAdapter(new PopularAdapter(this, data));
        //set recycler's adapter

    }

    @Override
    public void onLoaderReset(Loader loader) {
        recycler.setAdapter(null);
//setrecycler null
    }

    @Override
    protected void onDestroy() {
        try {
            getSupportLoaderManager().destroyLoader(0);
            getSupportLoaderManager().destroyLoader(1);
        } catch (Exception e) {
            Log.i("loader not there", e + "");
        }
        super.onDestroy();
    }
}