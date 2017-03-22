package com.example.android.popularmovies2.utilities;

import java.text.DecimalFormat;

public final class TheMovieDatabaseUtils {
    public static String OWM_MESSAGE_CODE = "status_code";
    public static String OWN_LIST = "results";
    public static String OWN_POSTER_PATH = "poster_path";
    public static String OWN_ADULT = "adult";
    public static String OWN_OVERVIEW = "overview";
    public static String OWN_RELEASE_DATE = "release_date";
    public static String OWN_GENDER_IDS = "genre_ids";
    public static String OWN_ID = "id";
    public static String OWN_ORIGINAL_TITLE = "original_title";
    public static String OWN_ORIGINAL_LANGUAGE = "original_language";
    public static String OWN_TITLE = "title";
    public static String OWN_BACKDROP_PATH = "backdrop_path";
    public static String OWN_POPULARITY = "popularity";
    public static String OWN_VOTE_COUNT = "vote_count";
    public static String OWN_VIDEO = "video";
    public static String OWN_VOTE_AVERAGE = "vote_average";
    public static String OWN_RUNTIME = "runtime";

    public static String OWN_VIDEO_KEY = "key";
    public static String OWN_VIDEO_SITE = "site";
    public static String OWN_VIDEO_ID = "id";

    public static String OWN_REVIEW_AUTHOR = "author";
    public static String OWN_REVIEW_CONTENT = "content";
    public static String OWN_REVIEW_ID = "id";

    public static String formatDate(String releaseDateString) {
        return releaseDateString.substring(0, releaseDateString.indexOf("-"));
    }

    public static String formatRating(double voteAverage) {
        DecimalFormat df = new DecimalFormat("####0.0");
        return "" + df.format(voteAverage) + "/10";
    }
}
