package com.example.android.popularmovies2.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class MovieProvider extends ContentProvider{

    public static final int CODE_MOVIE = 100;
    public static final int CODE_REVIEWS = 104;
    public static final int CODE_VIDEOS = 105;
    public static final int CODE_FAVOURITE = 106;
    public static final int CODE_MOVIE_WITH_ID = 101;
    public static final int CODE_VIDEO_WITH_MOVIE_ID = 102;
    public static final int CODE_REVIEW_WITH_MOVIE_ID = 103;
    public static final int CODE_FAVOURITE_WITH_MOVIE_ID = 107;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS, CODE_REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_VIDEOS, CODE_VIDEOS);
        matcher.addURI(authority, MovieContract.PATH_FAVOURITE, CODE_FAVOURITE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS + "/#", CODE_REVIEW_WITH_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_VIDEOS + "/#", CODE_VIDEO_WITH_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_FAVOURITE + "/#", CODE_FAVOURITE_WITH_MOVIE_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE: {
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            }
            case CODE_FAVOURITE: {
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.FavouriteEntry.TABLE_NAME, null, value);
                        if (_id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            }
            case CODE_REVIEWS: {
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            }

            case CODE_VIDEOS: {
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.VideosEntry.TABLE_NAME, null, value);
                        if (_id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_WITH_ID: {
                String[] selectionArguments = new String[]{uri.getLastPathSegment()};
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_MOVIE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_FAVOURITE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavouriteEntry.COLUMN_FAVOURITE_IS_FAVOURITE + " = 1 ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CODE_FAVOURITE_WITH_MOVIE_ID: {
                String[] selectionArguments = new String[]{uri.getLastPathSegment()};
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        projection,
                        "( " + MovieContract.FavouriteEntry.COLUMN_FAVOURITE_ID + " = ? AND " + MovieContract.FavouriteEntry.COLUMN_FAVOURITE_IS_FAVOURITE + " <> 0) ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_REVIEW_WITH_MOVIE_ID: {
                String[] selectionArguments = new String[]{uri.getLastPathSegment()};
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        "( " + MovieContract.ReviewsEntry.COLUMN_REVIEW_MOVIE_ID + " = ? AND " + MovieContract.ReviewsEntry.COLUMN_REVIEW_CONTENT + " IS NOT NULL) ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_VIDEO_WITH_MOVIE_ID: {
                String[] selectionArguments = new String[]{uri.getLastPathSegment()};
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.VideosEntry.TABLE_NAME,
                        projection,
                        "( " + MovieContract.VideosEntry.COLUMN_VIDEO_MOVIE_ID + " = ? AND " + MovieContract.VideosEntry.COLUMN_VIDEO_SITE + " IS NOT NULL) ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case CODE_MOVIE:{
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            }
            case CODE_MOVIE_WITH_ID:{
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted;
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CODE_REVIEWS:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.ReviewsEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CODE_VIDEOS:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.VideosEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CODE_FAVOURITE:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        throw new RuntimeException(
                "We are not implementing insert in PopularMovies. Use bulkInsert instead");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numRowsUpdated;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVOURITE_WITH_MOVIE_ID: {
                String[] selectionArguments = new String[]{uri.getLastPathSegment()};
                db.beginTransaction();
                numRowsUpdated = db.update(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.FavouriteEntry.COLUMN_FAVOURITE_ID + " = ? ",
                        selectionArguments
                );
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsUpdated;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
