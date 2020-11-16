package com.wwsl.mdsj.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;

public interface ShareExecutor {


    void shareVideo(Activity context, int platform, String text,String title, String videoUrl, String videoCover, String description);

    void shareMessage(Activity context, int platform, String text);

    //网络图片
    void shareTextWithImg(Activity context, int platform, String text, String imgUrl);

    //多图分享
    void shareTextWithImg(Activity context, int platform, String text, String... imgUrls);

    //资源文件
    void shareTextWithImg(Activity context, int platform, String text, int imgRes);

    //bitmap文件
    void shareTextWithImg(Activity context, int platform, String text, Bitmap bitmap);

    //本地文件
    void shareTextWithImg(Activity context, int platform, String text, File file);

    //分享链接
    void shareWeb(Activity context, int platform, String text, String thumb, String description,String url);

    //分享音乐
    void shareMusic(Activity context, int platform, String text, String thumb, String description, String url);

}
