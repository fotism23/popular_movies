package com.example.android.popularmovies2.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.popularmovies2.data.MovieContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class PopularMoviesSyncUtils {
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    private static final String POPULAR_MOVIES_SYNC_TAG = "popular-movies-sync";

    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncPopularMoviesJob = dispatcher.newJobBuilder()
                .setService(PopularMoviesFirebaseJobService.class)
                .setTag(POPULAR_MOVIES_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(
                        Trigger.executionWindow(
                                SYNC_INTERVAL_SECONDS,
                                SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS
                        ))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncPopularMoviesJob);
    }

    synchronized public static void initialize(@NonNull final Context context) {
        if (sInitialized) return;

        sInitialized = true;
        scheduleFirebaseJobDispatcherSync(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri queryUri = MovieContract.MovieEntry.CONTENT_URI;

                String[] projectionColumns = {MovieContract.MovieEntry._ID};
                Cursor cursor = context.getContentResolver().query(
                        queryUri,
                        projectionColumns,
                        null,
                        null,
                        null
                );
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                cursor.close();
            }
        });
        checkForEmpty.start();
    }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately;
        intentToSyncImmediately = new Intent(context, PopularMoviesSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
