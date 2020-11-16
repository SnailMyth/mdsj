package com.wwsl.mdsj.activity.login;

import android.app.Dialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.MainActivity;
import com.wwsl.mdsj.activity.common.ActivityManager;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.event.PhoneLoginSuccessEvent;
import com.wwsl.mdsj.event.RegSuccessEvent;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.ValidatePhoneUtil;
import com.wwsl.mdsj.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import cn.hutool.core.util.StrUtil;


/**
 * @author :
 * @date : 2020/6/23 18:36
 * @description : LoginActivity
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "LoginActivity";

    private EditText mEditPhone;
    private EditText etPhone2;
    private EditText mEditPwd;
    private View mBtnLogin;
    private boolean mRegistered = true;//是否是第一次登录
    private String mLoginType = "phone";//登录方式
    private String phone = "";
    private LinearLayout msgLoginLayout;
    private ConstraintLayout phoneLoginLayout;
    private LinearLayout phoneInputLayout;
    private TextView btnPhoneShort;
    private ImageView ivAgree;
    private boolean isAgree = true;


    private void initListener() {

        ivAgree.setOnClickListener(this);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEditPhone.getText().toString();
                String pwd = mEditPwd.getText().toString();
                mBtnLogin.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mEditPhone.addTextChangedListener(textWatcher);
        mEditPwd.addTextChangedListener(textWatcher);

    }

    public static void forward() {
        Intent intent = new Intent(AppContext.sInstance, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppContext.sInstance.startActivity(intent);
    }

    public static void forward(String userPhone) {
        Intent intent = new Intent(AppContext.sInstance, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone", userPhone);
        AppContext.sInstance.startActivity(intent);
    }

    public void loginClick(View v) {
        switch (v.getId()) {
            case R.id.btn_account_login:
                login();
                break;
            case R.id.btn_register:
                register();
                break;
            case R.id.btn_forget_pwd:
                forgetPwd();
                break;
            case R.id.btn_tip:
                forwardTip();
                break;
            case R.id.changeMsgLogin:
                changeUI(1);
                break;
            case R.id.changeAccountLogin:
                changeUI(0);
                break;
            case R.id.btnPhoneShort:
                forwardValidate();
                break;
            case R.id.btnPhoneGetCode:
                String trim = etPhone2.getText().toString().trim();

                if (!StringUtil.isInteger(trim)) {
                    ToastUtil.show("请输入正确的手机号");
                    return;
                }

                phone = trim;

                forwardValidate();
                break;
        }
    }

    private void changeUI(int i) {
        if (i == 0) {
            //账号登陆
            msgLoginLayout.setVisibility(View.VISIBLE);
            phoneLoginLayout.setVisibility(View.GONE);
        } else if (i == 1) {
            //手机号登录
            phoneLoginLayout.setVisibility(View.VISIBLE);
            msgLoginLayout.setVisibility(View.GONE);
        }
    }

    //注册
    private void register() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    //忘记密码
    private void forgetPwd() {
        startActivity(new Intent(this, FindPwdActivity.class));
    }


    //验证码登录
    private void forwardValidate() {
        if (!StrUtil.isEmpty(phone)) {
            HttpUtil.getQuickLoginCode(phone, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    ToastUtil.show(msg);
                    if (code == 0) {
                        MsgValidateLoginActivity.forward(LoginActivity.this, phone);
                    }
                }
            });
        }
    }


    //手机号密码登录
    private void login() {
        mBtnLogin.requestFocus();
        if (!isAgree) {
            ToastUtil.show("请先勾选同意用户协议!");
            return;
        }


        String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.login_input_phone));
            mEditPhone.requestFocus();
            return;
        }
        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.login_phone_error));
            mEditPhone.requestFocus();
            return;
        }
        String pwd = mEditPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            mEditPwd.setError(WordUtil.getString(R.string.login_input_pwd));
            mEditPwd.requestFocus();
            return;
        }
        mLoginType = "phone";

        showLoadCancelable(false, "登录中");

        HttpUtil.login(phoneNum, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }

            @Override
            public void onError() {
                dismissLoad();
            }
        });
    }

    //登录即代表同意服务和隐私条款
    private void forwardTip() {
        WebViewActivity.forward(this, HtmlConfig.WEB_LINK_PRIVACY_PROTOCOL);
    }

    //登录成功！
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            JSONObject obj = JSON.parseObject(info[0]);
            mRegistered = obj.getIntValue("isreg") == 1;
            String uid = obj.getString("id");
            String token = obj.getString("token");
            String wxUid = obj.getString("wechat_uid");
            if (!mRegistered) {
                dismissLoad();
                ToastUtil.show("暂未注册,请先绑定手机号");
                BindPhoneActivity.forward(this, wxUid);
                finish();
            } else {
                AppConfig.getInstance().setLoginInfo(uid, token, true);
                updateUserInfo(uid);
            }
        } else {
            dismissLoad();
            ToastUtil.show(msg);
        }
    }

    /**
     * 获取用户信息
     */
    private void updateUserInfo(String uid) {
        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                dismissLoad();
                if (bean != null) {
                    MobclickAgent.onProfileSignIn(mLoginType, uid);
                    ConfigBean config = AppConfig.getInstance().getConfig();
                    if (config == null) return;
                    String isNeedAuthWx = StringUtil.checkNullStr(config.getIsNeedAuthWx());
                    if (isNeedAuthWx.equals("1") && !"1".equals(bean.getIsWxAuth())) {
                        wxAuth();
                    } else {
                        if (!mRegistered) {
                            RecommendActivity.forward(LoginActivity.this);
                        } else {
                            MainActivity.forward(LoginActivity.this);
                            overridePendingTransition(R.anim.anim_fade_in, 0);
                            finish();
                        }
                    }
                }
            }
        });

    }

    /**
     * 三方登录
     */
    private void loginBuyThird(Map<String, String> map) {
        HttpUtil.loginByWx(map, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }

            @Override
            public void onError() {
                dismissLoad();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegSuccessEvent(RegSuccessEvent e) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegSuccessEvent(PhoneLoginSuccessEvent e) {
        finish();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        phone = getIntent().getStringExtra("phone");
        mEditPhone = findViewById(R.id.edit_phone);
        etPhone2 = findViewById(R.id.etPhone2);
        mEditPwd = findViewById(R.id.edit_pwd);
        ivAgree = findViewById(R.id.ivAgree);
        msgLoginLayout = findViewById(R.id.msgLoginLayout);
        phoneLoginLayout = findViewById(R.id.phoneLoginLayout);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        mBtnLogin = findViewById(R.id.btn_account_login);
        btnPhoneShort = findViewById(R.id.btnPhoneShort);

        changeUI(StrUtil.isEmpty(phone) ? 0 : 1);

        if (!StrUtil.isEmpty(phone)) {
            String temp = StrUtil.replace(phone, 3, phone.length() - 2, '*');
            btnPhoneShort.setText(String.format("%s 一键登录", temp));
            phoneInputLayout.setVisibility(View.GONE);
            btnPhoneShort.setVisibility(View.VISIBLE);
        } else {
            btnPhoneShort.setVisibility(View.GONE);
            phoneInputLayout.setVisibility(View.VISIBLE);
        }

        initListener();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        HttpUtil.cancel(HttpConst.LOGIN);
        HttpUtil.cancel(HttpConst.GET_QQ_LOGIN_UNION_ID);
        HttpUtil.cancel(HttpConst.LOGIN_BY_THIRD);
        HttpUtil.cancel(HttpConst.GET_BASE_INFO);
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.anim_fade_out);
    }

    @Override
    public void onBackPressed() {
        LogUtils.e(TAG, "onBackPressed: ");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LogUtils.e(TAG, "onKeyDown: KEYCODE_BACK");
            ActivityManager.getInstance().AppExit(getApplicationContext());
            //实现只在冷启动时显示启动页，即点击返回键与点击HOME键退出效果一致
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void wxLogin(View view) {

        if (!isAgree) {
            ToastUtil.show("请先勾选同意用户协议!");
            return;
        }

        UMShareAPI umShareAPI = UMShareAPI.get(this);
        showLoadCancelable(false, "获取微信授权");
        umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                LogUtils.e(TAG, "微信登录开始授权 ");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                ToastUtil.show("微信授权成功");
                showLoadCancelable(false, "登录中...");
                loginBuyThird(map);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                dismissLoad();
                ToastUtil.show("微信授权失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                dismissLoad();
                ToastUtil.show("微信授权取消");
            }
        });
    }

    public void wxAuth() {
        DialogUtil.showSimpleDialog(LoginActivity.this, "暂未绑定微信,请先绑定微信", new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                dialog.dismiss();
                showLoadCancelable(false, "获取授权中...");
                UMShareAPI umShareAPI = UMShareAPI.get(LoginActivity.this);
                umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        LogUtils.e(TAG, "微信登录开始授权 ");
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        dismissLoad();
                        String name = map.get("name");
                        HttpUtil.bindWx(map.get("openid"), map.get("unionid"), name, map.get("iconurl"), new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                ToastUtil.show(msg);
                                if (code == 0) {
                                    AppConfig.getInstance().getUserBean().setWxName(name);
                                    AppConfig.getInstance().getUserBean().setIsWxAuth("1");
                                    MainActivity.forward(LoginActivity.this);
                                    overridePendingTransition(R.anim.anim_fade_in, 0);
                                    finish();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        dismissLoad();
                        ToastUtil.show("微信授权失败");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        dismissLoad();
                        ToastUtil.show("微信授权取消");
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAgree:
                isAgree = !isAgree;
                ivAgree.setBackgroundResource(isAgree ? R.mipmap.icon_agree_enable : R.mipmap.icon_agree_disable);
                break;
        }
    }
}
