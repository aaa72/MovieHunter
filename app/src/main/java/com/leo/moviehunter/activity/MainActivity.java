package com.leo.moviehunter.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.leo.moviehunter.R;
import com.leo.moviehunter.fragment.GenreMainFragment;
import com.leo.moviehunter.fragment.NowPlayingFragment;
import com.leo.moviehunter.fragment.SearchMovieFragment;
import com.leo.moviehunter.fragment.WatchListFragment;
import com.leo.moviehunter.fragment.WatchedListFragment;
import com.leo.moviehunter.util.CommonUtil;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHConstants;
import com.leo.moviehunter.widget.DrawerAdapter;
import com.leo.moviehunter.widget.DrawerAdapter.DrawerItem;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int DRAWER_GRAVITY = Gravity.START;
    private static final int RC_SIGN_IN = 1;

    @BindView(R.id.drawer) DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer_list) ListView mDrawerListView;
    @BindView(R.id.my_toolbar) Toolbar mToolBar;
    private SearchView mSearchView;
    private boolean mLeaveFlag;
    private DrawerAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private DrawerItem mLogInOutItem;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Log.d(TAG, "token = " + FirebaseInstanceId.getInstance().getToken());

//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        DrawerItem[] drawerItems = new DrawerItem[] {
                DrawerItem.createAccountItem(),
                DrawerItem.createLinkItem(R.string.genre, getString(R.string.genre)),
                DrawerItem.createLinkItem(R.string.now_playing, getString(R.string.now_playing)),
                DrawerItem.createLinkItem(R.string.search_movie, getString(R.string.search_movie)),
                DrawerItem.createLinkItem(R.string.watch_list, getString(R.string.watch_list)),
                DrawerItem.createLinkItem(R.string.watch_history, getString(R.string.watch_history)),
                mLogInOutItem = DrawerItem.createLinkItem(R.string.login_account, getString(R.string.login_account)),
        };
        mAdapter = new DrawerAdapter();
        mAdapter.setDrawerItemList(Arrays.asList(drawerItems));
        mDrawerListView.setAdapter(mAdapter);
        mDrawerListView.setOnItemClickListener(mDrawerItemClickListener);

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.w(TAG, "onConnectionFailed() - connectionResult: " + connectionResult);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(MHConstants.GOOGLE_OAUTH_WEB_CLIENT_KEY)
                        .requestEmail()
                        .build())
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();

        openFragment(WatchListFragment.newInstance());
    }

    @Override
    protected void onStart() {
        super.onStart();

        handleSignInResult(mFirebaseAuth.getCurrentUser());

        if (!CommonUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() - requestCode: " + requestCode + ", resultCode: " + resultCode + ", " + CommonUtil.parseIntentInfo(data));

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null && result.isSuccess()) {
                firebaseAuthWithGoogle(result.getSignInAccount());
            }
        }
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
                openFragment(SearchMovieFragment.newInstance(query));

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

    private void handleSignInResult(FirebaseUser user) {
        mAdapter.setSignInAccount(user);
        mLogInOutItem.setTitle(getString(user == null ? R.string.login_account : R.string.logout_account));
        Log.d(TAG, "account: " + user);
        if (user != null) {
            Log.d(TAG, "getDisplayName = " + user.getDisplayName());
            Log.d(TAG, "getEmail = " + user.getEmail());
            Log.d(TAG, "getPhotoUrl = " + user.getPhotoUrl());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "getId: " + acct.getId());
        Log.d(TAG, "getIdToken: " + acct.getIdToken());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            handleSignInResult(user);
                            openFragment(WatchListFragment.newInstance());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            handleSignInResult(null);
                        }
                    }
                });
    }

    private void clickSignInOutAccount() {
        if (mFirebaseAuth.getCurrentUser() == null) {
            // sign in
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            // sign out
            new AlertDialog.Builder(this)
                    .setMessage(R.string.confirm_logout)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mFirebaseAuth.signOut();
                            handleSignInResult(null);
                            openFragment(GenreMainFragment.newInstance());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null).create().show();
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

            DrawerItem item = mAdapter.getItem(position);

            if (item != null) {
                Class<? extends Fragment> fragment = null;
                switch (item.getId()) {
                    case R.string.genre:
                        openFragment(GenreMainFragment.newInstance());
                        break;

                    case R.string.now_playing:
                        openFragment(NowPlayingFragment.newInstance());
                        break;

                    case R.string.search_movie:
                        mSearchView.setIconified(false);
                        break;

                    case R.string.watch_list:
                        openFragment(WatchListFragment.newInstance());
                        break;

                    case R.string.watch_history:
                        openFragment(WatchedListFragment.newInstance());
                        break;

                    case R.string.login_account:
                        clickSignInOutAccount();
                        break;
                }
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

    private void openFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        clearAllFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            transaction.replace(R.id.frame, fragment);
            transaction.commit();
        } catch (Exception e) {
        }
    }
}
