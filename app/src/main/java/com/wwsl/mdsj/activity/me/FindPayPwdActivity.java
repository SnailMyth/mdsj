package com.wwsl.mdsj.activity.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ButtonUtils;
import com.wwsl.mdsj.utils.CodeCutDownTimer;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;

public class FindPayPwdActivity extends AbsActivity {

    private EditText editPhone;
    private EditText editCode;
    private TextView btnCode;
    private EditText editPwd1;
    private EditText editPwd2;

    private CodeCutDownTimer timer;

    @Override
    protected void main(Bundle savedInstanceState) {
        super.main(savedInstanceState);
        initView();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_pay_pwd;
    }

    private void initView() {
        editPhone = findViewById(R.id.edit_phone);
        editCode = findViewById(R.id.edit_code);
        btnCode = findViewById(R.id.btn_code);
        editPwd1 = findViewById(R.id.edit_pwd_1);
        editPwd2 = findViewById(R.id.edit_pwd_2);
        setTitle("找回交易密码");
        timer = new CodeCutDownTimer(btnCode);
        timer.setColor("#ffffff", "#F95921");

    }

    private void submit() {
        // validate
        String phone = editPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "phone不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String code = editCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "code不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String pwd1 = editPwd1.getText().toString().trim();
        if (TextUtils.isEmpty(pwd1)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String pwd2 = editPwd2.getText().toString().trim();
        if (TextUtils.isEmpty(pwd2)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pwd1.equals(pwd2)) {
            ToastUtil.show("两次输入不一致!");
        }
        showLoadCancelable(false,"修改中...");
        HttpUtil.findPayPwdBindCode(phone, pwd1, code, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                dismissLoad();
                if (code == 0) {
                    ToastUtil.show("操作成功");
                    finish();
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onError() {
                dismissLoad();
            }
        });
    }

    public void findClick(View view) {
        if (ButtonUtils.isFastClick()) return;

        submit();
    }

    public void getFindCode(View view) {
        if (!StringUtil.isInteger(editPhone.getText().toString().trim())) return;
        HttpUtil.getFindPayPwdBindCode(editPhone.getText().toString().trim(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("验证码已发送");
                    timer.start();
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, FindPayPwdActivity.class);
        context.startActivity(intent);
    }

}
