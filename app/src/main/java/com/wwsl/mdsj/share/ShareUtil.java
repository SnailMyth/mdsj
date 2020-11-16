package com.wwsl.mdsj.share;

import android.app.Activity;
import android.graphics.Bitmap;

import com.frame.fire.util.LogUtils;
import com.wwsl.mdsj.AppContext;

import java.io.File;

public class ShareUtil {
    private final static String TAG = "ShareUtil";

    public static ShareUtil sInstance;

    private static ShareExecutor shareExecutor;


    private ShareUtil() {

    }

    public static ShareUtil getInstance() {
        if (sInstance == null) {
            synchronized (ShareUtil.class) {
                if (sInstance == null) {
                    sInstance = new ShareUtil();
                }
            }
        }
        return sInstance;
    }

    public void init(ShareExecutor executor) {
        shareExecutor = executor;
    }

    public void shareVideo(Activity activity, int platform, String text, String title, String videoUrl, String videoCover, String description) {
        LogUtils.e(TAG, "shareVideo: " + platform);
        if (shareExecutor == null) {
            LogUtils.e(TAG, "shareVideo: 请先初始化shareUtil");
            return;
        }
        shareExecutor.shareVideo(activity, platform, text, title, videoUrl, videoCover, description);
    }

    public void shareMessage(Activity activity, int platform, String text) {
        if (shareExecutor == null) {
            LogUtils.e(TAG, "shareVideo: 请先初始化shareUtil");
            return;
        }
        shareExecutor.shareMessage(activity, platform, text);
    }

    public void shareTextWithImg(Activity activity, int platform, String text, String imgUrl) {
        if (shareExecutor == null) {
            LogUtils.e(TAG, "shareVideo: 请先初始化shareUtil");
            return;
        }
        shareExecutor.shareTextWithImg(activity, platform, text, imgUrl);
    }

    public void shareTextWithImg(Activity activity, int platform, String text, String... imgUrls) {
        if (shareExecutor == null) {
            LogUtils.e(TAG, "shareVideo: 请先初始化shareUtil");
            return;
        }
        shareExecutor.shareTextWithImg(activity, platform, text, imgUrls);
    }

    public void shareTextWithImg(Activity activity, int platform, String text, int imgRes) {
        if (shareExecutor == null) {
            LogUtils.e(TAG, "shareVideo: 请先初始化shareUtil");
            return;
        }
        shareExecutor.shareTextWithImg(activity, platform, text, imgRes);
    }

    public void shareTextWithImg(Activity activity, int platform, String text, Bitmap bitmap) {
        if (shareExecutor == null) {
            LogUtils.e(TAG, "shareVideo: 请先初始化shareUtil");
            return;
        }
        shareExecutor.shareTextWithImg(activity, platform, text, bitmap);
    }

    public void shareTextWithImg(Activity activity, int platform, String text, File file) {
        if (shareExecutor == null) {
            LogUtils.e(TAG, "shareVideo: 请先初始化shareUtil");
            return;
        }
        shareExecutor.shareTextWithImg(activity, platform, text, file);
    }

    public void shareWeb(Activity activity, int platform, String text, String thumb, String description,String url) {
        if (shareExecutor == null) {
            LogUtils.e(TAG, "shareVideo: 请先初始化shareUtil");
            return;
        }
        shareExecutor.shareWeb(activity, platform, text, thumb, description,url);
    }

    public void shareMusic(Activity activity, int platform, String text, String thumb, String description, String url) {
        if (shareExecutor == null) {
            LogUtils.e(TAG, "shareVideo: 请先初始化shareUtil");
            return;
        }
        shareExecutor.shareMusic(activity, platform, text, thumb, description, url);
    }

}
