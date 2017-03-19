package com.leo.moviehunter.util;

import android.app.Activity;
import android.text.TextUtils;

import com.leo.moviehunter.data.Genre;
import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.data.user.WatchItem;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MHUtil {
    private static final String TAG = "MHUtil";

    public static void setActivityToolbarSubTitle(Activity activity, String title) {
        try {
            Method method = activity.getClass().getDeclaredMethod("setSubTitle", String.class);
            method.invoke(activity, title);
        } catch (Exception e) {
            Log.w(TAG, "do not have setSubTitle method");
        }
    }

    public static float score2Star(float score, int starNum) {
        return score / MHConstants.MAX_MOVIE_SCORE * starNum;
    }

    public static float star2Score(float star, int starNum) {
        return star / starNum * MHConstants.MAX_MOVIE_SCORE;
    }

    public static String genreIdsToString(String[] genreIds, Map<String, Genre> map) {
        if (genreIds == null || genreIds.length <= 0 || map == null || map.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genreIds.length; i++) {
            Genre genre;
            if ((genre = map.get(genreIds[i])) != null && !TextUtils.isEmpty(genre.getName())) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(genre.getName());
            }
        }
        return sb.toString();
    }

    public static HashMap<String, Genre> genresToMap(Genre[] genres) {
        if (genres == null) {
            return null;
        }
        HashMap<String, Genre> map = new HashMap<>();
        for (Genre genre : genres) {
            map.put(genre.getId(), genre);
        }
        return map;
    }

    public static WatchItem createWatchItem(Movie movie) {
        WatchItem watchItem = new WatchItem();
        watchItem.setMovieId(movie.getId());
        watchItem.setGenreIds(movie.getGenreIds());
        return watchItem;
    }
}
