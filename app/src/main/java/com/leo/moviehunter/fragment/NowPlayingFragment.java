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
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.leo.moviehunter.R;
import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.task.GetNowPlayingTask;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHUtil;
import com.leo.moviehunter.widget.Application;
import com.leo.moviehunter.widget.MovieAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NowPlayingFragment extends Fragment {
    private static final String TAG = "NowPlayingFragment";

    private final List<Movie> mMovieList = new ArrayList<>();
    private Tracker mTracker;
    @BindView(R.id.recycler) RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MovieAdapter mAdapter;
    private int mNextMoviePage = 1;
    private int mTotalPages = 0;
    private boolean mIsLoadingMovie = false;

    public static NowPlayingFragment newInstance() {
        NowPlayingFragment fragment = new NowPlayingFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application application = (Application) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        mAdapter = new MovieAdapter(this);
        mAdapter.setMovieList(mMovieList);
        mAdapter.setGetMoreMovieClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Click")
                        .setAction("Get More Movie")
                        .build());

                getMoreMovie();
            }
        });

        getMoreMovie();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View root = inflater.inflate(R.layout.fragment_now_playing, container, false);
        ButterKnife.bind(this, root);

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

    @Override
    public void onStart() {
        super.onStart();
        MHUtil.setActivityToolbarSubTitle(getActivity(), getString(R.string.now_playing));
    }

    @UiThread
    private void getMoreMovie() {
        if (mIsLoadingMovie) {
            return;
        }

        if (mTotalPages > 0 && mNextMoviePage >= mTotalPages) {
            return;
        }
        mIsLoadingMovie = true;

        new GetNowPlayingTask() {
            @Override
            public void onGetNowPlaying(Movie[] movies, int totalMovies, int page, int totalPages) {
                if (mTotalPages != totalPages) {
                    Log.d(TAG, "totalPages: " + totalPages + ", totalMovies: " + totalMovies);
                    mTotalPages = totalPages;
                }
                Log.d(TAG, "current page: " + page + ", page size: " + movies.length);

                mAdapter.showHasMoreButton(page < totalPages);
                mMovieList.addAll(Arrays.asList(movies));
                mAdapter.notifyDataSetChanged();
                mIsLoadingMovie = false;
            }

            @Override
            protected void onFail(int code, String msg) {
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
