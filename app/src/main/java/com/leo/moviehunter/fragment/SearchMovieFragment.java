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

import com.leo.moviehunter.R;
import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.task.GetImageBaseUrlTask;
import com.leo.moviehunter.task.GetWatchListTask;
import com.leo.moviehunter.task.SearchMovieTask;
import com.leo.moviehunter.util.CommonUtil;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHConstants;
import com.leo.moviehunter.widget.MovieAdapter;

import java.util.List;

public class SearchMovieFragment extends Fragment {
    private static final String TAG = "SearchMovieFragment";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MovieAdapter mAdapter;
    private int mNextMoviePage = 1;
    private int mTotalPages = 0;
    private boolean mIsLoadingMovie = false;
    private String mSearchString;

    public static SearchMovieFragment newInstance(String searchString) {
        SearchMovieFragment fragment = new SearchMovieFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MHConstants.BUNDLE_KEY_SEARCH_STRING, searchString);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearchString = getSearchString();

        mAdapter = new MovieAdapter(this);
        mAdapter.setGetMoreMovieClickListener(new OnClickListener() {
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
                getMoreMovie();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        new GetWatchListTask(getActivity()) {
            @Override
            protected void onGetWatchList(List<WatchItem> watchList) {
                mAdapter.setWatchList(watchList);
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
        CommonUtil.setActivityToolbarSubTitle(getActivity(), getString(R.string.search_movie) + " - " + mSearchString);
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

        SearchMovieTask task = new SearchMovieTask() {
            @Override
            public void onSearchMovie(String searchString, Movie[] movies, int totalMovies, int page, int totalPages) {
                if (!searchString.equals(mSearchString)) {
                    return;
                }

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
        };
        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, task.new Params(mSearchString, mNextMoviePage++));
    }

    private String getSearchString() {
        return getArguments() != null ? getArguments().getString(MHConstants.BUNDLE_KEY_SEARCH_STRING) : null;
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
