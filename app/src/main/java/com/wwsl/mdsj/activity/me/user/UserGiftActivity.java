package com.wwsl.mdsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.af.FourthActivity;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.utils.ClickUtil;
import com.wwsl.mdsj.utils.ToastUtil;

public class UserGiftActivity extends BaseActivity {

    private TextView tvVote;
    private TextView tvLabel;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_gift;
    }

    @Override
    protected void init() {
        initView();
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        tvVote = (TextView) findViewById(R.id.tvMoney);
        tvLabel = (TextView) findViewById(R.id.tvLabel);
        tvLabel.setText(String.format("可提现%s", AppConfig.getInstance().getVotesName()));

        findViewById(R.id.record_list).setOnClickListener(v -> {
            if (!ClickUtil.canClick()) return;

            Intent intent = new Intent(mActivity, FourthActivity.class);
            intent.putExtra("R.id", v.getId());
            intent.putExtra("type", 2);
            intent.putExtra("title", "提现记录");
            startActivity(intent);
        });
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, UserGiftActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvVote.setText(AppConfig.getInstance().getUserBean().getVotes());
    }

    public void showRule(View view) {
        WebViewActivity.forward(this, HtmlConfig.WEB_LINK_DEPOSIT_RULE);
    }

    public void showBankCard(View view) {
        BankCardActivity.forward(this);
    }

    public void showGiftInCome(View view) {
        MoneyDetailActivity.forward(this, HttpConst.DETAIL_TYPE_GIFT_INCOME, HttpConst.DETAIL_ACTION_GIFT);
    }

    public void showGiftExpend(View view) {
        MoneyDetailActivity.forward(this, HttpConst.DETAIL_TYPE_GIFT_EXPEND, HttpConst.DETAIL_ACTION_GIFT);
    }

    public void toDoudingExchange(View view) {
        CoinExchangeActivity.forward(this, Constants.EXCHANGE_TYPE_DOUDING);
    }

    public void depositNow(View view) {
        if (AppConfig.getInstance().getUserBean().getIsIdIdentify() != 1) {
            ToastUtil.show("暂未身份认证,请先进行身份认证");
            UserIdentifyActivity.forward(UserGiftActivity.this);
        } else {
            CoinExchangeActivity.forward(this, Constants.EXCHANGE_TYPE_BALANCE);
        }
    }
}
