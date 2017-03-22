package com.example.android.popularmovies2;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;


public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder>{
    private final Context mContext;
    final private PosterAdapterOnClickHandler mClickHandler;

    public interface PosterAdapterOnClickHandler {
        void onClick(long id);
    }
    private Cursor mCursor;

    public PosterAdapter(@NonNull Context context, PosterAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public PosterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.poster_wall_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        view.setFocusable(true);
        return new PosterAdapterViewHolder(view);
    }



    @Override
    public void onBindViewHolder(PosterAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String posterString = mCursor.getString(MainActivity.INDEX_MOVIE_POSTER);

        URL url = NetworkUtils.getPosterImageUrl(posterString);
        if (url != null) Picasso.with(mContext).load(url.toString()).into(holder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class PosterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mMovieImageView;

        public PosterAdapterViewHolder(View view) {
            super(view);
            mMovieImageView = (ImageView) view.findViewById(R.id.iv_poster_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long movieSelected = mCursor.getLong(MainActivity.INDEX_MOVIE_ID);
            mClickHandler.onClick(movieSelected);
        }
    }
}
