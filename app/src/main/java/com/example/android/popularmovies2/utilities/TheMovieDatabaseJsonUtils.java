package com.example.android.popularmovies2.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.popularmovies2.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TheMovieDatabaseJsonUtils {

    public static ContentValues[] getMovieContentValuesFromJson(Context context, String jsonStr) throws JSONException {
        JSONObject resultsJson = new JSONObject(jsonStr);

        if (resultsJson.has(TheMovieDatabaseUtils.OWM_MESSAGE_CODE)) {
            return null;
        }

        JSONArray moviesArray = resultsJson.getJSONArray(TheMovieDatabaseUtils.OWN_LIST);
        ContentValues[] parsedMovieData = new ContentValues[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieDetails = moviesArray.getJSONObject(i);

            String posterPath = movieDetails.getString(TheMovieDatabaseUtils.OWN_POSTER_PATH);
            boolean adult = movieDetails.getBoolean(TheMovieDatabaseUtils.OWN_ADULT);
            String overview = movieDetails.getString(TheMovieDatabaseUtils.OWN_OVERVIEW);
            String releaseDate = movieDetails.getString(TheMovieDatabaseUtils.OWN_RELEASE_DATE);
            int movieId = movieDetails.getInt(TheMovieDatabaseUtils.OWN_ID);
            String originalTitle = movieDetails.getString(TheMovieDatabaseUtils.OWN_ORIGINAL_TITLE);
            String originalLanguage = movieDetails.getString(TheMovieDatabaseUtils.OWN_ORIGINAL_LANGUAGE);
            String title = movieDetails.getString(TheMovieDatabaseUtils.OWN_TITLE);
            String backdropPath = movieDetails.getString(TheMovieDatabaseUtils.OWN_BACKDROP_PATH);
            double popularity = movieDetails.getDouble(TheMovieDatabaseUtils.OWN_POPULARITY);
            int voteCount = movieDetails.getInt(TheMovieDatabaseUtils.OWN_VOTE_COUNT);
            boolean video = movieDetails.getBoolean(TheMovieDatabaseUtils.OWN_VIDEO);
            double voteAverage = movieDetails.getDouble(TheMovieDatabaseUtils.OWN_VOTE_AVERAGE);
            JSONArray genreIdsArray = movieDetails.getJSONArray(TheMovieDatabaseUtils.OWN_GENDER_IDS);
            int[] ids = new int[genreIdsArray.length()];
            for (int j = 0; j < ids.length; j++) {
                ids[j] = genreIdsArray.getInt(j);
            }

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ADULT, adult);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, originalLanguage);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCount);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, video);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);

            parsedMovieData[i] = movieValues;
        }
        return parsedMovieData;
    }

    public static Integer getRuntime(String jsonStr) throws JSONException {
        JSONObject resultsJson = new JSONObject(jsonStr);
        if (resultsJson.has(TheMovieDatabaseUtils.OWM_MESSAGE_CODE)) {
            return null;
        }
        return resultsJson.getInt(TheMovieDatabaseUtils.OWN_RUNTIME);
    }

    public static ContentValues[] getReviews(int id, String jsonStr) throws JSONException {
        JSONObject resultsJson = new JSONObject(jsonStr);
        if (resultsJson.has(TheMovieDatabaseUtils.OWM_MESSAGE_CODE)) {
            return null;
        }
        if (!resultsJson.has(TheMovieDatabaseUtils.OWN_LIST)){
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_MOVIE_ID, id);
            return new ContentValues[]{cv};
        }
        JSONArray reviewsArray = resultsJson.getJSONArray(TheMovieDatabaseUtils.OWN_LIST);
        ContentValues[] reviews = new ContentValues[reviewsArray.length()];
        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject reviewDetails = reviewsArray.getJSONObject(i);
            String reviewAuthor = reviewDetails.getString(TheMovieDatabaseUtils.OWN_REVIEW_AUTHOR);
            String reviewContent = reviewDetails.getString(TheMovieDatabaseUtils.OWN_REVIEW_CONTENT);
            String reviewId = reviewDetails.getString(TheMovieDatabaseUtils.OWN_REVIEW_ID);

            ContentValues reviewValues = new ContentValues();
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, reviewAuthor);
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_CONTENT, reviewContent);
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_ID, reviewId);
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_MOVIE_ID, id);

            reviews[i] = reviewValues;
        }

        return reviews;
    }

    public static ContentValues[] getVideos(int id, String jsonStr) throws JSONException {
        JSONObject resultsJson = new JSONObject(jsonStr);

        if (resultsJson.has(TheMovieDatabaseUtils.OWM_MESSAGE_CODE)) {
            return null;
        }
        if (!resultsJson.has(TheMovieDatabaseUtils.OWN_LIST)){
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.VideosEntry.COLUMN_VIDEO_MOVIE_ID, id);
            return new ContentValues[]{cv};
        }
        JSONArray videosArray = resultsJson.getJSONArray(TheMovieDatabaseUtils.OWN_LIST);
        ContentValues[] videos = new ContentValues[videosArray.length()];

        for (int i = 0; i < videosArray.length(); i++) {
            JSONObject videoDetails = videosArray.getJSONObject(i);

            String videoUrl = videoDetails.getString(TheMovieDatabaseUtils.OWN_VIDEO_KEY);
            String videoSite = videoDetails.getString(TheMovieDatabaseUtils.OWN_VIDEO_SITE);
            String videoId = videoDetails.getString(TheMovieDatabaseUtils.OWN_VIDEO_ID);

            ContentValues videoValues = new ContentValues();
            videoValues.put(MovieContract.VideosEntry.COLUMN_VIDEO_MOVIE_ID, id);
            videoValues.put(MovieContract.VideosEntry.COLUMN_VIDEO_URL, videoUrl);
            videoValues.put(MovieContract.VideosEntry.COLUMN_VIDEO_SITE, videoSite);
            videoValues.put(MovieContract.VideosEntry.COLUMN_VIDEO_ID, videoId);

            videos[i] = videoValues;
        }
        return videos;
    }
}
