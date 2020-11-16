package com.wwsl.mdsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.webank.facelight.contants.WbCloudFaceContant;
import com.webank.facelight.contants.WbFaceError;
import com.webank.facelight.contants.WbFaceVerifyResult;
import com.webank.facelight.listerners.WbCloudFaceVeirfyLoginListner;
import com.webank.facelight.listerners.WbCloudFaceVeirfyResultListener;
import com.webank.facelight.tools.IdentifyCardValidate;
import com.webank.facelight.tools.WbCloudFaceVerifySdk;
import com.webank.facelight.ui.FaceVerifyStatus;
import com.webank.mbank.wehttp.WeLog;
import com.webank.mbank.wehttp.WeOkHttp;
import com.webank.mbank.wehttp.WeReq;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.CommonSuccessActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.pay.PayCallback;
import com.wwsl.mdsj.pay.ali.AliPayBuilder;
import com.wwsl.mdsj.pay.wx.WxApiWrapper;
import com.wwsl.mdsj.pay.wx.WxPayBuilder;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.verify.AppHandler;
import com.wwsl.mdsj.verify.GetFaceId;
import com.wwsl.mdsj.verify.SignUseCase;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;
import com.wwsl.mdsj.wxapi.ThirdConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;

public class UserAuthActivity extends BaseActivity {
    private final static String TAG = "UserAuthActivity";
    private String name;
    private String id;
    private AppHandler appHandler;
    private SignUseCase signUseCase;
    private String appId;
    private String order;
    private String nonce;
    private String userId;

    private String aliOrderNum = "";//平台自身调用支付所需 阿里
    private Map<String, String> wxPay;//平台自身调用支付所需 微信
    private String money = "";//支付金额
    private String isNeed = "";//是否需要用户支付

    private WeOkHttp myOkHttp = new WeOkHttp();
    private String sign = "";

    private void initHttp() {
        //拿到OkHttp的配置对象进行配置
        //WeHttp封装的配置
        myOkHttp.config()
                //配置超时,单位:s
                .timeout(20, 20, 20)
                //添加PIN
                .log(WeLog.Level.BODY);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_auth;
    }

    @Override
    protected void init() {
        wxPay = new HashMap<>();
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");
        appId = ThirdConfig.TX_VERIFY_APP_ID;
        appHandler = new AppHandler(this);
        signUseCase = new SignUseCase(appHandler);
        initHttp();
        appId = ThirdConfig.TX_VERIFY_APP_ID;
        userId = AppConfig.getInstance().getUid();
        loadData();
    }

    private void loadData() {
        isNeed = getIntent().getStringExtra("isNeed");
        money = getIntent().getStringExtra("money");
    }


    public void backClick(View view) {
        finish();
    }

    public void doneConfirm(View view) {
        if ("1".equals(isNeed) && !StrUtil.isEmpty(money)) {
            //弹窗选择支付方式
            DialogUtil.showAuthPayDialog(UserAuthActivity.this, money, (view1, object) -> {
                int payType = (int) object;
                if (payType < 0 || payType > 2) return;
                if (payType == 0) {
                    if (!StrUtil.isEmpty(aliOrderNum)) {
                        aliPay();
                    } else {
                        getPayOrder(payType);
                    }
                } else if (payType == 1) {
                    if (wxPay != null && wxPay.size() > 0) {
                        wxPay();
                    } else {
                        getPayOrder(payType);
                    }
                } else {
                    ToastUtil.show("暂不支持银联支付");
                }
            });
        } else {
            startAuth();
        }
    }

    private void getPayOrder(int payType) {

        int type = -1;//支付方式 1微信支付 2支付宝支付 3 银联
        switch (payType) {
            case 0:
                type = 2;
                break;
            case 1:
                type = 1;
                break;
            case 2:
                type = 3;
                break;
        }
        showLoad();
        HttpUtil.getAuthOrder(type, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                dismissLoad();
                if (code == 0 && info != null) {
                    JSONObject order = JSON.parseObject(info[0]);
                    if (payType == 0) {
                        JSONObject pay = order.getJSONObject("pay");
                        aliOrderNum = pay.getString("pay_package");
                        aliPay();
                    } else if (payType == 1) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        JSONObject pay = obj.getJSONObject("pay");
                        wxPay.put("partnerid", pay.getString("partnerid"));
                        wxPay.put("prepayid", pay.getString("prepayid"));
                        wxPay.put("package", pay.getString("package"));
                        wxPay.put("noncestr", pay.getString("noncestr"));
                        wxPay.put("timestamp", pay.getString("timestamp"));
                        wxPay.put("sign", pay.getString("sign"));
                        wxPay();
                    } else if (payType == 2) {
                        ToastUtil.show("银联暂未接入");
                    }
                }
            }

            @Override
            public void onError() {
                dismissLoad();
            }
        });
    }

    private void wxPay() {
        PayCallback mPayCallback = new PayCallback() {
            @Override
            public void onSuccess() {
                startAuth();
            }

            @Override
            public void onFailed() {
                ToastUtil.show("支付失败");
            }
        };
        WxPayBuilder builder = new WxPayBuilder(this);
        builder.setPayCallback(mPayCallback);
        builder.payAuth(wxPay.get("partnerid"),
                wxPay.get("prepayid"),
                wxPay.get("package"),
                wxPay.get("noncestr"),
                wxPay.get("timestamp"),
                wxPay.get("sign"));

    }


    private void aliPay() {
        if (StrUtil.isEmpty(aliOrderNum)) return;
        AliPayBuilder builder = new AliPayBuilder(UserAuthActivity.this);
        builder.setPayCallback(new PayCallback() {
            @Override
            public void onSuccess() {
                //支付成功
                ToastUtil.show("支付成功");
                startAuth();
            }

            @Override
            public void onFailed() {

            }
        });
        builder.identifyPay(aliOrderNum);
    }


    private void startAuth() {
        showLoadCancelable(false, "加载中");
        //1.获取Nonce
        HttpUtil.getVerifyNonce(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length != 0) {
                    JSONObject jsonObject = JSON.parseObject(info[0]);
                    nonce = jsonObject.getString("nonceStr");
                    sign = jsonObject.getString("sign");
                    order = jsonObject.getString("order_no");
                }

                if (!StrUtil.isEmpty(nonce) && !StrUtil.isEmpty(sign) && !StrUtil.isEmpty(order)) {
                    checkOnId();
                }
            }
        });
    }


    private void checkOnId() {

        if (name != null && name.length() != 0) {
            if (id != null && id.length() != 0) {
                if (id.contains("x")) {
                    id = id.replace('x', 'X');
                }

                IdentifyCardValidate vali = new IdentifyCardValidate();
                String msg = vali.validate_effective(id);

                if (msg.equals(id)) {
                    LogUtils.e(TAG, "Param right! Called Face Verify Sdk MODE=" + AppHandler.DATA_MODE_ACT_DESENSE);
                    //2.获取sign
                    getFaceId(FaceVerifyStatus.Mode.ACT, sign);

//                    signUseCase.execute(AppHandler.DATA_MODE_ACT_DESENSE, appId, userId, nonce);
                } else {
                    ToastUtil.show("用户证件号错误");
                }
            } else {
                ToastUtil.show("用户证件号不能为空");
            }
        } else {
            ToastUtil.show("用户姓名不能为空");
        }

    }

    public void getFaceId(final FaceVerifyStatus.Mode mode, final String sign) {
        //3.获取faceId
        LogUtils.e(TAG, "start getFaceId: ");
        GetFaceId.GetFaceIdParam param = new GetFaceId.GetFaceIdParam();
        param.orderNo = order;
        param.webankAppId = appId;
        param.version = "1.0.0";
        param.userId = userId;
        param.sign = sign;
        param.name = name;
        param.idNo = id;

        GetFaceId.requestExec(myOkHttp, ThirdConfig.TX_VERIFY_FACE_ID_URL, param, new WeReq.WeCallback<GetFaceId.GetFaceIdResponse>() {
            @Override
            public void onStart(WeReq weReq) {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailed(WeReq weReq, int i, int code, String message, IOException e) {
                dismissLoad();
                LogUtils.e(TAG, "faceId请求失败:code=" + code + ",message=" + message);
                ToastUtil.show("登录异常(faceId请求失败:code=" + code + ",message=" + message);
            }

            @Override
            public void onSuccess(WeReq weReq, GetFaceId.GetFaceIdResponse getFaceIdResponse) {
                if (getFaceIdResponse != null) {
                    String code = getFaceIdResponse.code;
                    if (code.equals("0")) {
                        GetFaceId.Result result = getFaceIdResponse.result;
                        if (result != null) {
                            String faceId = result.faceId;
                            if (!TextUtils.isEmpty(faceId)) {
                                LogUtils.e(TAG, "faceId请求成功:" + faceId);
                                //4.拉起刷脸认证
                                openCloudFaceService(mode, appId, order, sign, faceId);
                            } else {
                                dismissLoad();
                                LogUtils.e(TAG, "faceId为空:");
                                ToastUtil.show("登录异常(faceId为空)");
                            }
                        } else {
                            dismissLoad();
                            LogUtils.e(TAG, "faceId请求失败:getFaceIdResponse result is null.");
                            ToastUtil.show("登录异常(faceId请求失败:getFaceIdResponse result is null)");
                        }
                    } else {
                        dismissLoad();
                        LogUtils.e(TAG, "faceId请求失败:code=" + code + "msg=" + getFaceIdResponse.msg);
                        ToastUtil.show("登录异常(faceId请求失败:code=" + code + "msg=" + getFaceIdResponse.msg + ")");
                    }
                } else {
                    dismissLoad();
                    LogUtils.e(TAG, "faceId请求失败:getFaceIdResponse is null.");
                    ToastUtil.show("登录异常(faceId请求失败:getFaceIdResponse is null)");
                }
            }
        });
    }

    //拉起刷脸sdk
    public void openCloudFaceService(FaceVerifyStatus.Mode mode, String appId, String order, String sign, String faceId) {
        Bundle data = new Bundle();
        WbCloudFaceVerifySdk.InputData inputData = new WbCloudFaceVerifySdk.InputData(
                faceId,
                order,
                appId,
                "1.0.0",
                nonce,
                userId,
                sign,
                mode,
                ThirdConfig.TX_VERIFY_LICENCE);

        data.putSerializable(WbCloudFaceContant.INPUT_DATA, inputData);
        //是否展示刷脸成功页面，默认展示
        data.putBoolean(WbCloudFaceContant.SHOW_SUCCESS_PAGE, true);
        //是否展示刷脸失败页面，默认展示
        data.putBoolean(WbCloudFaceContant.SHOW_FAIL_PAGE, true);
        //颜色设置
        data.putString(WbCloudFaceContant.COLOR_MODE, WbCloudFaceContant.BLACK);
        //是否需要录制上传视频 默认需要
        data.putBoolean(WbCloudFaceContant.VIDEO_UPLOAD, true);
        //是否开启闭眼检测，默认不开启
        data.putBoolean(WbCloudFaceContant.ENABLE_CLOSE_EYES, true);
        //是否播放提示音，默认播放
        data.putBoolean(WbCloudFaceContant.PLAY_VOICE, true);
        //设置选择的比对类型  默认为公安网纹图片对比
        //公安网纹图片比对 WbCloudFaceVerifySdk.ID_CRAD
        //自带比对源比对  WbCloudFaceVerifySdk.SRC_IMG
        //仅活体检测  WbCloudFaceVerifySdk.NONE
        //默认公安网纹图片比对
        data.putString(WbCloudFaceContant.COMPARE_TYPE, WbCloudFaceContant.ID_CARD);

        WbCloudFaceVerifySdk.getInstance().initSdk(UserAuthActivity.this, data, new WbCloudFaceVeirfyLoginListner() {
            @Override
            public void onLoginSuccess() {
                LogUtils.e(TAG, "onLoginSuccess: ");
                dismissLoad();

                WbCloudFaceVerifySdk.getInstance().startWbFaceVeirifySdk(UserAuthActivity.this, new WbCloudFaceVeirfyResultListener() {
                    @Override
                    public void onFinish(WbFaceVerifyResult result) {
                        if (result != null) {
                            if (result.isSuccess()) {
                                LogUtils.e(TAG, "onSuccess: " + result.getUserImageString());

                                HttpUtil.userAuthSuccess(name, id, new HttpCallback() {
                                    @Override
                                    public void onSuccess(int code, String msg, String[] info) {
                                        if (code == 0) {
                                            CommonSuccessActivity.forward(UserAuthActivity.this, "实名认证", "实名认证成功", Constants.SUCCESS_PAGE_TYPE_USER_ID_AUTH);
                                        } else {
                                            ToastUtil.show(msg);
                                        }
                                        finish();
                                    }
                                });
                            } else {
                                HttpUtil.userAuthFail(name, id);
                                ToastUtil.show("认证失败,请稍后再试!");
                                finish();
                                WbFaceError error = result.getError();
                                if (error != null) {
                                    LogUtils.e(TAG, "刷脸失败！domain=" + error.getDomain() + " ;code= " + error.getCode()
                                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason());

                                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainCompareServer)) {
                                        LogUtils.e(TAG, "对比失败，liveRate=" + result.getLiveRate() +
                                                "; similarity=" + result.getSimilarity());
                                    }

                                } else {
                                    LogUtils.e(TAG, "sdk返回error为空！");
                                }
                            }
                        } else {
                            HttpUtil.userAuthFail(name, id);
                            LogUtils.e(TAG, "sdk返回error为空！");
                        }
                    }
                });
                finish();
            }

            @Override
            public void onLoginFailed(WbFaceError error) {
                dismissLoad();
                HttpUtil.userAuthFail(name, id);
                ToastUtil.show("认证失败,请稍后再试!");
                finish();
                LogUtils.e(TAG, "onLoginFailed: ");
                if (error != null) {
                    LogUtils.e(TAG, "登录失败！domain=" + error.getDomain() + " ;code= " + error.getCode()
                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason());
                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainParams)) {
                        LogUtils.e("传入参数有误！" + error.getDesc());
                    } else {
                        ToastUtil.show("登录刷脸sdk失败！" + error.getDesc());
                    }
                } else {
                    LogUtils.e(TAG, "sdk返回error为空: ");
                }
            }
        });
    }

    public static void forward(Context context, String name, String id, String isNeed, String money) {
        Intent intent = new Intent(context, UserAuthActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        intent.putExtra("isNeed", isNeed);
        intent.putExtra("money", money);
        context.startActivity(intent);
    }
}
