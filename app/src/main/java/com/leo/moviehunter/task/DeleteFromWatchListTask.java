package com.leo.moviehunter.task;

import android.content.Context;
import android.os.AsyncTask;

import com.leo.moviehunter.data.user.UserDataHelper;

public abstract class DeleteFromWatchListTask extends AsyncTask<String, Void, String[]> {
    private Context mContext;

    public DeleteFromWatchListTask(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    protected String[] doInBackground(String... params) {
        int ret = UserDataHelper.deleteFromWatchList(mContext, params);
        return ret > 0 ? params : null;
    }

    @Override
    protected void onPostExecute(String ...deletedIds) {
        onDone(deletedIds);
    }

    abstract protected void onDone(String ...deletedIds);
}
