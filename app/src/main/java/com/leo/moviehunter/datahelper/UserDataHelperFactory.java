package com.leo.moviehunter.datahelper;

import android.content.Context;

public class UserDataHelperFactory {
    public static IUserDataHelper get(Context context) {
        return new FirebaseUserDataHelper();
    }
}
