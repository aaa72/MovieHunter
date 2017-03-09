package com.leo.moviehunter.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leo.moviehunter.R;
import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.task.DiscoverMoreMovieTask;
import com.leo.moviehunter.task.GetWatchListTask;
import com.leo.moviehunter.tmdb.TMDBConstants;
import com.leo.moviehunter.tmdb.response.DiscoverMovie;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.task.GetImageBaseUrlTask;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHConstants;
import com.leo.moviehunter.util.MovieResultAdapter;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GenreMovieFragment extends Fragment {
    private static final String TAG = "GenreMovieFragment";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MovieResultAdapter mAdapter;
    private int mGenreId = -1;
    private int mNextMoviePage = 1;
    private int mTotalPage = 0;
    private boolean mIsLoadingMovie = false;
    private List<WatchItem> mWatchList;
    private DiscoverMoreMovieTask mDiscoverMoreTask;

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
        mAdapter = new MovieResultAdapter(this);
        mAdapter.setGetMoreMovieClickListenter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMoreMovie();
            }
        });

        // load image base url
        new GetImageBaseUrlTask() {
            @Override
            public void onGetUrl(String url) {
                mAdapter.setImageBaseUrl(url);
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        // get watch list
        new GetWatchListTask(getActivity()) {
            @Override
            public void onGetWatchList(List<WatchItem> list) {
                mWatchList = list;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        // get movie list
        getMoreMovie();
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
        mRecyclerView.setAdapter(mAdapter);

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

        new DiscoverMoreMovieTask(mGenreId) {
            @Override
            public void onGetDiscoverMovie(DiscoverMovie discoverMovie) {
                if (discoverMovie != null) {
                    if (mTotalPage <= 0) {
                        Log.d(TAG, "total pages: " + discoverMovie.total_pages
                                + ", total results: " + discoverMovie.total_results);
                        mTotalPage = discoverMovie.total_pages;
                    }
                    Log.d(TAG, "current page: " + discoverMovie.page + ", page size: " + discoverMovie.results.length);

                    mAdapter.addMovies(discoverMovie.results, discoverMovie.page < discoverMovie.total_pages);
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
}
