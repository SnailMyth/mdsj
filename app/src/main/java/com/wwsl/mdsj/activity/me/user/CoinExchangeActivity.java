package com.wwsl.mdsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.lxj.xpopup.XPopup;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.af.FifthActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.bean.net.NetBankCardBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.ClickUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.dialog.InputPwdDialog;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

import cn.hutool.core.util.StrUtil;

/**
 * @author :
 * @date : 2020/7/6 19:06
 * @description : 余额兑换豆丁界面
 */
public class CoinExchangeActivity extends BaseActivity {
    private TextView title;
    private int type = Constants.EXCHANGE_TYPE_DOUDING;
    private TextView tvDes;
    private TextView tvLabel;
    private ConstraintLayout top;
    private TextView tvMoney;
    private EditText etInput;
    private TextView maxMoney;
    private TextView exchangeAll;
    private TextView tvFee;
    private TextView tvBankTitle;
    private TextView tvCardNum;
    private TextView btnDone;
    private TextView txTips;
    private TextView tvLabelGet;
    private TextView tvMoneyGet;
    private TextView temp_tv_tip;
    private ConstraintLayout layoutBank;
    private String votes;
    private NetBankCardBean bankBean;
    private String maxWelfare;
    private WebView webView;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_coin_exchange;
    }

    @Override
    protected void init() {
        votes = AppConfig.getInstance().getUserBean().getVotes();
        type = getIntent().getIntExtra("type", 0);
        initView();
    }

    public void backClick(View view) {
        finish();
    }

    public void jumpDes(View view) {
        if (!ClickUtil.canClick()) return;

        if (type == Constants.EXCHANGE_TYPE_DOUDING) {
            Intent intent = new Intent(mActivity, FifthActivity.class);
            intent.putExtra("R.id", R.id.record_list);
            intent.putExtra("type", 1);
            intent.putExtra("title", "兑换记录");
            startActivity(intent);
        } else if (type == Constants.EXCHANGE_TYPE_DD_DEPOSIT) {
            //福利豆丁转换余额
            CoinExchangeActivity.forward(this, Constants.EXCHANGE_TYPE_DD_2_BALANCE);
            finish();
        }
    }

    public void exchangeDone(View view) {
        String trim = etInput.getText().toString().trim();
        double money;
        try {
            money = Double.parseDouble(trim);
        } catch (Exception e) {
            money = 0;
        }
        if (money == 0) {
            ToastUtil.show("请输入正确金额");
            return;
        }
        submit();
    }


    public static void forward(Context context, int type) {
        Intent intent = new Intent(context, CoinExchangeActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    private void initView() {
        tvDes = findViewById(R.id.tvDes);
        btnDone = findViewById(R.id.btnDone);
        title = findViewById(R.id.title);
        tvLabelGet = findViewById(R.id.tvLabelGet);
        tvMoneyGet = findViewById(R.id.tvMoneyGet);
        tvLabel = findViewById(R.id.tvLabel);
        top = findViewById(R.id.top);
        temp_tv_tip = findViewById(R.id.temp_tv_tip);
        tvMoney = findViewById(R.id.tvMoney);
        etInput = findViewById(R.id.etInput);
        maxMoney = findViewById(R.id.maxMoney);
        exchangeAll = findViewById(R.id.exchangeAll);
        tvFee = findViewById(R.id.tvFee);
        tvBankTitle = findViewById(R.id.tvBankTitle);
        tvCardNum = findViewById(R.id.tvCardNum);
        layoutBank = findViewById(R.id.layoutBank);
        txTips = findViewById(R.id.txTips);
        webView = findViewById(R.id.explain);

        maxWelfare = AppConfig.getInstance().getUserBean().getCommission();
        if (type == Constants.EXCHANGE_TYPE_DOUDING) {
            title.setText("福利兑换");
            tvDes.setText("兑换记录");
            tvDes.setVisibility(View.VISIBLE);
            tvMoney.setVisibility(View.GONE);
            tvFee.setVisibility(View.GONE);
            tvFee.setVisibility(View.GONE);
            layoutBank.setVisibility(View.GONE);
            tvLabel.setText("兑换金额");
            exchangeAll.setText("全部兑换");
            btnDone.setText("兑换");
            txTips.setVisibility(View.GONE);
            maxMoney.setText(String.format("最多可兑换金额￥%s", votes));
            tvLabelGet.setVisibility(View.GONE);
            tvMoneyGet.setVisibility(View.GONE);

            temp_tv_tip.setText("兑换说明");
            webView.setVisibility(View.VISIBLE);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                    String mUrl = request.getUrl().toString();
                    webView.loadUrl(mUrl);
                    return true;
                }
            });
            webView.loadUrl(HtmlConfig.WEB_LINK_EXCHANGE_RULE + "&uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken());
        } else if (type == Constants.EXCHANGE_TYPE_BALANCE) {
            tvLabelGet.setVisibility(View.VISIBLE);
            tvMoneyGet.setVisibility(View.VISIBLE);
            title.setText("礼物提现");
            btnDone.setText("立即提现");
            tvLabel.setText("兑换金额");
            exchangeAll.setText("全部提现");
            String format = String.format("最多可兑换%s%s", AppConfig.getInstance().getVotesName(), "￥" + votes);
            maxMoney.setText(format);
            tvDes.setVisibility(View.GONE);
            tvMoney.setVisibility(View.VISIBLE);
            tvMoney.setText(String.format("当前%s:%s", AppConfig.getInstance().getVotesName(), AppConfig.getInstance().getUserBean().getVotes()));
            tvFee.setVisibility(View.VISIBLE);
            tvFee.setText(String.format("提现比例:%s", AppConfig.getInstance().getUserBean().getMoneyRate() + "%"));
            temp_tv_tip.setVisibility(View.VISIBLE);
            txTips.setVisibility(View.VISIBLE);
//            txTips.setText(String.format("10%s可提现%s元RMB", AppConfig.getInstance().getVotesName(), getDepositMoney("10")));
            txTips.setText(String.format("10%s可提现%s元RMB", AppConfig.getInstance().getVotesName(), 1));
            layoutBank.setVisibility(View.VISIBLE);

            etInput.setHint("最低提现金额100");
        } else if (type == Constants.EXCHANGE_TYPE_DD_DEPOSIT) {
            title.setText("福利提现");
            tvLabel.setText("兑换金额");
            exchangeAll.setText("全部提现");
            btnDone.setText("提现");
            maxMoney.setText(String.format("最多可兑换￥%s元福利", maxWelfare));
            tvDes.setText("兑换余额");
            tvDes.setVisibility(View.VISIBLE);
            tvMoney.setVisibility(View.VISIBLE);
            txTips.setVisibility(View.GONE);

            String commission = AppConfig.getInstance().getUserBean().getCommission();
            tvMoney.setText(String.format("当前福利:%s", commission));
            tvFee.setVisibility(View.GONE);
            layoutBank.setVisibility(View.VISIBLE);
            tvLabelGet.setVisibility(View.VISIBLE);
            tvMoneyGet.setVisibility(View.VISIBLE);
            temp_tv_tip.setVisibility(View.VISIBLE);
            txTips.setVisibility(View.VISIBLE);
//            txTips.setText(String.format("10%s可提现%s元RMB", "福利豆丁", getDDDepositMoney("10")));
            txTips.setText("兑换比例: 10.00%\n1福利可提现1元RMB");
        } else if (type == Constants.EXCHANGE_TYPE_DD_2_BALANCE) {
            title.setText("福利兑换");
            tvDes.setVisibility(View.GONE);
            tvMoney.setVisibility(View.GONE);
            tvFee.setVisibility(View.GONE);
            tvFee.setVisibility(View.GONE);
            layoutBank.setVisibility(View.GONE);
            tvLabel.setText("兑换金额");
            exchangeAll.setText("全部兑换");
            exchangeAll.setVisibility(View.GONE);
            btnDone.setText("兑换");
            txTips.setVisibility(View.GONE);
            maxMoney.setVisibility(View.GONE);
            maxMoney.setText(String.format("最多可兑换金额￥%s", votes));
            tvLabelGet.setVisibility(View.GONE);
            tvMoneyGet.setVisibility(View.GONE);
            temp_tv_tip.setVisibility(View.GONE);
        } else {
            ToastUtil.show("参数传递错误");
            finish();
        }

        exchangeAll.setOnClickListener(v -> {
            if (type == Constants.EXCHANGE_TYPE_DOUDING) {
                etInput.setText(votes);
            } else if (type == Constants.EXCHANGE_TYPE_BALANCE) {
                etInput.setText(votes);
            } else if (type == Constants.EXCHANGE_TYPE_DD_DEPOSIT) {
                etInput.setText(maxWelfare);
            } else if (type == Constants.EXCHANGE_TYPE_DD_2_BALANCE) {
                etInput.setText(maxWelfare);
            }
        });

        layoutBank.setOnClickListener(v -> jumpBankSelected());

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && StringUtil.isMoney(s.toString())) {
                    double money;
                    try {
                        money = Double.parseDouble(String.valueOf(s));
                    } catch (Exception e) {
                        money = 0;
                    }
                    if (money < 100) {
                        return;
                    }
                    if (type == Constants.EXCHANGE_TYPE_BALANCE) {
                        tvMoneyGet.setText(String.format("￥%s元", getDepositMoney(s.toString())));
                    } else if (type == Constants.EXCHANGE_TYPE_DD_DEPOSIT) {
                        double res = money - 10;
//                        tvMoneyGet.setText(String.format("￥%s元", getDDDepositMoney(s.toString())));
                        BigDecimal b = new BigDecimal(res);
                        res = b.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                        tvMoneyGet.setText(String.format("￥%s元", res));
                    }
                } else {
                    tvMoneyGet.setText(String.format("￥%s元", "0.00"));
                }
            }
        });
    }

    private void jumpBankSelected() {
        startActivityForResult(new Intent(CoinExchangeActivity.this, SelectBankCardActivity.class), 1001);
    }

    private void submit() {
        // validate
        String money = etInput.getText().toString().trim();

        if (TextUtils.isEmpty(money) || !StringUtil.isMoney(money)) {
            Toast.makeText(this, "请正确输入金额", Toast.LENGTH_SHORT).show();
            return;
        }

        if (type == Constants.EXCHANGE_TYPE_BALANCE || type == Constants.EXCHANGE_TYPE_DD_DEPOSIT) {
            if (null == bankBean || StrUtil.isEmpty(bankBean.getId())) {
                ToastUtil.show("请选择到账银行卡");
                return;
            }
        }

        new XPopup.Builder(CoinExchangeActivity.this)
                .hasShadowBg(true)
                .customAnimator(new DialogUtil.DialogAnimator())
                .asCustom(new InputPwdDialog(CoinExchangeActivity.this, "", new OnDialogCallBackListener() {
                    @Override
                    public void onDialogViewClick(View view, Object object) {
                        String pwd = (String) object;
                        if (type == Constants.EXCHANGE_TYPE_DOUDING) {
                            //兑换豆丁
                            HttpUtil.doVoteCash(money, new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    ToastUtil.show(msg);
                                    if (code == 0) {
                                        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                                            @Override
                                            public void callback(UserBean bean) {
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });
                        } else if (type == Constants.EXCHANGE_TYPE_BALANCE) {
                            HttpUtil.doCash(money, pwd, bankBean.getId(), new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    ToastUtil.show(msg);
                                    if (code == 0) {
                                        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                                            @Override
                                            public void callback(UserBean bean) {
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });
                        } else if (type == Constants.EXCHANGE_TYPE_DD_DEPOSIT) {
                            HttpUtil.doWelfareCash(money, pwd, bankBean.getId(), new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    ToastUtil.show(msg);
                                    if (code == 0) {
                                        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                                            @Override
                                            public void callback(UserBean bean) {
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });
                        } else if (type == Constants.EXCHANGE_TYPE_DD_2_BALANCE) {
                            HttpUtil.doWelfareExchange(money, new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    ToastUtil.show(msg);
                                    if (code == 0) {
                                        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                                            @Override
                                            public void callback(UserBean bean) {
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                }))
                .show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && data != null) {
            bankBean = (NetBankCardBean) data.getSerializableExtra("bank");
            tvBankTitle.setText(bankBean.getBankName());
            String cardNumber = bankBean.getCardNumber();
            if (!StrUtil.isEmpty(cardNumber)) {
                String pre = cardNumber.substring(0, 4);
                String suffix = cardNumber.substring(cardNumber.length() - 4);
                String num = pre + "**** **** ***" + suffix;
                tvCardNum.setText(num);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getDepositMoney(String money) {

        // res=money/10  * (100 -5)*100%
        BigDecimal subtract = new BigDecimal(100).setScale(2, RoundingMode.HALF_UP).subtract(new BigDecimal(AppConfig.getInstance().getUserBean().getMoneyRate())).divide(new BigDecimal(100), RoundingMode.HALF_UP);
        BigDecimal multiply = new BigDecimal(money).divide(new BigDecimal(10), RoundingMode.HALF_UP).multiply(subtract);
        return multiply.toString();
    }

    public String getDDDepositMoney(String money) {

        // res=money/10  * (100 -5)*100%
        BigDecimal subtract = new BigDecimal(100).setScale(2, RoundingMode.HALF_UP).subtract(new BigDecimal(AppConfig.getInstance().getUserBean().getWelfareDepositRate())).divide(new BigDecimal(100), RoundingMode.HALF_UP);
        BigDecimal multiply = new BigDecimal(money).divide(new BigDecimal(10), RoundingMode.HALF_UP).multiply(subtract);
        return multiply.toString();
    }
}
