package com.leo.moviehunter.task;

import android.content.Context;
import android.os.AsyncTask;

import com.leo.moviehunter.data.user.UserDataHelper;
import com.leo.moviehunter.data.user.WatchItem;

import java.util.List;

public abstract class GetWatchListTask extends AsyncTask<Void, Void, List<WatchItem>> {
    private Context mContext;

    public GetWatchListTask(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    protected List<WatchItem> doInBackground(Void... params) {
        return UserDataHelper.getWatchList(mContext);
    }

    @Override
    protected void onPostExecute(List<WatchItem> watchList) {
        onGetWatchList(watchList);
    }

    abstract protected void onGetWatchList(List<WatchItem> watchList);
}
