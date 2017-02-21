package com.leo.moviehunter.util;

public class Log {
    private static final String TAG = "Movie Hunter";
    private static final String TAG_SEP = ": ";
    private static final String PREFIX_SEP = "- ";

    private static String tag(String tag) {
        return tag + TAG_SEP;
    }

    private static String prefix(String prefix) {
        return prefix + PREFIX_SEP;
    }

    public static void d(String tag, String msg) {
        android.util.Log.d(TAG, tag(tag) + msg);
    }

    public static void d(String tag, String prefix, String msg) {
        android.util.Log.d(TAG, tag(tag) + prefix(prefix) + msg);
    }

    public static void d(String tag, String msg, Throwable e) {
        android.util.Log.d(TAG, tag(tag) + msg, e);
    }

    public static void d(String tag, String prefix, String msg, Throwable e) {
        android.util.Log.d(TAG, tag(tag) + prefix(prefix) + msg, e);
    }



    public static void w(String tag, String msg) {
        android.util.Log.w(TAG, tag(tag) + msg);
    }

    public static void w(String tag, String prefix, String msg) {
        android.util.Log.d(TAG, tag(tag) + prefix(prefix) + msg);
    }

    public static void w(String tag, String msg, Throwable e) {
        android.util.Log.w(TAG, tag(tag) + msg, e);
    }

    public static void w(String tag, String prefix, String msg, Throwable e) {
        android.util.Log.d(TAG, tag(tag) + prefix(prefix) + msg, e);
    }
}
