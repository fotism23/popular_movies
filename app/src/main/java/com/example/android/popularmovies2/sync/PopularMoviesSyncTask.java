package com.example.android.popularmovies2.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.example.android.popularmovies2.MainActivity;
import com.example.android.popularmovies2.data.MovieContract;
import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.example.android.popularmovies2.utilities.TheMovieDatabaseJsonUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class PopularMoviesSyncTask {
    private static final int SORT_POPULAR = 0;
    private static final int SORT_TOP_RATED = 1;

    synchronized public static void syncMovies(Context context) {
        URL movieRequestUrl = null;

        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        int sortingOrder = sharedPreferences.getInt(MainActivity.MY_PREFERENCES_SORT_ORDER, MainActivity.POPULAR_ORDER_PARAM);

        if (sortingOrder == SORT_POPULAR) {
            movieRequestUrl = NetworkUtils.buildPopularUrl();
        }else if (sortingOrder == SORT_TOP_RATED) {
            movieRequestUrl = NetworkUtils.buildTopUrl();
        }

        addMovies(context, movieRequestUrl);
    }

    private static void addMovies(Context context, URL movieRequestUrl) {
        try {
            String jsonMovieDataResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            ContentValues[] movieValues = TheMovieDatabaseJsonUtils.getMovieContentValuesFromJson(context, jsonMovieDataResponse);

            if (movieValues != null && movieValues.length != 0) {
                ContentResolver popularMoviesContentResolver = context.getContentResolver();

                for (ContentValues cv : movieValues) {
                    int movieId = cv.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                    cv.putAll(addDetails(movieId));
                }

                popularMoviesContentResolver.bulkInsert(
                        MovieContract.FavouriteEntry.CONTENT_URI,
                        copyFavourites(popularMoviesContentResolver, movieValues)
                        //(ContentValues[]) allValuesArray.toArray(new ContentValues[1])
                );

                popularMoviesContentResolver.delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,null
                );


                popularMoviesContentResolver.bulkInsert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieValues
                );

                addAdditionalDetails(popularMoviesContentResolver, movieValues);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static ContentValues addDetails(int movieId) throws Exception {
        URL url = NetworkUtils.buildDetailUrlWithId(movieId);
        String jsonMovieDataResponse = NetworkUtils.getResponseFromHttpUrl(url);
        Integer runtime = TheMovieDatabaseJsonUtils.getRuntime(jsonMovieDataResponse);
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_RUNTIME, runtime);
        return cv;
    }


    private static ContentValues[] getReviews(int movieId) throws Exception {
        URL url = NetworkUtils.buildReviewsUrlWithId(movieId);
        String jsonMovieDataResponse = NetworkUtils.getResponseFromHttpUrl(url);
        return TheMovieDatabaseJsonUtils.getReviews(movieId, jsonMovieDataResponse);
    }

    private static ContentValues[] getVideos(int movieId) throws Exception {
        URL url = NetworkUtils.buildVideosUrlWithId(movieId);
        String jsonMovieDataResponse = NetworkUtils.getResponseFromHttpUrl(url);
        return TheMovieDatabaseJsonUtils.getVideos(movieId, jsonMovieDataResponse);
    }

    private static ContentValues[] copyFavourites(ContentResolver popularMoviesContentResolver, ContentValues[] movies) throws Exception{
        int movieId, runtime;
        String movieTitle, moviePosterPath, overview, releaseDate;
        double voteAverage;
        ArrayList<ContentValues> allValuesArray = new ArrayList<>();

        for (ContentValues cv : movies) {
            movieId = cv.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            movieTitle = cv.getAsString(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
            moviePosterPath = cv.getAsString(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

            Cursor cursor = popularMoviesContentResolver.query(MovieContract.FavouriteEntry.buildMovieUriWithId((long) movieId),
                    new String[] {MovieContract.FavouriteEntry.COLUMN_FAVOURITE_IS_FAVOURITE},
                    null,
                    null,
                    null);

            ContentValues values = new ContentValues();

            values.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_TITLE, movieTitle);
            values.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_ID, movieId);
            values.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_POSTER_PATH, moviePosterPath);

            if (cursor.getCount() == 0) {
                values.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_IS_FAVOURITE, 0);
            } else {
                values.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_IS_FAVOURITE, 1);
            }
            allValuesArray.add(values);
        }
        return (ContentValues[]) allValuesArray.toArray(new ContentValues[1]);
    }

    private static void addAdditionalDetails(ContentResolver popularMoviesContentResolver, ContentValues[] movies) throws Exception {
        int movieId;
        ArrayList<ContentValues> allValuesArray = new ArrayList<>();

        for (ContentValues cv : movies) {
            movieId = cv.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            allValuesArray.addAll(Arrays.asList(getReviews(movieId)));
        }

        popularMoviesContentResolver.bulkInsert(
                MovieContract.ReviewsEntry.CONTENT_URI,
                (ContentValues[]) allValuesArray.toArray(new ContentValues[1])
        );

        allValuesArray = new ArrayList<>();

        for (ContentValues cv : movies) {
            movieId = cv.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            allValuesArray.addAll(Arrays.asList(getVideos(movieId)));
        }

        popularMoviesContentResolver.bulkInsert(
                MovieContract.VideosEntry.CONTENT_URI,
                (ContentValues[]) allValuesArray.toArray(new ContentValues[1])
        );

    }

}
