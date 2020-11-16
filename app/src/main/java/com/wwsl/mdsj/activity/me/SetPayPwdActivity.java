package com.wwsl.mdsj.activity.me;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

/**
 * Created by cxf on 2018/10/7.
 * 设置支付密码
 */

public class SetPayPwdActivity extends AbsActivity implements View.OnClickListener {

    private EditText mEditNew;
    private EditText mEditConfirm;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_pay_pwd;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return false;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.set_pay_pwd));
        mEditNew = (EditText) findViewById(R.id.edit_new);
        mEditConfirm = (EditText) findViewById(R.id.edit_confirm);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        setPayPwd();
    }

    private void setPayPwd() {

        String pwdNew = mEditNew.getText().toString().trim();
        if (TextUtils.isEmpty(pwdNew)) {
            ToastUtil.show(WordUtil.getString(R.string.pl_input_pay_pwd));
            return;
        }
        String pwdConfirm = mEditConfirm.getText().toString().trim();
        if (TextUtils.isEmpty(pwdConfirm)) {
            ToastUtil.show(WordUtil.getString(R.string.modify_pwd_confirm));
            return;
        }
        if (!pwdNew.equals(pwdConfirm)) {
            ToastUtil.show(WordUtil.getString(R.string.reg_pwd_error));
            return;
        }
        HttpUtil.setPayPwd(pwdNew, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                    finish();
                } else {
                    ToastUtil.show(msg);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConst.SET_PAY_PWD);
        super.onDestroy();
    }
}
