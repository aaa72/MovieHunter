package com.leo.moviehunter.task;

import android.content.Context;
import android.os.AsyncTask;

import com.leo.moviehunter.data.user.UserDataHelper;
import com.leo.moviehunter.data.user.WatchItem;

import java.util.List;

public abstract class AddToWatchListTask extends AsyncTask<List<WatchItem>, Void, Void> {
    private Context mContext;

    public AddToWatchListTask(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    protected Void doInBackground(List<WatchItem>... params) {
        UserDataHelper.addToWatchList(mContext, params[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        onDone();
    }

    abstract protected void onDone();
}
