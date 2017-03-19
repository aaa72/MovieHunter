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
import com.leo.moviehunter.task.GetMovieDetailTask;
import com.leo.moviehunter.task.GetWatchedListTask;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHUtil;
import com.leo.moviehunter.widget.MovieAdapter;

import java.util.ArrayList;
import java.util.List;

public class WatchedListFragment extends Fragment {
    private static final String TAG = "WatchedListFragment";

    private final List<Movie> mMovieList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MovieAdapter mAdapter;

    public static WatchedListFragment newInstance() {
        WatchedListFragment fragment = new WatchedListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new MovieAdapter(this);
        mAdapter.setMovieList(mMovieList);

        new GetWatchedListTask(getActivity()) {
            @Override
            protected void onGetWatchedList(final List<WatchItem> watchedList) {
                if (watchedList == null) {
                    return;
                }
                
                for (WatchItem watchItem : watchedList) {
                    new GetMovieDetailTask(getActivity(), watchItem.getMovieId()) {
                        @Override
                        protected void onGetMovie(Movie movie) {
                            mMovieList.add(movie);
                            mAdapter.notifyDataSetChanged();
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
        MHUtil.setActivityToolbarSubTitle(getActivity(), getString(R.string.watch_history));
    }
}
