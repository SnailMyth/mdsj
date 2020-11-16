package com.wwsl.mdsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.frame.fire.util.LogUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.me.FindPayPwdActivity;
import com.wwsl.mdsj.activity.me.ModifyPwdActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.util.Map;

import cn.hutool.core.util.StrUtil;

public class UserAccountManageActivity extends BaseActivity {

    private TextView tvPhone;
    private TextView tvIdentify;
    private TextView tvWxId;
    private TextView tvSuperiorTgCode;
    private Switch phoneSwitch;
    private boolean isIdentify;
    private boolean isBindWx;
    private String pid;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_account;
    }

    @Override
    protected void init() {
        tvPhone = findViewById(R.id.tvPhone);
        tvIdentify = findViewById(R.id.tvIdentify);
        tvWxId = findViewById(R.id.tvWxId);
        tvSuperiorTgCode = findViewById(R.id.tvSuperiorTgCode);
        phoneSwitch = findViewById(R.id.phoneSwitch);
        phoneSwitch.setChecked(AppConfig.getInstance().getUserBean().getIsPhonePublic() > 0);
        setListener();
    }

    private void showData() {
        isBindWx = AppConfig.getInstance().isBindWx();
        UserBean userBean = AppConfig.getInstance().getUserBean();
        pid = userBean.getPid();
        tvSuperiorTgCode.setText(pid);
        if (!isBindWx) {
            tvWxId.setText("未绑定微信");
        }
        tvWxId.setText(AppConfig.getInstance().getUserBean().getWxName());
        tvPhone.setText(userBean.getMobile());
        isIdentify = userBean.getIsIdIdentify() > 0;
        tvIdentify.setText(isIdentify ? "已认证" : "未认证");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showData();
    }

    private void setListener() {
        phoneSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppConfig.getInstance().getUserBean().setIsPhonePublic(isChecked ? 1 : 0);
            HttpUtil.updateUserData(HttpConst.USER_INFO_PHONE_PUBLIC, isChecked ? "1" : "0", new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == HttpConst.SUCCESS) {
                        ToastUtil.show(msg);
                    }
                }

                @Override
                public void onError() {
                    super.onError();
                    phoneSwitch.setChecked(!isChecked);
                }
            });
        });
    }

    public void backClick(View view) {
        finish();
    }

    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.layoutLoginPwd:
                ModifyPwdActivity.forward(this, 0);
                break;
            case R.id.layoutSafePwd:
                ModifyPwdActivity.forward(this, 1);
                break;
            case R.id.layoutIdCard:
                UserIdentifyActivity.forward(UserAccountManageActivity.this);
                break;
            case R.id.wxBind:
                gotoBindWx();
                break;
            case R.id.tgLayout:
                showInputTgCode();
                break;
            case R.id.layoutFindSafePwd:
                FindPayPwdActivity.forward(this);
                break;
        }
    }

    private void showInputTgCode() {
        if (!StrUtil.isEmpty(pid) && !"0".equals(pid)) {
            ToastUtil.show("上级id只能设置一次");
            return;
        }

        DialogUtil.showSimpleInputDialog(this, WordUtil.getString(R.string.main_input_invatation_code), (dialog, content) -> {
            if (TextUtils.isEmpty(content)) {
                ToastUtil.show(R.string.main_input_invatation_ID);
                return;
            }
            HttpUtil.setInvitation(content, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        pid = content;
                        tvSuperiorTgCode.setText(pid);
                        ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                        dialog.dismiss();
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            });
        });
    }

    private final static String TAG = "UserAccountManageActivity";

    private void gotoBindWx() {
        if (!isBindWx) {
            UMShareAPI umShareAPI = UMShareAPI.get(this);

            umShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    LogUtils.e(TAG, "微信登录开始授权 ");
                }

                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                    String name = map.get("name");
                    HttpUtil.bindWx(map.get("openid"), map.get("unionid"), name, map.get("iconurl"), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            ToastUtil.show(msg);
                            if (code == 0) {
                                AppConfig.getInstance().getUserBean().setWxName(name);
                                AppConfig.getInstance().getUserBean().setIsWxAuth("1");
                                tvWxId.setText(name);
                                isBindWx = true;
                            }
                        }
                    });
                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                    ToastUtil.show("微信授权失败");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {
                    ToastUtil.show("微信授权取消");
                }
            });
        }
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, UserAccountManageActivity.class);
        context.startActivity(intent);
    }

}
