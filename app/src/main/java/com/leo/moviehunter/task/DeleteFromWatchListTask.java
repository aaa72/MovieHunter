package com.leo.moviehunter.task;

import android.content.Context;
import android.os.AsyncTask;

import com.leo.moviehunter.data.user.UserDataHelper;
import com.leo.moviehunter.data.user.WatchItem;

import java.util.List;

public abstract class DeleteFromWatchListTask extends AsyncTask<List<WatchItem>, Void, List<WatchItem>> {
    private Context mContext;

    public DeleteFromWatchListTask(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    protected List<WatchItem> doInBackground(List<WatchItem>... params) {
        int ret = UserDataHelper.deleteFromToWatchList(mContext, params[0]);
        return ret > 0 ? params[0] : null;
    }

    @Override
    protected void onPostExecute(List<WatchItem> deletedList) {
        onDone(deletedList);
    }

    abstract protected void onDone(List<WatchItem> deletedList);
}
