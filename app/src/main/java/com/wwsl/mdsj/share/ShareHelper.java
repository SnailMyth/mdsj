package com.wwsl.mdsj.share;

import android.app.Activity;
import android.graphics.Bitmap;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.ShareBean;
import com.wwsl.mdsj.bean.VideoBean;

public class ShareHelper {

    public static void shareVideo(Activity activity, ShareBean shareBean, VideoBean videoBean) {
        if (null == shareBean || null == videoBean) return;
        ShareUtil.getInstance().shareVideo(activity, shareBean.getType(), videoBean.getTitle(),
                videoBean.getTitle(), videoBean.getVideoUrl(), videoBean.getCoverUrl(), "毛豆视界,开启新世界!");
    }

    public static void shareTextWithImg(Activity activity, ShareBean shareBean, Bitmap bitmap) {
        if (null == shareBean) return;
        ShareUtil.getInstance().shareTextWithImg(activity, shareBean.getType(), "毛豆视界,开启新世界!", bitmap);
    }

    public static void shareText(Activity activity, ShareBean shareBean, String content) {
        if (null == shareBean) return;
        ShareUtil.getInstance().shareMessage(activity, shareBean.getType(), content);
    }

    public static void shareLive(Activity activity, ShareBean shareBean,String imgUrl) {
        if (null == shareBean) return;
        ConfigBean config = AppConfig.getInstance().getConfig();
        if (config != null) {
            ShareUtil.getInstance().shareWeb(activity, shareBean.getType(), config.getLiveShareTitle(), imgUrl, config.getLiveShareDes(), config.getDownloadApkUrl());
        }
    }
}
