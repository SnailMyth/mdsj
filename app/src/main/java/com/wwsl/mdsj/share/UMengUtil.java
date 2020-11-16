package com.wwsl.mdsj.share;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wwsl.mdsj.BuildConfig;
import com.wwsl.mdsj.wxapi.ThirdConfig;

public class UMengUtil {

    public static void init(Context context) {
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        UMConfigure.init(context, BuildConfig.UMENG_KEY, getChannel(context), UMConfigure.DEVICE_TYPE_PHONE, null);
        PlatformConfig.setWeixin(ThirdConfig.WX_APP_ID, ThirdConfig.WX_APP_SECRET);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

    }


    public static String getChannel(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException ignored) {

        }
        return "";
    }

    public static void login(Activity activity, SHARE_MEDIA platform, UMAuthListener authListener) {
        UMShareAPI.get(activity).getPlatformInfo(activity, platform, authListener);
    }
}
