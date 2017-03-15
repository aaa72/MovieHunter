package com.leo.moviehunter.task;

import android.content.Context;
import android.os.AsyncTask;

import com.leo.moviehunter.data.user.UserDataHelper;
import com.leo.moviehunter.data.user.WatchItem;

import java.util.List;

public abstract class GetToWatchListTask extends AsyncTask<Void, Void, List<WatchItem>> {
    private Context mContext;

    public GetToWatchListTask(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    protected List<WatchItem> doInBackground(Void... params) {
        return UserDataHelper.getToWatchList(mContext);
    }

    @Override
    protected void onPostExecute(List<WatchItem> toWatchList) {
        onGetToWatchList(toWatchList);
    }

    abstract protected void onGetToWatchList(List<WatchItem> toWatchList);
}
