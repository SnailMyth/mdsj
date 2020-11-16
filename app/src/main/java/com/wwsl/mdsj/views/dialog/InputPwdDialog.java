package com.wwsl.mdsj.views.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.BasePopupView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.utils.ClickUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.PasswordView;


public class InputPwdDialog extends BasePopupView implements View.OnClickListener {

    private TextView tvMoney;
    private TextView btnOpen;
    private String money = "";
    private PasswordView passwordView;
    private OnDialogCallBackListener listener;

    public InputPwdDialog(@NonNull Context context) {
        super(context);
    }

    public InputPwdDialog(@NonNull Context context, String money, OnDialogCallBackListener listener) {
        super(context);
        this.money = money;
        this.listener = listener;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        passwordView = findViewById(R.id.password);
        tvMoney = findViewById(R.id.tvMoney);
        btnOpen = findViewById(R.id.btnOpen);
        tvMoney.setText(money);
        btnOpen.setOnClickListener(this);
        findViewById(R.id.ivClose).setOnClickListener(this);
    }


    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_input_pwd;
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btnOpen:
                if (passwordView.getPassword().length() < 6) {
                    ToastUtil.show("请输入交易密码");
                } else {
                    dismiss();
                    listener.onDialogViewClick(null, passwordView.getPassword());
                }
                break;
            case R.id.ivClose:
                dismiss();
                break;
        }
    }
}
