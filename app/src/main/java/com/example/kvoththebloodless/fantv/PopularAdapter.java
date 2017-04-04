package com.example.kvoththebloodless.fantv;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvoththebloodless.fantv.FanTvSync.FanTvSyncTask;
import com.example.kvoththebloodless.fantv.Misc.Utility;
import com.example.kvoththebloodless.fantv.Provider.TvContract;
import com.squareup.picasso.Picasso;


public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.MyViewHolder> {
    Context context;
    Cursor data;

    PopularAdapter(Context c, Cursor data) {
        context = c;
        this.data = data;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popularitem, null));

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        if (data == null || data.getCount() == 0)
            return;
        data.moveToPosition(position);

        holder.runtime = data.getString(data.getColumnIndex(TvContract.General.RUNTIME));
        holder.airdate = data.getString(data.getColumnIndex(TvContract.General.AIRDATE));
        holder.lang = data.getString(data.getColumnIndex(TvContract.General.ORIGIN));
        holder.plot = data.getString(data.getColumnIndex(TvContract.General.OVERVIEW));
        holder.rating = data.getString(data.getColumnIndex(TvContract.General.VOTE_AVG));
        holder.Name.setText(data.getString(data.getColumnIndex(TvContract.General.SHOW_NAME)));
        holder.Genre.setText(data.getString(data.getColumnIndex(TvContract.General.GENRE)));
        holder.popularpic.setContentDescription(holder.Name.getText().toString());

        holder.tmdbid = data.getInt(data.getColumnIndex(TvContract.General.TMDB_ID));
        String fav = data.getString(data.getColumnIndex(TvContract.General.FAVOURITE));

        holder.poster = data.getString(data.getColumnIndex(TvContract.General.POSTER));
        Picasso.with(context).load("https://image.tmdb.org/t/p/w500/" + data.getString(data.getColumnIndex(TvContract.General.BACKDROP))).into(holder.popularpic);


        if (fav.equals("yes")) {
            holder.Fav.setChecked(true);
        }


    }

    @Override
    public int getItemCount() {
        return data.getCount();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView Rating, Airdate, Runtime, Lang, Plot;
        Button Readmore;

        DynamicHeightNetworkImageView Overlay_Poster;
        String rating = "NA", airdate = "NA", runtime = "NA", plot = "NA", lang = "N/A", poster = "NA";
        int tmdbid;
        SwitchCompat Fav;
        TextView Name;
        TextView Genre;
        ImageButton Info;
        FrameLayout Overlay;
        DynamicHeightNetworkImageView popularpic;
        DynamicHeightNetworkImageView popularpicgradient;

        public MyViewHolder(View itemView) {

            super(itemView);
            setIsRecyclable(false);
            Fav = (SwitchCompat) itemView.findViewById(R.id.addfav);
            Name = (TextView) itemView.findViewById(R.id.nameofshow);
            Genre = (TextView) itemView.findViewById(R.id.genre);
            Info = (ImageButton) itemView.findViewById(R.id.infoo);
            Overlay = (FrameLayout) itemView.findViewById(R.id.overlay);
            popularpic = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.popularpic);
            popularpicgradient = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.popularpicgradient);
            itemView.setOnClickListener(this);
            popularpic.setAspectRatio(1.777f);
            popularpicgradient.setAspectRatio(1.777f);

            Fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {

                    if (!Utility.isConnected(context)) {
                        compoundButton.setEnabled(false);
                        Toast.makeText(context, R.string.internet_loss, Toast.LENGTH_SHORT).show();


                    }

                    if (b) {
                        Cursor c = (context.getContentResolver().query(TvContract.Seasons.CONTENT_URI_JOINED, new String[]{
                                        TvContract.Seasons.SEASON_ID},
                                TvContract.Seasons.TABLE_NAME + "." + TvContract.Seasons.TMDB_ID + "=" + tmdbid, null, null));
                        if (c != null && c.getCount() != 0) {
                            c.close();
                            return;

                        }
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected void onPreExecute() {

                                super.onPreExecute();
                            }

                            @Override
                            protected Void doInBackground(Void... no) {
                                FanTvSyncTask.c = context;
                                FanTvSyncTask.pullTvChanges(tmdbid);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                ContentValues cv = new ContentValues();
                                cv.put(TvContract.General.FAVOURITE, "yes");


                                context.getContentResolver().update(TvContract.General.CONTENT_URI_ALL, cv,
                                        TvContract.General.TMDB_ID + "=" + tmdbid, null);
                            }
                        }.execute();
                    } else {
                        ContentValues cv = new ContentValues();
                        cv.put(TvContract.General.FAVOURITE, "no");
                        context.getContentResolver().delete(TvContract.Seasons.CONTENT_URI_ALONE, TvContract.General.TMDB_ID + "=" + tmdbid, null);
                        context.getContentResolver().delete(TvContract.Episodes.CONTENT_URI, TvContract.General.TMDB_ID + "=" + tmdbid, null);
                        context.getContentResolver().update(TvContract.General.CONTENT_URI_ALL, cv, TvContract.General.TMDB_ID + "=" + tmdbid, null);
                    }
                }
            });

            Info.setOnClickListener(this);
            Overlay.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (view instanceof ImageButton) {
                Overlay.setVisibility(View.VISIBLE);

                if (Rating == null) {
                    Overlay_Poster = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.overlay_poster);
                    Rating = (TextView) itemView.findViewById(R.id.rating1);
                    Airdate = (TextView) itemView.findViewById(R.id.airdate);
                    Runtime = (TextView) itemView.findViewById(R.id.runtime);
                    Lang = (TextView) itemView.findViewById(R.id.lang);
                    Plot = (TextView) itemView.findViewById(R.id.plot);
                    Readmore = (Button) itemView.findViewById(R.id.readmore);

                    Readmore.setOnClickListener(this);

                }


                Overlay_Poster.setAspectRatio(0.6667f);

                Picasso.with(context).load("https://image.tmdb.org/t/p/w500/" + poster).into(Overlay_Poster);
                Overlay_Poster.setContentDescription(Name.getText().toString());
                Rating.setText(rating);
                Airdate.setText(airdate);
                Runtime.setText(runtime);
                Lang.setText(lang);
                Plot.setText(plot);

            } else if (view instanceof FrameLayout)
                Overlay.setVisibility(View.GONE);
            else if (view instanceof Button
                    ) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
// ...Irrelevant code for customizing the buttons and title
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.alert_plot, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle(Name.getText().toString());

                TextView t = (TextView) dialogView.findViewById(R.id.plot_extended);
                t.setText(plot);
                dialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();


            } else {
                Intent in = new Intent(context, DetailActivity.class);
                in.putExtra("tmdbid", tmdbid);
                if (Fav.isChecked())
                    in.putExtra("isitvisible", true);
                else
                    in.putExtra("isitvisible", false);

                context.startActivity(in);
            }
        }
    }
}

