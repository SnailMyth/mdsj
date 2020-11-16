package com.wwsl.mdsj.activity.me;

import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.frame.fire.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.activity.login.LoginActivity;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ButtonUtils;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.util.Map;

/**
 * Created by cxf on 2018/10/7.
 * 重置密码
 */

public class ModifyPwdActivity extends AbsActivity implements View.OnClickListener {

    private EditText mEditOld;
    private EditText mEditNew;
    private EditText mEditConfirm;
    private int type = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_pwd;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return false;
    }

    @Override
    protected void main() {
        mEditOld = findViewById(R.id.edit_old);
        mEditNew = findViewById(R.id.edit_new);
        mEditConfirm = findViewById(R.id.edit_confirm);
        type = getIntent().getIntExtra("type", 0);

        if (type == 0) {
            setTitle(WordUtil.getString(R.string.modify_pwd));
            mEditOld.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD | EditorInfo.TYPE_CLASS_TEXT);
            mEditNew.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD | EditorInfo.TYPE_CLASS_TEXT);
            mEditConfirm.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD | EditorInfo.TYPE_CLASS_TEXT);
        } else {
            setTitle(WordUtil.getString(R.string.modify_pay_pwd));
            mEditOld.setInputType(EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD | EditorInfo.TYPE_CLASS_NUMBER);
            mEditNew.setInputType(EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD | EditorInfo.TYPE_CLASS_NUMBER);
            mEditConfirm.setInputType(EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD | EditorInfo.TYPE_CLASS_NUMBER);
            mEditOld.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            mEditNew.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            mEditConfirm.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        }


        findViewById(R.id.btn_confirm).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (ButtonUtils.isFastClick()) return;
        modify();
    }

    private void modify() {
        String pwdOld = mEditOld.getText().toString().trim();

        if (TextUtils.isEmpty(pwdOld)) {
            ToastUtil.show(WordUtil.getString(R.string.modify_pwd_old_1));
            return;
        }

        String pwdNew = mEditNew.getText().toString().trim();

        if (TextUtils.isEmpty(pwdNew)) {
            ToastUtil.show(WordUtil.getString(R.string.modify_pwd_new_1));
            return;
        }

        String pwdConfirm = mEditConfirm.getText().toString().trim();
        if (TextUtils.isEmpty(pwdConfirm)) {
            ToastUtil.show(WordUtil.getString(R.string.modify_pwd_confirm_1));
            return;
        }

        if (!pwdNew.equals(pwdConfirm)) {
            ToastUtil.show(WordUtil.getString(R.string.reg_pwd_error));
            return;
        }

        if (type == 0) {

            if (!StringUtil.checkPasswordRule(pwdNew)) {
                mEditNew.setError(WordUtil.getString(R.string.reg_input_pwd_1_info));
                return;
            }

            showLoadCancelable(false, "修改中...");
            HttpUtil.modifyPwd(pwdOld, pwdNew, pwdConfirm, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    dismissLoad();
                    if (code == 0) {
                        ToastUtil.show("修改成功,请重新登录!");
                        logout();
                    } else {
                        ToastUtil.show(msg);
                    }
                }

                @Override
                public void onError() {
                    dismissLoad();
                }
            });
        } else {
            if (pwdNew.length() != 6) {
                mEditNew.setError(WordUtil.getString(R.string.modify_input_rule_tip));
                return;
            }
            showLoadCancelable(false, "修改中...");
            HttpUtil.setPayPwd(pwdNew, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    dismissLoad();
                    ToastUtil.show(msg);
                    if (code == 0) {
                        finish();
                    }
                }

                @Override
                public void onError() {
                    dismissLoad();
                }
            });
        }


    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConst.MODIFY_PWD);
        super.onDestroy();
    }

    public static void forward(Context context, int type) {
        Intent intent = new Intent(context, ModifyPwdActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    /**
     * 退出登录
     */
    private void logout() {
        AppConfig.getInstance().clearLoginInfo();
        MobclickAgent.onProfileSignOff();
        UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                LogUtils.e(TAG, "onComplete: 删除微信授权成功");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                LogUtils.e(TAG, "onComplete: 删除微信授权成功");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                LogUtils.e(TAG, "onComplete: 删除微信授权失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });

        LoginActivity.forward();
    }

    private final static String TAG = "ModifyPwdActivity";
}
