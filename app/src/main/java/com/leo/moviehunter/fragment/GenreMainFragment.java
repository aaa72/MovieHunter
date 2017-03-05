package com.leo.moviehunter.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leo.moviehunter.R;
import com.leo.moviehunter.data.CollectionItem;
import com.leo.moviehunter.tmdb.response.Genre;
import com.leo.moviehunter.tmdb.response.GetGenres;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.GetGenreCoverUrlTask;
import com.leo.moviehunter.util.GetImgeBaseUrlTask;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MyCollectionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreMainFragment extends Fragment {
    private static final String TAG = "GenreMainFragment";

    private RecyclerView mRecyclerView;
    private GenresAdapter mGenresAdapter;
    private GridLayoutManager mLayoutManager;
    private Genre[] mGenres;
    private final HashMap<Integer, String> mCoverUrlMap = new HashMap();
    private String mImageBaseUrl;

    public static GenreMainFragment newInstance() {
        GenreMainFragment fragment = new GenreMainFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGenresAdapter = new GenresAdapter();

        new GetImgeBaseUrlTask() {
            @Override
            public void onGetUrl(String url) {
                mImageBaseUrl = url;

                getGenres();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        List<CollectionItem> list = MyCollectionHelper.getMyCollection(getActivity());
        if (list != null) {
            for (CollectionItem item : list) {
                Log.d(TAG, "item = " + item.getMovieId());
            }
        }

        List<CollectionItem> list2 = new ArrayList<>();
        CollectionItem item = new CollectionItem();
        item.setMovieId(597);
        list2.add(item);
        item = new CollectionItem();
        item.setMovieId(49047);
        list2.add(item);

        MyCollectionHelper.addMyCollection(getActivity(), list2);
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
        mRecyclerView.setAdapter(mGenresAdapter);

        return root;
    }

    private void getGenres() {
        TMDBServiceManager.getTMDBService().getGenres().enqueue(new Callback<GetGenres>() {
            @Override
            public void onResponse(Call<GetGenres> call, Response<GetGenres> response) {
                if (response.isSuccessful()) {
                    GetGenres body = response.body();
                    if (body != null) {
                        Log.w(TAG, "getGenres success");
                        mGenres = body.genres;
                        mGenresAdapter.notifyDataSetChanged();

                        getGenreCover();
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

    private void getGenreCover() {
        if (mGenres == null) {
            return;
        }

        for (final Genre genre : mGenres) {
            new GetGenreCoverUrlTask(getActivity(), genre.id) {
                @Override
                public void onGetUrl(int genreId, String url) {
                    mCoverUrlMap.put(genreId, url);
                    mGenresAdapter.notifyDataSetChanged();
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager.setSpanCount(4);
        } else {
            mLayoutManager.setSpanCount(2);
        }
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
                if (!TextUtils.isEmpty(mImageBaseUrl) && mCoverUrlMap.containsKey(mGenres[position].id)) {
                    Glide.with(getActivity())
                            .load(mImageBaseUrl + mCoverUrlMap.get(mGenres[position].id))
                            .placeholder(android.R.drawable.ic_dialog_alert)
                            .centerCrop()
                            .crossFade()
                            .into(holder.mImageView);
                }
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
