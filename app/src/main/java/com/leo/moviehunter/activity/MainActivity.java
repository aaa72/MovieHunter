package com.leo.moviehunter.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.leo.moviehunter.R;
import com.leo.moviehunter.fragment.GenreMainFragment;
import com.leo.moviehunter.fragment.NowPlayingFragment;
import com.leo.moviehunter.fragment.SearchMovieFragment;
import com.leo.moviehunter.fragment.WatchListFragment;
import com.leo.moviehunter.fragment.WatchedListFragment;
import com.leo.moviehunter.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int DRAWER_GRAVITY = Gravity.START;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Toolbar mToolBar;
    private SearchView mSearchView;
    private boolean mLeaveFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,
                new String[] {
                        getString(R.string.genre),              // 0
                        getString(R.string.now_playing),        // 1
                        getString(R.string.search_movie),       // 2
                        getString(R.string.watch_list),         // 3
                        getString(R.string.watch_history),      // 4
                }
                ));
        mDrawerList.setOnItemClickListener(mDrawerItemClickListener);

        mToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(android.R.drawable.btn_dialog);
//        mToolBar.setLogo(R.mipmap.ic_launcher);
        mToolBar.setOnMenuItemClickListener(mOnMenuItemClick);
        mToolBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(DRAWER_GRAVITY);
            }
        });
        mToolBar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        clearAllFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, WatchListFragment.newInstance());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem menuSearchItem = menu.findItem(R.id.action_search);

        mSearchView = (SearchView) menuSearchItem.getActionView();
        mSearchView.setIconifiedByDefault(true);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            public boolean onQueryTextSubmit(String query) {
                clearAllFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, SearchMovieFragment.newInstance(query));
                transaction.commit();

                mSearchView.onActionViewCollapsed();
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
            if (mLeaveFlag) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, R.string.back_again, Toast.LENGTH_SHORT).show();
                mLeaveFlag = true;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLeaveFlag = false;
                    }
                }, 5000);
            }
        }
    }

    @SuppressWarnings("unused") // method call by reflection
    public void setSubTitle(String title) {
        if (mToolBar != null) {
            mToolBar.setSubtitle(title);
        }
    }

    private final Toolbar.OnMenuItemClickListener mOnMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
            }
            return true;
        }
    };

    private final OnItemClickListener mDrawerItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick() - position: " + position);
            switch(position) {
                case 0: {
                    clearAllFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, GenreMainFragment.newInstance());
                    transaction.commit();
                }
                break;

                case 1: {
                    clearAllFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, NowPlayingFragment.newInstance());
                    transaction.commit();
                }
                break;

                case 2: {
                    mSearchView.setIconified(false);
                }
                break;

                case 3: {
                    clearAllFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, WatchListFragment.newInstance());
                    transaction.commit();
                }
                break;

                case 4: {
                    clearAllFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, WatchedListFragment.newInstance());
                    transaction.commit();
                }
                break;
            }
            mDrawerLayout.closeDrawer(DRAWER_GRAVITY);
        }
    };

    private void clearAllFragment() {
        FragmentManager fm = getFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }
}
