package com.example.android.popularmovies2.sync;

import android.app.IntentService;
import android.content.Intent;

public class PopularMoviesSyncIntentService extends IntentService {

    public PopularMoviesSyncIntentService() {
        super("PopularMoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PopularMoviesSyncTask.syncMovies(this);
    }
}