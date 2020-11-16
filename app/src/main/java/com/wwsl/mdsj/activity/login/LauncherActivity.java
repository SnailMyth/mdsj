package com.wwsl.mdsj.activity.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;
import com.frame.fire.util.LogUtils;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.permissionx.guolindev.PermissionX;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.MainActivity;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.LaunchAdBean;
import com.wwsl.mdsj.bean.PartnerCityBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.custom.AdvertSkipView;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.interfaces.NoDoubleClickListener;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.DownloadUtil;
import com.wwsl.mdsj.utils.LocationUtil;
import com.wwsl.mdsj.utils.SpUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.SystemUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.VersionUtil;
import com.wwsl.mdsj.views.CountdownView;
import com.wwsl.mdsj.views.dialog.wheelpick.DatePickerUtil;
import com.zj.zjsdk.ad.ZjAdError;
import com.zj.zjsdk.ad.ZjSplashAd;
import com.zj.zjsdk.ad.ZjSplashAdListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;


/**
 * @author :
 * @date : 2020/6/11 19:51
 * @description : LauncherActivity
 * 启动图-》本地视频-》接口-》图片/视频广告-》众简广告
 */
public class LauncherActivity extends AppCompatActivity {
    private static WeakReference<LauncherActivity> instance;
    private Window window;
    protected Context mContext;
    //众简广告
    private ConstraintLayout zjContainer;
    private ZjSplashAd splashAd;
    //视频广告 本地广告
    private RelativeLayout ui_video;
    private VideoView mVideoView;
    private View clickView;
    private ControlWrapper mControlWrapper;
    private int videoIndex = 0;
    private String videoAdUrl = "";
    //图片广告
    private RelativeLayout ui_img;
    private ImageView ivAdvert;
    private TextView tvAdvertSkip;
    private CountdownView mCountdownView;
    private AdvertSkipView advertSkipView;
    //apk更新
    private ConstraintLayout downloadLayout;
    private RingProgressBar loadPb;
    //启动图片
    private ImageView launch_img;

    /**
     * 0.登录
     * 1.进主页
     * 2.绑定微信
     * 3.绑定手机号
     */
    private int mGoType = 0;
    private boolean isGrand = false;//是否已经授权
    private boolean isPlayEnd = false;//视频是否播放完毕
    private boolean isLoadConfigFinish = false;//是否已配置完成
    private boolean isNeedVersion = false;//是否强制更新apk
    private boolean isGoShowingAd = false;//是点击了广告

    private String phone;
    private String uid;
    private String token;
    private String[] ps = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    private Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 11:
                showUI(2);
                videoIndex = 1;
                mVideoView.start();
                initZJAd();
                break;
            case 12:
                prepareFinish();
                break;
        }
        return true;
    });

    //众简广告初始化开屏广告
    private void initZJAd() {
        splashAd = new ZjSplashAd(this, new ZjSplashAdListener() {
            @Override
            public void onZjAdLoaded() {
            }

            @Override
            public void onZjAdLoadTimeOut() {
                LogUtils.e("onZjAdLoadTimeOut(--)广告加载超时...");//新装app报错
                goNext("");
            }

            @Override
            public void onZjAdShow() {
            }

            @Override
            public void onZjAdClicked() {
            }

            //倒计时结束
            @Override
            public void onZjAdTickOver() {
            }

            //跳过
            @Override
            public void onZjAdDismissed() {
                goNext("");
            }

            @Override
            public void onZjAdError(ZjAdError zjAdError) {
                LogUtils.e("onZjAdError(--)" + zjAdError.getErrorCode() + "-" + zjAdError.getErrorMsg());
                goNext("");
            }
        }, "zjad_945429071", 3);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            int option = window.getDecorView().getSystemUiVisibility()
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(option);
            window.setNavigationBarColor(Color.parseColor("#000000"));
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.GONE);
        }

        setContentView(R.layout.activity_launcher);

        if (receiveGGNotification()) return;

        mContext = this;

        zjContainer = findViewById(R.id.rootView);

        ui_video = findViewById(R.id.ui_video);
        mVideoView = findViewById(R.id.videoView);
        clickView = findViewById(R.id.clickView);
        if (!StringUtil.isEmpty(videoAdUrl)) {
            clickView.setOnClickListener(v -> {
                isGoShowingAd = true;
                goNext(videoAdUrl);
            });
        }

        ui_img = findViewById(R.id.ui_img);
        ivAdvert = findViewById(R.id.ivAdvert);
        tvAdvertSkip = findViewById(R.id.tvAdvertSkip);
        mCountdownView = findViewById(R.id.count_down_view);
        mCountdownView.setCountNum(5);
        mCountdownView.setOnCountdownListener(() -> mCountdownView.setVisibility(View.GONE));

        launch_img = findViewById(R.id.launch_img);

        downloadLayout = findViewById(R.id.updateLayout);
        loadPb = findViewById(R.id.loadPb);

        initVideoView();
        loadSpData();

        //1.检查权限
        checkPermission();
    }

    /**
     * 第三方广告平台广告
     */
    private void showAd3() {
        showUI(4);
        splashAd.fetchAndShowIn(zjContainer);
    }

    private void loadSpData() {
        String[] temp = SpUtil.getInstance().getMultiStringValue(SpUtil.FIRST_LOGIN, SpUtil.USER_PHONE);
        this.phone = temp[1];
        String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(SpUtil.UID, SpUtil.TOKEN);
        this.uid = uidAndToken[0];
        this.token = uidAndToken[1];
    }

    private void initVideoView() {
        mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT);
        int rid = R.raw.launch_video;
        String intt = DatePickerUtil.formatDate(System.currentTimeMillis(), "yyyyMMdd");
        try {
            int anInt = Integer.parseInt(intt);
            if (anInt > 20200930 && anInt < 20201009) {
                rid = R.raw.national_day;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(rid));
        RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(this);
        try {
            rawResourceDataSource.open(dataSpec);
        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
            e.printStackTrace();
        }
        String url = rawResourceDataSource.getUri().toString();
        mVideoView.setUrl(url);
        mVideoView.setLooping(false);
        BaseVideoController baseVideoController = new BaseVideoController(this) {
            @Override
            protected int getLayoutId() {
                return 0;
            }
        };
        baseVideoController.addControlComponent(new IControlComponent() {
            @Override
            public void attach(@NonNull ControlWrapper controlWrapper) {
                mControlWrapper = controlWrapper;
            }

            @Override
            public View getView() {
                return null;
            }

            @Override
            public void onVisibilityChanged(boolean isVisible, Animation anim) {
            }

            @Override
            public void onPlayStateChanged(int playState) {
            }

            @Override
            public void onPlayerStateChanged(int playerState) {
            }

            @Override
            public void setProgress(int duration, int position) {
            }

            @Override
            public void onLockStateChanged(boolean isLocked) {
            }
        });
        mVideoView.setVideoController(baseVideoController);
        mVideoView.setOnStateChangeListener(new VideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {
            }

            @Override
            public void onPlayStateChanged(int playState) {
                switch (playState) {
                    case VideoView.STATE_PLAYING://3
                        mControlWrapper.startProgress();
                        break;
                    case VideoView.STATE_PREPARING://1
                        //静音播放
//                        mVideoView.setMute(true);
                        break;
                    case VideoView.STATE_PLAYBACK_COMPLETED://5
                        //启动视频
                        isPlayEnd = true;
                        if (videoIndex == 1) {
                            //填补空白等待页
                            Bitmap bitmap = mControlWrapper.doScreenShot();
                            window.setBackgroundDrawable(new BitmapDrawable(bitmap));
                            window.setLayout(bitmap.getWidth(), bitmap.getHeight());
                            handler.sendEmptyMessage(12);
                        } else if (videoIndex == 2) {
                            //广告视频
                            if (!isGoShowingAd) {
                                goNext("");
                            }
                        }
                        break;
                }
            }
        });
    }

    private void loadCity() {
        String cityStr = SpUtil.getInstance().getStringValue(SpUtil.CITY);
        if (!StrUtil.isEmpty(cityStr)) {
            List<PartnerCityBean> cityBeans = JSON.parseArray(cityStr, PartnerCityBean.class);
            AppConfig.getInstance().setCityBeans(cityBeans);
        } else {
            HttpUtil.getCityConfig();
        }
    }

    private boolean receiveGGNotification() {
        //下面的代码是为了防止一个bug:
        // 收到极光通知后，点击通知，如果没有启动app,则启动app。然后切后台，再次点击桌面图标，app会重新启动，而不是回到前台。
        instance = new WeakReference<>(this);
        Intent intent = getIntent();
        if (!isTaskRoot()
                && intent != null
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return true;
        }
        return false;
    }

    private final static String TAG = "LauncherActivity";

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public void checkPermission() {
        SpUtil.getInstance().setStringValue(SpUtil.FIRST_LOGIN, "0");

        boolean p1 = PermissionX.isGranted(mContext, ps[0]);
        boolean p2 = PermissionX.isGranted(mContext, ps[1]);
        boolean p3 = PermissionX.isGranted(mContext, ps[2]);
        boolean p4 = PermissionX.isGranted(mContext, ps[3]);
        List<String> lps = new ArrayList<>();
        if (!p1) lps.add(ps[0]);
        if (!p2) lps.add(ps[1]);
        if (!p3) lps.add(ps[2]);
        if (!p4) lps.add(ps[3]);

        if (lps.size() == 0) {
            isGrand = true;
            getConfig();
            handler.sendEmptyMessageDelayed(11, 4000);
        } else {
            //暂时没有适配android 10 后台定位
            PermissionX.init(this)
                    .permissions(lps)
                    .request((allGranted, grantedList, deniedList) -> {
                        //获取设备唯一识别id
                        String deviceId = SpUtil.getInstance().getStringValue(SpUtil.DEVICE_ID);
                        if (grantedList.contains(Manifest.permission.READ_PHONE_STATE)) {
                            //手机号获取已经授权
                            TelephonyManager tm = (TelephonyManager) LauncherActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                            phone = tm.getLine1Number();
                            phone = StrUtil.replace(phone, "+86", "");
                            SpUtil.getInstance().setStringValue(SpUtil.USER_PHONE, phone);
                            //当前没有设备id
                            if (StrUtil.isEmpty(deviceId)) {
                                deviceId = tm.getDeviceId();
                            }
                            AppConfig.getInstance().getMobileBean().setTel(tm.getLine1Number());
                            AppConfig.getInstance().getMobileBean().setImei(tm.getSimSerialNumber());
                            AppConfig.getInstance().getMobileBean().setImsi(tm.getSubscriberId());
                        }
                        if (StrUtil.isEmpty(deviceId)) {
                            //仍没有拿到设备id
                            deviceId = SystemUtil.getDeviceUUID().replace("-", "");
                        }
                        AppConfig.getInstance().setDeviceId(deviceId);
                        SpUtil.getInstance().setStringValue(SpUtil.DEVICE_ID, deviceId);
                        //开启定位
                        if (grantedList.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            LocationUtil.getInstance().startLocation();
                        }
                        if (deniedList.contains(ps[1]) || deniedList.contains(ps[0])) {
                            isGrand = false;
                            ToastUtil.show("部分权限未授权,部分功能不能正常使用");
                        }
                        isGrand = true;
                        getConfig();
                        handler.sendEmptyMessageDelayed(11, 4000);
                    });
        }
    }

    /**
     * 1 launch_img
     * 2 ui_video
     * 3 ui_img
     * 4 zjContainer
     * 5 downloadLayout
     */
    private void showUI(int i) {
        if (i == 1) {
            launch_img.setVisibility(View.VISIBLE);
            downloadLayout.setVisibility(View.GONE);
            ui_img.setVisibility(View.GONE);
            ui_video.setVisibility(View.GONE);
            zjContainer.setVisibility(View.GONE);
        } else if (i == 2) {
            ui_video.setVisibility(View.VISIBLE);
            downloadLayout.setVisibility(View.GONE);
            ui_img.setVisibility(View.GONE);
            zjContainer.setVisibility(View.GONE);
            launch_img.setVisibility(View.GONE);
        } else if (i == 3) {
            ui_img.setVisibility(View.VISIBLE);
            downloadLayout.setVisibility(View.GONE);
            ui_video.setVisibility(View.GONE);
            zjContainer.setVisibility(View.GONE);
            launch_img.setVisibility(View.GONE);
        } else if (i == 4) {
            zjContainer.setVisibility(View.VISIBLE);
            downloadLayout.setVisibility(View.GONE);
            ui_img.setVisibility(View.GONE);
            ui_video.setVisibility(View.GONE);
            launch_img.setVisibility(View.GONE);
        } else if (i == 5) {
            downloadLayout.setVisibility(View.VISIBLE);
            ui_img.setVisibility(View.GONE);
            ui_video.setVisibility(View.GONE);
            zjContainer.setVisibility(View.GONE);
            launch_img.setVisibility(View.GONE);
        }
    }

    /**
     * 获取Config信息
     **/
    private void getConfig() {
        //必须获取首页导航才能登录
        loadCity();//获取市，区

        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                if (bean != null) {
                    if (!VersionUtil.isLatest(bean.getVersion())) {
                        isNeedVersion = true;
                    }
                    isLoadConfigFinish = true;
                    mGoType = 0;
                    if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
                        AppConfig.getInstance().setLoginInfo(uid, token, false);
                        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                            @Override
                            public void callback(UserBean bean) {
                                if (bean != null) {
                                    mGoType = 1;
                                    AppConfig.getInstance().setLoginInfo(uid, token, true);
                                    String isNeedAuthWx = AppConfig.getInstance().getConfig().getIsNeedAuthWx();
                                    if (isNeedAuthWx.equals("1") && !"1".equals(bean.getIsWxAuth())) {
                                        mGoType = 2;//需要先绑定微信
                                    }
                                } else {
                                    LogUtils.e(TAG, "获取用户最新数据失败");
                                }
                            }
                        });
                    }
                } else {
                    ToastUtil.show("配置获取失败");
                    mGoType = 0;
                }
            }
        });
    }

    private void showAdvertise() {
        ConfigBean configBean = AppConfig.getInstance().getConfig();
        List<LaunchAdBean> adList = configBean.getAdList();
        if (null != adList && adList.size() == 1 && "1".equals(configBean.getIsLaunch())) {
            LaunchAdBean launchAdBean = adList.get(0);
            if ("1".equals(launchAdBean.getType()) && !StrUtil.isEmpty(launchAdBean.getThumb())) {
                //图片
                mCountdownView.setVisibility(View.GONE);
                setAdvert(launchAdBean.getThumb(), launchAdBean.getUrl());
            } else if ("2".equals(launchAdBean.getType()) && !StrUtil.isEmpty(launchAdBean.getThumb())) {
                showUI(2);
                //视频
                videoIndex = 2;
                videoAdUrl = launchAdBean.getUrl();
                mVideoView.release();
                mVideoView.setUrl(launchAdBean.getThumb());
                mVideoView.start();
            } else {
                showAd3();
            }
        } else {
            showAd3();
        }
    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.release();
        }
        HttpUtil.cancel(HttpConst.IF_TOKEN);
        HttpUtil.cancel(HttpConst.GET_BASE_INFO);
        HttpUtil.cancel(HttpConst.GET_CONFIG);
        super.onDestroy();
    }

    private void setAdvert(String slidePic, String slideUrl) {
        showUI(3);
        ImgLoader.display(slidePic, ivAdvert, 0, 0, false, null, new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                goNext("");
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                ivAdvert.setVisibility(View.VISIBLE);
                tvAdvertSkip.setVisibility(View.VISIBLE);
                advertSkipView = new AdvertSkipView(6000, 1000, tvAdvertSkip);
                advertSkipView.setAdvertSkipFinishListener(() -> goNext(""));
                advertSkipView.start();

                tvAdvertSkip.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    protected void onNoDoubleClick(View v) {
                        advertSkipView.cancel();
                        goNext("");
                    }
                });
                ivAdvert.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    protected void onNoDoubleClick(View v) {
                        advertSkipView.cancel();
                        goNext(slideUrl);
                    }
                });
                return false;
            }
        }, null);
    }

    private void goNextActivity() {
        if (mGoType == 0) {
            LoginActivity.forward(phone);
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        } else if (mGoType == 1) {
            MainActivity.forward(mContext);
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        } else if (mGoType == 2) {
            wxAuth();
        }
    }

    private void goNext(String slideUrl) {
        if (TextUtils.isEmpty(slideUrl)) {
            //是否点击了广告
            if (!isGoShowingAd) {
                goNextActivity();
            }
        } else {
            WebViewActivity.forwardForResult(mContext, slideUrl);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        goNextActivity();
    }

    @Override
    public void onBackPressed() {
        if (advertSkipView != null) {
            advertSkipView.cancel();
        }
        super.onBackPressed();
    }

    public static void finishActivity() {
        if (instance != null && instance.get() != null) {
            instance.get().finish();
        }
    }

    public void wxAuth() {
        DialogUtil.showSimpleDialog(mContext, "暂未绑定微信,请先绑定微信", new DialogUtil.SimpleCallback2() {
            @Override
            public void onCancelClick() {
                LoginActivity.forward(phone);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                finish();
            }

            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                dialog.dismiss();
                UMShareAPI umShareAPI = UMShareAPI.get(LauncherActivity.this);
                umShareAPI.getPlatformInfo(LauncherActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        LogUtils.e(TAG, "微信登录开始授权 ");
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        HttpUtil.bindWx(map.get("openid"), map.get("unionid"), map.get("name"), map.get("iconurl"), new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                ToastUtil.show(msg);
                                if (code == 0) {
                                    MainActivity.forward(mContext);
                                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                } else {
                                    wxAuthBack(msg);
                                }
                            }

                            @Override
                            public void onError() {
                                wxAuthBack("绑定失败,请登录!");
                            }
                        });
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        wxAuthBack("微信授权失败");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        wxAuthBack("微信授权取消");
                    }
                });
            }
        });
    }

    private void wxAuthBack(String str) {
        SpUtil.getInstance().removeValue(SpUtil.UID);
        SpUtil.getInstance().removeValue(SpUtil.TOKEN);
        ToastUtil.show(str);
        LoginActivity.forward(phone);
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        finish();
    }

    /**
     * 准备完成事件
     */
    public void prepareFinish() {
        if (isGrand && isPlayEnd && isLoadConfigFinish) {
            ConfigBean configBean = AppConfig.getInstance().getConfig();
            if (isNeedVersion && "1".equals(configBean.getIsNeedUpdate())) {
                VersionUtil.showDialog(
                        mContext,
                        false,
                        configBean.getVersion(),
                        configBean.getUpdateDes(),
                        (view, object) -> downloadNewApk(configBean));
            } else {
                showAdvertise();
            }
        }
    }

    /**
     * 新版本弹窗
     */
    private void downloadNewApk(ConfigBean configBean) {
        String url = configBean.getUpdateApkUrl();
        if (!TextUtils.isEmpty(url)) {
            try {
                DownloadUtil downloadUtil = new DownloadUtil();
                showUI(5);
                downloadUtil.download(Constants.APP_APK, mContext.getFilesDir(), Constants.APK_FILE_NAME, url, new DownloadUtil.Callback() {
                    @Override
                    public void onSuccess(File file) {
                        showUI(1);
                        VersionUtil.installNormal(LauncherActivity.this, file.getPath());
                    }

                    @Override
                    public void onProgress(int progress) {
                        loadPb.setProgress(progress);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.show("下载失败");
                        showUI(1);
                    }
                });
            } catch (Exception e) {
                ToastUtil.show(R.string.version_download_url_error);
            }
        } else {
            ToastUtil.show(R.string.version_download_url_error);
        }
    }
}
