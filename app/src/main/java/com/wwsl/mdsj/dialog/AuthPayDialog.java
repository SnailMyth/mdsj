package com.wwsl.mdsj.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxj.xpopup.core.BasePopupView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import java.util.ArrayList;
import java.util.List;

public class AuthPayDialog extends BasePopupView implements View.OnClickListener {


    private TextView txPrice;
    private ConstraintLayout aliLayout;
    private ConstraintLayout wxLayout;
    private ConstraintLayout bankLayout;

    private List<ImageView> ivChecks;
    private OnDialogCallBackListener listener;
    private int selected = 0;
    private String pirce;

    public AuthPayDialog(@NonNull Context context, String pirce, OnDialogCallBackListener listener) {
        super(context);
        this.listener = listener;
        this.ivChecks = new ArrayList<>();
        this.pirce = pirce;
    }


    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_auth_pay;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        txPrice = findViewById(R.id.txPrice);
        aliLayout = findViewById(R.id.aliLayout);
        wxLayout = findViewById(R.id.wxLayout);
        bankLayout = findViewById(R.id.bankLayout);

        txPrice.setText(String.format("您需要支付:%s元", pirce));

        ImageView ivAliCheck = findViewById(R.id.ivAliCheck);
        ImageView ivWxCheck = findViewById(R.id.ivWxCheck);
        ImageView ivBankCheck = findViewById(R.id.ivBankCheck);
        ivChecks.add(ivAliCheck);
        ivChecks.add(ivWxCheck);
        ivChecks.add(ivBankCheck);
        ivAliCheck.setOnClickListener(this);
        ivWxCheck.setOnClickListener(this);
        ivBankCheck.setOnClickListener(this);


        findViewById(R.id.btnPay).setOnClickListener(this);
        findViewById(R.id.ivClose).setOnClickListener(v -> {
            dismiss();
        });


        HttpUtil.getBalance(
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            String ali = obj.getString("aliapp_switch");
                            String wx = obj.getString("wx_switch");
                            String bank = obj.getString("yinlian_switch");
                            aliLayout.setVisibility("1".equals(ali) ? VISIBLE : GONE);
                            wxLayout.setVisibility("1".equals(wx) ? VISIBLE : GONE);
                            bankLayout.setVisibility("1".equals(bank) ? VISIBLE : GONE);
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAliCheck:
                changeUI(0);
                break;
            case R.id.ivWxCheck:
                changeUI(1);
                break;
            case R.id.ivBankCheck:
                changeUI(2);
                break;
            case R.id.btnPay:
                dismiss();
                if (listener != null) {
                    listener.onDialogViewClick(null, selected);
                }
                break;
        }
    }

    private void changeUI(int i) {
        for (int j = 0; j < ivChecks.size(); j++) {
            ivChecks.get(j).setBackgroundResource(j == i ? R.mipmap.icon_agree_enable : R.mipmap.icon_agree_disable);
        }

        selected = i;
    }
}
