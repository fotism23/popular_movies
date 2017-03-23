package com.example.android.popularmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies2.data.MovieContract.MovieEntry;
import com.example.android.popularmovies2.data.MovieContract.FavouriteEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 61;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_ADULT + " BOOL NOT NULL, " +
                        MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                        MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                        MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                        MovieEntry.COLUMN_VIDEO + " BOOL NOT NULL, " +
                        MovieEntry.COLUMN_VIDEO_URL + " TEXT, " +
                        MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                        MovieEntry.COLUMN_RUNTIME + " INTEGER, " +
                        " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_FAVOURITE_TABLE =
                "CREATE TABLE " + FavouriteEntry.TABLE_NAME + " (" +
                        FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavouriteEntry.COLUMN_FAVOURITE_ID + " INTEGER NOT NULL, " +
                        FavouriteEntry.COLUMN_FAVOURITE_TITLE + " TEXT NOT NULL, " +
                        FavouriteEntry.COLUMN_FAVOURITE_POSTER_PATH + " TEXT NOT NULL, " +
                        FavouriteEntry.COLUMN_FAVOURITE_IS_FAVOURITE + " INTEGER NOT NULL DEFAULT 0, " +
                        " UNIQUE (" + FavouriteEntry.COLUMN_FAVOURITE_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_TABLE);

        final String SQL_CREATE_VIDEOS_TABLE =
                "CREATE TABLE " + MovieContract.VideosEntry.TABLE_NAME + " (" +
                        MovieContract.VideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.VideosEntry.COLUMN_VIDEO_MOVIE_ID + " INTEGER, " +
                        MovieContract.VideosEntry.COLUMN_VIDEO_SITE + " TEXT, " +
                        MovieContract.VideosEntry.COLUMN_VIDEO_URL + " TEXT, " +
                        MovieContract.VideosEntry.COLUMN_VIDEO_ID + " TEXT, " +
                        " UNIQUE (" + MovieContract.VideosEntry.COLUMN_VIDEO_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEOS_TABLE);

        final String SQL_CREATE_REVIEWS_TABLE =
                "CREATE TABLE " + MovieContract.ReviewsEntry.TABLE_NAME + " (" +
                        MovieContract.ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.ReviewsEntry.COLUMN_REVIEW_ID + " INTEGER, " +
                        MovieContract.ReviewsEntry.COLUMN_REVIEW_MOVIE_ID + " INTEGER, " +
                        MovieContract.ReviewsEntry.COLUMN_REVIEW_CONTENT + " TEXT, " +
                        MovieContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR + " TEXT, " +
                        " UNIQUE (" + MovieContract.ReviewsEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.VideosEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
