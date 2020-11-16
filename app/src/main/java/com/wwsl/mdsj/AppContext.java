package com.wwsl.mdsj;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.multidex.MultiDexApplication;

import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.VideoViewConfig;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.frame.fire.util.LogUtils;
import com.lxj.xpopup.XPopup;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.smartzheng.launcherstarter.LauncherStarter;
import com.smartzheng.launcherstarter.task.Task;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.ugc.TXUGCBase;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.im.ImMessageUtil;
import com.wwsl.mdsj.im.ImPushUtil;
import com.wwsl.mdsj.share.ShareUtil;
import com.wwsl.mdsj.share.UMengShareExecutor;
import com.wwsl.mdsj.share.UMengUtil;
import com.wwsl.mdsj.utils.CrashHandler;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.SecurityUtil;
import com.wwsl.mdsj.wxapi.ThirdConfig;
import com.zj.zjsdk.ZjSdk;

/**
 * @author :
 * @date : 2020/8/5 19:56
 * @description : AppContext
 */
public class AppContext extends MultiDexApplication {

    public static AppContext sInstance;
    public static boolean sDeBug;
    private int mCount;
    private boolean mFront;//是否前台

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        //众简广告初始化
//        ZjSdk.init(this, "zj_11120200724005");

        CrashHandler.getInstance().init(this);
        sDeBug = BuildConfig.DEBUG;

        if (sDeBug) {
            Logger.addLogAdapter(new AndroidLogAdapter());
        }

        //初始化Http
        HttpUtil.init();
        //全局配置视频解码器
        LogUtils.e(TAG, "init---->配置视频解码器");
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                .setPlayerFactory(IjkPlayerFactory.create()) //使用使用IjkPlayer解码
                .setPlayOnMobileNetwork(true)
                .build());

        registerActivityLifecycleCallbacks();

        asyncInitSDK();
    }

    //异步初始化sdk
    private void asyncInitSDK() {
        LauncherStarter.init(this);

        LauncherStarter starter = LauncherStarter.createInstance();

        starter.addTask(new Task() {
            @Override
            public void run() {
                SecurityUtil.xPosedCheck();

                //众简广告初始化
                ZjSdk.init(sInstance, "zj_11120200724001");

                //X5 初始化
                initX5();

                //初始化腾讯短视频
                LogUtils.e(TAG, "init---->加载腾讯直播SDK");
                TXLiveBase.getInstance().setLicence(getApplicationContext(), BuildConfig.tx_live_LicenseUrl, BuildConfig.tx_dsp_Key);

                //初始化腾讯直播
                LogUtils.e(TAG, "init---->加载腾讯短视频SDK");
                TXUGCBase.getInstance().setLicence(getApplicationContext(), BuildConfig.tx_dsp_LicenseUrl, BuildConfig.tx_live_Key);

                //初始化极光推送
                LogUtils.e(TAG, "init---->初始化极光推送");
                ImPushUtil.getInstance().init(getApplicationContext());
                //初始化极光IM
                LogUtils.e(TAG, "init---->初始化极光IM");
                ImMessageUtil.getInstance().init();

                //初始化ZXing二维码
                LogUtils.e(TAG, "init---->初始化ZXing二维码");
                ZXingLibrary.initDisplayOpinion(getApplicationContext());

                //初始化友盟
                LogUtils.e(TAG, "init---->初始化友盟");
                UMengUtil.init(getApplicationContext());

                //初始化友盟分享
                LogUtils.e(TAG, "init---->初始化友盟分享");
                ShareUtil.getInstance().init(new UMengShareExecutor());

                //初始化美洽客服
                LogUtils.e(TAG, "init---->初始化美洽客服");
                MQConfig.init(getApplicationContext(), ThirdConfig.MQ_KEY, new OnInitCallback() {
                    @Override
                    public void onSuccess(String clientId) {
                        LogUtils.e(TAG, "onSuccess: ");
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        LogUtils.e(TAG, "onFailure: ");
                    }
                });

                //初始化XPopup全局配置
                LogUtils.e(TAG, "init---->初始化XPopup全局配置");
                XPopup.setPrimaryColor(Color.parseColor("#F95921"));
            }
        });
        starter.start();
        starter.await();
    }

    private final static String TAG = "AppContext";

    private void initX5() {
        LogUtils.e(TAG, "init---->加载腾讯X5");
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                L.e("onCoreInitFinished");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                L.e("onViewInitFinished:" + b);
            }
        });
    }


    private void registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mCount++;
                if (!mFront) {
                    mFront = true;
                    L.e("AppContext------->处于前台");
                    AppConfig.getInstance().setFrontGround(true);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mCount--;
                if (mCount == 0) {
                    mFront = false;
                    L.e("AppContext------->处于后台");
                    AppConfig.getInstance().setFrontGround(false);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }
}
