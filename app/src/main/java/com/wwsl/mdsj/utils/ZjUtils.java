package com.wwsl.mdsj.utils;

import android.app.Activity;

import com.frame.fire.util.LogUtils;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.listener.ListenerZjVideo;
import com.zj.zjsdk.ad.ZjRewardVideoAd;

public class ZjUtils {

    private static ZjUtils sInstance;
    private ZjRewardVideoAd rewardVideoAD;

    public static ZjUtils getInstance() {
        if (sInstance == null) {
            synchronized (ZjUtils.class) {
                if (sInstance == null) {
                    sInstance = new ZjUtils();
                }
            }
        }
        return sInstance;
    }

    public void showAd() {
        if (rewardVideoAD != null) rewardVideoAD.showAD();
        loadAd();
    }

    private void loadAd() {
        if (rewardVideoAD != null) rewardVideoAD.loadAd();
    }

    /**
     * 激励视频
     */
    public void init(Activity activity) {
        if (rewardVideoAD != null) return;

        rewardVideoAD = new ZjRewardVideoAd(activity, "zjad_945429070", new ListenerZjVideo());
        rewardVideoAD.setUserId(AppConfig.getInstance().getUid());//登录人员id  便于统计多少人浏览了广告
        loadAd();
    }

}
