package com.example.android.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.popularmovies2.data.MovieContract;
import com.example.android.popularmovies2.sync.PopularMoviesSyncUtils;
import com.example.android.popularmovies2.utilities.LayoutUtils;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        PosterAdapter.PosterAdapterOnClickHandler {

    private final String TAG = MainActivity.class.getSimpleName();

    public static final String[] MAIN_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };

    public static final String[] FAVOURITE_PROJECTION = {
            MovieContract.FavouriteEntry.COLUMN_FAVOURITE_POSTER_PATH,
            MovieContract.FavouriteEntry.COLUMN_FAVOURITE_ID
    };

    public static final int INDEX_MOVIE_POSTER = 0;
    public static final int INDEX_MOVIE_ID = 1;

    public static final int ID_MOVIE_LOADER = 44;
    public static final int ID_FAVOURITE_LOADER = 55;

    public static int POPULAR_ORDER_PARAM = 0;
    public static int TOP_RATED_ORDER_PARAM = 1;

    public static int DEFAULT_ORDER = TOP_RATED_ORDER_PARAM;

    public static final String MY_PREFERENCES = "my_prefs";
    public static final String MY_PREFERENCES_SORT_ORDER = "sort_order";

    private RecyclerView mRecyclerView;
    private PosterAdapter mPosterAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    private SharedPreferences sharedPreferences;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0f);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_poster_wall);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        int numberOfColumns = LayoutUtils.calculateNoOfColumns(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mPosterAdapter = new PosterAdapter(this,this);
        mRecyclerView.setAdapter(mPosterAdapter);

        showLoading();
        initSharedPrefs();

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);

        PopularMoviesSyncUtils.initialize(this);
    }

    private void initSharedPrefs() {
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MY_PREFERENCES_SORT_ORDER, DEFAULT_ORDER);
        editor.apply();
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showMovieDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int numberOfColumns = LayoutUtils.calculateNoOfColumns(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_MOVIE_LOADER: {
                Uri queryUri = MovieContract.MovieEntry.CONTENT_URI;
                return new CursorLoader(
                        this,
                        queryUri,
                        MAIN_PROJECTION,
                        null,
                        null,
                        null
                );
            }
            case ID_FAVOURITE_LOADER: {
                Uri queryUri = MovieContract.FavouriteEntry.CONTENT_URI;
                return new CursorLoader(
                        this,
                        queryUri,
                        FAVOURITE_PROJECTION,
                        null,
                        null,
                        null
                );
            }
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mPosterAdapter.swapCursor(data);

        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showMovieDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPosterAdapter.swapCursor(null);
    }

    @Override
    public void onClick(long id) {
        Intent movieDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForIdClicked = MovieContract.MovieEntry.buildMovieUriWithId(id);
        movieDetailIntent.setData(uriForIdClicked);
        startActivity(movieDetailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popular){
            loadPopularMovies();
            return true;
        } else if (id == R.id.action_sort_top_rated){
            loadTopRatedMovies();
            return true;
        } else if (id == R.id.action_show_favourite) {
            loadFavourite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFavourite() {
        setTitle("Favourite Movies");
        getSupportLoaderManager().initLoader(ID_FAVOURITE_LOADER, null, this);
    }

    private void loadPopularMovies() {
        setTitle("Popular Movies");
        getSupportLoaderManager().destroyLoader(ID_FAVOURITE_LOADER);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MY_PREFERENCES_SORT_ORDER, POPULAR_ORDER_PARAM);
        editor.apply();
        PopularMoviesSyncUtils.startImmediateSync(this);
    }

    private void loadTopRatedMovies() {
        setTitle("Top Rated Movies");
        getSupportLoaderManager().destroyLoader(ID_FAVOURITE_LOADER);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MY_PREFERENCES_SORT_ORDER, TOP_RATED_ORDER_PARAM);
        editor.apply();
        PopularMoviesSyncUtils.startImmediateSync(this);
    }
}
