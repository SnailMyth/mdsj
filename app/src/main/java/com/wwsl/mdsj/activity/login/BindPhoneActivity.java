package com.wwsl.mdsj.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.MainActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.im.ImMessageUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.CodeCutDownTimer;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;

public class BindPhoneActivity extends BaseActivity {

    private EditText editPhone;
    private EditText editCode;
    private TextView btnCode;
    private CodeCutDownTimer timer;
    private String wxId;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_bind_phone;
    }

    @Override
    protected void init() {
        initView();
    }

    public void backClick(View view) {
        finish();
    }

    public void getCode() {
        String phone = editPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || !StringUtil.isInteger(phone)) {
            Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        HttpUtil.getBindPhoneCode(phone, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
                if (code == 0) {
                    timer.start();
                }
            }
        });
    }

    public void bind(View view) {
        submit();
    }

    private void initView() {
        editPhone = findViewById(R.id.edit_phone);
        editCode = findViewById(R.id.edit_code);
        btnCode = findViewById(R.id.btn_code);
        timer = new CodeCutDownTimer(btnCode);
        wxId = getIntent().getStringExtra("wxId");
        btnCode.setOnClickListener(v -> {
            getCode();
        });
    }

    private void submit() {
        // validate
        String phone = editPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || !StringUtil.isInteger(phone)) {
            Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        String code = editCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        HttpUtil.bindLogin(phone, wxId, code, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String uid = obj.getString("id");
                    String token = obj.getString("token");
                    AppConfig.getInstance().setLoginInfo(uid, token, true);
                    getBaseUserInfo();
                    MobclickAgent.onProfileSignIn("wx", uid);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });

    }

    public static void forward(Context context, String wxId) {
        Intent intent = new Intent(context, BindPhoneActivity.class);
        intent.putExtra("wxId", wxId);
        context.startActivity(intent);
    }

    private void getBaseUserInfo() {
        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                RecommendActivity.forward(BindPhoneActivity.this);
                finish();
            }
        });
    }
}
