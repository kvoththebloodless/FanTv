package com.example.kvoththebloodless.fantv;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvoththebloodless.fantv.Provider.TvContract;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    static int position;
    @BindView(R.id.rating1)
    TextView Rating;
    @BindView(R.id.airdate)
    TextView Airdate;
    @BindView(R.id.runtime)
    TextView Runtime;
    @BindView(R.id.lang)
    TextView Lang;
    @BindView(R.id.plot)
    TextView plot;
    @BindView(R.id.overlay_poster)
    DynamicHeightNetworkImageView Poster;
    @BindView(R.id.detail_still)
    DynamicHeightNetworkImageView still;
    @BindView(R.id.Epname)
    TextView Epname;
    @BindView(R.id.nextep)
    ImageButton next;
    @BindView(R.id.prevep)
    ImageButton prev;
    @BindView(R.id.bleedpic)
    DynamicHeightNetworkImageView banner;
    @BindView(R.id.calendarview)
    View view;
    @BindView(R.id.readmore)
    Button Readmore;
    @BindView(R.id.watched)
    CheckBox watched;
    Cursor epdata;
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_detail);
        Log.i("detailactivity", "yayyy");
        ButterKnife.bind(this);
        getSupportLoaderManager().initLoader(0, null, this);
//
//        mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        watched.setEnabled(false);


        if (getIntent().getBooleanExtra("isitvisible", false)) {
            getSupportLoaderManager().initLoader(1, null, this);
            Cursor c = getContentResolver().query(TvContract.Seasons.CONTENT_URI_JOINED, null, TvContract.Seasons.TABLE_NAME
                    + "." + TvContract.Seasons.TMDB_ID + "=" +
                    getIntent().getIntExtra("tmdbid", -5), null, null);
            if (c.getCount() == 0)
                Log.i("zerooo", "hehh");
            else

                view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new CursorLoader(this, TvContract.General.CONTENT_URI_ALL, null, TvContract.General.TMDB_ID + "=" +
                        getIntent().getIntExtra("tmdbid", -5), null, null);

            case 1:
                return new CursorLoader(this, TvContract.Seasons.CONTENT_URI_JOINED, null, TvContract.Seasons.TABLE_NAME
                        + "." + TvContract.Seasons.TMDB_ID + "=?"
                        , new String[]{"" + getIntent().getIntExtra("tmdbid", -5)}, TvContract.Episodes.TABLE_NAME
                        + "." + TvContract.Episodes.FIRST);

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, final Cursor data) {
        if (data == null || data.getCount() == 0) {
            watched.setEnabled(false);
            return;
        }


        Log.i("loadfinished", "loader:" + loader.getId());
        switch (loader.getId()) {
            case 0:
                data.moveToFirst();
//                Rating.setText(data.getColumnIndex(TvContract.General.VOTE_AVG));
                Airdate.setText(data.getString(data.getColumnIndex(TvContract.General.AIRDATE)));
                Runtime.setText(data.getString(data.getColumnIndex(TvContract.General.RUNTIME)));
                Lang.setText(data.getString(data.getColumnIndex(TvContract.General.ORIGIN)));
                plot.setText(data.getString(data.getColumnIndex(TvContract.General.OVERVIEW)));
                String URL = data.getString(data.getColumnIndex(TvContract.General.POSTER));
                Poster.setAspectRatio(0.667f);
                banner.setAspectRatio(5.41f);

                Readmore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DetailActivity.this);
// ...Irrelevant code for customizing the buttons and title
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View dialogView = inflater.inflate(R.layout.alert_plot, null);
                        dialogBuilder.setView(dialogView);
                        dialogBuilder.setTitle(data.getString(data.getColumnIndex(TvContract.General.SHOW_NAME)));

                        TextView t = (TextView) dialogView.findViewById(R.id.plot_extended);
                        t.setText(plot.getText().toString());
                        dialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                    }
                });


                Picasso.with(this).load("https://image.tmdb.org/t/p/w500/" + URL).into(Poster);
                Picasso.with(this).load("http://thetvdb.com/banners/" + data.getString(data.getColumnIndex(TvContract.General.BANNER))).into(banner);

                break;

            case 1:

                watched.setEnabled(true);
                watched.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        ContentValues cv = new ContentValues();
                        if (b) {
                            cv.put(TvContract.Episodes.WATCHED, "yes");
                            getContentResolver().update(TvContract.Episodes.CONTENT_URI, cv,
                                    TvContract.Episodes.EP_ID + "=" + epdata.getInt(epdata.getColumnIndex(TvContract.Episodes.EP_ID)), null);
                        } else {
                            cv.put(TvContract.Episodes.WATCHED, "no");
                            getContentResolver().update(TvContract.Episodes.CONTENT_URI, cv,
                                    TvContract.Episodes.EP_ID + "=" + epdata.getInt(epdata.getColumnIndex(TvContract.Episodes.EP_ID)), null);
                        }

                    }
                });

                epdata = data;
                epdata.moveToPosition(position);
                still.setAspectRatio(1.77f);

                updateUI();

                // getSupportLoaderManager().destroyLoader(1);
                prev.setTag(1);
                prev.setOnClickListener(this);
                next.setTag(2);
                next.setOnClickListener(this);

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    protected void onDestroy() {
        getSupportLoaderManager().destroyLoader(3);
        getSupportLoaderManager().destroyLoader(4);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        switch ((int) (view.getTag())) {
            case 1:
                epdata.moveToPrevious();
                updateUI();
                position--;
                break;
            case 2:
                epdata.moveToNext();
                position++;
                updateUI();
                break;

        }


    }


    void updateUI() {
        try {
            Epname.setText(epdata.getString(epdata.getColumnIndex(TvContract.Seasons.SEASON_NO)) + "X" +
                    epdata.getInt(epdata.getColumnIndex(TvContract.Episodes.EP_NO)) +
                    epdata.getString(epdata.getColumnIndex(TvContract.Episodes.EP_NAME)));

            String a = epdata.getString(epdata.getColumnIndex(TvContract.Episodes.WATCHED));
            if (a.equals("yes"))
                watched.setChecked(true);
            else
                watched.setChecked(false);

            Picasso.with(this).load("https://image.tmdb.org/t/p/w500/" + epdata.getString(epdata.getColumnIndex(TvContract.Episodes.STILLS))).into(still);
            still.setContentDescription(epdata.getString(epdata.getColumnIndex(TvContract.Episodes.EP_NAME)));
            if ((epdata.getString(epdata.getColumnIndex(TvContract.Episodes.WATCHED)).equals("yes"))) {
                watched.setChecked(true);
            } else
                watched.setChecked(false);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this, "No Episodes!", Toast.LENGTH_SHORT).show();
        }
    }
}
