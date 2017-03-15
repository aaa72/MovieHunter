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
import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.task.GetImageBaseUrlTask;
import com.leo.moviehunter.task.GetNowPlayingTask;
import com.leo.moviehunter.task.GetToWatchListTask;
import com.leo.moviehunter.util.CommonUtil;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.widget.Application;
import com.leo.moviehunter.widget.MovieAdapter;

import java.util.List;

public class NowPlayingFragment extends Fragment {
    private static final String TAG = "NowPlayingFragment";

    private Tracker mTracker;
    private RecyclerView mRecyclerView;
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

        // load image base url
        new GetImageBaseUrlTask() {
            @Override
            public void onGetUrl(String url) {
                mAdapter.setImageBaseUrl(url);
                getMoreMovie();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        new GetToWatchListTask(getActivity()) {
            @Override
            protected void onGetToWatchList(List<WatchItem> toWatchList) {
                mAdapter.setWatchList(toWatchList);
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View root = inflater.inflate(R.layout.fragment_now_playing, container, false);

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

    @Override
    public void onStart() {
        super.onStart();
        CommonUtil.setActivityToolbarSubTitle(getActivity(), getString(R.string.now_playing));
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

                mAdapter.addMovies(movies, page < totalPages);
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
