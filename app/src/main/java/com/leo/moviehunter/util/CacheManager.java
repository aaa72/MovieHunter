package com.leo.moviehunter.util;

import android.content.Context;
import android.content.pm.PackageManager;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.JsonSerializer;
import com.vincentbrison.openlibraries.android.dualcache.SizeOf;

import java.io.IOException;
import java.util.HashMap;

public class CacheManager {
    private static final int RAM_MAX_SIZE = 1024 * 1024 * 12; // 12 MB
    private static final int DISK_MAX_SIZE = 1024 * 1024 * 16; // 16 MB

    private static CacheManager sInst;

    private final Context mContext;
    private final int mAppVersionCode;
    private final HashMap<Class<?>, DualCache<?>> mCacheMap = new HashMap<>();

    private CacheManager(Context context) {
        mContext = context.getApplicationContext();
        int versionCode;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionCode = 1;
        }
        mAppVersionCode = versionCode;
    }

    public static synchronized CacheManager getInstance(Context context) {
        if (sInst == null) {
            sInst = new CacheManager(context);
        }
        return sInst;
    }

    public <T> DualCache getCache(Class<T> clz) {
        synchronized (mCacheMap) {
            if (mCacheMap.containsKey(clz)) {
                return mCacheMap.get(clz);
            }

            DualCache cache = new Builder<T>(clz.getName(), mAppVersionCode)
                    .enableLog()
                    .useReferenceInRam(RAM_MAX_SIZE, new SizeOf<T>() {
                        @Override
                        public int sizeOf(T object) {
                            try {
                                return CommonUtil.sizeOf(object);
                            } catch (IOException e) {
                                return 0;
                            }
                        }
                    })
                    .useSerializerInDisk(DISK_MAX_SIZE, true, new JsonSerializer<>(clz), mContext)
                    .build();

            mCacheMap.put(clz, cache);
            return cache;
        }
    }

}
