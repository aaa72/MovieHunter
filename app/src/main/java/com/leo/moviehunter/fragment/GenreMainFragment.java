package com.leo.moviehunter.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreMainFragment extends Fragment {
    private static final String TAG = "GenreMainFragment";

    private RecyclerView mRecyclerView;
    private GenresAdapter mGenresAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Genre[] mGenres;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TMDBServiceManager.getTMDBService().getGenres().enqueue(new Callback<GetGenres>() {
            @Override
            public void onResponse(Call<GetGenres> call, Response<GetGenres> response) {
                if (response.isSuccessful()) {
                    GetGenres body = response.body();
                    if (body != null) {
                        Log.w(TAG, "getGenres success");
                        mGenres = body.genres;
                        if (mGenresAdapter != null) {
                            mGenresAdapter.notifyDataSetChanged();
                        }
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View root = inflater.inflate(R.layout.frament_genre_main, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mGenresAdapter = new GenresAdapter();
        mRecyclerView.setAdapter(mGenresAdapter);

        return root;
    }

    private class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ViewGroup mContainerView;
            private ImageView mImageView;
            private TextView mTextView;
            private GenreOnClickListener mGenreOnClickListener;

            public ViewHolder(ViewGroup container) {
                super(container);
                mContainerView = container;
                mImageView = (ImageView) container.findViewById(R.id.genre_image);
                mTextView = (TextView) container.findViewById(R.id.genre_text);

                mGenreOnClickListener = new GenreOnClickListener();
                mContainerView.setOnClickListener(mGenreOnClickListener);
            }
        }

        @Override
        public GenresAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

        private class GenreOnClickListener implements OnClickListener {
            private int mGenreId;

            public  void setGenreId(int id) {
                mGenreId = id;
            }

            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, GenreMovieFragment.newInstance(mGenreId));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }
}
