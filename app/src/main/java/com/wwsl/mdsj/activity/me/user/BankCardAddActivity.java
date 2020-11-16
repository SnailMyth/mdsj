package com.wwsl.mdsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.BankInfo;
import com.wwsl.mdsj.utils.CodeCutDownTimer;
import com.wwsl.mdsj.utils.ToastUtil;

public class BankCardAddActivity extends BaseActivity {

    private EditText etRealName;
    private EditText etBanCard;
    private AppCompatEditText etBanCardPhone;
    private AppCompatEditText etCode;
    private TextView getCode;
    private TextView btnSave;
    private TextView bankName;
    private CodeCutDownTimer timer;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_bank_card_add;
    }

    @Override
    protected void init() {
        initView();
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, BankCardAddActivity.class);
        context.startActivity(intent);
    }

    public void backClick(View view) {
        finish();
    }

    public void saveBankCard(View view) {
        submit();
    }

    private void initView() {
        etRealName = (EditText) findViewById(R.id.etRealName);
        etBanCard = (EditText) findViewById(R.id.etBanCard);
        etBanCardPhone = (AppCompatEditText) findViewById(R.id.etBanCardPhone);
        etCode = (AppCompatEditText) findViewById(R.id.etCode);
        getCode = (TextView) findViewById(R.id.getCode);
        btnSave = (TextView) findViewById(R.id.btnSave);
        bankName = (TextView) findViewById(R.id.bankName);

        timer = new CodeCutDownTimer(getCode);
        timer.setColor("#ffffff", "#ffffff");

        etBanCard.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 在输入数据时监听
                int huoqu = etBanCard.getText().toString().length();
                if (huoqu >= 6) {
                    String huoqucc = etBanCard.getText().toString();
                    char[] cardNumber = huoqucc.toCharArray();
                    String name = BankInfo.getNameOfBank(cardNumber, 0);// 获取银行卡的信息
                    bankName.setText(name);
                } else {
                    bankName.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // 在输入数据前监听

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 在输入数据后监听

            }
        });

    }

    private void submit() {
        // validate
        String realName = etRealName.getText().toString().trim();
        if (TextUtils.isEmpty(realName)) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String bankCardNum = etBanCard.getText().toString().trim();
        if (TextUtils.isEmpty(bankCardNum)) {
            Toast.makeText(this, "银行卡号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String phone = etBanCardPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        HttpUtil.addBankCard(realName, bankCardNum, phone, code, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("添加成功");
                    finish();
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    public void getCode(View view) {
        HttpUtil.getBankBindCode(etBanCardPhone.getText().toString().trim(), new HttpCallback() {
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
}
