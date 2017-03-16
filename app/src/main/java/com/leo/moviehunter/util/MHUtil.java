package com.leo.moviehunter.util;

import android.app.Activity;

import java.lang.reflect.Method;

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
}
