package com.leo.moviehunter.datahelper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.data.user.WatchItem.Status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class FirebaseUserDataHelper implements IUserDataHelper {
    private static final String TAG = "UserDataHelper";

    @Override
    public List<WatchItem> getToWatchList() {
        List<WatchItem> list = _getWatchList();
        if (list == null) {
            return null;
        }

        for (Iterator<WatchItem> it = list.iterator() ; it.hasNext() ; ) {
            WatchItem item = it.next();
            if ((item.getStatus() & Status.TO_WATCH) == 0) {
                it.remove();
            }
        }
        return list;
    }

    @Override
    public List<WatchItem> getWatchedList() {
        List<WatchItem> list = _getWatchList();
        if (list == null) {
            return null;
        }

        for (Iterator<WatchItem> it = list.iterator() ; it.hasNext() ; ) {
            WatchItem item = it.next();
            if ((item.getStatus() & Status.WATCHED) == 0) {
                it.remove();
            }
        }
        return list;
    }

    private static List<WatchItem> _getWatchList() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return null;
        }

        final ArrayList<WatchItem> list = new ArrayList<>();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        synchronized (list) {
            database.child(user.getUid()).child("watch_list").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (list) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            list.add(snapshot.getValue(WatchItem.class));
                        }
                        list.notify();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    synchronized (list) {
                        list.notify();
                    }
                }
            });
            try {
                list.wait(15000);
            } catch (InterruptedException e) {
            }
        }

        return list;
    }

    @Override
    public int addToWatchList(List<WatchItem> list) {
        for (WatchItem watchItem : list) {
            watchItem.setStatus(watchItem.getStatus() | Status.TO_WATCH);
            watchItem.setAddedEpochTime(System.currentTimeMillis());
        }
        return _addToWatchList(list);
    }

    @Override
    public int addToWatchedList(List<WatchItem> list) {
        for (WatchItem watchItem : list) {
            watchItem.setStatus(watchItem.getStatus() & ~Status.TO_WATCH | Status.WATCHED);
        }
        return _addToWatchList(list);
    }

    private static int _addToWatchList(List<WatchItem> list) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return -1;
        }

        if (list == null) {
            return -1;
        }

        if (list.size() == 0) {
            return 0;
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        for (WatchItem watchItem : list) {
            database.child(user.getUid()).child("watch_list").child(watchItem.getMovieId()).setValue(watchItem);
        }

        return list.size();
    }

    @Override
    public int deleteFromToWatchList(List<WatchItem> list) {
        for (WatchItem watchItem : list) {
            watchItem.setStatus(watchItem.getStatus() ^ Status.TO_WATCH);
            watchItem.setAddedEpochTime(0);
        }
        return _deleteFromWatchList(list);
    }

    @Override
    public int deleteFromWatchedList(List<WatchItem> list) {
        for (WatchItem watchItem : list) {
            watchItem.setStatus(watchItem.getStatus() ^ Status.WATCHED);
            watchItem.setWatchedEpochTime(0);
        }
        return _deleteFromWatchList(list);
    }

    private static int _deleteFromWatchList(List<WatchItem> list) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return -1;
        }

        if (list == null || list.size() <= 0) {
            return 0;
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        for (WatchItem watchItem : list) {
            database.child(user.getUid()).child("watch_list").child(watchItem.getMovieId()).removeValue();
        }

        return list.size();
    }
}
