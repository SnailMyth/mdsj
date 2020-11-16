package com.wwsl.mdsj;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Trace;
import android.text.Html;
import android.text.TextUtils;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.lzy.okgo.model.HttpHeaders;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.LevelBean;
import com.wwsl.mdsj.bean.LiveGiftBean;
import com.wwsl.mdsj.bean.MobileBean;
import com.wwsl.mdsj.bean.PartnerCityBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.bean.VideoTagBean;
import com.wwsl.mdsj.bean.VideoTypeBean;
import com.wwsl.mdsj.bean.net.MdBaseDataBean;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.im.ImMessageUtil;
import com.wwsl.mdsj.im.ImPushUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.SpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :
 * @date : 2020/7/25 14:42
 * @description : AppConfig
 */
public class AppConfig {
    //视频宽高比例
    public static final float mVideoRadio = 1.78f;

    private MobileBean mobileBean;
    private HttpHeaders httpHeaders;

    /**
     * 视频高度
     */
    public static int getVidowHeight() {
        return (int) (AppContext.sInstance.getResources().getDisplayMetrics().widthPixels / 2 * mVideoRadio);
    }

    //域名
    public static final String HOST = BuildConfig.SERVER_URL;

    //外部sd卡
    public static final String DCMI_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //内部存储 /data/data/<application package>/files目录
    public static final String INNER_PATH = AppContext.sInstance.getFilesDir().getAbsolutePath();
    //文件夹名字
    private static final String DIR_NAME = "mdsj";
    //保存视频的时候，在sd卡存储短视频的路径DCIM下
    public static final String VIDEO_PATH = DCMI_PATH + "/" + DIR_NAME + "/video/";
    //下载缓存路径
    public static final String MEDIA_CACHE_PATH = DCMI_PATH + "/" + DIR_NAME + "/cache/";

    //下载缓存
    public static final String CACHE_PATH = INNER_PATH + "/cache/";

    //下载贴纸的时候保存的路径
    public static final String VIDEO_TIE_ZHI_PATH = DCMI_PATH + "/" + DIR_NAME + "/tiezhi/";
    //下载音乐的时候保存的路径
    public static final String MUSIC_PATH = DCMI_PATH + "/" + DIR_NAME + "/music/";
    //拍照时图片保存路径
    public static final String CAMERA_IMAGE_PATH = DCMI_PATH + "/" + DIR_NAME + "/camera/";

    public static final String GIF_PATH = INNER_PATH + "/gif/";

    //QQ登录是否与PC端互通
    public static final boolean QQ_LOGIN_WITH_PC = false;
    //是否使用游戏
    public static final boolean GAME_ENABLE = false;
    //系统消息图标是否使用app图标
    public static final boolean SYSTEM_MSG_APP_ICON = false;

    //下载视频后拼接视频地址
    public static final String joinPath = DCMI_PATH + "/logoVideo.mp4";

    private static AppConfig sInstance;

    private AppConfig() {
        mobileBean = new MobileBean();
        ;
    }

    public static AppConfig getInstance() {
        if (sInstance == null) {
            synchronized (AppConfig.class) {
                if (sInstance == null) {
                    sInstance = new AppConfig();
                }
            }
        }
        return sInstance;
    }

    private String mUid;
    private String mToken;
    private ConfigBean mConfig;
    private double mLng;
    private double mLat;
    private String mProvince;//省
    private String mCity;//市
    private String mDistrict;//区
    private UserBean mUserBean;
    private String mVersion;
    private boolean mLoginIM;//IM是否登录了
    private boolean mLaunched;//App是否启动了
    private String mJPushAppKey;//极光推送的AppKey
    private String mTxLocationKey;//腾讯定位，地图的AppKey
    private SparseArray<LevelBean> mLevelMap;
    private SparseArray<LevelBean> mAnchorLevelMap;
    private List<LiveGiftBean> mGiftList;
    private String todayPrice;//今日交易价格

    private List<VideoTagBean> videoTags;//视频标签
    private List<VideoTypeBean> videoTypes;//视频分类

    private boolean mFrontGround;

    private List<PartnerCityBean> cityBeans = new ArrayList<>();

    private String tgUrl;//推广url地址

    public String getTgUrl() {
        return tgUrl;
    }

    public void setTgUrl(String tgUrl) {
        this.tgUrl = tgUrl;
    }

    public String getUid() {
        if (TextUtils.isEmpty(mUid)) {
            String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(new String[]{SpUtil.UID, SpUtil.TOKEN});
            if (uidAndToken != null) {
                if (!TextUtils.isEmpty(uidAndToken[0]) && !TextUtils.isEmpty(uidAndToken[1])) {
                    mUid = uidAndToken[0];
                    mToken = uidAndToken[1];
                    return mUid;
                }
            } else {
                return "-1";
            }
        }
        return mUid;
    }

    public String getToken() {
        return mToken;
    }

    public String getCoinName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getCoinName();
        }
        return Constants.DIAMONDS;
    }

    public String getVotesName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getVotesName();
        }
        return Constants.VOTES;
    }

    public ConfigBean getConfig() {
        if (mConfig == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                mConfig = JSON.parseObject(configString, ConfigBean.class);
            }
        }
        return mConfig;
    }

    public void getConfig(CommonCallback<ConfigBean> callback) {
        if (callback == null) {
            return;
        }
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            callback.callback(configBean);
        } else {
            HttpUtil.getConfig(callback);
        }
    }

    public void setConfig(ConfigBean config) {
        mConfig = config;
    }

    public void setCityBeans(List<PartnerCityBean> cityBeans) {
        this.cityBeans = cityBeans;
    }

    public List<PartnerCityBean> getCityBeans() {
        return cityBeans;
    }

    /**
     * 经度
     */
    public double getLng() {
        if (mLng == 0) {
            String lng = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LNG);
            if (!TextUtils.isEmpty(lng)) {
                try {
                    mLng = Double.parseDouble(lng);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mLng = 0;
            }
        }
        return mLng;
    }

    /**
     * 纬度
     */
    public double getLat() {
        if (mLat == 0) {
            String lat = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LAT);
            if (!TextUtils.isEmpty(lat)) {
                try {
                    mLat = Double.parseDouble(lat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mLat;
    }

    /**
     * 省
     */
    public String getProvince() {
        if (TextUtils.isEmpty(mProvince)) {
            mProvince = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_PROVINCE);
        }
        return mProvince == null ? "" : mProvince;
    }

    /**
     * 市
     */
    public String getCity() {
        if (TextUtils.isEmpty(mCity)) {
            mCity = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_CITY);
        }
        return mCity == null ? "" : mCity;
    }

    /**
     * 区
     */
    public String getDistrict() {
        if (TextUtils.isEmpty(mDistrict)) {
            mDistrict = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_DISTRICT);
        }
        return mDistrict == null ? "" : mDistrict;
    }

    public void setUserBean(UserBean bean) {
        LogUtils.e(TAG, "setUserBean: ");
        mUserBean = bean;
    }

    private final static String TAG = "AppConfig";

    public UserBean getUserBean() {

        if (mUserBean == null) {
            String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
            if (!TextUtils.isEmpty(userBeanJson)) {
                mUserBean = JSON.parseObject(userBeanJson, UserBean.class);
            }
        }
        return mUserBean;
    }

    //是否具有橱窗
    public boolean canAddGood() {
        if (mUserBean != null) {
            return mUserBean.getIsHaveShowWindow() > 0;
        }
        return false;
    }


    //是否实名认证
    public boolean isIdentifyIdCard() {
        if (mUserBean != null) {
            return mUserBean.getIsIdIdentify() == 1;
        }
        return false;
    }

    /**
     * 设置登录信息
     */
    public void setLoginInfo(String uid, String token, boolean save) {
        LogUtils.e("登录成功", "uid------>" + uid);
        LogUtils.e("登录成功", "token------>" + token);
        mUid = uid;
        mToken = token;

        getHttpHeaders().put("uid", uid);
        getHttpHeaders().put("token", token);

        if (save) {
            Map<String, String> map = new HashMap<>();
            map.put(SpUtil.UID, uid);
            map.put(SpUtil.TOKEN, token);
            SpUtil.getInstance().setMultiStringValue(map);
        }
    }

    /**
     * 清除登录信息
     */
    public void clearLoginInfo() {
        mUid = null;
        mToken = null;
        mLoginIM = false;
        ImMessageUtil.getInstance().logoutEMClient();
        ImPushUtil.getInstance().logout();
        SpUtil.getInstance().clear();
    }


    /**
     * 设置位置信息
     *
     * @param lng      经度
     * @param lat      纬度
     * @param province 省
     * @param city     市
     */
    public void setLocationInfo(double lng, double lat, String province, String city, String district) {
        mLng = lng;
        mLat = lat;
        mProvince = province;
        mCity = city;
        mDistrict = district;
        Map<String, String> map = new HashMap<>();
        map.put(SpUtil.LOCATION_LNG, String.valueOf(lng));
        map.put(SpUtil.LOCATION_LAT, String.valueOf(lat));
        map.put(SpUtil.LOCATION_PROVINCE, province);
        map.put(SpUtil.LOCATION_CITY, city);
        map.put(SpUtil.LOCATION_DISTRICT, district);
        SpUtil.getInstance().setMultiStringValue(map);
    }


    public boolean isLoginIM() {
        return mLoginIM;
    }

    public void setLoginIM(boolean loginIM) {
        mLoginIM = loginIM;
    }

    /**
     * 获取版本号
     */
    public String getVersion() {
        if (TextUtils.isEmpty(mVersion)) {
            try {
                PackageManager manager = AppContext.sInstance.getPackageManager();
                PackageInfo info = manager.getPackageInfo(AppContext.sInstance.getPackageName(), 0);
                mVersion = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mVersion;
    }

    /**
     * 获取MetaData中的极光AppKey
     *
     * @return
     */
    public String getJPushAppKey() {
        if (mJPushAppKey == null) {
            mJPushAppKey = getMetaDataString("JPUSH_APPKEY");
        }
        return mJPushAppKey;
    }


    /**
     * 获取MetaData中的腾讯定位，地图的AppKey
     *
     * @return
     */
    public String getTxLocationKey() {
        if (mTxLocationKey == null) {
            mTxLocationKey = getMetaDataString("TencentMapSDK");
        }
        return mTxLocationKey;
    }

    private String getMetaDataString(String key) {
        String res = null;
        try {
            ApplicationInfo appInfo = AppContext.sInstance.getPackageManager().getApplicationInfo(AppContext.sInstance.getPackageName(), PackageManager.GET_META_DATA);
            res = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 保存用户等级信息
     */
    public void setLevel(String levelJson) {
        if (TextUtils.isEmpty(levelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(levelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mLevelMap == null) {
            mLevelMap = new SparseArray<>();
        }
        mLevelMap.clear();
        for (LevelBean bean : list) {
            mLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 保存主播等级信息
     */
    public void setAnchorLevel(String anchorLevelJson) {
        if (TextUtils.isEmpty(anchorLevelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(anchorLevelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mAnchorLevelMap == null) {
            mAnchorLevelMap = new SparseArray<>();
        }
        mAnchorLevelMap.clear();
        for (LevelBean bean : list) {
            mAnchorLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 获取用户等级
     */
    public LevelBean getLevel(int level) {
        if (mLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setLevel(obj.getString("level"));
            }
        }

        if (mLevelMap == null || mLevelMap.size() == 0) {
            return null;
        }
        return mLevelMap.get(level);
    }

    /**
     * 获取主播等级
     */
    public LevelBean getAnchorLevel(int level) {
        if (mAnchorLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setAnchorLevel(obj.getString("levelanchor"));
            }
        }
        if (mAnchorLevelMap == null || mAnchorLevelMap.size() == 0) {
            return null;
        }
        return mAnchorLevelMap.get(level);
    }

    public List<LiveGiftBean> getGiftList() {
        return mGiftList;
    }

    public void setGiftList(List<LiveGiftBean> giftList) {
        mGiftList = giftList;
    }

    /**
     * 判断某APP是否安装
     */
    public static boolean isAppExist(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            PackageManager manager = AppContext.sInstance.getPackageManager();
            List<PackageInfo> list = manager.getInstalledPackages(0);
            for (PackageInfo info : list) {
                if (packageName.equalsIgnoreCase(info.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isLaunched() {
        return mLaunched;
    }

    public void setLaunched(boolean launched) {
        mLaunched = launched;
    }

    //app是否在前台
    public boolean isFrontGround() {
        return mFrontGround;
    }

    //app是否在前台
    public void setFrontGround(boolean frontGround) {
        mFrontGround = frontGround;
    }

    /**
     * 清除定位信息
     */
    public void clearLocationInfo() {
        mLng = 0;
        mLat = 0;
        mProvince = null;
        mCity = null;
        mDistrict = null;
        SpUtil.getInstance().removeValue(
                SpUtil.LOCATION_LNG,
                SpUtil.LOCATION_LAT,
                SpUtil.LOCATION_PROVINCE,
                SpUtil.LOCATION_CITY,
                SpUtil.LOCATION_DISTRICT);

    }

    /**
     * 是否显示"心愿单"
     */
    public static boolean showWishBill() {
//        switch (BuildConfig.CHANNEL) {
//            case "mili":
//                return true;
//            default:
//                return false;
//        }
        return true;
    }

    /**
     * 是否显示"心愿单"，直播间中
     */
    public static boolean showWishBillBigLogo() {
        switch (BuildConfig.CHANNEL) {
            case "tctd":
                return true;
            default:
                return false;
        }
    }

    /**
     * 主界面第二栏显示内容
     *
     * @return 0 直播；1 短视频；2 动态
     */
    public static int showMainSecondType() {
        switch (BuildConfig.CHANNEL) {
            case "mili":
                return 1;
            default:
                return 2;
        }
    }

    /**
     * 在“我的”界面
     * 0，活动中心；1，排行；2，消息；
     */
    public static int showRankingOnMainMe() {
        switch (BuildConfig.CHANNEL) {
            case "mili":
                return 1;
            case "fengche":
                return 1;
            case "huatian":
                return 2;
            default:
                return 0;
        }
    }

    /**
     * 主界面是否显示“活动”
     */
    public static boolean showMainAction() {
        switch (BuildConfig.CHANNEL) {
            case "mili":
            case "fengche":
                return true;
            default:
                return false;
        }
    }

    /**
     * 隐藏拍摄图片
     */
    public static boolean hideTakePictureOnVideo() {
        switch (BuildConfig.CHANNEL) {
            case "mili":
            case "fengche":
            case "huatian":
                return true;
            default:
                return false;
        }
    }

    /**
     * 隐藏私信功能
     */
    public static boolean hideChatRoom() {
        switch (BuildConfig.CHANNEL) {
            case "mili":
                return true;
            default:
                return false;
        }
    }

    /**
     * 隐藏观众禁言信息
     */
    public static boolean hideShutUpForAudience() {
        switch (BuildConfig.CHANNEL) {
            case "mili":
            case "fengche":
            case "huatian":
                return true;
            case "tctd":
            case "tianjiaoa":
            case "qinglong":
            default:
                return false;
        }
    }

    /**
     * 是否上下滑动切换直播间
     */
    public static boolean liveRoomScroll() {
        return false;
    }

    /**
     * 支付宝充值时，是否显示APP名称
     */
    public static boolean aliPayShowAppName() {
        switch (BuildConfig.CHANNEL) {
            case "tctd":
                return true;
            default:
                return false;
        }
    }

    /**
     * 禁止定位功能（全部禁止）
     */
    public static boolean forbidLocation() {
        switch (BuildConfig.CHANNEL) {
            case "tctd":
            case "huatian":
            default:
                return true;
        }
    }

    /**
     * 守护文字重新定义
     */
    public static boolean guardWordModify() {
        switch (BuildConfig.CHANNEL) {
            case "tctd":
                return true;
            default:
                return false;
        }
    }

    /**
     * 将七天守护图标变灰
     */
    public static boolean grayGuardIcon() {
        switch (BuildConfig.CHANNEL) {
            case "mili":
                return true;
            default:
                return false;
        }
    }

    /**
     * 隐藏"动态"分享按钮
     */
    public static boolean hideTrendShare() {
        switch (BuildConfig.CHANNEL) {
            case "qinglong":
                return true;
            default:
                return false;
        }
    }

    public List<VideoTagBean> getVideoTags() {
        if (videoTags == null) {
            videoTags = new ArrayList<>();
        }
        return videoTags;
    }

    public void setVideoTags(List<VideoTagBean> beans) {
        if (null == videoTags) {
            videoTags = new ArrayList<>();
        }
        videoTags.clear();
        videoTags.addAll(beans);
    }

    public List<VideoTypeBean> getVideoTypes() {
        if (videoTypes == null) {
            videoTypes = new ArrayList<>();
        }
        return videoTypes;
    }

    public void setVideoTypes(List<VideoTypeBean> beans) {
        if (null == videoTypes) {
            videoTypes = new ArrayList<>();
        }
        videoTypes.clear();
        videoTypes.addAll(beans);
    }

    public String getShareText() {
        return getConfig().getVideoShareDes() + getQRContent() + "  邀请码:" + AppConfig.getInstance().getUserBean().getTgCode();
    }

    public String getQRContent() {
        String shareUrl = Html.fromHtml(getConfig().getShareUrl()).toString();
        if (shareUrl.contains("?")) {
            shareUrl += "&p_uid=" + getUid();
        } else {
            shareUrl += "?p_uid=" + getUid();
        }
        return shareUrl;
    }

    private MdBaseDataBean mdBaseDataBean;

    public MdBaseDataBean getMdBaseDataBean() {
        return mdBaseDataBean;
    }

    public void setMdBaseDataBean(MdBaseDataBean mdBaseDataBean) {
        this.mdBaseDataBean = mdBaseDataBean;
    }


    public boolean isBindWx() {
        return "1".equals(mUserBean.getIsWxAuth());
    }

    public boolean isCanPubYxYp() {
        return "1".equals(mUserBean.getIsCanPubYxYp());
    }

    public String getTodayPrice() {
        return todayPrice;
    }

    public void setTodayPrice(String todayPrice) {
        this.todayPrice = todayPrice;
    }

    public String getMarketUrl() {
        String url = mUserBean.getMarketUrl();
        if (url.contains("?")) {
            url += "&uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
        } else {
            url += "?uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
        }

        if (!url.contains("username")) {
            url += "&username=" + AppConfig.getInstance().getUserBean().getMobile();
        }
        return url;
    }

    public String getDeviceId() {
        return mobileBean.getDeviceId();
    }

    public void setDeviceId(String deviceId) {
        mobileBean.setDeviceId(deviceId);
        getHttpHeaders().put("device_id", deviceId);
    }

    public String getDeviceName() {
        return mobileBean.getDeviceName();
    }

    public MobileBean getMobileBean() {
        return mobileBean;
    }

    public HttpHeaders getHttpHeaders() {
        if (httpHeaders == null) {
            httpHeaders = new HttpHeaders();
            httpHeaders.put("version", BuildConfig.VERSION_NAME);
            httpHeaders.put("devicename", Build.BRAND);
            httpHeaders.put("devicemodel", Build.MODEL);
            httpHeaders.put("deviceid", "");
            httpHeaders.put("token", "");
            httpHeaders.put("uid", "");
        }
        return httpHeaders;
    }
}


