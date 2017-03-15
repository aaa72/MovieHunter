package com.leo.moviehunter.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leo.moviehunter.R;
import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.task.GetImageBaseUrlTask;
import com.leo.moviehunter.task.GetMovieDetailTask;
import com.leo.moviehunter.task.GetToWatchListTask;
import com.leo.moviehunter.util.CommonUtil;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.widget.MovieAdapter;

import java.util.ArrayList;
import java.util.List;

public class WatchListFragment extends Fragment {
    private static final String TAG = "WatchListFragment";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MovieAdapter mAdapter;

    public static WatchListFragment newInstance() {
        WatchListFragment fragment = new WatchListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new MovieAdapter(this);

        // load image base url
        new GetImageBaseUrlTask() {
            @Override
            public void onGetUrl(String url) {
                mAdapter.setImageBaseUrl(url);
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        new GetToWatchListTask(getActivity()) {
            private final int FREQUENCY = 3;
            private ArrayList<Movie> mMovieList = new ArrayList<>();
            private int mCount = 0;
            @Override
            protected void onGetToWatchList(final List<WatchItem> toWatchList) {
                if (toWatchList == null) {
                    return;
                }

                mAdapter.setWatchList(toWatchList);

                for (WatchItem watchItem : toWatchList) {
                    new GetMovieDetailTask(getActivity(), watchItem.getMovieId()) {
                        @Override
                        protected void onGetMovie(Movie movie) {
                            mMovieList.add(movie);
                            ++mCount;
                            if (mCount == toWatchList.size()) {
                                pushToAdapter();
                            } else if (mCount % FREQUENCY == 0) {
                                pushToAdapter();
                            }
                        }

                        private void pushToAdapter() {
                            mAdapter.addMovies(mMovieList.toArray(new Movie[mMovieList.size()]), false);
                            mMovieList.clear();
                        }
                    }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View root = inflater.inflate(R.layout.fragment_watch_list, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        CommonUtil.setActivityToolbarSubTitle(getActivity(), getString(R.string.watch_list));
    }
}
