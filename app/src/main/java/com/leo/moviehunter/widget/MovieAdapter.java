package com.leo.moviehunter.widget;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
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
import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.fragment.MovieDetailFragment;
import com.leo.moviehunter.task.AddToWatchListTask;
import com.leo.moviehunter.task.DeleteFromWatchListTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private static final String TAG = "MovieAdapter";

    private final Fragment mFragment;
    private OnClickListener mGetMoreMovieClickListener;
    private String mImageBaseUrl;
    private final List<Movie> mMovieList = new ArrayList<>();
    private boolean mShowHasMoreButton = true;
    private final Map<String, WatchItem> mWatchList = new HashMap<>();
    private final Drawable mStarOn;
    private final Drawable mStarOff;

    public MovieAdapter(Fragment fragment) {
        mFragment = fragment;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mStarOn = fragment.getActivity().getResources().getDrawable(android.R.drawable.btn_star_big_on);
            mStarOff = fragment.getActivity().getResources().getDrawable(android.R.drawable.btn_star_big_off);
        } else {
            mStarOn = fragment.getActivity().getResources().getDrawable(android.R.drawable.btn_star_big_on, null);
            mStarOff = fragment.getActivity().getResources().getDrawable(android.R.drawable.btn_star_big_off, null);
        }
    }

    public void setImageBaseUrl(String url) {
        mImageBaseUrl = url;
        notifyDataSetChanged();
    }

    public void setGetMoreMovieClickListener(OnClickListener listener) {
        mGetMoreMovieClickListener = listener;
    }

    public void addMovies(Movie[] movies, boolean showHasMoreButton) {
        mShowHasMoreButton = showHasMoreButton;
        mMovieList.addAll(Arrays.asList(movies));
        notifyDataSetChanged();
    }

    public void setWatchList(List<WatchItem> watchList) {
        mWatchList.clear();
        if (watchList != null) {
            for (WatchItem watchItem : watchList) {
                mWatchList.put(watchItem.getMovieId(), watchItem);
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewGroup mContainer;
        private ViewGroup mMovieContainer;
        private ImageView mImage;
        private ImageView mStatusImage;
        private TextView mText1;
        private TextView mText2;
        private TextView mText3;
        private ViewGroup mGetMoreContainer;
        private MovieOnClickListener mMovieOnClickListener;
        private StatusOnClickListener mStatusOnClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (ViewGroup) itemView;
            mMovieContainer = (ViewGroup) itemView.findViewById(R.id.movie_container);
            mImage = (ImageView) itemView.findViewById(R.id.movie_image);
            mStatusImage = (ImageView) itemView.findViewById(R.id.movie_status);
            mStatusOnClickListener = new StatusOnClickListener(mStatusImage);
            mStatusImage.setOnClickListener(mStatusOnClickListener);
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.movie_list_item, null);
        return new ViewHolder(container);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mShowHasMoreButton && position == getItemCount() - 1 /* last item */) {
            holder.mMovieContainer.setVisibility(View.GONE);
            holder.mGetMoreContainer.setVisibility(View.VISIBLE);
            return;
        }

        holder.mMovieContainer.setVisibility(View.VISIBLE);
        holder.mGetMoreContainer.setVisibility(View.GONE);

        Context context = holder.mContainer.getContext();
        Movie movie = mMovieList.get(position);
        if (!TextUtils.isEmpty(mImageBaseUrl)) {
            Glide.with(context)
                    .load(mImageBaseUrl + movie.getCoverImageUrl())
                    .placeholder(android.R.drawable.ic_dialog_alert)
                    .centerCrop()
                    .crossFade()
                    .into(holder.mImage);
        }
        setupMovieStatus(holder, movie);
        holder.mText1.setText(movie.getTitle() + " / " + movie.getOriginalTitle());
        holder.mText2.setText(context.getString(R.string.score) + ": " + movie.getScore());
        holder.mText3.setText(movie.getReleaseDate());
        holder.mMovieOnClickListener.setMovie(movie);
    }

    @Override
    public int getItemCount() {
        if (!mShowHasMoreButton) {
            return mMovieList.size();
        } else if (mMovieList.size() > 0) {
            return mMovieList.size() + 1;
        } else {
            return 0;
        }
    }

    private void setupMovieStatus(ViewHolder holder, Movie movie) {
        holder.mStatusOnClickListener.setMovie(movie);
        if (mWatchList.get(movie.getId()) != null) {
            holder.mStatusImage.setImageDrawable(mStarOn);
            holder.mStatusImage.setTag(mStarOn);
        } else {
            holder.mStatusImage.setImageDrawable(mStarOff);
            holder.mStatusImage.setTag(mStarOff);
        }
    }

    private class MovieOnClickListener implements OnClickListener {
        private Movie mMovie;

        public  void setMovie(Movie movie) {
            mMovie = movie;
        }

        @Override
        public void onClick(View v) {
            FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, MovieDetailFragment.newInstance(mMovie.getId()));
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private class StatusOnClickListener implements OnClickListener {
        private final ImageView mStatusView;
        private Movie mMovie;

        public StatusOnClickListener(ImageView statusView) {
            mStatusView = statusView;
        }

        public  void setMovie(Movie movie) {
            mMovie = movie;
        }

        @Override
        public void onClick(View v) {
            if (mStatusView.getTag() == mStarOn) {
                new DeleteFromWatchListTask(mStatusView.getContext()) {
                    @Override
                    protected void onDone(String ...movieIds) {
                        if (movieIds == null) {
                            return;
                        }
                        for (String movieId : movieIds) {
                            mWatchList.remove(movieId);
                        }
                    }
                }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, mMovie.getId());
                mStatusView.setImageDrawable(mStarOff);
                mStatusView.setTag(mStarOff);
            } else if (mStatusView.getTag() == mStarOff) {
                List<WatchItem> list = new ArrayList<>();
                WatchItem item = new WatchItem();
                item.setMovieId(mMovie.getId());
                item.setAddedEpochTime(System.currentTimeMillis());
                item.setGenreIds(mMovie.getGenreIds());
                list.add(item);
                new AddToWatchListTask(mStatusView.getContext()) {
                    @Override
                    protected void onDone(List<WatchItem> addedList) {
                        if (addedList == null) {
                            return;
                        }
                        for (WatchItem watchItem : addedList) {
                            mWatchList.put(watchItem.getMovieId(), watchItem);
                        }
                    }
                }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, list);
                mStatusView.setImageDrawable(mStarOn);
                mStatusView.setTag(mStarOn);
            }
        }
    }
}
