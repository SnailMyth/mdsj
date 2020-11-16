package com.wwsl.mdsj.utils;

import android.annotation.SuppressLint;

import com.wwsl.mdsj.bean.VideoBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by cxf on 2018/6/9.
 */

public class VideoStorage {

    private static VideoStorage sInstance;
    private Map<String, List<VideoBean>> mMap;
    private Queue<String> keyQuenue;

    private VideoStorage() {
        mMap = new HashMap<>();
        keyQuenue = new ConcurrentLinkedDeque<>();
    }

    public static VideoStorage getInstance() {
        if (sInstance == null) {
            synchronized (VideoStorage.class) {
                if (sInstance == null) {
                    sInstance = new VideoStorage();
                }
            }
        }
        return sInstance;
    }

    public void put(String key, List<VideoBean> list) {
        if (mMap != null) {
            //只存10个列表
            if (keyQuenue.size() == 10) {
                String headKey = keyQuenue.poll();
                mMap.remove(headKey);
            }
            keyQuenue.add(key);
            mMap.put(key, list);
        }
    }

    @SuppressLint("DefaultLocale")

    public void putVideoList(String uid, String type, List<VideoBean> list) {
        put(String.format("%s_%s", uid, type).intern(), list);
    }

    @SuppressLint("DefaultLocale")
    public List<VideoBean> getVideoList(String uid, String type) {
        if (mMap != null) {
            return mMap.get(String.format("%s_%s", uid, type).intern());
        }
        return null;
    }


    public List<VideoBean> get(String key) {
        if (mMap != null) {
            return mMap.get(key);
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    public void remove(String uid, int type) {
        if (mMap != null) {
            mMap.remove(String.format("%s_%d", uid, type));
        }
    }

    public void remove(String key) {
        if (mMap != null) {
            mMap.remove(key);
        }
    }


    public void clear() {
        if (mMap != null) {
            mMap.clear();
        }
    }
}
