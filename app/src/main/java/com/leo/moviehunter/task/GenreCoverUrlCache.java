package com.leo.moviehunter.task;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

class GenreCoverUrlCache {
    private static final String TAG = "GenreCoverUrlCache";
    private static final String PREF_NAME = "GenreCoverUrl";

    private static GenreCoverUrlCache sInst;
    private SharedPreferences mSharedPref;
    private final HashMap<Integer, String> mUrlMap = new HashMap<>();

    private GenreCoverUrlCache(Context context) {
        mSharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Map<String, ?> map = mSharedPref.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            mUrlMap.put(Integer.parseInt(entry.getKey()), (String) entry.getValue());
        }
    }

    public synchronized static GenreCoverUrlCache getInstance(Context context) {
        if (sInst == null) {
            sInst = new GenreCoverUrlCache(context);
        }
        return sInst;
    }

    public String getCoverUrl(int id) {
        return mUrlMap.get(id);
    }

    public boolean containCoverUrl(int id) {
        return mUrlMap.containsKey(id);
    }

    public void putCoverUrl(int id, String url) {
        mUrlMap.put(id, url);
        mSharedPref.edit().putString(String.valueOf(id), url).apply();
    }
}
