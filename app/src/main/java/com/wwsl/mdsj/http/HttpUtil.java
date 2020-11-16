package com.wwsl.mdsj.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.PartnerCityBean;
import com.wwsl.mdsj.bean.TxLocationBean;
import com.wwsl.mdsj.bean.TxLocationPoiBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.bean.net.MdBaseDataBean;
import com.wwsl.mdsj.event.VideoFollowEvent;
import com.wwsl.mdsj.im.ImPushUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.MD5Util;
import com.wwsl.mdsj.utils.SpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;

/**
 * @author :
 * @date : 2020/7/14 10:43
 * @description : HttpUtil
 */
public class HttpUtil {

    private static final String SALT = "76576076c1f5f657b634e966c8836a06";
    private static final String DEVICE = "android";
    private static final String VIDEO_SALT = "#2hgfk85cm23mk58vncsark";

    /**
     * 初始化
     */
    public static void init() {
        HttpClient.getInstance().init();
    }

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 使用腾讯定位sdk获取 位置信息
     *
     * @param lng 经度
     * @param lat 纬度
     * @param poi 是否要查询POI
     */
    public static void getAddressInfoByTxLocaitonSdk(final double lng, final double lat, final int poi, int pageIndex, String tag, final CommonCallback<TxLocationBean> commonCallback) {
        OkGo.<String>get("https://apis.map.qq.com/ws/geocoder/v1/")
                .params("key", AppConfig.getInstance().getTxLocationKey())
                .params("location", lat + "," + lng)
                .params("get_poi", poi)
                .params("poi_options", "address_format=short;radius=1000;page_size=20;page_index=" + pageIndex + ";policy=5")
                .tag(tag)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSON.parseObject(response.body());
                        if (obj.getIntValue("status") == 0) {
                            JSONObject result = obj.getJSONObject("result");
                            if (result != null) {
                                TxLocationBean bean = new TxLocationBean();
                                bean.setLng(lng);
                                bean.setLat(lat);
                                bean.setAddress(result.getString("address"));
                                JSONObject addressComponent = result.getJSONObject("address_component");
                                if (addressComponent != null) {
                                    bean.setNation(addressComponent.getString("nation"));
                                    bean.setProvince(addressComponent.getString("province"));
                                    bean.setCity(addressComponent.getString("city"));
                                    bean.setDistrict(addressComponent.getString("district"));
                                    bean.setStreet(addressComponent.getString("street"));
                                }
                                if (poi == 1) {
                                    List<TxLocationPoiBean> poiList = JSON.parseArray(result.getString("pois"), TxLocationPoiBean.class);
                                    bean.setPoiList(poiList);
                                }
                                if (commonCallback != null) {
                                    commonCallback.callback(bean);
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 使用腾讯地图API进行搜索
     *
     * @param lng 经度
     * @param lat 纬度
     */
    public static void searchAddressInfoByTxLocaitonSdk(final double lng, final double lat, String keyword, int pageIndex, final CommonCallback<List<TxLocationPoiBean>> commonCallback) {
        OkGo.<String>get("https://apis.map.qq.com/ws/place/v1/search?")
                .params("keyword", keyword)
                .params("boundary", "nearby(" + lat + "," + lng + ",1000)&orderby=_distance&page_size=20&page_index=" + pageIndex)
                .params("key", AppConfig.getInstance().getTxLocationKey())
                .tag(HttpConst.GET_MAP_SEARCH)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSON.parseObject(response.body());
                        if (obj.getIntValue("status") == 0) {
                            List<TxLocationPoiBean> poiList = JSON.parseArray(obj.getString("data"), TxLocationPoiBean.class);
                            if (commonCallback != null) {
                                commonCallback.callback(poiList);
                            }
                        }
                    }
                });
    }

    /**
     * 验证token是否过期
     */
    public static void ifToken(String uid, String token, HttpCallback callback) {
        HttpClient.getInstance().post("User.iftoken", HttpConst.IF_TOKEN)
                .params("uid", uid)
                .params("token", token)
                .execute(callback);
    }


    /**
     * 获取config
     */
    public static void getConfig(final CommonCallback<ConfigBean> commonCallback) {
        HttpClient.getInstance().post("Home.getConfig", HttpConst.GET_CONFIG).execute(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    try {
                        JSONObject obj = JSON.parseObject(info[0]);
                        ConfigBean bean = JSON.toJavaObject(obj, ConfigBean.class);
                        AppConfig.getInstance().setConfig(bean);
                        AppConfig.getInstance().setLevel(obj.getString("level"));
                        AppConfig.getInstance().setAnchorLevel(obj.getString("levelanchor"));
                        SpUtil.getInstance().setStringValue(SpUtil.CONFIG, info[0]);
                        returnFailConfig(commonCallback, bean);
                    } catch (Exception e) {
                        LogUtils.e(TAG, "GetConfig接口返回数据异常");
                        ToastUtil.show("配置获取失败");
                        returnFailConfig(commonCallback, null);
                    }
                } else {
                    returnFailConfig(commonCallback, null);
                }
            }

            @Override
            public void onError() {
                returnFailConfig(commonCallback, null);
            }
        });
    }

    private static void returnFailConfig(CommonCallback<ConfigBean> commonCallback, ConfigBean o) {
        if (commonCallback != null) {
            commonCallback.callback(o);
        }
    }

    /**
     * 获取城市json
     */
    public static void getCityConfig(String citylevel, String type, HttpCallback callback) {
        HttpClient.getInstance().post("Home.getAreaAll", HttpConst.GET_BASE_CITY_INFO)
                .params("citylevel", citylevel)
                .params("type", type)
                .execute(callback);
    }

    public static void getCityConfig() {
        HttpClient.getInstance().post("Home.getAreaAll", HttpConst.GET_BASE_CITY_INFO)
                .params("citylevel", "1")
                .params("type", "1")
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            List<PartnerCityBean> cityBeans = JSON.parseArray(Arrays.toString(info), PartnerCityBean.class);
                            SpUtil.getInstance().setStringValue(SpUtil.CITY, JSON.toJSONString(cityBeans));
                            AppConfig.getInstance().setCityBeans(cityBeans);
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
    }

    /**
     * 获取用户信息
     */
    public static void getBaseInfo(final CommonCallback<UserBean> commonCallback) {
        HttpClient.getInstance().post("User.getBaseInfo", HttpConst.GET_BASE_INFO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            UserBean bean = JSON.toJavaObject(obj, UserBean.class);
                            AppConfig.getInstance().setUserBean(bean);
                            SpUtil.getInstance().setStringValue(SpUtil.USER_INFO, info[0]);
                            if (commonCallback != null) {
                                commonCallback.callback(bean);
                            }
                        } else {
                            commonCallback.callback(null);
                        }
                    }

                    @Override
                    public void onError() {
                        if (commonCallback != null) {
                            commonCallback.callback(null);
                        }
                    }
                });
    }

    public static void getMDBaseInfo(HttpCallback callback) {
        HttpClient.getInstance().post("activity.getUserActivity", HttpConst.GET_USER_ACTIVE_ALL)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    public static void getTaskCutTime(HttpCallback callback) {
        HttpClient.getInstance().post("Editionapi.getTaskNums", HttpConst.GET_USER_ACTIVE_ALL)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    //获取区块链信息
    public static void getMDBaseInfo() {
        HttpClient.getInstance().post("activity.getUserActivity", HttpConst.GET_USER_ACTIVE_ALL)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 200 && null != info && info.length > 0) {
                            MdBaseDataBean parse = JSON.parseObject(info[0], MdBaseDataBean.class);
                            if (null != parse) {
                                AppConfig.getInstance().setMdBaseDataBean(parse);
                            }
                        }
                    }
                });
    }


    /**
     * 手机号 密码登录
     */
    public static void login(String phoneNum, String pwd, HttpCallback callback) {
        HttpClient.getInstance().post("Login.userLogin", HttpConst.LOGIN)
                .params("user_login", phoneNum)
                .params("user_pass", pwd)
                .params("pushid", ImPushUtil.getInstance().getPushID())
                .execute(callback);
    }

    /**
     * 手机号 密码登录
     */
    public static void quickLogin(String mobile, String code, HttpCallback callback) {
        HttpClient.getInstance().post("Login.userOnekeyLogin", HttpConst.QUICK_LOGIN)
                .params("mobile", mobile)
                .params("code", code)
                .params("source", DEVICE)
                .params("pushid", ImPushUtil.getInstance().getPushID())
                .execute(callback);
    }


    /**
     * 微信登录绑定手机号码
     */
    public static void bindLogin(String mobile, String wechatUid, String code, HttpCallback callback) {
        HttpClient.getInstance().post("Login.wechatLoginBind", HttpConst.QUICK_LOGIN)
                .params("mobile", mobile)
                .params("code", code)
                .params("source", DEVICE)
                .params("wechat_uid", wechatUid)
                .params("pushid", ImPushUtil.getInstance().getPushID())
                .execute(callback);
    }


    /**
     * 第三方登录
     */
    public static void loginByWx(Map<String, String> map, HttpCallback callback) {
        PostRequest<JsonBean> params = HttpClient.getInstance().post("Login.wechatLogin", HttpConst.LOGIN_BY_THIRD)
                .params("source", DEVICE)
                .params("pushid", ImPushUtil.getInstance().getPushID());

        String openid = map.get("openid");
        String unionid = map.get("unionid");
        String nickname = map.get("name");
        String avatar = map.get("iconurl");

//        if (StrUtil.isEmpty(openid) || StrUtil.isEmpty(unionid) || StrUtil.isEmpty(nickname) || StrUtil.isEmpty(avatar)) {
//            ToastUtil.show("微信授权信息获取失败,请重试!");
//            callback.onError();
//            return;
//        }

        if (StrUtil.isEmpty(openid) || StrUtil.isEmpty(unionid) || StrUtil.isEmpty(nickname)) {
            ToastUtil.show("微信授权信息获取失败,请重试!");
            callback.onError();
            return;
        }

        params.params("openid", openid);
        params.params("unionid", unionid);
        params.params("nickname", nickname);
        params.params("avatar", avatar);

//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            params.params(entry.getKey(), entry.getValue());
//        }
        params.execute(callback);
    }


    /**
     * QQ登录的时候 获取unionID 与PC端互通的时候用
     */
    public static void getQQLoginUnionID(String accessToken, final CommonCallback<String> commonCallback) {
        OkGo.<String>get("https://graph.qq.com/oauth2.0/me?access_token=" + accessToken + "&unionid=1")
                .tag(HttpConst.GET_QQ_LOGIN_UNION_ID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (commonCallback != null) {
                            String data = response.body();
                            data = data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1);
                            L.e("getQQLoginUnionID------>" + data);
                            JSONObject obj = JSON.parseObject(data);
                            commonCallback.callback(obj.getString("unionid"));
                        }
                    }
                });
    }

    /**
     * 绑定微信
     */
    public static void bindWx(String openId, String unionId, String nickname, String avatar, HttpCallback callback) {
        HttpClient.getInstance().post("login.loginBind", HttpConst.GET_REGISTER_CODE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("openid", openId)
                .params("union_id", unionId)
                .params("nickname", nickname)
                .params("avatar", avatar)
                .execute(callback);
    }


    /**
     * 获取验证码接口 注册用
     */
    public static void getRegisterCode(String mobile, HttpCallback callback) {
        String sign = MD5Util.getMD5("mobile=" + mobile + "&" + SALT);
        HttpClient.getInstance().post("Login.getCode", HttpConst.GET_REGISTER_CODE)
                .params("mobile", mobile)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 获取验证码接口
     */
    public static void getBindPhoneCode(String mobile, HttpCallback callback) {
        String sign = MD5Util.getMD5("mobile=" + mobile + "&" + SALT);
        HttpClient.getInstance().post("Login.getWechatCode", HttpConst.GET_REGISTER_CODE)
                .params("mobile", mobile)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 获取快捷登录获取验证码
     */
    public static void getQuickLoginCode(String mobile, HttpCallback callback) {
        String sign = MD5Util.getMD5("mobile=" + mobile + "&" + SALT);
        HttpClient.getInstance().post("Login.getOnekeyCode", HttpConst.GET_QUICK_LOGIN_CODE)
                .params("mobile", mobile)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 银行卡绑定获取验证码
     */
    public static void getBankBindCode(String mobile, HttpCallback callback) {
        String sign = MD5Util.getMD5("mobile=" + mobile + "&" + SALT);
        HttpClient.getInstance().post("Login.getAddBankCardCode", HttpConst.GET_BANK_CARD_CODE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("mobile", mobile)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 找回交易密码获取验证码
     */
    public static void getFindPayPwdBindCode(String mobile, HttpCallback callback) {
        String sign = MD5Util.getMD5("mobile=" + mobile + "&" + SALT);
        HttpClient.getInstance().post("Login.getAqpassCode", HttpConst.GET_BANK_CARD_CODE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("mobile", mobile)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 找回交易密码获取验证码
     */
    public static void findPayPwdBindCode(String mobile, String pass, String code, HttpCallback callback) {
        HttpClient.getInstance().post("User.getAqPass", HttpConst.GET_BANK_CARD_CODE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("mobile", mobile)
                .params("pass", pass)
                .params("code", code)
                .execute(callback);
    }


    /**
     * 获取充值链接
     */
    public static void getChargeUrl(String type, String czname, String czly, String changeId, String money, String coin, HttpCallback callback) {
        HttpClient.getInstance().post("User.chongZhi4", HttpConst.CHONG_ZHI)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("changeid", changeId)
                .params("money", money)
                .params("coin", coin)
                .params("type", type)
                .params("czname", czname)
                .params("czly", czly)
                .execute(callback);
    }


    /**
     * 手机注册接口
     */
    public static void register(String user_login, String pass, String pass2, String code, HttpCallback callback) {
        HttpClient.getInstance().post("Login.userReg", HttpConst.REGISTER)
                .params("user_login", user_login)
                .params("user_pass", pass)
                .params("user_pass2", pass2)
                .params("code", code)
                .params("source", DEVICE)
                .execute(callback);
    }

    /**
     * 找回密码
     */
    public static void findPwd(String user_login, String pass, String pass2, String code, HttpCallback callback) {
        HttpClient.getInstance().post("Login.userFindPass", HttpConst.FIND_PWD)
                .params("user_login", user_login)
                .params("user_pass", pass)
                .params("user_pass2", pass2)
                .params("code", code)
                .execute(callback);
    }


    /**
     * 重置密码
     */
    public static void modifyPwd(String oldpass, String pass, String pass2, HttpCallback callback) {
        HttpClient.getInstance().post("User.updatePass", HttpConst.MODIFY_PWD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("oldpass", oldpass)
                .params("pass", pass)
                .params("pass2", pass2)
                .execute(callback);
    }

    /**
     * 重置密码
     */
    public static void setPayPwd(String pass, HttpCallback callback) {
        HttpClient.getInstance().post("user.setcoinuserpassword", HttpConst.MODIFY_PWD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("password", pass)
                .execute(callback);
    }


    /**
     * 获取验证码接口 找回密码用
     */
    public static void getFindPwdCode(String mobile, HttpCallback callback) {
        String sign = MD5Util.getMD5("mobile=" + mobile + "&" + SALT);
        HttpClient.getInstance().post("Login.getForgetCode", HttpConst.GET_FIND_PWD_CODE)
                .params("mobile", mobile)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 首页直播
     */
    public static void getHot(int p, HttpCallback callback) {
        HttpClient.getInstance().post("Home.getHot", HttpConst.GET_HOT)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 获取直播列表
     *
     * @param p
     * @param callback
     */
    public static void getLiveList(int p, HttpCallback callback) {
        HttpClient.getInstance().post("Home.getLiveAll", HttpConst.GET_LIVING)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 分类直播
     */
    public static void getClassLive(int classId, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Home.getClassLive", HttpConst.GET_CLASS_LIVE)
                .params("liveclassid", classId)
                .params("p", p)
                .execute(callback);
    }


    //排行  收益榜
    public static void profitList(String type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Home.profitList", HttpConst.PROFIT_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", p)
                .execute(callback);
    }

    //排行  贡献榜
    public static void consumeList(String type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Home.consumeList", HttpConst.CONSUME_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", p)
                .execute(callback);
    }

    public static void getMagnateList(String type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Home.chongZhiList", HttpConst.CONSUME_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", p)
                .execute(callback);
    }

    public static void getGamblerList(String type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Home.duShenList", HttpConst.CONSUME_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", p)
                .execute(callback);
    }

    //礼物贡献榜
    public static void consumeList(String touid, String type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Home.consumeList", HttpConst.CONSUME_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", p)
                .params("touid", touid)
                .execute(callback);

    }

    /**
     * 关注别人 或 取消对别人的关注的接口
     */
    public static void setAttention(int from, String touid, CommonCallback<Integer> callback) {
        setAttention(HttpConst.SET_ATTENTION, from, touid, callback);
    }

    /**
     * 关注别人 或 取消对别人的关注的接口
     */
    public static void setAttention(String tag, final int from, final String touid, final CommonCallback<Integer> callback) {
        if (touid.equals(AppConfig.getInstance().getUid())) {
            ToastUtil.show(WordUtil.getString(R.string.cannot_follow_self));
            return;
        }
        HttpClient.getInstance().post("User.setAttent", tag)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            int isAttention = JSON.parseObject(info[0]).getIntValue("isattent");//1是 关注  0是未关注
                            EventBus.getDefault().post(new VideoFollowEvent(from, touid, isAttention));
                            if (callback != null) {
                                callback.callback(isAttention);
                            }
                        }
                    }
                });
    }

    /**
     * 上传头像，用post
     */
    public static void updateAvatar(File file, HttpCallback callback) {
        HttpClient.getInstance().post("User.updateAvatar", HttpConst.UPDATE_AVATAR)
                .isMultipart(true)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("file", file)
                .execute(callback);
    }

    /**
     * 主播封面
     */
    public static void updateCover(File file, HttpCallback callback) {
        HttpClient.getInstance().post("User.uptLiveThumb", HttpConst.UPDATE_COVER)
                .isMultipart(true)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("file", file)
                .execute(callback);
    }

    /**
     * 更新用户资料
     *
     * @param fields 用户资料 ,以json形式出现
     */
    public static void updateFields(String fields, HttpCallback callback) {
        HttpClient.getInstance().post("User.updateFields", HttpConst.UPDATE_FIELDS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("fields", fields)
                .execute(callback);
    }


    public static void updateUserData(int type, String content, HttpCallback callback) {
        String fields = null;
        switch (type) {
            case HttpConst.USER_INFO_NICKNAME:
                fields = "{\"user_nicename\":\"" + content + "\"}";
                break;
            case HttpConst.USER_INFO_EMAIL:
                fields = "{\"user_email\":\"" + content + "\"}";
                break;
            case HttpConst.USER_INFO_SIGNATURE:
                fields = "{\"signature\":\"" + content + "\"}";
                break;
            case HttpConst.USER_INFO_MOBILE:
                fields = "{\"mobile\":\"" + content + "\"}";
                break;
            case HttpConst.USER_INFO_PHONE_PUBLIC:
                fields = "{\"is_eye_phone\":\"" + content + "\"}";
                break;
            default:
                break;
        }
        HttpClient.getInstance().post("User.setOtherUpdate", HttpConst.UPDATE_FIELDS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("fields", fields)
                .execute(callback);
    }


    /**
     * 获取对方的关注列表
     *
     * @param touid 对方的uid
     */
    public static void getFollowList(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("User.getFollowsList", HttpConst.GET_FOLLOW_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取对方的粉丝列表
     * <p>
     * touid 对方的uid
     */
    public static void getFansList(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("User.getFansList", HttpConst.GET_FANS_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取对方的粉丝列表
     * <p>
     * touid 对方的uid
     */
    public static void getFriendsList(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("User.getFriendsList", HttpConst.GET_FANS_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 粉我列表
     * getFansList -> getMessageList
     * 新增 type:3
     */
    public static void getMyFansList(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("User.getMessageList", HttpConst.GET_FANS_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("p", p)
                .params("type", 3)
                .execute(callback);
    }

    /**
     * 获取对方的粉丝列表
     * getAtme -> getMessageList
     * 新增 type:2
     */
    public static void getAtMeList(String uid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("User.getAtme", HttpConst.GET_AT_ME_LIST)
                .params("uid", uid)
                .params("p", p)
                .params("type", 2)
                .execute(callback);
    }

    /**
     * 获取用户的评论列表
     * getMyComments -> getMessageList
     * 新增 type:1
     */
    public static void getComments(String uid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("User.getMessageList", HttpConst.GET_COMMENT_LIST)
                .params("uid", uid)
                .params("p", p)
                .params("type", 1)
                .execute(callback);
    }

    /**
     * 获取用户的被赞列表
     * getLikeMyVideo -> getMessageList
     * 新增 type:0
     */
    public static void getFavorite(String uid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("User.getMessageList", HttpConst.GET_LIKE_LIST)
                .params("uid", uid)
                .params("p", p)
                .params("type", 0)
                .execute(callback);
    }


    /**
     * 获取用户的直播记录
     *
     * @param touid 对方的uid
     */
    public static void getLiveRecord(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("User.getLiverecord", HttpConst.GET_LIVE_RECORD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取个性设置列表
     */
    public static void getSettingList(HttpCallback callback) {
        HttpClient.getInstance().post("User.getPerSetting", HttpConst.GET_SETTING_LIST)
                .execute(callback);
    }

    /**
     * 请求签到奖励
     */
    public static void requestBonus(HttpCallback callback) {
        HttpClient.getInstance().post("User.Bonus", HttpConst.REQUEST_BONUS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取签到奖励
     */
    public static void getBonus(HttpCallback callback) {
        HttpClient.getInstance().post("User.getBonus", HttpConst.GET_BONUS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 主播开播
     *
     * @param title    直播标题
     * @param type     直播类型 普通 密码 收费等
     * @param typeVal  密码 价格等
     * @param file     封面图片文件
     * @param callback
     */
    public static void createRoom(String title, int liveClassId, int type, int typeVal, File file, String city, String goodIds, HttpCallback callback) {
        AppConfig appConfig = AppConfig.getInstance();
        UserBean u = appConfig.getUserBean();
        if (u == null) {
            return;
        }
        PostRequest<JsonBean> request = HttpClient.getInstance().post("Live.createRoom", HttpConst.CREATE_ROOM)
                .params("uid", appConfig.getUid())
                .params("token", appConfig.getToken())
                .params("user_nicename", u.getUsername())
                .params("avatar", u.getAvatar())
                .params("avatar_thumb", u.getAvatarThumb())
                .params("city", city)
                .params("province", appConfig.getProvince())
                .params("lat", appConfig.getLat())
                .params("lng", appConfig.getLng())
                .params("title", title)
                .params("liveclassid", liveClassId)
                .params("type", type)
                .params("type_val", typeVal)
                .params("good_ids", goodIds);
        if (file != null) {
            request.params("file", file);
        }
        request.execute(callback);
    }

    /**
     * 修改直播状态
     */
    public static void changeLive(String stream) {
        HttpClient.getInstance().post("Live.changeLive", HttpConst.CHANGE_LIVE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("status", "1")
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        L.e("开播---changeLive---->" + info[0]);
                    }
                });
    }

    /**
     * 设置连麦开关
     */
    public static void setMic(boolean isMic, HttpCallback callback) {
        HttpClient.getInstance().post("Linkmic.setMic", HttpConst.SET_MIC)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("ismic", isMic ? 1 : 0)
                .execute(callback);
    }

    /**
     * 判断主播是否可连麦
     */
    public static void isMic(String liveUid, HttpCallback callback) {
        HttpClient.getInstance().post("Linkmic.isMic", HttpConst.IS_MIC)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .execute(callback);
    }

    /**
     * 主播结束直播
     */
    public static void stopLive(String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Live.stopRoom", HttpConst.STOP_LIVE)
                .params("stream", stream)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 直播结束后，获取直播收益，观看人数，时长等信息
     */
    public static void getLiveEndInfo(String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Live.stopInfo", HttpConst.GET_LIVE_END_INFO)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 检查直播间状态，是否收费 是否有密码等
     *
     * @param liveUid 主播的uid
     * @param stream  主播的stream
     */
    public static void checkLive(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Live.checkLive", HttpConst.CHECK_LIVE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 当直播间是门票收费，计时收费或切换成计时收费的时候，观众请求这个接口
     *
     * @param liveUid 主播的uid
     * @param stream  主播的stream
     */
    public static void roomCharge(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Live.roomCharge", HttpConst.ROOM_CHARGE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("liveuid", liveUid)
                .execute(callback);

    }

    /**
     * 当视频是门票收费
     */
    public static void videoCharge(String videoId, HttpCallback callback) {
        HttpClient.getInstance().post("Video.videoCharge", HttpConst.ROOM_CHARGE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", videoId)
                .execute(callback);

    }

    /**
     * 当前视频是否可以观看
     */
    public static void videoCheck(HttpCallback callback) {
        HttpClient.getInstance().post("Video.setGuankanNum", HttpConst.ROOM_CHARGE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);

    }

    /**
     * 当直播间是计时收费的时候，观众每隔一分钟请求这个接口进行扣费
     *
     * @param liveUid 主播的uid
     * @param stream  主播的stream
     */
    public static void timeCharge(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Live.timeCharge", HttpConst.TIME_CHARGE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("liveuid", liveUid)
                .execute(callback);
    }

    /**
     * 观众进入直播间
     */
    public static void enterRoom(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Live.enterRoom", HttpConst.ENTER_ROOM)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("city", AppConfig.getInstance().getCity())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 发送弹幕
     */
    public static void sendDanmu(String content, String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Live.sendBarrage", HttpConst.SEND_DANMU)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("giftid", "1")
                .params("giftcount", "1")
                .params("content", content)
                .execute(callback);
    }

    /**
     * 发送消息前是否有屏蔽信息
     */
    public static void sendMsg(String content, String liveUid, HttpCallback callback) {
        HttpClient.getInstance().post("Live.sendMsg", HttpConst.SEND_MSG)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("content", content)
                .execute(callback);
    }

    /**
     * 获取礼物列表，同时会返回剩余的钱
     */
    public static void getGiftList(HttpCallback callback) {
        HttpClient.getInstance().post("Live.getGiftList", HttpConst.GET_GIFT_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取用户余额
     */
    public static void getCoin(HttpCallback callback) {
        HttpClient.getInstance().post("Live.getCoin", HttpConst.GET_COIN)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 观众给主播送礼物
     */
    public static void sendGift(String liveUid, String stream, int giftId, String giftCount, HttpCallback callback) {
        HttpClient.getInstance().post("Live.sendGift", HttpConst.SEND_GIFT)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("giftid", giftId)
                .params("giftcount", giftCount)
                .execute(callback);
    }

    /**
     * 观众给主播送礼物U
     */
    public static void sendVideoGift(String videoId, String videoUid, int giftId, String giftCount, HttpCallback callback) {
        HttpClient.getInstance().post("Live.sendVideo", HttpConst.SEND_GIFT)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("videouid", videoUid)
                .params("videoid", videoId)
                .params("giftid", giftId)
                .params("giftcount", giftCount)
                .execute(callback);
    }


    /**
     * 获取主播印象列表
     */
    public static void getAllImpress(String touid, HttpCallback callback) {
        HttpClient.getInstance().post("User.GetUserLabel", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .execute(callback);
    }


    /**
     * 获取自己收到的主播印象列表
     */
    public static void getMyImpress(String touid, HttpCallback callback) {
        HttpClient.getInstance().post("User.GetMyLabel", HttpConst.GET_MY_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 给主播设置印象
     */
    public static void setImpress(String touid, String ImpressIDs, HttpCallback callback) {
        HttpClient.getInstance().post("User.setUserLabel", HttpConst.SET_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("labels", ImpressIDs)
                .execute(callback);
    }

    /**
     * 直播间点击聊天列表和头像出现的弹窗
     */
    public static void getLiveUser(String touid, String liveUid, HttpCallback callback) {
        HttpClient.getInstance().post("Live.getPop", HttpConst.GET_LIVE_USER)
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("liveuid", liveUid)
                .execute(callback);
    }

    /**
     * 获取当前直播间的禁言列表
     */
    public static void getGapList(String liveUid, HttpCallback callback) {
        HttpClient.getInstance().post("Live.shutList", HttpConst.GET_GAP_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .execute(callback);
    }

    /**
     * 取消直播间的禁言
     */
    public static void cancelGap(String liveUid, String touid, HttpCallback callback) {
        HttpClient.getInstance().post("Live.cancelShut", HttpConst.CANCEL_GAP)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 获取当前直播间的管理员列表
     */
    public static void getAdminList(String liveUid, HttpCallback callback) {
        HttpClient.getInstance().post("Live.getAdminList", HttpConst.GET_ADMIN_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("liveuid", liveUid)
                .execute(callback);
    }

    /**
     * 主播设置或取消直播间的管理员
     */
    public static void setAdmin(String liveUid, String touid, HttpCallback callback) {
        HttpClient.getInstance().post("Live.setAdmin", HttpConst.SET_ADMIN)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 主播或管理员踢人
     */
    public static void kicking(String liveUid, String touid, HttpCallback callback) {
        HttpClient.getInstance().post("Live.kicking", HttpConst.KICKING)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", touid)
                .execute(callback);
    }


    /**
     * 主播或管理员禁言
     */
    public static void setShutUp(String liveUid, String touid, HttpCallback callback) {
        HttpClient.getInstance().post("Live.setShutUp", HttpConst.SET_SHUT_UP)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", touid)
                .execute(callback);
    }


    /**
     * 超管关闭直播间或禁用账户
     */
    public static void superCloseRoom(String liveUid, boolean forbidAccount, HttpCallback callback) {
        HttpClient.getInstance().post("Live.superStopRoom", HttpConst.SUPER_CLOSE_ROOM)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("type", forbidAccount ? 1 : 0)
                .execute(callback);
    }


    /**
     * 举报用户
     */
    public static void setReport(String touid, HttpCallback callback) {
        HttpClient.getInstance().post("Live.setReport", HttpConst.SET_REPORT)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("content", WordUtil.getString(R.string.live_illegal))
                .execute(callback);
    }

    /**
     * 用户个人主页信息
     */
    public static void getUserHome(String touid, HttpCallback callback) {
        HttpClient.getInstance().post("User.getUserHome", HttpConst.GET_USER_HOME)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 获取推荐用户
     */
    public static void getRecommendUser(int page, HttpCallback callback) {
        HttpClient.getInstance().post("User.getTuijianList", HttpConst.GET_USER_HOME)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", String.valueOf(page))
                .execute(callback);
    }

    /**
     * 拉黑对方， 解除拉黑
     */
    public static void setBlack(String toUid, HttpCallback callback) {
        HttpClient.getInstance().post("User.setBlack", HttpConst.SET_BLACK)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", toUid)
                .execute(callback);
    }

    /**
     * 主播添加背景音乐时，搜索歌曲
     *
     * @param key      关键字
     * @param callback
     */
    public static void searchMusic(String key, HttpCallback callback) {
        HttpClient.getInstance().post("Livemusic.searchMusic", HttpConst.SEARCH_MUSIC)
                .params("key", key)
                .execute(callback);
    }

    /**
     * 获取歌曲的地址 和歌词的地址
     */
    public static void getMusicUrl(String musicId, HttpCallback callback) {
        HttpClient.getInstance().post("Livemusic.getDownurl", HttpConst.GET_MUSIC_URL)
                .params("audio_id", musicId)
                .execute(callback);
    }

    /**
     * 获取 我的收益 可提现金额数
     */
    public static void getProfit(HttpCallback callback) {
        HttpClient.getInstance().post("User.getProfit", HttpConst.GET_PROFIT)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取 提现账户列表
     */
    public static void getCashAccountList(HttpCallback callback) {
        HttpClient.getInstance().post("User.GetUserAccountList", HttpConst.GET_USER_ACCOUNT_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 添加 提现账户
     */
    public static void addCashAccount(String account, String name, String bank, int type, HttpCallback callback) {
        HttpClient.getInstance().post("User.SetUserAccount", HttpConst.ADD_CASH_ACCOUNT)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("account", account)
                .params("name", name)
                .params("account_bank", bank)
                .params("type", type)
                .execute(callback);
    }

    /**
     * 删除 提现账户
     */
    public static void deleteAccountBank(String accountId, HttpCallback callback) {
        HttpClient.getInstance().post("User.delBankCard", HttpConst.DEL_CASH_ACCOUNT)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("id", accountId)
                .execute(callback);
    }

    /**
     * 礼物值兑换
     */
    public static void doVoteCash(String votes, HttpCallback callback) {
        HttpClient.getInstance().post("User.setCash", HttpConst.DO_CASH)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("cashvote", votes)//提现的票数
                .execute(callback);
    }


    /**
     * 获取商品ali支付信息
     */
    public static void getGoodsAliOrder(String orderSn, String type, String payType, HttpCallback callback) {
        HttpClient.getInstance().post("goods.getAliOrder", HttpConst.DO_CASH)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("order_no", orderSn)//提现的票数
                .params("type", type)//提现的票数
                .params("pay_type", payType)
                .execute(callback);
    }

    /**
     * 获取商品wx支付信息
     */
    public static void getGoodsWxOrder(String orderSn, String type, String payType, HttpCallback callback) {
        HttpClient.getInstance().post("goods.getWxOrder", HttpConst.DO_CASH)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("order_no", orderSn)//提现的票数
                .params("type", type)
                .params("pay_type", payType)
                .execute(callback);
    }

    /**
     * 余额提现
     */
    public static void doCash(String votes, String pwd, String accountId, HttpCallback callback) {
        HttpClient.getInstance().post("User.setMoneyCash", HttpConst.DO_CASH)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("cashvote", votes)//提现的票数
                .params("password", pwd)//账号ID
                .params("accountid", accountId)//账号ID
                .execute(callback);
    }

    /**
     * 余额提现
     */
    public static void doWelfareExchange(String money, HttpCallback callback) {
        HttpClient.getInstance().post("User.setProfitCoin", HttpConst.DO_CASH)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("cashvote", money)//提现的票数
                .execute(callback);
    }

    /**
     * 福利豆丁提现
     */
    public static void doWelfareCash(String votes, String pwd, String accountId, HttpCallback callback) {
        HttpClient.getInstance().post("User.setCommissionCash", HttpConst.DO_CASH)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("cashvote", votes)//提现的票数
                .params("password", pwd)//账号ID
                .params("accountid", accountId)//账号ID
                .execute(callback);
    }


    /**
     * 充值页面，我的钻石
     */
    public static void getBalance(HttpCallback callback) {
        HttpClient.getInstance().post("User.getBalance", HttpConst.GET_BALANCE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 用支付宝充值 的时候在服务端生成订单号
     *
     * @param money    RMB价格
     * @param changeid 要购买的钻石的id
     * @param coin     要购买的钻石的数量
     * @param callback
     */
    public static void getAliOrder(String money, String changeid, String coin, HttpCallback callback) {
        HttpClient.getInstance().post("Charge.getAliOrder", HttpConst.GET_ALI_ORDER)
                .params("uid", AppConfig.getInstance().getUid())
                .params("money", money)
                .params("changeid", changeid)
                .params("coin", coin)
                .execute(callback);
    }

    /**
     * 用微信支付充值 的时候在服务端生成订单号
     *
     * @param money    RMB价格
     * @param changeid 要购买的钻石的id
     * @param coin     要购买的钻石的数量
     * @param callback
     */
    public static void getWxOrder(String money, String changeid, String coin, HttpCallback callback) {
        HttpClient.getInstance().post("Charge.getWxOrder", HttpConst.GET_WX_ORDER)
                .params("uid", AppConfig.getInstance().getUid())
                .params("money", money)
                .params("changeid", changeid)
                .params("coin", coin)
                .execute(callback);
    }

    /**
     * 私信聊天页面用于获取用户信息
     */
    public static void getImUserInfo(String uids, HttpCallback callback) {
        HttpClient.getInstance().post("User.GetUidsInfo", HttpConst.GET_IM_USER_INFO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("uids", uids)
                .execute(callback);
    }

    /**
     * 实名认证
     */
    public static void userAuthSuccess(String truename, String idcard, HttpCallback callback) {

        String time = CommonUtil.getTickTimeStr();

        String uid = AppConfig.getInstance().getUid();
        String randomStr = MD5Util.getMD5(uid + "-" + time + "-" + VIDEO_SALT);
        HttpClient.getInstance().post("Editionapi.setAuth", HttpConst.GET_IM_USER_INFO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("idcard", idcard)
                .params("truename", truename)
                .params("random_str", randomStr)
                .execute(callback);
    }

    /**
     * 实名认证
     */
    public static void userAuthFail(String truename, String idcard) {
        HttpClient.getInstance().post("Auth.authError", HttpConst.GET_IM_USER_INFO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("id_card", idcard)
                .params("true_name", truename)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {

                    }
                });
    }

    /**
     * 直播权限开通
     */
    public static void livePay(String pwd, HttpCallback callback) {
        HttpClient.getInstance().post("User.setLiveAuth", HttpConst.GET_IM_USER_INFO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("aqpassword", pwd)
                .execute(callback);
    }

    /**
     * 判断自己有没有被对方拉黑，聊天的时候用到
     */
    public static void checkBlack(String touid, HttpCallback callback) {
        HttpClient.getInstance().post("User.checkBlack", HttpConst.CHECK_BLACK)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 搜索
     */
    public static void search(String key, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Home.search", HttpConst.SEARCH)
                .params("uid", AppConfig.getInstance().getUid())
                .params("key", key)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 观众跟主播连麦时，获取自己的流地址
     */
    public static void getLinkMicStream(HttpCallback callback) {
        HttpClient.getInstance().post("Linkmic.RequestLVBAddrForLinkMic", HttpConst.GET_LINK_MIC_STREAM)
                .params("uid", AppConfig.getInstance().getUid())
                .execute(callback);
    }

    /**
     * 主播连麦成功后，要把这些信息提交给服务器
     *
     * @param touid    连麦用户ID
     * @param pull_url 连麦用户播流地址
     */
    public static void linkMicShowVideo(String touid, String pull_url) {
        HttpClient.getInstance().post("Live.showVideo", HttpConst.LINK_MIC_SHOW_VIDEO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("pull_url", pull_url)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {

                    }
                });
    }


    /**
     * 获取当前直播间的用户列表
     */
    public static void getUserList(String liveuid, String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Live.getUserLists", HttpConst.GET_USER_LIST)
                .params("liveuid", liveuid)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 获取当前直播间优惠券
     */
    public static void getCoupon(String liveuid, HttpCallback callback) {
        HttpClient.getInstance().post("Goods.getLiveCoupon", HttpConst.GET_GOODS_COUPON)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("live_id", liveuid)
                .execute(callback);
    }

    /**
     * 获取直播回放url
     *
     * @param recordId 视频的id
     */
    public static void getAliCdnRecord(String recordId, HttpCallback callback) {
        HttpClient.getInstance().post("User.getAliCdnRecord", HttpConst.GET_ALI_CDN_RECORD)
                .params("id", recordId)
                .execute(callback);
    }


    /**
     * 用于用户首次登录推荐
     */
    public static void getRecommend(AbsCallback callback) {
        HttpClient.getInstance().post("Home.getRecommend", HttpConst.GET_RECOMMEND)
                .params("uid", AppConfig.getInstance().getUid())
                .execute(callback);
    }


    /**
     * 用于用户首次登录推荐,关注主播
     */
    public static void recommendFollow(String touid, HttpCallback callback) {
        HttpClient.getInstance().post("Home.attentRecommend", HttpConst.RECOMMEND_FOLLOW)
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 用于用户首次登录设置分销关系
     */
    public static void setInvitation(String code, HttpCallback callback) {
        HttpClient.getInstance().post("User.setDistribut", HttpConst.SET_DISTRIBUT)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("code", code)
                .execute(callback);
    }

    /**
     * 守护商品类型列表
     */
    public static void getGuardBuyList(HttpCallback callback) {
        HttpClient.getInstance().post("Guard.getList", HttpConst.GET_GUARD_BUY_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 购买守护接口
     */
    public static void buyGuard(String liveUid, String stream, int guardId, HttpCallback callback) {
        HttpClient.getInstance().post("Guard.BuyGuard", HttpConst.BUY_GUARD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("guardid", guardId)
                .execute(callback);
    }


    /**
     * 查看主播的守护列表
     */
    public static void getGuardList(String liveUid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Guard.GetGuardList", HttpConst.GET_GUARD_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 获取主播连麦pk列表
     */
    public static void getLivePkList(int p, HttpCallback callback) {
        HttpClient.getInstance().post("Livepk.GetLiveList", HttpConst.GET_LIVE_PK_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 连麦pk搜索主播
     */
    public static void livePkSearchAnchor(String key, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Livepk.Search", HttpConst.LIVE_PK_SEARCH_ANCHOR)
                .params("uid", AppConfig.getInstance().getUid())
                .params("key", key)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 连麦pk检查对方主播在线状态
     */
    public static void livePkCheckLive(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Livepk.checkLive", HttpConst.LIVE_PK_CHECK_LIVE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .execute(callback);
    }


    /**
     * 直播间发红包
     */
    public static void sendRedPack(String stream, String coin, String count, String title, int type, int sendType, HttpCallback callback) {
        HttpClient.getInstance().post("Red.SendRed", HttpConst.SEND_RED_PACK)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("coin", coin)
                .params("nums", count)
                .params("des", title)
                .params("type", type)
                .params("type_grant", sendType)
                .execute(callback);
    }

    /**
     * 获取直播间红包列表
     */
    public static void getRedPackList(String stream, HttpCallback callback) {
        String sign = MD5Util.getMD5("stream=" + stream + "&" + SALT);
        HttpClient.getInstance().post("Red.GetRedList", HttpConst.GET_RED_PACK_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 聊天发红包
     */
    public static void sendChatRedPack(String toUid, String money, String remark, String pwd, HttpCallback callback) {
        HttpClient.getInstance().post("RedPackage.sendPackage", HttpConst.CHAT_SEND_RED_PACK)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("to_uid", toUid)
                .params("price", money)
                .params("remarks", remark)
                .params("pay_pwd", pwd)
                .execute(callback);
    }

    /**
     * 聊天开红包
     */
    public static void receiveChatRedPack(String redPackageId, HttpCallback callback) {
        HttpClient.getInstance().post("RedPackage.receiveRedPackage", HttpConst.CHAT_SEND_RED_PACK)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("red_package_id", redPackageId)
                .execute(callback);
    }

    /**
     * 聊天发红包
     */
    public static void checkChatRedPack(String redPackageId, HttpCallback callback) {
        HttpClient.getInstance().post("RedPackage.queryRedPackage", HttpConst.CHAT_SEND_RED_PACK)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("red_package_id", redPackageId)
                .execute(callback);
    }


    /**
     * 直播间抢红包
     */
    public static void robRedPack(String stream, int redPackId, HttpCallback callback) {
        String uid = AppConfig.getInstance().getUid();
        String sign = MD5Util.getMD5("redid=" + redPackId + "&stream=" + stream + "&uid=" + uid + "&" + SALT);
        HttpClient.getInstance().post("Red.RobRed", HttpConst.ROB_RED_PACK)
                .params("uid", uid)
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("redid", redPackId)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 直播间红包领取详情
     */
    public static void getRedPackResult(String stream, int redPackId, HttpCallback callback) {
        String uid = AppConfig.getInstance().getUid();
        String sign = MD5Util.getMD5("redid=" + redPackId + "&stream=" + stream + "&" + SALT);
        HttpClient.getInstance().post("Red.GetRedRobList", HttpConst.GET_RED_PACK_RESULT)
                .params("uid", uid)
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("redid", redPackId)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 获取系统消息列表
     */
    public static void getSystemMessageList(int type, HttpCallback callback) {
        HttpClient.getInstance().post("Message.GetList", HttpConst.GET_SYSTEM_MESSAGE_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("type", type)
                .execute(callback);
    }

    /**
     * 获取系统消息列表
     */
    public static void readSystemMessageList(int type) {
        HttpClient.getInstance().post("Message.lookMessage", HttpConst.GET_SYSTEM_MESSAGE_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("type", type)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {

                    }
                });
    }

    /**
     * 获取系统消息列表
     */
    public static void getSystemMsgConservationList(int p, HttpCallback callback) {
        HttpClient.getInstance().post("Message.getHuihuaList", HttpConst.GET_SYSTEM_MESSAGE_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }


    /**
     * 获取首页视频列表
     * p应该大于或等于0, 但现在p = -1
     *
     * @param type //1热门
     */
    public static void getHotVideoList(int type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Video.getHotVideoList", HttpConst.GET_VIDEO_LIST + type)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .params("type", type)
                .params("lng", AppConfig.getInstance().getLng())
                .params("lat", AppConfig.getInstance().getLat())
                .execute(callback);
    }

    /**
     * 获取首页视频列表
     *
     * @param type //2.一乡一品
     */
    public static void getYxYpVideoList(int type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Video.getHighGradeVideoList", HttpConst.GET_VIDEO_LIST + type)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .params("type", type)
                .params("lng", AppConfig.getInstance().getLng())
                .params("lat", AppConfig.getInstance().getLat())
                .execute(callback);
    }

    /**
     * 获取首页视频列表
     *
     * @param type //1助农
     */
    public static void getZnVideoList(int type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Video.getZnVideoList", HttpConst.GET_VIDEO_LIST + type)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .params("type", type)
                .params("lng", AppConfig.getInstance().getLng())
                .params("lat", AppConfig.getInstance().getLat())
                .execute(callback);
    }

    /**
     * 获取首页视频列表
     *
     * @param type //1喜欢
     */
    public static void getFollowVideoList(int type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Video.getLikeVideoList", HttpConst.GET_VIDEO_LIST + type)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .params("type", type)
                .params("lng", AppConfig.getInstance().getLng())
                .params("lat", AppConfig.getInstance().getLat())
                .execute(callback);
    }

    /**
     * 获取首页视频列表
     *
     * @param type //1家乡
     */
    public static void getHometownVideoList(int type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Video.getJxVideoList", HttpConst.GET_VIDEO_LIST + type)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .params("type", type)
                .params("lng", AppConfig.getInstance().getLng())
                .params("lat", AppConfig.getInstance().getLat())
                .execute(callback);
    }


    /**
     * @param musicId
     */
    public static void getVideoList(String musicId, HttpCallback callback) {
        HttpClient.getInstance().post("Video.getMusicVideo", HttpConst.GET_SAME_VIDEO_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("music_id", musicId)
                .execute(callback);
    }

    /**
     * videoId  视频id
     */
    public static void getVideo(String videoId, HttpCallback callback) {
        HttpClient.getInstance().post("Video.getVideo", HttpConst.GET_VIDEO_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", videoId)
                .execute(callback);
    }

    /**
     * 视频点赞
     */
    public static void setVideoLike(String tag, String videoid, HttpCallback callback) {
        HttpClient.getInstance().post("Video.AddLike", tag)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", videoid)
                .execute(callback);
    }

    /**
     * 获取视频评论
     */
    public static void getVideoCommentList(String videoid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Video.GetComments", HttpConst.GET_VIDEO_COMMENT_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", videoid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取某动态评论
     */
    public static void getTrendCommentList(String videoid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Video.CircleComments", HttpConst.GET_VIDEO_COMMENT_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", videoid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 评论点赞
     */
    public static void setCommentLike(String commentid, HttpCallback callback) {
        HttpClient.getInstance().post("Video.addCommentLike", HttpConst.SET_COMMENT_LIKE)
                .params("commentid", commentid)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 发表评论
     */
    public static void setComment(String toUid, String videoId, String content, String commentId, String parentId, HttpCallback callback) {
        HttpClient.getInstance().post("Video.setComment", HttpConst.SET_COMMENT)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", toUid)
                .params("videoid", videoId)
                .params("commentid", commentId)
                .params("parentid", parentId)
                .params("content", content)
                .params("at_info", "")
                .execute(callback);
    }


    /**
     * 获取评论回复
     */
    public static void getCommentReply(String commentid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Video.getReplys", HttpConst.GET_COMMENT_REPLY)
                .params("commentid", commentid)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取视频音乐分类列表
     */
    public static void getMusicClassList(HttpCallback callback) {
        HttpClient.getInstance().post("Music.classify_list", HttpConst.GET_MUSIC_CLASS_LIST)
                .execute(callback);
    }

    /**
     * 获取热门视频音乐列表
     */
    public static void getHotMusicList(int p, HttpCallback callback) {
        HttpClient.getInstance().post("Music.hotLists", HttpConst.GET_HOT_MUSIC_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 音乐收藏
     */
    public static void setMusicCollect(int muiscId, HttpCallback callback) {
        HttpClient.getInstance().post("Music.collectMusic", HttpConst.SET_MUSIC_COLLECT)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("musicid", muiscId)
                .execute(callback);
    }

    /**
     * 音乐收藏列表
     */
    public static void getMusicCollectList(int p, HttpCallback callback) {
        HttpClient.getInstance().post("Music.getCollectMusicLists", HttpConst.GET_MUSIC_COLLECT_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取具体分类下的音乐列表
     */
    public static void getMusicList(String classId, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Music.music_list", HttpConst.GET_MUSIC_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("classify", classId)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 搜索音乐
     */
    public static void videoSearchMusic(String key, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Music.searchMusic", HttpConst.VIDEO_SEARCH_MUSIC)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("key", key)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 上传视频，获取七牛云token的接口
     */
    public static void getQiNiuToken(HttpCallback callback) {
        HttpClient.getInstance().post("Video.getQiniuToken", HttpConst.GET_QI_NIU_TOKEN)
                .execute(callback);
    }


    /**
     * 短视频上传信息
     *
     * @param type    0视频动态，1是图片动态
     * @param title
     * @param thumb
     * @param href    短视频视频url
     * @param musicId 背景音乐Id
     * @param images  图片数组（发布图片动态时使用）
     */


    /**
     * @param title         短视频标题
     * @param thumb
     * @param href
     * @param musicId
     * @param videoTimeLong
     * @param lat
     * @param lng
     * @param address
     * @param atUser
     * @param goodsId
     * @param callback
     */
    public static void saveUploadVideoInfo(String title, String thumb, String href, int musicId, long videoTimeLong,
                                           double lat, double lng, String address, String atUser, String goodsId,
                                           boolean isPublic, boolean isZn, boolean isYxYp, String tag, HttpCallback callback) {
        HttpClient.getInstance().post("Video.setVideo", HttpConst.SAVE_UPLOAD_VIDEO_INFO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("title", title)
                .params("thumb", thumb)
                .params("href", href)
                .params("lat", lat)
                .params("lng", lng)
                .params("city", address)
                .params("music_id", musicId)
                .params("video_time", videoTimeLong)
                .params("video_at_user", atUser)
                .params("video_goods_id", goodsId)
                .params("is_public", isPublic ? 1 : 0)
                .params("is_zn", isZn ? 1 : 0)
                .params("is_high_grade", isYxYp ? 1 : 0)
                .params("tag", tag)
                .execute(callback);
    }

    /**
     * 获取腾讯云储存上传签名
     */
    public static void getTxUploadCredential(StringCallback callback) {
        OkGo.<String>get("http://upload.qq163.iego.cn:8088/cam").execute(callback);
    }

    /**
     * 获取某人发布的视频
     */
    public static void getHomeVideo(String type, String toUid, int p, HttpCallback callback) {
        HttpClient.getInstance().post("user.getHomeVideo", HttpConst.GET_HOME_VIDEO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", toUid)
                .params("type", type)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 人脸核身接口调用
     * 获取认真配置
     */
    public static void getVerifyStatus(HttpCallback callback) {
        HttpClient.getInstance().post("auth.getAuthStatus", HttpConst.GET_HOME_VIDEO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 人脸核身接口调用
     * 获取认证支付订单号
     */
    public static void getAuthOrder(int payType, HttpCallback callback) {
        HttpClient.getInstance().post("auth.payAuth", HttpConst.GET_HOME_VIDEO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("pay_type", payType)
                .execute(callback);
    }

    /**
     * 人脸核身接口调用
     * 身份证号是否可用
     */
    public static void verifyIdDuplicated(String id, HttpCallback callback) {
        HttpClient.getInstance().post("User.setIdCard", HttpConst.GET_HOME_VIDEO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("idcard", id)
                .execute(callback);
    }


    /**
     * 人脸核身接口调用
     * 获取nonce
     */
    public static void getVerifyNonce(HttpCallback callback) {
        HttpClient.getInstance().post("auth.getSign", HttpConst.GET_HOME_VIDEO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 人脸核身接口调用
     * 获取Sign
     */
    public static void getVerifyNonceSign(HttpCallback callback) {
        HttpClient.getInstance().post("user.getHomeVideo", HttpConst.GET_HOME_VIDEO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 获取举报内容列表
     */
    public static void getVideoReportList(HttpCallback callback) {
        HttpClient.getInstance().post("Video.getReportContentlist", HttpConst.GET_VIDEO_REPORT_LIST)
                .execute(callback);
    }

    /**
     * 举报视频接口
     */
    public static void videoReport(String videoId, String reportId, String content, HttpCallback callback) {
        HttpClient.getInstance().post("Video.report", HttpConst.VIDEO_REPORT)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", videoId)
                .params("type", reportId)
                .params("content", content)
                .execute(callback);
    }

    /**
     * 删除自己的视频
     */
    public static void videoDelete(String videoid, HttpCallback callback) {
        HttpClient.getInstance().post("Video.del", HttpConst.VIDEO_DELETE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", videoid)
                .execute(callback);
    }

    /**
     * 分享视频
     */
    public static void setVideoShare(String videoid, HttpCallback callback) {
        String uid = AppConfig.getInstance().getUid();
        String s = MD5Util.getMD5(uid + "-" + videoid + "-" + VIDEO_SALT);
        HttpClient.getInstance().post("Video.addShare", HttpConst.SET_VIDEO_SHARE)
                .params("uid", uid)
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", videoid)
                .params("random_str", s)
                .execute(callback);
    }


    /**
     * 开始观看视频的时候请求这个接口
     * videoid应该大于或等于1, 但现在videoid = 0
     * 改为post
     */
    public static void videoWatchStart(String videoUid, String videoId) {
        String uid = AppConfig.getInstance().getUid();
        if (TextUtils.isEmpty(uid) || uid.equals(videoUid) || TextUtils.isEmpty(videoId) || "0".equals(videoId)) {
            return;
        }
        HttpUtil.cancel(HttpConst.VIDEO_WATCH_START);
        String s = MD5Util.getMD5(uid + "-" + videoId + "-" + VIDEO_SALT);
        HttpClient.getInstance().post("Video.addView", HttpConst.VIDEO_WATCH_START)
                .params("uid", uid)
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", videoId)
                .params("random_str", s)
                .execute(NO_CALLBACK);
    }

    /**
     * 完整观看完视频后请求这个接口
     * videoid应该大于或等于1, 但现在videoid = 0
     */
    public static void videoWatchEnd(String videoUid, String videoId) {
        String uid = AppConfig.getInstance().getUid();
        if (TextUtils.isEmpty(uid) || uid.equals(videoUid) || TextUtils.isEmpty(videoId) || "0".equals(videoId)) {
            LogUtils.e(TAG, "videoWatchEnd: videoId null");
            return;
        }

        HttpUtil.cancel(HttpConst.VIDEO_WATCH_END);
        String s = MD5Util.getMD5(uid + "-" + videoId + "-" + VIDEO_SALT);
        HttpClient.getInstance().post("Video.setConversion", HttpConst.VIDEO_WATCH_END)
                .params("uid", uid)
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", videoId)
                .params("random_str", s)
                .execute(NO_CALLBACK);
    }


    //不做任何操作的HttpCallback
    private static final HttpCallback NO_CALLBACK = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {

        }
    };

    /**********************
     * 游戏
     *****************/

    /**
     * 创建炸金花游戏
     */
    public static void gameJinhuaCreate(String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Game.Jinhua", HttpConst.GAME_JINHUA_CREATE)
                .params("liveuid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 炸金花游戏下注
     */
    public static void gameJinhuaBet(String gameid, int coin, int grade, HttpCallback callback) {
        HttpClient.getInstance().post("Game.JinhuaBet", HttpConst.GAME_JINHUA_BET)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("gameid", gameid)
                .params("coin", coin)
                .params("grade", grade)
                .execute(callback);
    }

    /**
     * 游戏结果出来后，观众获取自己赢到的金额
     */
    public static void gameSettle(String gameid, HttpCallback callback) {
        HttpClient.getInstance().post("Game.settleGame", HttpConst.GAME_SETTLE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("gameid", gameid)
                .execute(callback);
    }

    /**
     * 创建海盗船长游戏
     */
    public static void gameHaidaoCreate(String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Game.Taurus", HttpConst.GAME_HAIDAO_CREATE)
                .params("liveuid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 海盗船长游戏下注
     */
    public static void gameHaidaoBet(String gameid, int coin, int grade, HttpCallback callback) {
        HttpClient.getInstance().post("Game.Taurus_Bet", HttpConst.GAME_HAIDAO_BET)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("gameid", gameid)
                .params("coin", coin)
                .params("grade", grade)
                .execute(callback);
    }

    /**
     * 创建幸运转盘游戏
     */
    public static void gameLuckPanCreate(String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Game.Dial", HttpConst.GAME_LUCK_PAN_CREATE)
                .params("liveuid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 幸运转盘游戏下注
     */
    public static void gameLuckPanBet(String gameid, int coin, int grade, HttpCallback callback) {
        HttpClient.getInstance().post("Game.Dial_Bet", HttpConst.GAME_LUCK_PAN_BET)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("gameid", gameid)
                .params("coin", coin)
                .params("grade", grade)
                .execute(callback);
    }

    /**
     * 创建开心牛仔游戏
     */
    public static void gameNiuzaiCreate(String stream, String bankerid, HttpCallback callback) {
        HttpClient.getInstance().post("Game.Cowboy", HttpConst.GAME_NIUZAI_CREATE)
                .params("liveuid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("bankerid", bankerid)
                .execute(callback);
    }

    /**
     * 开心牛仔游戏下注
     */
    public static void gameNiuzaiBet(String gameid, int coin, int grade, HttpCallback callback) {
        HttpClient.getInstance().post("Game.Cowboy_Bet", HttpConst.GAME_NIUZAI_BET)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("gameid", gameid)
                .params("coin", coin)
                .params("grade", grade)
                .execute(callback);
    }

    /**
     * 开心牛仔游戏胜负记录
     */
    public static void gameNiuRecord(String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Game.getGameRecord", HttpConst.GAME_NIU_RECORD)
                .params("action", "4")
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 开心牛仔庄家流水
     */
    public static void gameNiuBankerWater(String bankerId, String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Game.getBankerProfit", HttpConst.GAME_NIU_BANKER_WATER)
                .params("bankerid", bankerId)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 开心牛仔获取庄家列表,列表第一个为当前庄家
     */
    public static void gameNiuGetBanker(String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Game.getBanker", HttpConst.GAME_NIU_GET_BANKER)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 开心牛仔申请上庄
     */
    public static void gameNiuSetBanker(String stream, String deposit, HttpCallback callback) {
        HttpClient.getInstance().post("Game.setBanker", HttpConst.GAME_NIU_SET_BANKER)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("deposit", deposit)
                .execute(callback);
    }

    /**
     * 开心牛仔申请下庄
     */
    public static void gameNiuQuitBanker(String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Game.quietBanker", HttpConst.GAME_NIU_QUIT_BANKER)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 创建二八贝游戏
     */
    public static void gameEbbCreate(String stream, HttpCallback callback) {
        HttpClient.getInstance().post("Game.Cowry", HttpConst.GAME_EBB_CREATE)
                .params("liveuid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 二八贝下注
     */
    public static void gameEbbBet(String gameid, int coin, int grade, HttpCallback callback) {
        HttpClient.getInstance().post("Game.Cowry_Bet", HttpConst.GAME_EBB_BET)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("gameid", gameid)
                .params("coin", coin)
                .params("grade", grade)
                .execute(callback);
    }

    /**
     * 活动列表
     *
     * @param type 0预售包括进行中，1历史
     */
    public static void getTicket(int type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Home.getActivity", HttpConst.TICKET)
                .params("uid", AppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 活动中心
     *
     * @param type 0预售包括进行中，1历史
     */
    public static void getActCenter(int type, int p, HttpCallback callback) {
        HttpClient.getInstance().post("Home.getActCenter", HttpConst.TICKET)
                .params("uid", AppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取滚动文字
     */
    public static void getLiveInfo(AbsCallback callback) {
        HttpClient.getInstance().post("Live.getconfiginfo", HttpConst.GET_LIVE_ACTIVE)
                .params("uid", AppConfig.getInstance().getUid())
                .execute(callback);
    }


    /**
     * 获取直播礼物明细
     */
    public static void getAllGiftDetail(String type, String action, int page, AbsCallback callback) {
        HttpClient.getInstance().post("user.userliwumx", HttpConst.GET_DETAIL_ALL_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("action", action)
                .params("type", type)
                .params("p", page)
                .execute(callback);
    }

    /**
     * 获取直播礼物明细
     */
    public static void getGiftDetail(String stream, int page, AbsCallback callback) {
        HttpClient.getInstance().post("live.liwumx", HttpConst.GET_DETAIL_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("p", page)
                .execute(callback);
    }


    /**
     * 我的门票
     */
    public static void getTicketMy(int p, HttpCallback callback) {
        HttpClient.getInstance().post("User.getUserTicket", HttpConst.TICKET_MY)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 购买门票
     *
     * @param act_id 活动id
     */
    public static void buyTicket(String act_id, HttpCallback callback) {
        HttpClient.getInstance().post("User.buyActTicket", HttpConst.TICKET_BUY)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("act_id", act_id)
                .execute(callback);
    }

    /**
     * 该用户当前是否有正在进行中的活动
     */
    public static void isActivityGoing(HttpCallback callback) {
        HttpClient.getInstance().post("Home.isActivity", HttpConst.ACTIVITY_GOING)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 该用户是否买了该主播的活动门票
     */
    public static void isActivityGoing(String touid, HttpCallback callback) {
        HttpClient.getInstance().post("Home.isActivity", HttpConst.ACTIVITY_GOING)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 获取主播心愿列表
     *
     * @param liveuid 主播uid
     */
    public static void getWishList(String liveuid, HttpCallback callback) {
        HttpClient.getInstance().post("Live.getWishList", HttpConst.WISH_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("liveuid", liveuid)
                .execute(callback);
    }

    /**
     * 设置心愿单
     *
     * @param gift [{"giftid":1,"giftcount":2},{"giftid":1,"giftcount":2}]
     */
    public static void setWish(String gift, HttpCallback callback) {
        HttpClient.getInstance().post("Live.setWish", HttpConst.WISH_SET)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("gift", gift)
                .execute(callback);
    }

    /**
     * 修改城市
     */
    public static void updateCity(String city, HttpCallback callback) {
        HttpClient.getInstance().post("User.setCity", HttpConst.UPDATE_FIELDS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("city", city)
                .execute(callback);
    }


    /**
     * 开通vip
     */
    public static void openVip(String pwd, HttpCallback callback) {
        HttpClient.getInstance().post("User.setVip", HttpConst.BUY_VIP)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("aqpassword", pwd)
                .execute(callback);
    }


    /**
     * 添加绑定银行卡
     */
    public static void addBankCard(String name, String cardNumber, String phone, String code, HttpCallback callback) {
        HttpClient.getInstance().post("User.addBankCard", HttpConst.ADD_BANK_CARD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("true_name", name)
                .params("bank_card_number", cardNumber)
                .params("reserved_phone", phone)
                .params("code", code)
                .execute(callback);
    }

    /**
     * 添加绑定银行卡
     */
    public static void getUserBankCard(HttpCallback callback) {
        HttpClient.getInstance().post("User.getUserBankCard", HttpConst.GET_USER_BANK_CARD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    public static void getUserRealInfo(HttpCallback callback) {
        HttpClient.getInstance().post("User.getUserTrueName", HttpConst.USER_REAL_INFO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 申请成为合伙人
     */
    public static void applyPartner(String name, String mobile, String idCard, String img_z, String img_f, String message, int type, String address, HttpCallback callback) {
        HttpClient.getInstance().post("User.setPartner", HttpConst.APPLY_PARTNER)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("username", name)
                .params("mobile", mobile)
                .params("idcard", idCard)
                .params("img_z", img_z)
                .params("img_f", img_f)
                .params("message", message)
                .params("type", type)
                .params("address", address)
                .execute(callback);
    }

    /**
     * 申请成为合伙人
     */
    public static void applyPartner2(String name, String mobile, String idCard, String img_z, String img_f, String message, int type, String address, int payType, HttpCallback callback) {
        HttpClient.getInstance().post("User.setPartner", HttpConst.APPLY_PARTNER)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("username", name)
                .params("mobile", mobile)
                .params("idcard", idCard)
                .params("img_z", img_z)
                .params("img_f", img_f)
                .params("message", message)
                .params("type", type)
                .params("address", address)
                .params("pay_type", payType)//新增支付参数 1=微信2=支付宝3=银联
                .execute(callback);
    }


    //达人等级列表
    public static void getZbLevel(HttpCallback callback) {
        HttpClient.getInstance().post("Wbapi.getZbLevel", HttpConst.USER_REAL_INFO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }


    //获取当前合伙人正在申请的状态
    public static void getApplyUserPartner(HttpCallback callback) {
        HttpClient.getInstance().post("User.getApplyUserPartner", HttpConst.USER_REAL_INFO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    //获取当前合伙人正在申请的状态
    public static void getUserPartner(int type, HttpCallback callback) {
        HttpClient.getInstance().post("User.getUserPartner", HttpConst.USER_REAL_INFO)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("type", String.valueOf(type))
                .execute(callback);
    }

    //购买铭文
    public static void buyMwTask(String mobile, String taskId, String pwd, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.havekuang", HttpConst.USER_REAL_INFO)
                .params("user_id", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("mobile", mobile)
                .params("store_id", taskId)
                .params("password", pwd)
                .execute(callback);
    }

    //获取铭文列表
    public static void getAllMwTask(HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.getuserkuanglist", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    //获取进行中的铭文任务
    public static void getProMwTask(HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.getuserkuanglistongoing", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    //获取历史任务的铭文任务
    public static void getMwTaskHistory(HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.getKuanginghistory", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }


    //获取地摊列表
    public static void getStallList(HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.getselllist", HttpConst.GET_ALL_STALL_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }


    //获取今日交易价格
    public static void getTodayPrice() {
        HttpClient.getInstance().post("Yangapi.gettodayprice", HttpConst.GET_TODAY_PRICE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 200) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            AppConfig.getInstance().setTodayPrice(obj.getString("today_price"));
                        }
                    }
                });
    }

    //获取今日交易价格
    public static void getTodayPrice(HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.gettodayprice", HttpConst.GET_TODAY_PRICE)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }


    //寄售毛豆
    public static void saleMd(String num, String price, String imgUrl, String password, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.dosell", HttpConst.SALE_MD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("num", num)
                .params("price", price)
                .params("imgurl", imgUrl)
                .params("password", password)
                .execute(callback);
    }

    //转账给直推人
    public static void transFriends(String num, String id, String pwd, HttpCallback callback) {
        HttpClient.getInstance().post("User.setTransferMaodou", HttpConst.SALE_MD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("maodounums", num)
                .params("touid", id)
                .params("coin_password", pwd)
                .execute(callback);
    }


    //订购毛豆
    public static void orderMd(String orderId, String password, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.dobuy_bean", HttpConst.ORDER_MD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .params("password", password)
                .execute(callback);
    }

    //获取毛豆订单列表
    public static void getMdOrderList(int p, int subType, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.getmyorderlist", HttpConst.GET_ALL_MD_ORDER_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .params("ordertype", subType)
                .execute(callback);
    }

    //获取毛豆卖单列表
    public static void getMdSaleList(int p, int subType, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.getmyorder_selllist", HttpConst.GET_ALL_MD_SELL_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .params("ordertype", subType)
                .execute(callback);
    }

    //获取我的活跃度
    public static void getHyData(int p, int subType, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.getactivelist", HttpConst.GET_ALL_MD_SELL_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .params("ordertype", subType)
                .execute(callback);
    }


    //获取毛豆记录
    public static void getMdRecord(int page, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.getmaorecordlist", HttpConst.GET_USER_ACTIVE_ALL)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", page)
                .execute(callback);
    }

    //获取今日进度
    public static void getTodayRecord(HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.get_today_progress", HttpConst.GET_TODAY_PROCESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    //获取家族认证活跃记录
    public static void getFamilyIdentify(int page, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.getrenzhenglist", HttpConst.GET_IDENTIFY_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", page)
                .execute(callback);
    }

    //获取家族未认证活跃记录
    public static void getFamilyNoIdentify(int page, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.getnotrenzhenglist", HttpConst.GET_IDENTIFY_LIST)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", page)
                .execute(callback);
    }


    //获取活跃度记录
    public static void getActiveRecord(int page, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.getactivelist", HttpConst.GET_USER_ACTIVE_ALL)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", page)
                .execute(callback);
    }

    //订单上传付款凭证接口（可重新上传）
    public static void orderUploadCertificate(String orderId, String imgUrl, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.upload_order_imgurl", HttpConst.GET_USER_ACTIVE_ALL)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .params("imgurl", imgUrl)
                .execute(callback);
    }

    //买家订单撤销
    public static void orderBuyerUndo(String orderId, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.buyer_cancel_order", HttpConst.GET_USER_ACTIVE_ALL)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .execute(callback);
    }

    //卖家取消交易
    public static void orderSellerUndo(String orderId, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.canceorderdetail", HttpConst.GET_USER_ACTIVE_ALL)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .execute(callback);
    }


    //用户确认打款接口
    public static void orderConfirmPay(String orderId, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.confirm_pay", HttpConst.GET_USER_ACTIVE_ALL)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .execute(callback);
    }

    //用户确认收款接口
    public static void orderConfirm(String orderId, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.confirm_order", HttpConst.GET_USER_ACTIVE_ALL)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .execute(callback);
    }

    //订单举报接口
    public static void reportOrderUser(String orderId, String content, String images, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.order_jubao", HttpConst.GET_USER_ACTIVE_ALL)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .params("content", content)
                .params("image_str", images)
                .execute(callback);
    }


    private final static String TAG = "HttpUtil";

    //获取历史任务的铭文任务
    public static void videoTaskTick() {
        String time = CommonUtil.getTickTimeStr();
        String uid = AppConfig.getInstance().getUid();
        String s = MD5Util.getMD5(uid + "-" + time + "-" + VIDEO_SALT);
        HttpClient.getInstance().post("Editionapi.setTaskTime", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("random_str", s)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            LogUtils.e(TAG, "onSuccess:videoTaskTick");
                        }
                    }
                });
    }

    //获取橱窗商品
    public static void getUserShopGoods(int p, HttpCallback callback) {
        HttpClient.getInstance().post("Goods.getGoods", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("page", p)
                .params("pagesize", HttpConst.ITEM_COUNT)
                .execute(callback);
    }

    //获取橱窗商品
    public static void getUserShopGoods(int p, String liveId, HttpCallback callback) {
        HttpClient.getInstance().post("Goods.getGoods", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("page", p)
                .params("live_id", liveId)
                .params("pagesize", HttpConst.ITEM_COUNT)
                .execute(callback);
    }


    //获取当前直播的所有商品
    public static void getLiveAllGoods(String liveId, HttpCallback callback) {
        HttpClient.getInstance().post("Goods.getLiveGoods", HttpConst.GET_LIVE_GOOD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("live_id", liveId)
                .execute(callback);
    }

    //获取当前直播的商品
    public static void getLiveNewGoods(String liveId, HttpCallback callback) {
        HttpClient.getInstance().post("Goods.getLiveNewGood", HttpConst.GET_LIVE_NEW_GOOD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("live_id", liveId)
                .execute(callback);
    }


    //获取橱窗收藏商品
    public static void getUserCollectedShopGoods(int p, HttpCallback callback) {
        HttpClient.getInstance().post("Goods.getCollectionGoods", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("page", p)
                .execute(callback);
    }


    //直播添加商品
    public static void liveAddGood(String goodId, String liveId, HttpCallback callback) {
        HttpClient.getInstance().post("Goods.addGoodLive", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("good_id", goodId)
                .params("live_id", liveId)
                .execute(callback);
    }

    //直播删除商品
    public static void liveRemoveGood(String goodId, String liveId, HttpCallback callback) {
        HttpClient.getInstance().post("Goods.removeGoodLive", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("good_id", goodId)
                .params("live_id", liveId)
                .execute(callback);
    }

    //收藏或取消收藏商品
    public static void collectGood(String goodId, HttpCallback callback) {
        HttpClient.getInstance().post("Goods.collectionGood", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("good_id", goodId)
                .execute(callback);
    }

    //助农福利列表
    public static void farmerWelfare(int p, HttpCallback callback) {
        HttpClient.getInstance().post("User.helpFarmList", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    //商城福利列表
    public static void mallWelfare(String status, int p, HttpCallback callback) {
        HttpClient.getInstance().post("goods.welfare", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("status", status)
                .params("page", p)
                .execute(callback);
    }

    //合伙人福利列表
    public static void partnerWelfare(int page, int pagesize, HttpCallback callback) {
        HttpClient.getInstance().post("Profit.getProfit ", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("page", page)
                .params("pagesize", pagesize)
                .execute(callback);
    }

    //合伙人福利列表详情
    public static void partnerWelfareDetail(String id, String day_time, HttpCallback callback) {
        HttpClient.getInstance().post("Profit.getInfo ", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("id", id)
                .params("day_time", day_time)
                .execute(callback);
    }

    //活跃度校准
    public static void activeProofread(HttpCallback callback) {
        HttpClient.getInstance().post("User.userActivityCorrect", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    //删除活跃度记录
    public static void deleteHistoryItem(String id, HttpCallback callback) {
        HttpClient.getInstance().post("Yangapi.del_history_kuang", HttpConst.GET_ALL_IMPRESS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("kuang_id", id)
                .execute(callback);
    }

    //删除短视频评论
    public static void deleteComment(String video_id, String comment_id, HttpCallback callback) {
        HttpClient.getInstance().post("video.delComments", HttpConst.VIDEO_DELCOMMENTS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", video_id)
                .params("commentid", comment_id)
                .execute(callback);
    }

    //获取推广链接
    public static void getPromote(HttpCallback callback) {
        HttpClient.getInstance().post("Editionapi.getZnActivity", HttpConst.VIDEO_DELCOMMENTS)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    //获取用户消息数量
    public static void getMsgCount(HttpCallback callback) {
        HttpClient.getInstance().post("user.getAllXiaoxiNum", HttpConst.ALLMSGNUM)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    //获取名片背景图
    public static void getQrShareBg(HttpCallback callback) {
        HttpClient.getInstance().post("Editionapi.getShareBg", HttpConst.SHAREBG)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取提现记录
     */
    public static void getTxRecord(int type, HttpCallback callback) {
        HttpClient.getInstance().post("user.getCashList", HttpConst.TXRECORD)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("type", type)
                .execute(callback);
    }

}




