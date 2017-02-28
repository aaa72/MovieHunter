package com.leo.moviehunter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leo.moviehunter.R;
import com.leo.moviehunter.tmdb.response.Genre;
import com.leo.moviehunter.tmdb.response.GetGenres;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private GenresAdapter mGenresAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mGenresAdapter = new GenresAdapter();
        mRecyclerView.setAdapter(mGenresAdapter);

        TMDBServiceManager.getTMDBService().getGenres().enqueue(new Callback<GetGenres>() {
            @Override
            public void onResponse(Call<GetGenres> call, Response<GetGenres> response) {
                if (response.isSuccessful()) {
                    GetGenres body = response.body();
                    if (body != null) {
                        Log.w(TAG, "getGenres success");
                        mGenresAdapter.setGenres(body.genres);
                    } else {
                        Log.w(TAG, "getGenres fail by response.body()");
                    }
                } else {
                    Log.w(TAG, "getGenres fail by code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetGenres> call, Throwable t) {
                Log.w(TAG, "getGenres onFailure", t);
            }
        });
    }

    public static class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.ViewHolder> {
        private Genre[] mGenres;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private ViewGroup mContainerView;
            private ImageView mImageView;
            private TextView mTextView;
            private GenreOnClickListener mGenreOnClickListener;

            public ViewHolder(ViewGroup container) {
                super(container);
                mContainerView = container;
                mImageView = (ImageView) container.findViewById(R.id.genre_image);
                mTextView = (TextView) container.findViewById(R.id.genre_text);

                mGenreOnClickListener = new GenreOnClickListener(container.getContext());
                mContainerView.setOnClickListener(mGenreOnClickListener);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflate = LayoutInflater.from(parent.getContext());
            ViewGroup layout = (ViewGroup) inflate.inflate(R.layout.genre_grid_item, null);
            return new ViewHolder(layout);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (mGenres != null) {
                holder.mTextView.setText(mGenres[position].name);
                holder.mGenreOnClickListener.setGenreId(mGenres[position].id);
            }
        }

        @Override
        public int getItemCount() {
            return mGenres != null ? mGenres.length : 0;
        }

        public void setGenres(Genre[] genres) {
            mGenres = genres;
            notifyDataSetChanged();
        }

        private static class GenreOnClickListener implements OnClickListener {
            private int mGenreId;
            private final Intent mLaunchIntent;

            public GenreOnClickListener(Context context) {
                mLaunchIntent = new Intent(context, GenreActivity.class);
            }

            public  void setGenreId(int id) {
                mGenreId = id;
            }

            @Override
            public void onClick(View v) {
                mLaunchIntent.putExtra(MHConstants.BUNDLE_KEY_GENRE_ID, mGenreId);
                v.getContext().startActivity(mLaunchIntent);
            }
        }
    }
}
