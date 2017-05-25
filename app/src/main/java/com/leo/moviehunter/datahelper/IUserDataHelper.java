package com.leo.moviehunter.datahelper;

import com.leo.moviehunter.data.user.WatchItem;

import java.util.List;

public interface IUserDataHelper {
    List<WatchItem> getToWatchList();
    List<WatchItem> getWatchedList();
    int addToWatchList(List<WatchItem> list);
    int addToWatchedList(List<WatchItem> list);
    int deleteFromToWatchList(List<WatchItem> list);
    int deleteFromWatchedList(List<WatchItem> list);
}
