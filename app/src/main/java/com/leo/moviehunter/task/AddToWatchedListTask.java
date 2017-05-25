package com.leo.moviehunter.task;

import android.content.Context;
import android.os.AsyncTask;

import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.datahelper.UserDataHelperFactory;

import java.util.List;

public abstract class AddToWatchedListTask extends AsyncTask<List<WatchItem>, Void, List<WatchItem>> {
    private Context mContext;

    public AddToWatchedListTask(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    protected List<WatchItem> doInBackground(List<WatchItem>... params) {
        int ret = UserDataHelperFactory.get(mContext).addToWatchedList(params[0]);
        return ret > 0 ? params[0] : null;
    }

    @Override
    protected void onPostExecute(List<WatchItem> addedList) {
        onDone(addedList);
    }

    abstract protected void onDone(List<WatchItem> addedList);
}
