package com.wwsl.mdsj.activity.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.MainActivity;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.CodeCutDownTimer;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.PasswordView;

import java.util.Map;

import cn.hutool.core.util.StrUtil;

public class MsgValidateLoginActivity extends AbsActivity {

    private final static String TAG = "MsgValidateLoginActivity";

    private TextView mBtnCode;
    private PasswordView mPass;
    private CodeCutDownTimer timer;
    private String phone;
    private ImageView ivAgree;
    private boolean isAgree = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_msg_validate_login;
    }

    @Override
    protected void main(Bundle savedInstanceState) {
        super.main(savedInstanceState);
        mBtnCode = findViewById(R.id.btnTime);
        mPass = findViewById(R.id.password);
        ivAgree = findViewById(R.id.ivAgree);
        timer = new CodeCutDownTimer(mBtnCode);
        timer.setColor("#F95921", "#F95921");
        phone = getIntent().getStringExtra("phone");
        timer.start();
    }

    public static void forward(Context context, String phone) {
        Intent intent = new Intent(context, MsgValidateLoginActivity.class);
        intent.putExtra("phone", phone);
        context.startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    public void requestCode() {
        if (!StrUtil.isEmpty(phone)) {
            HttpUtil.getQuickLoginCode(phone, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    ToastUtil.show(msg);
                    if (code == 0) {
                        timer.start();
                    }
                }
            });
        }
    }


    public void registerClick(View view) {
        requestCode();
    }


    public void backClick(View view) {
        finish();
    }


    public void loginClick(View view) {
        if (!isAgree) {
            ToastUtil.show("请先勾选同意开通协议!");
            return;
        }

        if (mPass.getPassword().length() != 4) {
            ToastUtil.show("请输入验证码");
        } else {

            HttpUtil.quickLogin(phone, mPass.getPassword(), new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    onLoginSuccess(code, msg, info);
                }
            });
        }
    }


    private boolean mFirstLogin;//是否是第一次登录

    //登录成功！
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("id");
            String token = obj.getString("token");
            mFirstLogin = obj.getIntValue("isreg") == 1;
            AppConfig.getInstance().setLoginInfo(uid, token, true);
            getBaseUserInfo();
            MobclickAgent.onProfileSignIn("phone", uid);
        } else {
            ToastUtil.show(msg);
        }
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                String isNeedAuthWx = AppConfig.getInstance().getConfig().getIsNeedAuthWx();
                if (isNeedAuthWx.equals("1") && !"1".equals(bean.getIsWxAuth())) {
                    wxAuth();
                } else {
                    if (mFirstLogin) {
                        RecommendActivity.forward(mContext);
                    } else {
                        MainActivity.forward(mContext);
                        overridePendingTransition(R.anim.anim_fade_in, 0);
                    }
                    finish();
                }
            }
        });


    }

    public void wxAuth() {
        // TODO: 2020/8/24 避免弹出dialog之前 页面被销毁  可用性待测试
        if (!MsgValidateLoginActivity.this.isFinishing()) {
            DialogUtil.showSimpleDialog(MsgValidateLoginActivity.this, "暂未绑定微信,请先绑定微信", false, new DialogUtil.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    dialog.dismiss();
                    showLoadCancelable(false, "获取授权中...");
                    UMShareAPI umShareAPI = UMShareAPI.get(MsgValidateLoginActivity.this);
                    umShareAPI.getPlatformInfo(MsgValidateLoginActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
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
                                        RecommendActivity.forward(mContext);
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

    public void showProtocol(View view) {
        WebViewActivity.forward(mContext, HtmlConfig.WEB_LINK_USER_PROTOCOL);
    }

    public void agreeProtocal(View view) {
        isAgree = !isAgree;
        ivAgree.setBackgroundResource(isAgree ? R.mipmap.icon_agree_enable : R.mipmap.icon_agree_disable);
    }
}
