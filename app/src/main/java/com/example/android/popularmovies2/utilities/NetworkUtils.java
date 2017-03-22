package com.example.android.popularmovies2.utilities;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {
    // INSERT YOUR API HERE
    private static final String API_KEY = "";

    private static final String MOVIE_DATABASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String MOVIE_POSTER_BASE_URL = "https://image.tmdb.org/t/p/";

    private static final String DATABASE_BASE_URL = MOVIE_DATABASE_URL;

    private static String TOP_RATED_PARAM = "top_rated";

    private static String POPULAR_PARAM = "popular";

    private static String VIDEOS_PARAM = "/videos";

    private static String REVIEWS_PARAM = "/reviews";

    private static String LANGUAGE_PARAM = "language";

    private static String PAGE_PARAM = "page";

    private static String EN_LANG_PARAM = "en-US";

    private static String API_KEY_PARAM = "api_key";

    private static int PAGE_VALUE_PARAM = 1;

    private static String POSTER_SMALL_SIZE_PARAM = "w300";

    private static String POSTER_LARGE_SIZE_PARAM = "w500";

    public static URL buildPopularUrl() {
        return buildUrl(true);
    }

    public static URL buildTopUrl() {
        return buildUrl(false);
    }

    private static URL buildUrl(boolean popularSort) {
        String urlBase = DATABASE_BASE_URL;

        if (popularSort) {
            urlBase += POPULAR_PARAM;
        } else {
            urlBase += TOP_RATED_PARAM;
        }

        Uri builtUri = Uri.parse(urlBase).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, EN_LANG_PARAM)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(PAGE_VALUE_PARAM))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL getPosterImageUrl(String posterPath) {
        try {
            return new URL(MOVIE_POSTER_BASE_URL + POSTER_SMALL_SIZE_PARAM + posterPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL buildDetailUrlWithId(int movieId) {
        String urlBase = DATABASE_BASE_URL + movieId;

        Uri builtUri = Uri.parse(urlBase).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, EN_LANG_PARAM)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(PAGE_VALUE_PARAM))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildReviewsUrlWithId(int movieId) {
        String urlBase = DATABASE_BASE_URL + movieId + REVIEWS_PARAM;

        Uri builtUri = Uri.parse(urlBase).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, EN_LANG_PARAM)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(PAGE_VALUE_PARAM))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildVideosUrlWithId(int movieId) {
        String urlBase = DATABASE_BASE_URL + movieId + VIDEOS_PARAM;

        Uri builtUri = Uri.parse(urlBase).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, EN_LANG_PARAM)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(PAGE_VALUE_PARAM))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}
