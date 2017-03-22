package com.example.android.popularmovies2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies2.data.MovieContract;
import com.example.android.popularmovies2.databinding.ActivityDetailBinding;
import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.example.android.popularmovies2.utilities.TheMovieDatabaseUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMovies";
    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";

    private static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RUNTIME,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };

    private static final String[] MOVIE_FAVOURITE_PROJECTION = {
            MovieContract.FavouriteEntry.COLUMN_FAVOURITE_IS_FAVOURITE
    };

    private static final String[] MOVIE_TRAILERS_PROJECTION = {
            MovieContract.VideosEntry.COLUMN_VIDEO_SITE,
            MovieContract.VideosEntry.COLUMN_VIDEO_URL,
    };

    private static final String[] MOVIE_REVIEWS_PROJECTION = {
            MovieContract.ReviewsEntry.COLUMN_REVIEW_CONTENT,
            MovieContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR
    };

    public static final int INDEX_POSTER_PATH = 0;
    public static final int INDEX_ORIGINAL_TITLE = 1;
    public static final int INDEX_RELEASE_DATE = 2;
    public static final int INDEX_VOTE_AVERAGE = 3;
    public static final int INDEX_RUNTIME = 4;
    public static final int INDEX_OVERVIEW = 5;
    public static final int INDEX_MOVIE_ID = 6;

    public static final int INDEX_IS_FAVOURITE = 0;

    public static final int INDEX_VIDEO_SITE = 0;
    public static final int INDEX_VIDEO_URL = 1;

    public static final int INDEX_REVIEW_CONTENT = 0;
    public static final int INDEX_REVIEW_AUTHOR = 1;

    private static final int ID_DETAIL_LOADER = 353;
    private static final int ID_TRAILER_LOADER = 444;
    private static final int ID_REVIEW_LOADER = 324;
    private static final int ID_FAVOURITE_LOADER = 388;

    private boolean isFavourite;

    private Uri mUri;
    private Uri mReviewsUri;
    private Uri mTrailerUri;
    private Uri mFavouriteUri;

    private int mMovieId;
    private String mMovieTitle;

    private ActivityDetailBinding mDetailBinding;

    private String shareVideoUrl;
    private String shareVideoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        loadUris();
        initLoaders();
    }

    private void initLoaders() {
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_REVIEW_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_TRAILER_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_FAVOURITE_LOADER, null, this);
    }

    private void loadUris() {
        mUri = getIntent().getData();
        String id = mUri.getLastPathSegment();
        mReviewsUri = MovieContract.ReviewsEntry.buildMovieUriWithId(Long.parseLong(id));
        mTrailerUri = MovieContract.VideosEntry.buildMovieUriWithId(Long.parseLong(id));
        mFavouriteUri = MovieContract.FavouriteEntry.buildMovieUriWithId(Long.parseLong(id));

        if (mReviewsUri == null || mTrailerUri == null || mFavouriteUri == null) throw new NullPointerException("URI for Reviews, Trailers and Favourites cannot be null");
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        MOVIE_DETAIL_PROJECTION,
                        null,
                        null,
                        null);
            case ID_REVIEW_LOADER:
                return new CursorLoader(this,
                        mReviewsUri,
                        MOVIE_REVIEWS_PROJECTION,
                        null,
                        null,
                        null);

            case ID_TRAILER_LOADER:
                return new CursorLoader(this,
                        mTrailerUri,
                        MOVIE_TRAILERS_PROJECTION,
                        null,
                        null,
                        null);
            case ID_FAVOURITE_LOADER:
                return new CursorLoader(this,
                        mFavouriteUri,
                        MOVIE_FAVOURITE_PROJECTION,
                        null,
                        null,
                        null
                        );
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()){
            case ID_DETAIL_LOADER: {
                showData(data);
                break;
            }
            case ID_TRAILER_LOADER: {
                showTrailers(data);
                break;
            }
            case ID_REVIEW_LOADER: {
                showReviews(data);
                break;
            }
            case ID_FAVOURITE_LOADER: {
                initFavourite(data);
                swapButtonText();
                break;
            }
        }
    }

    private void swapButtonText() {
        if (isFavourite) mDetailBinding.btFavourite.setText(getString(R.string.marked_as_favourite));
        else mDetailBinding.btFavourite.setText(getString(R.string.mark_as_favourite_button));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share){
            shareMovie();
            return true;
        } else if (id == R.id.action_favourite){
            markAsFavourite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareMovie() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, shareVideoTitle + MOVIE_SHARE_HASHTAG);
        i.putExtra(Intent.EXTRA_TEXT, shareVideoUrl);
        startActivity(Intent.createChooser(i, "Share Trailer"));
    }

    private void initFavourite(Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
                    /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }
        int favInt = data.getInt(INDEX_IS_FAVOURITE);
        isFavourite = favInt == 1;

    }

    private void markAsFavourite() {
        isFavourite = !isFavourite;
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_ID, mMovieId);
        if (isFavourite) cv.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_IS_FAVOURITE, 1);
        else cv.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_IS_FAVOURITE, 0);

        cv.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_TITLE, mMovieTitle);

        getContentResolver().update(mFavouriteUri, cv, null, null);
        getContentResolver().notifyChange(MovieContract.FavouriteEntry.CONTENT_URI, null);
    }

    public void onClickFavourite(View view) {
        markAsFavourite();
    }

    private void showData(Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
                    /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }
        mMovieId = data.getInt(INDEX_MOVIE_ID);
        mMovieTitle = data.getString(INDEX_ORIGINAL_TITLE);

        shareVideoTitle = data.getString(INDEX_ORIGINAL_TITLE);
        mDetailBinding.tvOriginalTitle.setText(data.getString(INDEX_ORIGINAL_TITLE));
        mDetailBinding.tvRating.setText(TheMovieDatabaseUtils.formatRating(data.getDouble(INDEX_VOTE_AVERAGE)));
        mDetailBinding.tvReleaseDate.setText(TheMovieDatabaseUtils.formatDate(data.getString(INDEX_RELEASE_DATE)));
        mDetailBinding.tvRuntime.setText(getString(R.string.format_runtime, data.getInt(INDEX_RUNTIME)));
        mDetailBinding.tvOverview.setText(data.getString(INDEX_OVERVIEW));
        URL url = NetworkUtils.getPosterImageUrl(data.getString(INDEX_POSTER_PATH));
        if (url != null) Picasso.with(this).load(url.toString()).into(mDetailBinding.ivPosterDetailImage);
    }
    
    private void showTrailers(Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
                    /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) {
            mDetailBinding.tvVideosNotFound.setVisibility(View.VISIBLE);
            mDetailBinding.trailersListView.setVisibility(View.INVISIBLE);
            /* No data to display, simply return and do nothing */
            return;
        }

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.trailers_list_view);
        LayoutInflater inflater = LayoutInflater.from(this);

        final String videoId = data.getString(INDEX_VIDEO_URL);
        shareVideoUrl = Uri.parse("http://www.youtube.com/watch?v=" + videoId).toString();

        do {
            View view = inflater.inflate(R.layout.trailer_list_item, linearLayout, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchYoutubeIntent(videoId);
                }
            });
            TextView siteTextView = (TextView) view.findViewById(R.id.tv_site);
            siteTextView.setText(data.getString(INDEX_VIDEO_SITE));
            linearLayout.addView(view);
        } while (data.moveToNext());
    }

    private void launchYoutubeIntent(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(YOUTUBE_BASE_URL + id));
        try {
            startActivity(appIntent);
        } catch (Exception ex) {
            startActivity(webIntent);
        }
    }

    private void showReviews(Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
                    /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) {
            mDetailBinding.tvReviewsNotFound.setVisibility(View.VISIBLE);
            mDetailBinding.reviewsListView.setVisibility(View.INVISIBLE);
            /* No data to display, simply return and do nothing */
            return;
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.reviews_list_view);
        LayoutInflater inflater = LayoutInflater.from(this);
        do {
            View view = inflater.inflate(R.layout.review_list_item, linearLayout, false);
            TextView authorTextView = (TextView) view.findViewById(R.id.tv_author);
            TextView contentTextView = (TextView) view.findViewById(R.id.tv_content);
            authorTextView.setText(data.getString(INDEX_REVIEW_AUTHOR));
            contentTextView.setText(data.getString(INDEX_REVIEW_CONTENT));
            linearLayout.addView(view);
        } while (data.moveToNext());
        
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
