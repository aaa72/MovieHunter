package com.leo.moviehunter.task;

import android.content.Context;
import android.os.AsyncTask;

import com.leo.moviehunter.data.user.UserDataHelper;
import com.leo.moviehunter.data.user.WatchItem;

import java.util.List;

public abstract class GetWatchedListTask extends AsyncTask<Void, Void, List<WatchItem>> {
    private Context mContext;

    public GetWatchedListTask(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    protected List<WatchItem> doInBackground(Void... params) {
        return UserDataHelper.getWatchedList(mContext);
    }

    @Override
    protected void onPostExecute(List<WatchItem> watchedList) {
        onGetWatchedList(watchedList);
    }

    abstract protected void onGetWatchedList(List<WatchItem> watchedList);
}
