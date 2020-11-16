package com.wwsl.mdsj.utils;

import android.util.Log;

import com.frame.fire.util.LogUtils;
import com.wwsl.mdsj.AppContext;

/**
 * Created by cxf on 2017/8/3.
 */

public class L {
    private final static String TAG = "MDSJ------>";

    public static void e(String s) {
        e(TAG, s);
    }

    public static void e(String tag, String s) {
        if (AppContext.sDeBug) {
            LogUtils.e(tag, s);
        }
    }
}
