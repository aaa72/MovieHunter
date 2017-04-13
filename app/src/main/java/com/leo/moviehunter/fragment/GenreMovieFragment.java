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
import com.leo.moviehunter.data.Genre;
import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.task.DiscoverMoreMovieTask;
import com.leo.moviehunter.task.GetGenresTask;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHConstants;
import com.leo.moviehunter.util.MHUtil;
import com.leo.moviehunter.widget.MovieAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenreMovieFragment extends Fragment {
    private static final String TAG = "GenreMovieFragment";

    private final List<Movie> mMovieList = new ArrayList<>();
    @BindView(R.id.recycler) RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MovieAdapter mAdapter;
    private String mGenreId;
    private String mGenreName;
    private int mNextMoviePage = 1;
    private int mTotalPages = 0;
    private boolean mIsLoadingMovie = false;

    public static GenreMovieFragment newInstance(String genreId) {
        GenreMovieFragment fragment = new GenreMovieFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MHConstants.BUNDLE_KEY_GENRE_ID, genreId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGenreId = getGenreId();
        mAdapter = new MovieAdapter(this);
        mAdapter.setMovieList(mMovieList);
        mAdapter.setGetMoreMovieClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMoreMovie();
            }
        });

        new GetGenresTask(getActivity()) {
            @Override
            protected void getGenres(Genre[] genres) {
                if (genres == null) {
                    return;
                }
                for (Genre genre : genres) {
                    if (mGenreId.equals(genre.getId())) {
                        mGenreName = genre.getName();
                        setSubTitle();
                        break;
                    }
                }
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
        setSubTitle();
    }

    private void setSubTitle() {
        if (mGenreName != null) {
            MHUtil.setActivityToolbarSubTitle(getActivity(), mGenreName);
        }
    }

    private String getGenreId() {
        return getArguments() != null ? getArguments().getString(MHConstants.BUNDLE_KEY_GENRE_ID) : null;
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

        new DiscoverMoreMovieTask(mGenreId) {
            @Override
            public void onGetDiscoverMovie(Movie[] movies, int totalMovies, int page, int totalPages) {
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
