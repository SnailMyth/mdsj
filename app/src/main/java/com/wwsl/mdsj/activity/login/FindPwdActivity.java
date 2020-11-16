package com.wwsl.mdsj.activity.login;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.MainActivity;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.event.RegSuccessEvent;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.ValidatePhoneUtil;
import com.wwsl.mdsj.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import cn.hutool.core.util.StrUtil;

/**
 * Created by cxf on 2018/9/25.
 */

public class FindPwdActivity extends AbsActivity {

    private EditText mEditPhone;
    private EditText mEditCode;
    private EditText mEditPwd1;
    private EditText mEditPwd2;
    private TextView mBtnCode;
    private View mBtnRegister;
    private Handler mHandler;
    private static final int TOTAL = 60;
    private int mCount = TOTAL;
    private String mGetCode;
    private String mGetCodeAgain;
    private Dialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_pwd;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.find_pwd_forget));
        mEditPhone = findViewById(R.id.edit_phone);
        mEditCode = findViewById(R.id.edit_code);
        mEditPwd1 = findViewById(R.id.edit_pwd_1);
        mEditPwd2 = findViewById(R.id.edit_pwd_2);
        mBtnCode = findViewById(R.id.btn_code);
        mBtnRegister = findViewById(R.id.btn_register);
        mGetCode = WordUtil.getString(R.string.reg_get_code);
        mGetCodeAgain = WordUtil.getString(R.string.reg_get_code_again);
        mEditPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && s.length() == 11) {
                    mBtnCode.setEnabled(true);
                } else {
                    mBtnCode.setEnabled(false);
                }
                changeEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mEditCode.addTextChangedListener(textWatcher);
        mEditPwd1.addTextChangedListener(textWatcher);
        mEditPwd2.addTextChangedListener(textWatcher);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mCount--;
                if (mCount > 0) {
                    mBtnCode.setText(mGetCodeAgain + "(" + mCount + "s)");
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                } else {
                    mBtnCode.setText(mGetCode);
                    mCount = TOTAL;
                    if (mBtnCode != null) {
                        mBtnCode.setEnabled(true);
                    }
                }
            }
        };

        mDialog = DialogUtil.loadingDialog(mContext, false);
        EventBus.getDefault().register(this);
    }

    private void changeEnable() {
        String phone = mEditPhone.getText().toString();
        String code = mEditCode.getText().toString();
        String pwd1 = mEditPwd1.getText().toString();
        String pwd2 = mEditPwd2.getText().toString();
        mBtnRegister.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(pwd1) && !TextUtils.isEmpty(pwd2));
    }

    public void registerClick(View v) {
        switch (v.getId()) {
            case R.id.btn_code:
                getCode();
                break;
            case R.id.btn_register:
                register();
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
//            mEditPhone.setError(WordUtil.getString(R.string.reg_input_phone));
            ToastUtil.show(WordUtil.getString(R.string.reg_input_phone));
            mEditPhone.requestFocus();
            return;
        }
        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
//            mEditPhone.setError(WordUtil.getString(R.string.login_phone_error));
            ToastUtil.show(WordUtil.getString(R.string.login_phone_error));
            mEditPhone.requestFocus();
            return;
        }
        mEditCode.requestFocus();
        HttpUtil.getFindPwdCode(phoneNum, mGetCodeCallback);
    }

    private HttpCallback mGetCodeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                mBtnCode.setEnabled(false);
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(0);
                }
                if (!TextUtils.isEmpty(msg) && msg.contains("123456")) {
                    ToastUtil.show(msg);
                }
            } else {
                ToastUtil.show(msg);
            }

        }
    };

    /**
     * 注册并登陆
     */
    private void register() {
        final String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
//            mEditPhone.setError(WordUtil.getString(R.string.reg_input_phone));
            ToastUtil.show(WordUtil.getString(R.string.reg_input_phone));
            mEditPhone.requestFocus();
            return;
        }
        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
//            mEditPhone.setError(WordUtil.getString(R.string.login_phone_error));
            ToastUtil.show(WordUtil.getString(R.string.login_phone_error));
            mEditPhone.requestFocus();
            return;
        }
        String code = mEditCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
//            mEditCode.setError(WordUtil.getString(R.string.reg_input_code));
            ToastUtil.show(WordUtil.getString(R.string.reg_input_code));
            mEditCode.requestFocus();
            return;
        }
        final String pwd = mEditPwd1.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
//            mEditPwd1.setError(WordUtil.getString(R.string.reg_input_pwd_1));
            ToastUtil.show(WordUtil.getString(R.string.reg_input_pwd_1));
            mEditPwd1.requestFocus();
            return;
        }
        String pwd2 = mEditPwd2.getText().toString().trim();
        if (TextUtils.isEmpty(pwd2)) {
//            mEditPwd2.setError(WordUtil.getString(R.string.reg_input_pwd_2));
            ToastUtil.show(WordUtil.getString(R.string.reg_input_pwd_2));
            mEditPwd2.requestFocus();
            return;
        }
        if (!pwd.equals(pwd2)) {
//            mEditPwd2.setError(WordUtil.getString(R.string.reg_pwd_error));
            ToastUtil.show(WordUtil.getString(R.string.reg_pwd_error));
            mEditPwd2.requestFocus();
            return;
        }
        if (mDialog != null) {
            mDialog.show();
        }
        HttpUtil.findPwd(phoneNum, pwd, pwd2, code, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    login(phoneNum, pwd);
                } else {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    if (!TextUtils.isEmpty(msg)) {
                        ToastUtil.show(msg);
                    }
                }
            }

            @Override
            public void onError() {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }
        });
    }

    private void login(String phoneNum, String pwd) {
        HttpUtil.login(phoneNum, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String uid = obj.getString("id");
                    String token = obj.getString("token");
                    AppConfig.getInstance().setLoginInfo(uid, token, true);
                    getBaseUserInfo();
                } else {
                    if (!TextUtils.isEmpty(msg)) {
                        ToastUtil.show(msg);
                    }
                }
            }

            @Override
            public void onError() {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }
        });
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {

                if (mDialog != null) {
                    mDialog.dismiss();
                }

                if (bean != null) {
                    String isNeedAuthWx = AppConfig.getInstance().getConfig().getIsNeedAuthWx();
                    if (isNeedAuthWx.equals("1") && !"1".equals(bean.getIsWxAuth())) {
                        wxAuth();
                    } else {
                        MainActivity.forward(mContext);
                        EventBus.getDefault().post(new RegSuccessEvent());
                    }
                }

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegSuccessEvent(RegSuccessEvent e) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        HttpUtil.cancel(HttpConst.GET_FIND_PWD_CODE);
        HttpUtil.cancel(HttpConst.FIND_PWD);
        HttpUtil.cancel(HttpConst.LOGIN);
        HttpUtil.cancel(HttpConst.GET_BASE_INFO);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    private final static String TAG = "FindPwdActivity";

    public void wxAuth() {
        DialogUtil.showSimpleDialog(FindPwdActivity.this, "暂未绑定微信,请先绑定微信", new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                dialog.dismiss();
                showLoadCancelable(false, "获取授权中...");
                UMShareAPI umShareAPI = UMShareAPI.get(FindPwdActivity.this);
                umShareAPI.getPlatformInfo(FindPwdActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
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
                                    MainActivity.forward(mContext);
                                    EventBus.getDefault().post(new RegSuccessEvent());
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
}
