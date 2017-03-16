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
import com.leo.moviehunter.data.Genre;
import com.leo.moviehunter.task.GetGenresTask;
import com.leo.moviehunter.task.GetImageBaseUrlTask;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHUtil;

public class GenreMainFragment extends Fragment {
    private static final String TAG = "GenreMainFragment";

    private RecyclerView mRecyclerView;
    private GenresAdapter mGenresAdapter;
    private GridLayoutManager mLayoutManager;
    private Genre[] mGenres;
    private String mImageBaseUrl;

    public static GenreMainFragment newInstance() {
        GenreMainFragment fragment = new GenreMainFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGenresAdapter = new GenresAdapter();

        new GetImageBaseUrlTask() {
            @Override
            public void onGetUrl(String url) {
                mImageBaseUrl = url;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);


        new GetGenresTask(getActivity()) {
            @Override
            protected void getGenres(Genre[] genres) {
                mGenres = genres;
                mGenresAdapter.notifyDataSetChanged();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager.setSpanCount(4);
        } else {
            mLayoutManager.setSpanCount(2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        MHUtil.setActivityToolbarSubTitle(getActivity(), getString(R.string.genre));
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
                holder.mTextView.setText(mGenres[position].getName());
                holder.mGenreOnClickListener.setGenreId(mGenres[position].getId());
                if (!TextUtils.isEmpty(mImageBaseUrl)) {
                    Glide.with(getActivity())
                            .load(mImageBaseUrl + mGenres[position].getCoverImageUrl())
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
            private String mGenreId;

            public  void setGenreId(String id) {
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
