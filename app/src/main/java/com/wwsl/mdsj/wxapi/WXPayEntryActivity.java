package com.wwsl.mdsj.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.wwsl.mdsj.pay.wx.WxApiWrapper;

import org.greenrobot.eventbus.EventBus;


/**
 * 微信支付的回调页面
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WxApiWrapper.getInstance().getWxApi();
        if (api == null) return;
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        EventBus.getDefault().post(resp);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            handleWXPay(resp);
        }
        finish();
    }

    private final static String TAG = "WXPayEntryActivity";

    //处理支付
    private void handleWXPay(BaseResp response) {
        Gson gson = new Gson();
        String json = gson.toJson(response);
        PayCallbackBean bean = gson.fromJson(json, PayCallbackBean.class);
        EventBus.getDefault().post(bean);
    }

}