package com.leo.moviehunter.util;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leo.moviehunter.R;
import com.leo.moviehunter.fragment.MovieDetailFragment;
import com.leo.moviehunter.tmdb.response.MovieResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieResultAdapter extends RecyclerView.Adapter<MovieResultAdapter.ViewHolder> {

    private final Fragment mFragment;
    private OnClickListener mGetMoreMovieClickListener;
    private String mImageBaseUrl;
    private final List<MovieResult> mMovieList = new ArrayList<>();
    private boolean mHasMoreMovie = true;

    public MovieResultAdapter(Fragment fragment) {
        mFragment = fragment;
    }

    public void setImageBaseUrl(String url) {
        mImageBaseUrl = url;
        notifyDataSetChanged();
    }

    public void setGetMoreMovieClickListenter(OnClickListener listener) {
        mGetMoreMovieClickListener = listener;
    }

    public void addMovies(MovieResult[] movies, boolean hasMoreMovie) {
        mHasMoreMovie = hasMoreMovie;
        mMovieList.addAll(Arrays.asList(movies));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewGroup mContainer;
        private ViewGroup mMovieContainer;
        private ImageView mImage;
        private TextView mText1;
        private TextView mText2;
        private TextView mText3;
        private ViewGroup mGetMoreContainer;
        private MovieOnClickListener mMovieOnClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (ViewGroup) itemView;
            mMovieContainer = (ViewGroup) itemView.findViewById(R.id.movie_container);
            mImage = (ImageView) itemView.findViewById(R.id.movie_image);
            mText1 = (TextView) itemView.findViewById(R.id.movie_text_1);
            mText2 = (TextView) itemView.findViewById(R.id.movie_text_2);
            mText3 = (TextView) itemView.findViewById(R.id.movie_text_3);
            mMovieOnClickListener = new MovieOnClickListener();
            mMovieContainer.setOnClickListener(mMovieOnClickListener);

            mGetMoreContainer = (ViewGroup) itemView.findViewById(R.id.get_more_container);
            mGetMoreContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGetMoreMovieClickListener != null) {
                        mGetMoreMovieClickListener.onClick(v);
                    }
                }
            });
        }
    }

    private class MovieOnClickListener implements View.OnClickListener {
        private int mMovieId;

        public  void setMovieId(int id) {
            mMovieId = id;
        }

        @Override
        public void onClick(View v) {
            FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, MovieDetailFragment.newInstance(mMovieId));
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.movie_list_item, null);
        return new ViewHolder(container);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mHasMoreMovie && position == getItemCount() - 1 /* last item */) {
            holder.mMovieContainer.setVisibility(View.GONE);
            holder.mGetMoreContainer.setVisibility(View.VISIBLE);
            return;
        }

        holder.mMovieContainer.setVisibility(View.VISIBLE);
        holder.mGetMoreContainer.setVisibility(View.GONE);

        Context context = holder.mContainer.getContext();
        MovieResult movie = mMovieList.get(position);
        if (!TextUtils.isEmpty(mImageBaseUrl)) {
            Glide.with(context)
                    .load(mImageBaseUrl + movie.poster_path)
                    .placeholder(android.R.drawable.ic_dialog_alert)
                    .centerCrop()
                    .crossFade()
                    .into(holder.mImage);
        }
        holder.mText1.setText(movie.title + " / " + movie.original_title);
        holder.mText2.setText(context.getString(R.string.score) + ": " + movie.vote_average);
        holder.mText3.setText(movie.release_date);
        holder.mMovieOnClickListener.setMovieId(movie.id);
    }

    @Override
    public int getItemCount() {
        if (!mHasMoreMovie) {
            return mMovieList.size();
        } else if (mMovieList.size() > 0) {
            return mMovieList.size() + 1;
        } else {
            return 0;
        }
    }
}
