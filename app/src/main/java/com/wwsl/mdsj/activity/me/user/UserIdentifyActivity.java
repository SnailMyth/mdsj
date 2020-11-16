package com.wwsl.mdsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxj.xpopup.XPopup;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.BaseEncryptHelper;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class UserIdentifyActivity extends BaseActivity {
    private final static String TAG = "UserIdentifyActivity";
    private EditText etName;
    private EditText etCardId;
    private TextView tvRealName;
    private TextView tvCardId;
    private ConstraintLayout unidentifiedLayout;
    private ConstraintLayout identifiedLayout;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_identify;
    }

    @Override
    protected void init() {
        tvRealName = findViewById(R.id.tvRealName);
        tvCardId = findViewById(R.id.tvCardId);
        etName = findViewById(R.id.etName);
        etCardId = findViewById(R.id.etCardId);
        unidentifiedLayout = findViewById(R.id.unidentifiedLayout);
        identifiedLayout = findViewById(R.id.identifiedLayout);
    }

    private void judgeAuth() {
        boolean isAuth = AppConfig.getInstance().getUserBean().getIsIdIdentify() > 0;
        unidentifiedLayout.setVisibility(isAuth ? View.GONE : View.VISIBLE);
        identifiedLayout.setVisibility(isAuth ? View.VISIBLE : View.GONE);

        if (isAuth) {
            loadData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        judgeAuth();
    }

    private void loadData() {
        showLoadCancelable(false, "获取数据中...");
        HttpUtil.getUserRealInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                dismissLoad();
                if (code == 0 && info != null && info.length > 0) {
                    String str = info[0];
                    String decode = BaseEncryptHelper.decode(str);

                    if (StrUtil.isEmpty(decode)) {
                        ToastUtil.show("获取信息失败");
                        return;
                    }

                    String[] data = decode.split("-");
                    if (data.length != 2) {
                        ToastUtil.show("获取信息失败");
                        return;
                    }

                    String name = data[0];
                    name = StrUtil.replace(name, 1, name.length(), '*');
                    String id = data[1];
                    id = StrUtil.replace(id, id.length() - 6, id.length(), '*');
                    tvRealName.setText(name);
                    tvCardId.setText(id);
                }
            }

            @Override
            public void onError() {
                dismissLoad();
            }
        });
    }

    public void backClick(View view) {
        finish();
    }


    public static void forward(Context context) {
        Intent intent = new Intent(context, UserIdentifyActivity.class);
        context.startActivity(intent);
    }

    public void nextStep(View view) {
        String name = etName.getText().toString().trim();
        String id = etCardId.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            ToastUtil.show(WordUtil.getString(R.string.pls_input_name));
            return;
        }

        if (TextUtils.isEmpty(id)) {
            ToastUtil.show(WordUtil.getString(R.string.pls_input_id_card));
            return;
        }

        nextAuth(name, id);

    }

    private void nextAuth(String name, String id) {
        showLoadCancelable(false, "验证输入信息中...");
        HttpUtil.verifyIdDuplicated(id, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    //只获取支付宝
                    HttpUtil.getVerifyStatus(new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            dismissLoad();
                            if (code == 0 && info != null && info.length != 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                String isNeed = obj.getString("is_pay");
                                String money = obj.getString("price");
                                String isCanAuth = obj.getString("is_auth");

                                if ("0".equals(isCanAuth)) {
                                    ToastUtil.show("当日验证次数已用完");
                                } else {
                                    UserAuthActivity.forward(UserIdentifyActivity.this, name, id, isNeed, money);
                                    finish();
                                }
                            } else {
                                ToastUtil.show(msg);
                            }
                        }
                    });
                } else {
                    ToastUtil.show(msg);
                    dismissLoad();
                }
            }

            @Override
            public void onError() {
                dismissLoad();
            }
        });
    }
}
