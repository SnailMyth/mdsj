package com.wwsl.mdsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.af.FourthActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.AdvertiseBean;
import com.wwsl.mdsj.bean.net.MdBaseDataBean;
import com.wwsl.mdsj.dialog.ChargeDialogFragment;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ClickUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.ZjUtils;

import java.util.List;

/**
 * 豆丁
 */
public class MyBalanceActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvMoney;
    private TextView tvFreezeMoney;
    private TextView tvMoneyA;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_my_balance;
    }

    @Override
    protected void init() {
        initView();
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        tvMoney = findViewById(R.id.tvMoney);
        TextView tvCharge = findViewById(R.id.tvCharge);
        tvMoneyA = findViewById(R.id.tvMoneyA);
        TextView tvDeposit = findViewById(R.id.tvDeposit);
        RoundedImageView ivAd1 = findViewById(R.id.ivAd1);
        RoundedImageView ivAd2 = findViewById(R.id.ivAd2);

        tvFreezeMoney = findViewById(R.id.tvFreezeMoney);
        TextView tvTitle = findViewById(R.id.title);
        tvCharge.setOnClickListener(this);
        tvDeposit.setOnClickListener(this);
        tvTitle.setText(AppConfig.getInstance().getCoinName());
        tvMoney.setText(AppConfig.getInstance().getUserBean().getCoin());
        tvFreezeMoney.setText(String.format("已冻结%s", "0"));
        findViewById(R.id.ll_mall_welfare).setOnClickListener(this);
        findViewById(R.id.ll_farmer_welfare).setOnClickListener(this);
        findViewById(R.id.ll_partner_welfare).setOnClickListener(this);
        findViewById(R.id.ll_bill).setOnClickListener(this);
        ivAd1.setOnClickListener(this);
        ivAd2.setOnClickListener(this);

        List<AdvertiseBean> adInnerList = AppConfig.getInstance().getConfig().getAdInnerList();
        if (adInnerList != null && adInnerList.size() >= 2) {
            ivAd1.setVisibility(View.VISIBLE);
            String adUrl1 = adInnerList.get(1).getUrl();
            ImgLoader.display(adInnerList.get(1).getThumb(), ivAd1);
        } else {
            ivAd1.setVisibility(View.GONE);
        }

        if (adInnerList != null && adInnerList.size() >= 3) {
            ivAd2.setVisibility(View.VISIBLE);
            String adUrl2 = adInnerList.get(2).getUrl();
            ImgLoader.display(adInnerList.get(2).getThumb(), ivAd2);
        } else {
            ivAd2.setVisibility(View.GONE);
        }

        findViewById(R.id.record_list).setOnClickListener(v -> {
            if (!ClickUtil.canClick()) return;

            Intent intent = new Intent(mActivity, FourthActivity.class);
            intent.putExtra("R.id", v.getId());
            intent.putExtra("type", 3);
            intent.putExtra("title", "提现记录");
            startActivity(intent);
        });
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, MyBalanceActivity.class);
        context.startActivity(intent);
    }

    private ChargeDialogFragment fragment;

    public void openChargeDialog() {
        if (null == fragment) {
            fragment = new ChargeDialogFragment();
        }
        fragment.show(getSupportFragmentManager(), "ChargeDialogFragment");
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canClick()) return;
        switch (v.getId()) {
            case R.id.tvCharge:
                openChargeDialog();
                break;
            case R.id.tvDeposit:
                if (AppConfig.getInstance().getUserBean().getIsIdIdentify() != 1) {
                    ToastUtil.show("暂未身份认证,请先进行身份认证");
                    UserIdentifyActivity.forward(MyBalanceActivity.this);
                } else {
                    CoinExchangeActivity.forward(this, Constants.EXCHANGE_TYPE_DD_DEPOSIT);
                }
                break;
            case R.id.ll_mall_welfare://商城福利
                MallWelfareActivity.invoke(this);
                break;
            case R.id.ll_farmer_welfare://助农福利
                FarmerWelfareActivity.invoke(this);
                break;
            case R.id.ll_partner_welfare://代理福利
                PartnerWelfareActivity.invoke(this);
                break;
            case R.id.ll_bill://账单
                MoneyDetailActivity.forward(this, HttpConst.DETAIL_ACTION_ALL, HttpConst.DETAIL_ACTION_ALL);
                break;
            case R.id.ivAd1:
            case R.id.ivAd2:
                showAd();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    tvMoney.setText(coin);
                    tvMoneyA.setText(AppConfig.getInstance().getUserBean().getCommission());
                }
            }
        });

        HttpUtil.getMDBaseInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 200 && null != info && info.length > 0) {
                    MdBaseDataBean parse = JSON.parseObject(info[0], MdBaseDataBean.class);
                    if (null != parse) {
                        AppConfig.getInstance().setMdBaseDataBean(parse);
                        tvFreezeMoney.setText(String.format("已冻结%s", parse.getMaodouFrozen()));
                    }
                }
            }
        });

    }

    private void showAd() {
        ZjUtils.getInstance().showAd();
    }
}
