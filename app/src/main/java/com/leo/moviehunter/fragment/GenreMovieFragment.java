package com.leo.moviehunter.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
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
import com.leo.moviehunter.tmdb.TMDBConstants;
import com.leo.moviehunter.tmdb.response.DiscoverMovie;
import com.leo.moviehunter.tmdb.response.MovieResult;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.GetImgeBaseUrlTask;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GenreMovieFragment extends Fragment {
    private static final String TAG = "GenreMovieFragment";

    private RecyclerView mRecyclerView;
    private DiscoverMovieAdapter mMovieAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mGenreId = -1;
    private int mNextMoviePage = 1;
    private int mTotalPage = 0;
    private boolean mIsLoadingMovie = false;
    private String mImageBaseUrl;
    private final List<MovieResult> mMovieList = new ArrayList<>();

    public static GenreMovieFragment newInstance(int genreId) {
        GenreMovieFragment fragment = new GenreMovieFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MHConstants.BUNDLE_KEY_GENRE_ID, genreId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGenreId = getGenreId();

        // load image base url
        new GetImgeBaseUrlTask() {
            @Override
            public void onGetUrl(String url) {
                mImageBaseUrl = url;
                getMoreMovie();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View root = inflater.inflate(R.layout.fragment_genre_movie, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mMovieAdapter = new DiscoverMovieAdapter();
        mRecyclerView.setAdapter(mMovieAdapter);

        // scroll to end auto get more movie
        if (false) { // disabled
            scrollToEndAutoGetMovie();
        }

        return root;
    }

    private int getGenreId() {
        return getArguments() != null ? getArguments().getInt(MHConstants.BUNDLE_KEY_GENRE_ID) : -1;
    }

    @UiThread
    private void getMoreMovie() {
        if (mIsLoadingMovie) {
            return;
        }

        if (mTotalPage > 0 && mNextMoviePage >= mTotalPage) {
            return;
        }
        mIsLoadingMovie = true;

        new AsyncTask<Integer, Void, DiscoverMovie>() {
            @Override
            protected DiscoverMovie doInBackground(Integer... params) {
                Call<DiscoverMovie> call = TMDBServiceManager.getTMDBService().discoverMovie(
                        TMDBConstants.SortBy.popularity.desc()
                        , true
                        , params[0]
                        , String.valueOf(mGenreId)
                );
                try {
                    Response<DiscoverMovie> response = call.execute();
                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        Log.w(TAG, "discoverMovie fail by code: " + response.code());
                    }
                } catch (IOException e) {
                    Log.w(TAG, "discoverMovie fail", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(DiscoverMovie discoverMovie) {
                if (discoverMovie != null) {
                    if (mTotalPage <= 0) {
                        Log.d(TAG, "total pages: " + discoverMovie.total_pages
                                + ", total results: + " + discoverMovie.total_results);
                        mTotalPage = discoverMovie.total_pages;
                    }
                    Log.d(TAG, "current page: " + discoverMovie.page + ", page size: " + discoverMovie.results.length);

                    mMovieList.addAll(Arrays.asList(discoverMovie.results));

                    if (mMovieAdapter != null) {
                        mMovieAdapter.notifyDataSetChanged();
                    }
                }
                mIsLoadingMovie = false;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, mNextMoviePage++);
    }

    private void scrollToEndAutoGetMovie() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    Log.d(TAG, "scroll to bottom");
                    getMoreMovie();
                }
            }
        });
    }

    private class DiscoverMovieAdapter extends RecyclerView.Adapter<DiscoverMovieAdapter.ViewHolder> {
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
                        getMoreMovie();
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
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, MovieDetailFragment.newInstance(mMovieId));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }

        @Override
        public DiscoverMovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ViewGroup container = (ViewGroup) inflater.inflate(R.layout.movie_list_item, null);
            return new DiscoverMovieAdapter.ViewHolder(container);
        }

        @Override
        public void onBindViewHolder(DiscoverMovieAdapter.ViewHolder holder, int position) {
            if (!isAllPagesDownloaded() && position == getItemCount() - 1 /* last item */) {
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
            if (isAllPagesDownloaded()) {
                return mMovieList.size();
            } else if (mMovieList.size() > 0) {
                return mMovieList.size() + 1;
            } else {
                return 0;
            }
        }

        private boolean isAllPagesDownloaded() {
            return mTotalPage > 0 && mNextMoviePage == mTotalPage;
        }
    }
}
