package com.leo.moviehunter.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leo.moviehunter.R;
import com.leo.moviehunter.tmdb.TMDBConstants;
import com.leo.moviehunter.tmdb.response.DiscoverMovie;
import com.leo.moviehunter.tmdb.service.TMDBConfiguration;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GenreActivity extends AppCompatActivity {
    private static final String TAG = "GenreActivity";

    private RecyclerView mRecyclerView;
    private DiscoverMovieAdapter mMovieAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mGenreId = -1;
    private int mNextMoviePage = 1;
    private int mTotalPage = 0;
    private boolean mIsLoadingMovie = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mGenreId = getGenreId();

        setContentView(R.layout.activity_genre);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mMovieAdapter = new DiscoverMovieAdapter();
        mRecyclerView.setAdapter(mMovieAdapter);
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

        // load image base url
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return TMDBConfiguration.getImageBaseUrl();
            }
            @Override
            protected void onPostExecute(String imageBaseUrl) {
                mMovieAdapter.setImageBaseUrl(imageBaseUrl);
                getMoreMovie();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private int getGenreId() {
        return getIntent() != null ? getIntent().getIntExtra(MHConstants.BUNDLE_KEY_GENRE_ID, -1) : -1;
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
                Call<DiscoverMovie> call = TMDBServiceManager.getTMDBService().discoverMovie(null
                        , TMDBConstants.SortBy.popularity.desc()
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
                    mTotalPage = discoverMovie.total_pages;
                    mMovieAdapter.addMovies(discoverMovie.results);
                }
                mIsLoadingMovie = false;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, mNextMoviePage++);
    }

    public static class DiscoverMovieAdapter extends RecyclerView.Adapter<DiscoverMovieAdapter.ViewHolder> {

        private List<DiscoverMovie.Result> mMovieList = new ArrayList<>();
        private String mImageBaseUrl;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private ViewGroup mContainer;
            private ImageView mImage;
            private TextView mText1;
            private TextView mText2;
            private TextView mText3;

            public ViewHolder(View itemView) {
                super(itemView);
                mContainer = (ViewGroup) itemView;
                mImage = (ImageView) itemView.findViewById(R.id.movie_image);
                mText1 = (TextView) itemView.findViewById(R.id.movie_text_1);
                mText2 = (TextView) itemView.findViewById(R.id.movie_text_2);
                mText3 = (TextView) itemView.findViewById(R.id.movie_text_3);
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
            Context context = holder.mContainer.getContext();
            DiscoverMovie.Result movie = mMovieList.get(position);
            if (!TextUtils.isEmpty(mImageBaseUrl)) {
                Glide.with(context)
                        .load(mImageBaseUrl + movie.backdrop_path)
                        .placeholder(android.R.drawable.ic_dialog_alert)
                        .centerCrop()
                        .crossFade()
                        .into(holder.mImage);
            }
            holder.mText1.setText(movie.title + " / " + movie.original_title);
            holder.mText2.setText(context.getString(R.string.score) + ": " + movie.vote_average);
            holder.mText3.setText(movie.release_date);
        }

        @Override
        public int getItemCount() {
            return mMovieList.size();
        }

        public void addMovies(DiscoverMovie.Result[] movies) {
            mMovieList.addAll(Arrays.asList(movies));
            notifyDataSetChanged();
        }

        public void setImageBaseUrl(String imageBaseUrl){
            mImageBaseUrl = imageBaseUrl;
            notifyDataSetChanged();
        }
    }
}
