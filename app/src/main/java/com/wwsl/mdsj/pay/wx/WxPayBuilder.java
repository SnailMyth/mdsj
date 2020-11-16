package com.wwsl.mdsj.pay.wx;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.CoinBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.pay.PayCallback;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.wxapi.ThirdConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

/**
 * Created by cxf on 2017/9/22.
 */

public class WxPayBuilder {

    private Context mContext;
    private String mAppId;
    private CoinBean mBean;
    private PayCallback mPayCallback;

    public WxPayBuilder(Context context) {
        mContext = context;
        mAppId = ThirdConfig.WX_APP_ID;
        WxApiWrapper.getInstance().setAppID(mAppId);
        EventBus.getDefault().register(this);
    }

    public WxPayBuilder setCoinBean(CoinBean bean) {
        mBean = bean;
        return this;
    }

    public WxPayBuilder setPayCallback(PayCallback callback) {
        mPayCallback = new WeakReference<>(callback).get();
        return this;
    }

    public void pay() {
        HttpUtil.getWxOrder(mBean.getMoney(), mBean.getId(), mBean.getCoin(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String partnerId = obj.getString("partnerid");
                    String prepayId = obj.getString("prepayid");
                    String packageValue = obj.getString("package");
                    String nonceStr = obj.getString("noncestr");
                    String timestamp = obj.getString("timestamp");
                    String sign = obj.getString("sign");
                    if (TextUtils.isEmpty(partnerId) ||
                            TextUtils.isEmpty(prepayId) ||
                            TextUtils.isEmpty(packageValue) ||
                            TextUtils.isEmpty(nonceStr) ||
                            TextUtils.isEmpty(timestamp) ||
                            TextUtils.isEmpty(sign)) {
                        ToastUtil.show(Constants.PAY_WX_NOT_ENABLE);
                        return;
                    }
                    PayReq req = new PayReq();
                    req.appId = mAppId;
                    req.partnerId = partnerId;
                    req.prepayId = prepayId;
                    req.packageValue = packageValue;
                    req.nonceStr = nonceStr;
                    req.timeStamp = timestamp;
                    req.sign = sign;
                    IWXAPI wxApi = WxApiWrapper.getInstance().getWxApi();
                    if (wxApi == null) {
                        ToastUtil.show(R.string.coin_charge_failed);
                        return;
                    }
                    boolean result = wxApi.sendReq(req);
                    if (!result) {
                        ToastUtil.show(R.string.coin_charge_failed);
                    }
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUtil.loadingDialog(mContext, false);
            }
        });
    }


    public void payAuth(String partnerid, String prepayid,
                        String packageStr, String noncestr,
                        String timestamp, String sign) {

        if (TextUtils.isEmpty(partnerid) ||
                TextUtils.isEmpty(prepayid) ||
                TextUtils.isEmpty(packageStr) ||
                TextUtils.isEmpty(noncestr) ||
                TextUtils.isEmpty(timestamp) ||
                TextUtils.isEmpty(sign)) {
            ToastUtil.show(Constants.PAY_WX_NOT_ENABLE);
            return;
        }

        PayReq req = new PayReq();
        req.appId = mAppId;
        req.partnerId = partnerid;
        req.prepayId = prepayid;
        req.packageValue = packageStr;
        req.nonceStr = noncestr;
        req.timeStamp = timestamp;
        req.sign = sign;

        IWXAPI wxApi = WxApiWrapper.getInstance().getWxApi();
        if (wxApi == null) {
            ToastUtil.show(R.string.pay_failed);
            return;
        }

        boolean result = wxApi.sendReq(req);
        if (!result) {
            ToastUtil.show(R.string.pay_failed);
        }

    }

    public void payGoods(String orderNo, String type, String payType) {
        HttpUtil.getGoodsWxOrder(orderNo, type, payType, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String partnerId = obj.getString("partnerid");
                    String prepayId = obj.getString("prepayid");
                    String packageValue = obj.getString("package");
                    String nonceStr = obj.getString("noncestr");
                    String timestamp = obj.getString("timestamp");
                    String sign = obj.getString("sign");

                    if (TextUtils.isEmpty(partnerId) ||
                            TextUtils.isEmpty(prepayId) ||
                            TextUtils.isEmpty(packageValue) ||
                            TextUtils.isEmpty(nonceStr) ||
                            TextUtils.isEmpty(timestamp) ||
                            TextUtils.isEmpty(sign)) {
                        ToastUtil.show(Constants.PAY_WX_NOT_ENABLE);
                        return;
                    }

                    PayReq req = new PayReq();
                    req.appId = mAppId;
                    req.partnerId = partnerId;
                    req.prepayId = prepayId;
                    req.packageValue = packageValue;
                    req.nonceStr = nonceStr;
                    req.timeStamp = timestamp;
                    req.sign = sign;
                    IWXAPI wxApi = WxApiWrapper.getInstance().getWxApi();

                    if (wxApi == null) {
                        ToastUtil.show(R.string.pay_failed);
                        return;
                    }

                    boolean result = wxApi.sendReq(req);

                    if (!result) {
                        ToastUtil.show(R.string.pay_failed);
                    }

                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUtil.loadingDialog(mContext, false);
            }

        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayResponse(BaseResp resp) {
        if (mPayCallback != null) {
            if (0 == resp.errCode) {//支付成功
                mPayCallback.onSuccess();
            } else {//支付失败
                mPayCallback.onFailed();
            }
        }
        mContext = null;
        mPayCallback = null;
        EventBus.getDefault().unregister(this);
    }


}
