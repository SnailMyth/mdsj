package com.wwsl.mdsj.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;
import com.wwsl.mdsj.Constants;

import java.io.File;


public class UMengShareExecutor implements ShareExecutor {

    @Override
    public void shareVideo(Activity context, int platform, String text, String title, String videoUrl, String videoCover, String description) {
        UMVideo video = new UMVideo(videoUrl);
        video.setTitle(title);//视频的标题
        video.setThumb(new UMImage(context, videoCover));//视频的缩略图
        video.setDescription(description);//视频的描述

        new ShareAction(context).setPlatform(getPlatform(platform)).withText(text).withMedia(video).share();
    }

    @Override
    public void shareMessage(Activity context, int platform, String text) {

    }

    @Override
    public void shareTextWithImg(Activity context, int platform, String text, String imgUrl) {

    }

    @Override
    public void shareTextWithImg(Activity context, int platform, String text, String... imgUrls) {

    }

    @Override
    public void shareTextWithImg(Activity context, int platform, String text, int imgRes) {

    }

    @Override
    public void shareTextWithImg(Activity context, int platform, String text, Bitmap bitmap) {
        UMImage image = new UMImage(context, bitmap);//bitmap文件
        image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
        image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享 压缩格式设置
        image.compressFormat = Bitmap.CompressFormat.PNG;//用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
        new ShareAction(context).setPlatform(getPlatform(platform)).withText(text).withMedia(image).share();
    }

    @Override
    public void shareTextWithImg(Activity context, int platform, String text, File file) {

    }

    @Override
    public void shareWeb(Activity context, int platform, String text, String thumb, String description, String url) {
        UMImage image = new UMImage(context, thumb);//网络图片
        UMWeb web = new UMWeb(url);
        web.setTitle(text);//标题
        web.setThumb(image);  //缩略图
        web.setDescription(description);//描述
        new ShareAction(context).setPlatform(getPlatform(platform)).withMedia(web).share();
    }

    @Override
    public void shareMusic(Activity context, int platform, String text, String thumb, String description, String url) {

    }

    private SHARE_MEDIA getPlatform(int type) {
        switch (type) {
            case Constants.WEIXIN_CIRCLE:
                return SHARE_MEDIA.WEIXIN_CIRCLE;
            case Constants.WEIXIN:
                return SHARE_MEDIA.WEIXIN;
            case Constants.QQ:
                return SHARE_MEDIA.QQ;
        }
        return null;
    }


}
