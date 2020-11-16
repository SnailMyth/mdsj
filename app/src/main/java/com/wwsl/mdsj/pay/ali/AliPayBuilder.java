package com.wwsl.mdsj.pay.ali;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.frame.fire.util.LogUtils;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.CoinBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.pay.PayCallback;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by cxf on 2017/9/21.
 */

public class AliPayBuilder {

    private Activity mActivity;
    private String mPayInfo;//支付宝订单信息 包括 商品信息，订单签名，签名类型
    private CoinBean mBean;
    private PayHandler mPayHandler;

    public AliPayBuilder(Activity activity) {
        mActivity = new WeakReference<>(activity).get();
    }

    public AliPayBuilder setCoinBean(CoinBean bean) {
        mBean = bean;
        return this;
    }


    public AliPayBuilder setPayCallback(PayCallback callback) {
        mPayHandler = new PayHandler(callback);
        return this;
    }


    /**
     * 从服务器端获取订单号,即下单
     */
    public void pay() {
        if (mBean == null) {
            return;
        }
        HttpUtil.getAliOrder(mBean.getMoney(), mBean.getId(), mBean.getCoin(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mPayInfo = obj.getString("pay_package");//商品信息
                    String ordeId = obj.getString("orderid");//商品信息
                    L.e("支付宝订单信息----->" + mPayInfo);
                    invokeAliPay();
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUtil.loadingDialog(mActivity, false);
            }

        });
    }

    public void identifyPay(String orderNum) {
        mPayInfo = orderNum;
        invokeAliPay();
    }


    public void payGoods(String orderNum, String type, String payType) {
        HttpUtil.getGoodsAliOrder(orderNum, type, payType, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mPayInfo = obj.getString("pay_package");//商品信息
                    String ordeId = obj.getString("orderid");//商品信息
                    L.e("支付宝订单信息----->" + mPayInfo);
                    invokeAliPay();
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
                return DialogUtil.loadingDialog(mActivity, false);
            }
        });
    }

    /**
     * 调用支付宝sdk
     */
    private void invokeAliPay() {
        new Thread(() -> {
            PayTask aliPay = new PayTask(mActivity);
            //执行支付，这是一个耗时操作，最后返回支付的结果，用handler发送到主线程
            Map<String, String> result = aliPay.payV2(mPayInfo, true);
            LogUtils.e("支付宝返回结果----->" + result);
            if (mPayHandler != null) {
                Message msg = Message.obtain();
                msg.obj = result;
                mPayHandler.sendMessage(msg);
            }
        }).start();
    }


    private static class PayHandler extends Handler {

        private PayCallback mPayCallback;

        public PayHandler(PayCallback payCallback) {
            mPayCallback = new WeakReference<>(payCallback).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mPayCallback != null) {
                @SuppressWarnings("unchecked")
                Map<String, String> result = (Map<String, String>) msg.obj;

                if ("9000".equals(result.get("resultStatus"))) {
                    mPayCallback.onSuccess();
                } else {
                    mPayCallback.onFailed();
                }

            }
            mPayCallback = null;
        }
    }

}
