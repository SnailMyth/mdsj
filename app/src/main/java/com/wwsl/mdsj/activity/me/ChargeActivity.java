package com.wwsl.mdsj.activity.me;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.CoinBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.pay.PayCallback;
import com.wwsl.mdsj.pay.ali.AliPayBuilder;
import com.wwsl.mdsj.pay.wx.WxPayBuilder;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.wxapi.PayCallbackBean;
import com.wwsl.mdsj.wxapi.ThirdConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * @author :
 * @date : 2020/7/13 16:41
 * @description : 充值界面
 */
public class ChargeActivity extends BaseActivity implements View.OnClickListener {

    private EditText tvMoney;
    private CheckBox aliCheck;
    private ConstraintLayout aliLayout;
    private CheckBox wxCheck;
    private ConstraintLayout wxLayout;
    private CheckBox bankCheck;
    private ConstraintLayout bankLayout;
    private List<CheckBox> boxes;
    private CoinBean coinBean;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_charge;
    }

    @Override
    protected void init() {
        coinBean = (CoinBean) getIntent().getSerializableExtra("coin");
        initView();
        if (null == coinBean) {
            tvMoney.setFocusableInTouchMode(true);
            tvMoney.setFocusable(true);
            tvMoney.requestFocus();
        } else {
            tvMoney.setText(coinBean.getMoney());
            tvMoney.setFocusable(false);
            tvMoney.setFocusableInTouchMode(false);
        }
        initListener();
        loadConfig();
        EventBus.getDefault().register(this);
    }

    private void loadConfig() {
        HttpUtil.getBalance(
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            String ali = obj.getString("aliapp_switch");
                            String wx = obj.getString("wx_switch");
                            String bank = obj.getString("yinlian_switch");
                            aliLayout.setVisibility(ali.equals("1") ? View.VISIBLE : View.GONE);
                            wxLayout.setVisibility(wx.equals("1") ? View.VISIBLE : View.GONE);
                            bankLayout.setVisibility(bank.equals("1") ? View.VISIBLE : View.GONE);
                        }
                    }
                });
    }

    @Subscribe
    public void chargeBack(PayCallbackBean bean) {
        if (null != bean) {
            LogUtils.e("chargeBack(Charge)===" + bean.errCode);
            switch (bean.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    ToastUtil.show("支付成功");
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    ToastUtil.show("用户取消");
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                default:
                    ToastUtil.show("支付失败");
                    break;
            }
        }
    }

    private void initListener() {
        aliLayout.setOnClickListener(this);
        wxLayout.setOnClickListener(this);
        bankLayout.setOnClickListener(this);
        aliCheck.setClickable(false);
        wxCheck.setClickable(false);
        bankCheck.setClickable(false);
    }

    @Override
    protected boolean isStatusBarWhite() {
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConst.GET_BALANCE);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private final static String TAG = "ChargeActivity";

    PayCallback mPayCallback = new PayCallback() {
        @Override
        public void onSuccess() {
            ToastUtil.show("充值成功");
        }

        @Override
        public void onFailed() {
            ToastUtil.show(R.string.coin_charge_failed);
        }
    };


    public static void forward(Context context, CoinBean bean) {
        Intent intent = new Intent(context, ChargeActivity.class);
        intent.putExtra("coin", bean);
        context.startActivity(intent);
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, ChargeActivity.class);
        context.startActivity(intent);
    }

    public void backClick(View view) {
        finish();
    }


    public void doChange(View view) {
        BigDecimal money = new BigDecimal(tvMoney.getText().toString().trim());
        if (1 != money.compareTo(BigDecimal.ZERO)) {
            ToastUtil.show("充值金额错误");
            return;
        }

        if (coinBean == null) {
            coinBean = new CoinBean();
            coinBean.setId("0");
            coinBean.setMoney(tvMoney.getText().toString().trim());
            coinBean.setCoin(tvMoney.getText().toString().trim());
        }

        if (aliCheck.isChecked()) {
            aliPay();
            return;
        }
        if (wxCheck.isChecked()) {
            wxPay();
            return;
        }

        if (bankCheck.isChecked()) {

            return;
        }
    }


    private void aliPay() {
        if (!AppConfig.isAppExist(Constants.PACKAGE_NAME_ALI)) {
            ToastUtil.show(R.string.coin_ali_not_install);
            return;
        }

        AliPayBuilder builder = new AliPayBuilder(this);
        builder.setCoinBean(coinBean);
        builder.setPayCallback(mPayCallback);
        builder.pay();

    }

    private void wxPay() {
        if (!AppConfig.isAppExist(Constants.PACKAGE_NAME_WX)) {
            ToastUtil.show(R.string.coin_wx_not_install);
            return;
        }

        WxPayBuilder builder = new WxPayBuilder(this);
        builder.setCoinBean(coinBean);
        builder.setPayCallback(mPayCallback);
        builder.pay();
    }


    private void initView() {
        tvMoney = findViewById(R.id.tvMoney);
        aliCheck = findViewById(R.id.aliCheck);
        aliLayout = findViewById(R.id.aliLayout);
        wxCheck = findViewById(R.id.wxCheck);
        wxLayout = findViewById(R.id.wxLayout);
        bankCheck = findViewById(R.id.bankCheck);
        bankLayout = findViewById(R.id.bankLayout);
        boxes = new ArrayList<>(3);
        boxes.add(aliCheck);
        boxes.add(wxCheck);
        boxes.add(bankCheck);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aliLayout:
                changeChargeType(0);
                break;
            case R.id.wxLayout:
                changeChargeType(1);
                break;
            case R.id.bankLayout:
                changeChargeType(2);
                break;
        }
    }

    private void changeChargeType(int type) {
        for (int i = 0; i < boxes.size(); i++) {
            boxes.get(i).setChecked(i == type);
        }
    }
}
