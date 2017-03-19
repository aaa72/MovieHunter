package com.leo.moviehunter.widget;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leo.moviehunter.R;
import com.leo.moviehunter.data.Genre;
import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.fragment.EditWatchedMovieFragment;
import com.leo.moviehunter.fragment.EditWatchedMovieFragment.OnEditDoneListener;
import com.leo.moviehunter.fragment.MovieDetailFragment;
import com.leo.moviehunter.task.AddToWatchListTask;
import com.leo.moviehunter.task.AddToWatchedListTask;
import com.leo.moviehunter.task.DeleteFromWatchListTask;
import com.leo.moviehunter.task.DeleteFromWatchedListTask;
import com.leo.moviehunter.task.GetGenresTask;
import com.leo.moviehunter.task.GetImageBaseUrlTask;
import com.leo.moviehunter.task.GetToWatchListTask;
import com.leo.moviehunter.task.GetWatchedListTask;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private static final String TAG = "MovieAdapter";

    private final Fragment mFragment;
    private final Map<String, Genre> mGenreMap = new HashMap<>();
    private final Map<String, WatchItem> mToWatchMap = new HashMap<>();
    private final Map<String, WatchItem> mWatchedMap = new HashMap<>();
    private List<Movie> mMovieList;
    private String mImageBaseUrl;
    private boolean mShowHasMoreButton = false;
    private OnClickListener mGetMoreMovieClickListener;
    private OnWatchItemChangeListener mOnWatchItemChangeListener;

    public MovieAdapter(Fragment fragment) {
        mFragment = fragment;

        // load image base url
        new GetImageBaseUrlTask() {
            @Override
            public void onGetUrl(String url) {
                mImageBaseUrl = url;
                notifyDataSetChanged();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        // load genres
        new GetGenresTask(fragment.getActivity()) {
            @Override
            protected void getGenres(Genre[] genres) {
                mGenreMap.putAll(MHUtil.genresToMap(genres));
                notifyDataSetChanged();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        // load watch list
        new GetToWatchListTask(fragment.getActivity()) {
            @Override
            public void onGetToWatchList(List<WatchItem> toWatchList) {
                mToWatchMap.clear();
                if (toWatchList != null) {
                    for (WatchItem watchItem : toWatchList) {
                        mToWatchMap.put(watchItem.getMovieId(), watchItem);
                    }
                }
                notifyDataSetChanged();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        // load watched list
        new GetWatchedListTask(fragment.getActivity()) {
            @Override
            public void onGetWatchedList(List<WatchItem> watchedList) {
                mWatchedMap.clear();
                if (watchedList != null) {
                    for (WatchItem watchItem : watchedList) {
                        mWatchedMap.put(watchItem.getMovieId(), watchItem);
                    }
                }
                notifyDataSetChanged();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public void setGetMoreMovieClickListener(OnClickListener listener) {
        mGetMoreMovieClickListener = listener;
    }

    public void setOnWatchItemChangeListener(OnWatchItemChangeListener listener) {
        mOnWatchItemChangeListener = listener;
    }

    public void setMovieList(List<Movie> list) {
        mMovieList = list;
        notifyDataSetChanged();
    }

    public void showHasMoreButton(boolean show) {
        mShowHasMoreButton = show;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewGroup mContainer;
        private ViewGroup mMovieContainer;
        private ImageView mImage;
        private ImageView mIconStarOff;
        private ImageView mIconStarOn;
        private ImageView mIconEdit;
        private ImageView mIconWatched;
        private TextView mText1;
        private TextView mText2;
        private TextView mText3;
        private TextView mText4;
        private ViewGroup mGetMoreContainer;
        private MovieOnClickListener mMovieOnClickListener;
        private IconStarOffOnClickListener mIconStarOffOnClickListener;
        private IconStarOnOnClickListener mIconStarOnOnClickListener;
        private IconEditOnClickListener mIconEditOnClickListener;
        private IconWatchedOnClickListener mIconWatchedOnClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (ViewGroup) itemView;
            mMovieContainer = (ViewGroup) itemView.findViewById(R.id.movie_container);
            mImage = (ImageView) itemView.findViewById(R.id.movie_image);
            mIconStarOff = (ImageView) itemView.findViewById(R.id.icon_star_off);
            mIconStarOffOnClickListener = new IconStarOffOnClickListener(this);
            mIconStarOff.setOnClickListener(mIconStarOffOnClickListener);
            mIconStarOn = (ImageView) itemView.findViewById(R.id.icon_star_on);
            mIconStarOnOnClickListener = new IconStarOnOnClickListener(this);
            mIconStarOn.setOnClickListener(mIconStarOnOnClickListener);
            mIconEdit = (ImageView) itemView.findViewById(R.id.icon_edit);
            mIconEditOnClickListener = new IconEditOnClickListener(this);
            mIconEdit.setOnClickListener(mIconEditOnClickListener);
            mIconWatched = (ImageView) itemView.findViewById(R.id.icon_watched);
            mIconWatchedOnClickListener = new IconWatchedOnClickListener(this);
            mIconWatched.setOnClickListener(mIconWatchedOnClickListener);
            mText1 = (TextView) itemView.findViewById(R.id.movie_text_1);
            mText2 = (TextView) itemView.findViewById(R.id.movie_text_2);
            mText3 = (TextView) itemView.findViewById(R.id.movie_text_3);
            mText4 = (TextView) itemView.findViewById(R.id.movie_text_4);
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
        WatchItem watched = mWatchedMap.get(movie.getId());

        if (!TextUtils.isEmpty(mImageBaseUrl)) {
            Glide.with(context)
                    .load(mImageBaseUrl + movie.getCoverImageUrl())
                    .placeholder(android.R.drawable.ic_dialog_alert)
                    .centerCrop()
                    .crossFade()
                    .into(holder.mImage);
        }
        holder.mText1.setText(movie.getTitle() + " / " + movie.getOriginalTitle());
        holder.mText2.setText(Html.fromHtml(context.getString(R.string.score) + " " + movie.getScore()
                + (watched != null ? "&nbsp&nbsp&nbsp&nbsp<font color=red>" + context.getString(R.string.rating) + " " + watched.getScore() + "</font>" : "")));
        holder.mText3.setText(movie.getReleaseDate());
        holder.mText4.setText(watched != null
                ? Html.fromHtml("<font color=red>" + context.getString(R.string.comment) + " " + watched.getComment() + "</font>")
                : MHUtil.genreIdsToString(movie.getGenreIds(), mGenreMap)
        );
        holder.mMovieOnClickListener.setMovie(movie);
        holder.mIconStarOffOnClickListener.setMovie(movie);
        holder.mIconStarOnOnClickListener.setMovie(movie);
        holder.mIconEditOnClickListener.setMovie(movie);
        holder.mIconWatchedOnClickListener.setMovie(movie);
        updateMovieIcon(holder, movie.getId());
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

    private void updateMovieIcon(ViewHolder holder, String movieId) {
        int vStarOff = View.GONE;
        int vStarOn = View.GONE;
        int vEdit = View.GONE;
        int vWatched = View.GONE;

        if (mToWatchMap.containsKey(movieId)) {
            vStarOn = View.VISIBLE;
            vEdit = View.VISIBLE;
        } else if (mWatchedMap.containsKey(movieId)) {
            vWatched = View.VISIBLE;
        } else {
            vStarOff = View.VISIBLE;
            vEdit = View.VISIBLE;
        }

        holder.mIconStarOff.setVisibility(vStarOff);
        holder.mIconStarOn.setVisibility(vStarOn);
        holder.mIconEdit.setVisibility(vEdit);
        holder.mIconWatched.setVisibility(vWatched);
    }

    private void addToWatchMovie(final Movie movie, final ViewHolder viewHolder) {
        final WatchItem watchItem = MHUtil.createWatchItem(movie);
        List<WatchItem> list = new ArrayList<>();
        list.add(watchItem);
        new AddToWatchListTask(mFragment.getActivity()) {
            @Override
            protected void onDone(List<WatchItem> addedList) {
                mToWatchMap.put(watchItem.getMovieId(), watchItem);
                if (mOnWatchItemChangeListener != null)
                    mOnWatchItemChangeListener.onAddToWatchList(MovieAdapter.this, movie);

                updateMovieIcon(viewHolder, watchItem.getMovieId());
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, list);
    }

    private void deleteToWatchMovie(final Movie movie, final ViewHolder viewHolder) {
        final WatchItem watchItem = mToWatchMap.get(movie.getId());
        if (watchItem == null) {
            return;
        }

        List<WatchItem> list = new ArrayList<>();
        list.add(watchItem);
        new DeleteFromWatchListTask(mFragment.getActivity()) {
            @Override
            protected void onDone(List<WatchItem> deleteList) {
                mToWatchMap.remove(watchItem.getMovieId());
                if (mOnWatchItemChangeListener != null)
                    mOnWatchItemChangeListener.onRemoveFromToWatchList(MovieAdapter.this, movie);

                updateMovieIcon(viewHolder, watchItem.getMovieId());
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, list);
    }

    private void editWatchedMovie(final Movie movie, final ViewHolder viewHolder) {
        final WatchItem watchItem;
        if (mWatchedMap.containsKey(movie.getId())) {
            watchItem = mWatchedMap.get(movie.getId());
        } else if (mToWatchMap.containsKey(movie.getId())) {
            watchItem = mToWatchMap.get(movie.getId());
        } else {
            watchItem = MHUtil.createWatchItem(movie);
        }

        EditWatchedMovieFragment fragment = EditWatchedMovieFragment.newInstance(movie, watchItem);
        fragment.setOnEditDoneListener(new OnEditDoneListener() {
            @Override
            public void onEditDone(String movieId, float score, String comment) {
                Log.d(TAG, "onEditDone() - movieId: " + movieId + ", score: " + score + ", comment: " + comment);

                watchItem.setScore(score);
                watchItem.setComment(comment);
                ArrayList<WatchItem> list = new ArrayList<>();
                list.add(watchItem);
                new AddToWatchedListTask(mFragment.getActivity()) {
                    @Override
                    protected void onDone(List<WatchItem> addedList) {
                        if (mToWatchMap.remove(watchItem.getMovieId()) != null) {
                            if (mOnWatchItemChangeListener != null)
                                mOnWatchItemChangeListener.onRemoveFromToWatchList(MovieAdapter.this, movie);
                        }

                        if (!mWatchedMap.containsKey(watchItem.getMovieId())) {
                            mWatchedMap.put(watchItem.getMovieId(), watchItem);
                            if (mOnWatchItemChangeListener != null)
                                mOnWatchItemChangeListener.onAddToWatchedList(MovieAdapter.this, movie);
                        }

                        updateMovieIcon(viewHolder, watchItem.getMovieId());
                        notifyDataSetChanged();
                    }
                }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, list);
            }
        });
        fragment.show(mFragment.getFragmentManager(), null);
    }

    private void deleteWatchedMovie(final Movie movie, final ViewHolder viewHolder) {
        final WatchItem watchItem = mWatchedMap.get(movie.getId());
        if (watchItem == null) {
            return;
        }

        ArrayList<WatchItem> list = new ArrayList<>();
        list.add(watchItem);
        new DeleteFromWatchedListTask(mFragment.getActivity()) {
            @Override
            protected void onDone(List<WatchItem> deletedList) {
                mWatchedMap.remove(watchItem.getMovieId());
                if (mOnWatchItemChangeListener != null)
                    mOnWatchItemChangeListener.onRemoveFromWatchedList(MovieAdapter.this, movie);

                updateMovieIcon(viewHolder, watchItem.getMovieId());
                notifyDataSetChanged();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, list);
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

    private class IconStarOffOnClickListener implements OnClickListener {
        private ViewHolder mViewHolder;
        private Movie mMovie;

        public IconStarOffOnClickListener(ViewHolder viewHolder) {
            mViewHolder = viewHolder;
        }

        public  void setMovie(Movie movie) {
            mMovie = movie;
        }

        @Override
        public void onClick(View v) {
            addToWatchMovie(mMovie, mViewHolder);
        }
    }

    private class IconStarOnOnClickListener implements OnClickListener {
        private ViewHolder mViewHolder;
        private Movie mMovie;

        public IconStarOnOnClickListener(ViewHolder viewHolder) {
            mViewHolder = viewHolder;
        }

        public  void setMovie(Movie movie) {
            mMovie = movie;
        }

        @Override
        public void onClick(View v) {
            deleteToWatchMovie(mMovie, mViewHolder);
        }
    }

    private class IconEditOnClickListener implements OnClickListener {
        private ViewHolder mViewHolder;
        private Movie mMovie;

        public IconEditOnClickListener(ViewHolder viewHolder) {
            mViewHolder = viewHolder;
        }

        public  void setMovie(Movie movie) {
            mMovie = movie;
        }

        @Override
        public void onClick(View v) {
            editWatchedMovie(mMovie, mViewHolder);
        }
    }

    private class IconWatchedOnClickListener implements OnClickListener {
        private ViewHolder mViewHolder;
        private Movie mMovie;

        public IconWatchedOnClickListener(ViewHolder viewHolder) {
            mViewHolder = viewHolder;
        }

        public  void setMovie(Movie movie) {
            mMovie = movie;
        }

        @Override
        public void onClick(View v) {
            View view = LayoutInflater.from(mFragment.getActivity()).inflate(R.layout.watched_popup, null);

            DisplayMetrics metrics = mFragment.getActivity().getResources().getDisplayMetrics();
            view.measure(MeasureSpec.makeMeasureSpec(metrics.widthPixels, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(metrics.heightPixels, MeasureSpec.AT_MOST));

            final PopupWindow popup = new PopupWindow(mFragment.getActivity());
            popup.setContentView(view);
            popup.setWidth(metrics.widthPixels / 3);
            popup.setHeight(view.getMeasuredHeight());
            popup.setFocusable(true);
            popup.setBackgroundDrawable(null);
            popup.showAsDropDown(v);

            View edit = view.findViewById(R.id.popup_edit);
            edit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editWatchedMovie(mMovie, mViewHolder);
                    popup.dismiss();
                }
            });
            View delete = view.findViewById(R.id.popup_delete);
            delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteWatchedMovie(mMovie, mViewHolder);
                    popup.dismiss();
                }
            });
        }
    }

    public interface OnWatchItemChangeListener {
        void onAddToWatchList(MovieAdapter adapter, Movie movie);
        void onAddToWatchedList(MovieAdapter adapter, Movie movie);
        void onRemoveFromToWatchList(MovieAdapter adapter, Movie movie);
        void onRemoveFromWatchedList(MovieAdapter adapter, Movie movie);
    }
}
