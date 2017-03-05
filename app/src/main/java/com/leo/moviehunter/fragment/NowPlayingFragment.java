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
import com.leo.moviehunter.tmdb.response.NowPlaying;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.GetImgeBaseUrlTask;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MovieResultAdapter;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class NowPlayingFragment extends Fragment {
    private static final String TAG = "NowPlayingFragment";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MovieResultAdapter mAdapter;
    private int mNextMoviePage = 1;
    private int mTotalPage = 0;
    private boolean mIsLoadingMovie = false;

    public static NowPlayingFragment newInstance() {
        NowPlayingFragment fragment = new NowPlayingFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new MovieResultAdapter(this);
        mAdapter.setGetMoreMovieClickListenter(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getMoreMovie();
            }
        });

        // load image base url
        new GetImgeBaseUrlTask() {
            @Override
            public void onGetUrl(String url) {
                mAdapter.setImageBaseUrl(url);
                getMoreMovie();
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

    @UiThread
    private void getMoreMovie() {
        if (mIsLoadingMovie) {
            return;
        }

        if (mTotalPage > 0 && mNextMoviePage >= mTotalPage) {
            return;
        }
        mIsLoadingMovie = true;

        new AsyncTask<Integer, Void, NowPlaying>() {
            @Override
            protected NowPlaying doInBackground(Integer... params) {
                Call<NowPlaying> call = TMDBServiceManager.getTMDBService().getNowPlaying(params[0]);
                try {
                    Response<NowPlaying> response = call.execute();
                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        Log.w(TAG, "getNowPlaying fail by code: " + response.code());
                    }
                } catch (IOException e) {
                    Log.w(TAG, "getNowPlaying fail", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(NowPlaying nowPlaying) {
                if (nowPlaying != null) {
                    if (mTotalPage <= 0) {
                        Log.d(TAG, "total pages: " + nowPlaying.total_pages
                                + ", total results: " + nowPlaying.total_results);
                        mTotalPage = nowPlaying.total_pages;
                    }
                    Log.d(TAG, "current page: " + nowPlaying.page + ", page size: " + nowPlaying.results.length);

                    mAdapter.addMovies(nowPlaying.results, nowPlaying.page < nowPlaying.total_pages);
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
