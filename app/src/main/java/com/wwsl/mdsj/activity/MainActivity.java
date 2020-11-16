package com.wwsl.mdsj.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxj.xpopup.XPopup;
import com.permissionx.guolindev.PermissionX;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.activity.common.SearchActivity;
import com.wwsl.mdsj.activity.live.LiveAnchorActivity;
import com.wwsl.mdsj.activity.login.LauncherActivity;
import com.wwsl.mdsj.activity.main.MainHomeViewHolder;
import com.wwsl.mdsj.activity.main.MainHometownViewHolder;
import com.wwsl.mdsj.activity.main.MainMessageViewHolder;
import com.wwsl.mdsj.activity.me.MainMeViewHolder;
import com.wwsl.mdsj.activity.me.UserVipActivity;
import com.wwsl.mdsj.activity.me.user.UserIdentifyActivity;
import com.wwsl.mdsj.activity.message.ChatActivity;
import com.wwsl.mdsj.activity.video.VideoRecordActivity;
import com.wwsl.mdsj.adapter.ViewPagerAdapter;
import com.wwsl.mdsj.bean.BonusBean;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.LiveBean;
import com.wwsl.mdsj.bean.LiveGiftBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.custom.TabButton;
import com.wwsl.mdsj.custom.TabButtonGroup;
import com.wwsl.mdsj.dialog.MainStartDialogFragment;
import com.wwsl.mdsj.event.PersonHomePageChangeEvent;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.im.ImMessageUtil;
import com.wwsl.mdsj.im.ImNotifyMsgEvent;
import com.wwsl.mdsj.im.ImUnReadCountEvent;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.interfaces.MainStartChooseCallback;
import com.wwsl.mdsj.notification.IconBadgeNumManager;
import com.wwsl.mdsj.presenter.CheckLivePresenter;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.DownloadUtil;
import com.wwsl.mdsj.utils.GifCacheUtil;
import com.wwsl.mdsj.utils.LiveStorge;
import com.wwsl.mdsj.utils.LocationUtil;
import com.wwsl.mdsj.utils.SpUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.VersionUtil;
import com.wwsl.mdsj.utils.VideoStorage;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.utils.ZjUtils;
import com.wwsl.mdsj.views.AbsMainViewHolder;
import com.wwsl.mdsj.views.BonusViewHolder;
import com.wwsl.mdsj.views.VideoCommentViewHolder;
import com.wwsl.mdsj.views.dialog.InputPwdDialog;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import cn.jpush.im.android.api.JMessageClient;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends AbsActivity {

    private final static String TAG = "MainActivity";
    private ViewGroup mRootView;
    private TabButtonGroup mTabButtonGroup;
    private TabButton tabMessage;
    private ViewPager mViewPager;
    private AbsMainViewHolder[] mViewHolders;
    private CheckLivePresenter mCheckLivePresenter;
    private boolean mLoad;
    private long mLastClickBackTime;//上次点击back键的时间
    private ConstraintLayout downloadLayout;
    private RingProgressBar loadPb;

    private MainHomeViewHolder mainHomeViewHolder;
    private MainHometownViewHolder mainHometownViewHolder;
    private MainMessageViewHolder mainMessageViewHolder;
    private MainMeViewHolder mainMeViewHolder;

    private boolean isShowPermission = false;

    //更多评论
    private VideoCommentViewHolder mVideoCommentViewHolder;
    private CommonCallback<File> mDownloadGifCallback;
    private List<LiveGiftBean> giftList;
    private int giftLoadIndex = 0;
    private int giftDownloadCount = 0;
    private int curPage = 0;

    private IconBadgeNumManager setIconBadgeNumManager;
    private Badge badge;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void main(Bundle savedInstanceState) {

        ZjUtils.getInstance().init(this);

        mRootView = findViewById(R.id.rootView);
        mTabButtonGroup = findViewById(R.id.tab_group);
        downloadLayout = findViewById(R.id.downloadLayout);
        loadPb = findViewById(R.id.loadPb);

        mViewPager = findViewById(R.id.viewPager);
        tabMessage = findViewById(R.id.tabMessage);


        mViewHolders = new AbsMainViewHolder[4];
        setIconBadgeNumManager = new IconBadgeNumManager();
        mainHomeViewHolder = new MainHomeViewHolder(mContext, mViewPager, this);
        mainHometownViewHolder = new MainHometownViewHolder(mContext, mViewPager, this);
        mainMessageViewHolder = new MainMessageViewHolder(mContext, mViewPager, this);
        mainMeViewHolder = new MainMeViewHolder(mContext, mViewPager, this);

//        mViewHolders[0] = mainTestViewHolder;
        mViewHolders[0] = mainHomeViewHolder;
        mViewHolders[1] = mainHometownViewHolder;
        mViewHolders[2] = mainMessageViewHolder;
        mViewHolders[3] = mainMeViewHolder;


        List<View> list = new ArrayList<>();
        for (AbsMainViewHolder vh : mViewHolders) {
            addAllLifeCycleListener(vh.getLifeCycleListenerList());
            list.add(vh.getContentView());
        }


        mViewPager.setAdapter(new ViewPagerAdapter(list));
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0, length = mViewHolders.length; i < length; i++) {
                    if (i == position) {
                        mViewHolders[i].setShowed(true);
                    } else {
                        mViewHolders[i].setShowed(false);
                    }
                }

                if (curPage != position) {
                    mViewHolders[curPage].onPause();
                    mViewHolders[position].onResume();
                }

                curPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabButtonGroup.setViewPager(mViewPager);
        mTabButtonGroup.setOnNoFragmentClick(this::showStartDialog);

        EventBus.getDefault().register(this);
        //弹出公告

        //检查版本
        checkVersion();
        //极光登录
        loginIM();
        AppConfig.getInstance().setLaunched(true);

        mDownloadGifCallback = new CommonCallback<File>() {
            @Override
            public void callback(File file) {
                giftLoadIndex++;
                loadGiftFile();
            }
        };
        new Thread() {
            @Override
            public void run() {
                loadGiftList();
            }
        }.start();
        mTabButtonGroup.setSelected(mViewPager.getCurrentItem());

        badge = new QBadgeView(this).bindTarget(tabMessage);
        int unReadCount = JMessageClient.getAllUnReadMsgCount();
        if (unReadCount > 0) {
            badge.setBadgeNumber(unReadCount);
        } else {
            badge.hide(false);
        }
    }

    public void mainClick(View v) {
        if (!canClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_search:
                SearchActivity.forward(mContext);
                break;
        }
    }

    private void showStartDialog() {
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        MainStartDialogFragment dialogFragment = new MainStartDialogFragment();
                        dialogFragment.setMainStartChooseCallback(mMainStartChooseCallback);
                        dialogFragment.show(getSupportFragmentManager(), "MainStartDialogFragment");
                    } else {
                        ToastUtil.show("权限未授予,无法发布视频或直播");
                    }
                });

    }

    private MainStartChooseCallback mMainStartChooseCallback = new MainStartChooseCallback() {
        @Override
        public void onLiveClick() {
            if (AppConfig.getInstance().getUserBean().getIsIdIdentify() != 1) {
                ToastUtil.show("暂未身份认证,请先进行身份认证");
                UserIdentifyActivity.forward(MainActivity.this);
            } else {
                if (AppConfig.getInstance().getUserBean().getAuth() != 1) {
                    openLivePayDialog();
                } else {
                    HttpUtil.isActivityGoing(new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (info != null && info.length > 0) {
                                try {
                                    JSONObject jsonObject = JSON.parseObject(info[0]);
                                    LiveAnchorActivity.forward(mContext, jsonObject.getInteger("isAct"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onVideoClick() {
            if (AppConfig.getInstance().getUserBean().getCanUploadVideo() != 1) {
                ToastUtil.show("当前用户没有发布视频权限");
                UserVipActivity.forward(MainActivity.this);
            } else {
                startActivity(new Intent(mContext, VideoRecordActivity.class));
            }
        }
    };

    private void openLivePayDialog() {
        DialogUtil.showSimpleDialog(MainActivity.this, "直播", "开启直播需要支付1000豆丁", false, new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                dialog.dismiss();
                new XPopup.Builder(MainActivity.this)
                        .hasShadowBg(true)
                        .customAnimator(new DialogUtil.DialogAnimator())
                        .asCustom(new InputPwdDialog(MainActivity.this, "将要扣除1000豆丁", new OnDialogCallBackListener() {
                            @Override
                            public void onDialogViewClick(View view, Object object) {
                                String pwd = (String) object;
                                HttpUtil.livePay(pwd, new HttpCallback() {
                                    @Override
                                    public void onSuccess(int code, String msg, String[] info) {
                                        if (code == 0) {
                                            AppConfig.getInstance().getUserBean().setAuth(1);
                                            HttpUtil.isActivityGoing(new HttpCallback() {
                                                @Override
                                                public void onSuccess(int code, String msg, String[] info) {
                                                    if (info != null && info.length > 0) {
                                                        try {
                                                            org.json.JSONObject jsonObject = new org.json.JSONObject(info[0]);
                                                            LiveAnchorActivity.forward(mContext, jsonObject.getInt("isAct"));
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            ToastUtil.show(msg);
                                        }
                                    }
                                });
                            }
                        }))
                        .show();
            }
        });
    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        HttpUtil.getPromote(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) return;
                try {
                    JSONArray array = new JSONArray(Arrays.toString(info));
                    for (int i = 0; i < array.length(); i++) {
                        org.json.JSONObject object = new org.json.JSONObject(array.optString(i));
                        if (object.optInt("link_type") == 0) {
                            DialogUtil.showPromoteHome(mActivity, object);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        AppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (configBean.getMaintainSwitch() == 1) {//开启维护
                        DialogUtil.showSimpleTipDialog(mContext, WordUtil.getString(R.string.main_maintain_notice), configBean.getMaintainTips());
                    }
                    if (!VersionUtil.isLatest(configBean.getVersion())) {
                        VersionUtil.showDialog(mContext, true, configBean.getVersion(), configBean.getUpdateDes(), (view, object) -> {
                            int i = (int) object;
                            if (0 == i) {
                                showInvitationCode();
                            } else if (1 == i) {
                                downloadNewApk(configBean);
                            }
                        });
                    } else {
                        showInvitationCode();
                    }
                }
            }
        });
    }

    private void downloadNewApk(ConfigBean configBean) {
        String url = configBean.getUpdateApkUrl();
        if (!TextUtils.isEmpty(url)) {
            try {
                DownloadUtil downloadUtil = new DownloadUtil();
                downloadLayout.setVisibility(View.VISIBLE);
                downloadUtil.download(Constants.APP_APK, mContext.getFilesDir(), Constants.APK_FILE_NAME, url, new DownloadUtil.Callback() {
                    @Override
                    public void onSuccess(File file) {
                        downloadLayout.setVisibility(View.GONE);
//                                                VersionUtil.installApk(mContext,file.getPath());
                        VersionUtil.installNormal(MainActivity.this, file.getPath());
                    }

                    @Override
                    public void onProgress(int progress) {
                        loadPb.setProgress(progress);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.show("下载失败");
                        downloadLayout.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                ToastUtil.show(R.string.version_download_url_error);
            }
        } else {
            ToastUtil.show(R.string.version_download_url_error);
        }
    }

    /**
     * 填写邀请码
     */
    private void showInvitationCode() {
        ConfigBean configBean = AppConfig.getInstance().getConfig();
        UserBean userBean = AppConfig.getInstance().getUserBean();
        if (configBean != null && userBean != null) {
            if ("1".equals(configBean.getIsRegCode())) {
                //必填
                if (!"1".equals(userBean.getIsHaveCode())) {
                    //未填，弹框
                    DialogUtil.showInputInviteDialog(MainActivity.this);
                } else {
                    //签到奖励
//                    requestBonus();
                }
            }
        }
    }

    /**
     * 签到奖励
     */
    private void requestBonus() {
        HttpUtil.requestBonus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (obj.getIntValue("bonus_switch") == 0) {
                        return;
                    }
                    int day = obj.getIntValue("bonus_day");
                    if (day <= 0) {
                        return;
                    }
                    List<BonusBean> list = JSON.parseArray(obj.getString("bonus_list"), BonusBean.class);
                    BonusViewHolder bonusViewHolder = new BonusViewHolder(mContext, mRootView);
                    bonusViewHolder.setData(list, day, obj.getString("count_day"));
                    bonusViewHolder.show();
                }
            }
        });
    }

    /**
     * 登录IM
     */
    private void loginIM() {
        String uid = AppConfig.getInstance().getUid();
        ImMessageUtil.getInstance().loginJMessage(uid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SpUtil.getInstance().removeValue(Constants.CHAT_STATUS);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancel("mdsj", 100087);
        }

        if (!isShowPermission) {
            PermissionX.init(this)
                    .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE)
                    .request((allGranted, grantedList, deniedList) -> {
                        isShowPermission = true;
                        if (grantedList.size() > 0) {
                            if (grantedList.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                //开启定位
                                LocationUtil.getInstance().startLocation();
                            }
                        }
                    });
        }

        LauncherActivity.finishActivity();

        mViewHolders[mViewPager.getCurrentItem()].onResume();
//
//        MainHomeViewHolder mvh = (MainHomeViewHolder) mViewHolders[0];
//        if (!mLoad) {
//            mLoad = true;
//            if (ImPushUtil.getInstance().isClickNotification()) {//MainActivity是点击通知打开的
//                ImPushUtil.getInstance().setClickNotification(false);
//                int notificationType = ImPushUtil.getInstance().getNotificationType();
//                switch (notificationType) {
//                    case Constants.JPUSH_TYPE_LIVE:
//                        mvh.setCurrentPage(0);
//                        break;
//                    case Constants.JPUSH_TYPE_MESSAGE:
//                        mvh.setCurrentPage(0);
//                        ChatActivity.forward(mContext);
//                        break;
//                }
//            } else {
//                mvh.setCurrentPage(0);
//            }
//            getLocation();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewHolders[mViewPager.getCurrentItem()].onPause();
    }

    @Override
    protected void onDestroy() {
        if (mTabButtonGroup != null) {
            mTabButtonGroup.cancelAnim();
        }
        EventBus.getDefault().unregister(this);
        HttpUtil.cancel(HttpConst.GET_CONFIG);
        HttpUtil.cancel(HttpConst.REQUEST_BONUS);
        HttpUtil.cancel(HttpConst.GET_BONUS);
        HttpUtil.cancel(HttpConst.SET_DISTRIBUT);
        HttpUtil.cancel(HttpConst.ACTIVITY_GOING);
        if (mCheckLivePresenter != null) {
            mCheckLivePresenter.cancel();
        }
        LocationUtil.getInstance().stopLocation();

        AppConfig.getInstance().setGiftList(null);
        AppConfig.getInstance().setLaunched(false);
        LiveStorge.getInstance().clear();
        VideoStorage.getInstance().clear();
        mViewHolders[mViewPager.getCurrentItem()].onDestroy();
        super.onDestroy();
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, String key, int position) {
        if (liveBean.getIslive() != null && liveBean.getIslive().equals("1")) {
            if (mCheckLivePresenter == null) {
                mCheckLivePresenter = new CheckLivePresenter(mContext);
            }
            mCheckLivePresenter.watchLive(liveBean, key, position);
        } else {
            ToastUtil.show("直播已经结束!!");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (!TextUtils.isEmpty(unReadCount)) {
            if (mainMessageViewHolder != null) {
                mainMessageViewHolder.updateUnreadNum(unReadCount);
            }
            int i = Integer.parseInt(unReadCount);
            if (i > 0) {
                badge.setBadgeNumber(i);
            } else {
                badge.hide(false);
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImBackEvent(ImNotifyMsgEvent e) {
        sendIconNumNotification(e);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPersonHomeChangeEvent(PersonHomePageChangeEvent event) {
        if (mViewPager.getCurrentItem() == 0 && null != mainHomeViewHolder) {
            mainHomeViewHolder.updateUserInfo(event.getUid());
        } else if (mViewPager.getCurrentItem() == 3) {
            UserHomePageActivity.forward(this, event.getUid());
        }
    }

    /**
     * 显示“动态”中的 更多评论
     */
    @Override
    public void showTrendCommentMore(VideoBean videoBean, int position) {
        super.showTrendCommentMore(videoBean, position);
        mVideoCommentViewHolder = new VideoCommentViewHolder(mContext, mRootView);
        mVideoCommentViewHolder.addToParent();
        mVideoCommentViewHolder.setVideoBean(videoBean);
        mVideoCommentViewHolder.showBottom();
    }

    @Override
    public void onBackPressed() {
        if (mVideoCommentViewHolder != null) {
            if (mVideoCommentViewHolder.isShowing()) {
                mVideoCommentViewHolder.hideBottom();
                return;
            }
        }

        if (mViewPager.getCurrentItem() == 0 && mViewHolders[0].onBackPressed()) {
            return;
        }

        long curTime = System.currentTimeMillis();
        if (curTime - mLastClickBackTime > 2000) {
            mLastClickBackTime = curTime;
            ToastUtil.show(R.string.main_click_next_exit);
            return;
        }

        super.onBackPressed();
    }

    /**
     * 获取礼物信息
     */
    private void loadGiftList() {
        giftList = AppConfig.getInstance().getGiftList();
        if (giftList == null) {
            HttpUtil.getGiftList(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        giftList = JSON.parseArray(obj.getString("giftlist"), LiveGiftBean.class);
                        AppConfig.getInstance().setGiftList(giftList);
                        loadGiftFile();
                    }
                }

                @Override
                public void onFinish() {

                }
            });
        } else {
            loadGiftFile();
        }
    }

    /**
     * 加载礼物文件
     */
    private void loadGiftFile() {
        if (giftDownloadCount < 3) {
            if (giftLoadIndex < giftList.size()) {
                for (int i = giftLoadIndex; i < giftList.size(); i++) {
                    LiveGiftBean bean = giftList.get(i);
                    if (bean.getType() == 1 && !TextUtils.isEmpty(bean.getSwf()) && (bean.getSwf().endsWith(".gif") || bean.getSwf().endsWith(".svga"))) {
                        GifCacheUtil.getFile(Constants.GIF_GIFT_PREFIX + bean.getId(), bean.getSwf(), mDownloadGifCallback);
                        giftLoadIndex = i;
                        break;
                    }
                }
            } else {
                giftDownloadCount++;
                giftLoadIndex = 0;
                loadGiftFile();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.REQUEST_CODE_SCAN) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    String userTempId = StrUtil.subAfter(result, "userId=", true);
                    UserHomePageActivity.forward(mContext, userTempId);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtil.show("解析二维码失败");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public int getCurrentItem() {
        if (mViewPager != null) {
            return mViewPager.getCurrentItem();
        }
        return -1;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    private void sendIconNumNotification(@NotNull ImNotifyMsgEvent event) {
        String title = event.getUsername();
        if (StringUtil.isEmpty(title)) title = "毛豆视界";
        String uid = event.getUid();
        String text = event.getLastMessage();
        int number = event.getUnReadCount();

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        String notificationChannelId = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = createNotificationChannel(uid);
            nm.createNotificationChannel(notificationChannel);
            notificationChannelId = notificationChannel.getId();
        }
        try {
            /*Message message = event.getEvent().getMessage();
            org.json.JSONObject eObj = new org.json.JSONObject(message.toJson());
            UserInfo fromUser = message.getFromUser();
            UserBean userBean = UserBean.builder()
                    .id(eObj.optString("from_id").split("_")[1])
                    .avatar(AppConfig.getInstance().getUserBean().getAvatar())//临时属性
                    .username(eObj.optString("from_name"))
                    .build();
            Intent intent = new Intent(AppContext.sInstance, ChatRoomActivity.class);
            intent.putExtra(Constants.USER_BEAN, userBean);
            intent.putExtra(Constants.FOLLOW, false);
            //分享的视频/红包
            if (eObj.optString("contentType").equals("custom")) {
                org.json.JSONObject object = new org.json.JSONObject(eObj.optString("msg_body"));
                if (object.has("CUSTOM_VIDEO_ID") && object.has("CUSTOM_VIDEO_COVER") && object.has("CUSTOM_VIDEO_TITLE")) {
                    String setId = object.optString("CUSTOM_VIDEO_ID");
                    String setCoverUrl = object.optString("CUSTOM_VIDEO_COVER");
                    String setTitle = object.optString("CUSTOM_VIDEO_TITLE");
                    if (setId.length() != 0 && setCoverUrl.length() != 0 && setTitle.length() != 0) {
                        VideoBean videoBean = new VideoBean();
                        videoBean.setId(setId);
                        videoBean.setCoverUrl(setCoverUrl);
                        videoBean.setTitle(setTitle);
                        videoBean.setUserBean(AppConfig.getInstance().getUserBean());
                        intent.putExtra(Constants.USER_VIDEO_BEAN, videoBean);//分享的视频
                    }
                }
            }*/

            Intent intent = new Intent(AppContext.sInstance, ChatActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

            Notification notification = new NotificationCompat.Builder(this, notificationChannelId)
                    .setSmallIcon(getApplicationInfo().icon)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setNumber(number)//number设计用来显示同种通知的数量和ContentInfo的位置一样，如果设置了ContentInfo则number会被隐藏
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build();
            // TODO: 2020/8/28 角标不消失暂时去掉
//            notification = setIconBadgeNumManager.setIconBadgeNum(getApplication(), notification, unreadNum);
            nm.notify("mdsj", 100087, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static NotificationChannel createNotificationChannel(String uid) {
        String channelId = "mdsj";
        NotificationChannel channel = new NotificationChannel(channelId, uid, NotificationManager.IMPORTANCE_HIGH);//重要通知
        channel.enableLights(true); //是否在桌面icon右上角展示小红点
        channel.setLightColor(Color.RED); //小红点颜色
        channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
        return channel;
    }

}
