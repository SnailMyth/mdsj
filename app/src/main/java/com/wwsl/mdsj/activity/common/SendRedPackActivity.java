package com.wwsl.mdsj.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PageRange;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.lxj.xpopup.XPopup;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.maodou.MdStallP2PActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.upload.PictureUploadCallback;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.dialog.InputPwdDialog;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

public class SendRedPackActivity extends BaseActivity {

    private EditText etMoney;
    private EditText etRemark;
    private TextView btnSend;
    private TextView txMoney;
    private String toUid;
    private String remark = "";
    private String money = "";

    @Override
    protected int setLayoutId() {
        return R.layout.activity_send_red_pack;
    }

    @Override
    protected void init() {
        toUid = getIntent().getStringExtra("toUid");
        initView();
        initListener();
    }

    private void initListener() {
        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txMoney.setText(String.format("豆丁:%s", s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        etMoney = findViewById(R.id.etMoney);
        etRemark = findViewById(R.id.etRemark);
        btnSend = findViewById(R.id.btnSend);
        txMoney = findViewById(R.id.txMoney);
    }


    private void submit() {
        // validate
        money = etMoney.getText().toString().trim();

        if (TextUtils.isEmpty(money)) {
            Toast.makeText(this, "请输入金额!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!StringUtil.isInteger(money)){
            Toast.makeText(this, "请输入正确的金额!", Toast.LENGTH_SHORT).show();
            return;
        }

        remark = etRemark.getText().toString().trim();
        if (TextUtils.isEmpty(remark)) {
            remark = etRemark.getHint().toString();
        }


        new XPopup.Builder(SendRedPackActivity.this)
                .hasShadowBg(true)
                .customAnimator(new DialogUtil.DialogAnimator())
                .asCustom(new InputPwdDialog(SendRedPackActivity.this, "", new OnDialogCallBackListener() {
                    @Override
                    public void onDialogViewClick(View view, Object object) {
                        String pwd = (String) object;
                        HttpUtil.sendChatRedPack(toUid, money, remark, pwd, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0 && null != info && info.length > 0) {
                                    JSONObject jsonObject = JSON.parseObject(info[0]);
                                    Intent intent = new Intent();
                                    intent.putExtra("packetId", jsonObject.getString("red_package_id"));
                                    intent.putExtra("money", jsonObject.getString("price"));
                                    intent.putExtra("remark", jsonObject.getString("remarks"));
                                    intent.putExtra("toUid", jsonObject.getString("to_uid"));
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    ToastUtil.show(msg);
                                }
                            }
                        });
                    }
                }))
                .show();
    }

    public void doSend(View view) {
        submit();
    }

    public static void forward(Activity context, int requestCode, String toUid) {
        Intent intent = new Intent(context, SendRedPackActivity.class);
        intent.putExtra("toUid", toUid);
        context.startActivityForResult(intent, requestCode);
    }
}
