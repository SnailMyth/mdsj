package com.wwsl.mdsj.activity.me;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.lxj.xpopup.XPopup;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.CommonSuccessActivity;
import com.wwsl.mdsj.activity.MainActivity;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.activity.live.LiveAnchorActivity;
import com.wwsl.mdsj.activity.me.user.UserIdentifyActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.CircleImageView;
import com.wwsl.mdsj.views.dialog.InputPwdDialog;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

public class UserVipActivity extends BaseActivity implements View.OnClickListener {


    private TextView btnOpen;
    private TextView btnBuyVip;
    private TextView btnBuyVoice;
    private TextView temp1;
    private TextView temp2;
    private TextView tvName;
    private ImageView ivVip;
    private ImageView ivVoice;
    private ImageView ivAgree;
    private SegmentTabLayout tabLayout;
    private CircleImageView ivAvatar;
    private LinearLayout agreeLayout;
    private ConstraintLayout vipLayout;
    private ConstraintLayout voiceLayout;
    private boolean isVip;
    private int selectedIndex = 0;
    private boolean isAgree = false;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_vip;
    }

    @Override
    protected void init() {
        btnOpen = findViewById(R.id.btnOpen);
        tvName = findViewById(R.id.tvName);
        ivVip = findViewById(R.id.ivVip);
        ivVoice = findViewById(R.id.ivVoice);
        ivAvatar = findViewById(R.id.ivAvatar);
        tabLayout = findViewById(R.id.tablayout);
        ivAgree = findViewById(R.id.ivAgree);
        agreeLayout = findViewById(R.id.agreeLayout);
        vipLayout = findViewById(R.id.vipLayout);
        voiceLayout = findViewById(R.id.voiceLayout);

        btnBuyVip = findViewById(R.id.btnBuyVip);
        btnBuyVoice = findViewById(R.id.btnBuyVoice);
        temp1 = findViewById(R.id.temp_tv_10);
        temp2 = findViewById(R.id.temp_tv_11);
        btnOpen.setOnClickListener(this);
        btnBuyVip.setOnClickListener(this);
        btnBuyVoice.setOnClickListener(this);
        ivAgree.setOnClickListener(this);
        tvName.setText(AppConfig.getInstance().getUserBean().getUsername());
        ImgLoader.displayAvatar(AppConfig.getInstance().getUserBean().getAvatar(), ivAvatar);
        String[] mTitles = {"会员", "金话筒"};
        tabLayout.setTabData(mTitles);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (selectedIndex != tabLayout.getCurrentTab()) {
                    selectedIndex = tabLayout.getCurrentTab();
                    changeMode();
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void changeMode() {
        if (selectedIndex == 0) {
            vipLayout.setVisibility(View.VISIBLE);
            voiceLayout.setVisibility(View.GONE);
            isVip = AppConfig.getInstance().getUserBean().getIsVip() > 0;
            agreeLayout.setVisibility(isVip ? View.GONE : View.VISIBLE);
        } else {
            boolean isAuth = AppConfig.getInstance().getUserBean().getAuth() == 1;
            voiceLayout.setVisibility(View.VISIBLE);
            vipLayout.setVisibility(View.GONE);
            agreeLayout.setVisibility(isAuth ? View.GONE : View.VISIBLE);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        isVip = AppConfig.getInstance().getUserBean().getIsVip() > 0;
        int res = isVip ? R.mipmap.icon_vip_enable : R.mipmap.icon_vip_disable;
        boolean isAuth = AppConfig.getInstance().getUserBean().getAuth() == 1;
        int resVoice = isAuth ? R.mipmap.icon_voice_enable : R.mipmap.icon_voice_disable;

        ivVip.setBackgroundResource(res);
        ivVoice.setBackgroundResource(resVoice);

        btnBuyVip.setVisibility(isVip ? View.GONE : View.VISIBLE);
        btnBuyVoice.setVisibility(isAuth ? View.GONE : View.VISIBLE);
        changeMode();
    }

    public void backClick(View view) {
        finish();
    }

    public void showDes(View view) {
        WebViewActivity.forward(this, HtmlConfig.WEB_LINK_BUY_VIP);
    }

    public void showPwdDialog() {
        if (!isAgree) {
            ToastUtil.show("请先勾选同意开通协议!");
            return;
        }


        new XPopup.Builder(this)
                .hasShadowBg(true)
                .customAnimator(new DialogUtil.DialogAnimator())
                .asCustom(new InputPwdDialog(this, String.format("将要扣除%s豆丁", 100), new OnDialogCallBackListener() {
                    @Override
                    public void onDialogViewClick(View view, Object object) {
                        String pwd = (String) object;
                        showLoadCancelable(false, "开通中...");
                        HttpUtil.openVip(pwd, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                dismissLoad();
                                if (code == 0) {
                                    HttpUtil.getBaseInfo(null);
                                    CommonSuccessActivity.forward(UserVipActivity.this, "开通Vip", "开通成功", Constants.SUCCESS_PAGE_TYPE_VIP);
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
                }))
                .show();
    }

    public void showProtocol(View view) {
        WebViewActivity.forward(this, HtmlConfig.WEB_LINK_VIP_RULE);
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, UserVipActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOpen:
            case R.id.btnBuyVip:
                showPwdDialog();
                break;
            case R.id.btnBuyVoice:
                openLivePayDialog();
                break;
            case R.id.ivAgree:
                isAgree = !isAgree;
                ivAgree.setBackgroundResource(isAgree ? R.mipmap.icon_agree_enable : R.mipmap.icon_agree_disable);
                break;
        }
    }

    private void openLivePayDialog() {
        if (AppConfig.getInstance().getUserBean().getIsIdIdentify() != 1) {
            ToastUtil.show("暂未身份认证,请先进行身份认证");
            UserIdentifyActivity.forward(UserVipActivity.this);
        } else {

            if (!isAgree) {
                ToastUtil.show("请先勾选同意开通协议!");
                return;
            }

            DialogUtil.showSimpleDialog(UserVipActivity.this, "直播", "开启直播需要支付1000豆丁", false, new DialogUtil.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    dialog.dismiss();
                    new XPopup.Builder(UserVipActivity.this)
                            .hasShadowBg(true)
                            .customAnimator(new DialogUtil.DialogAnimator())
                            .asCustom(new InputPwdDialog(UserVipActivity.this, "将要扣除1000豆丁", new OnDialogCallBackListener() {
                                @Override
                                public void onDialogViewClick(View view, Object object) {
                                    String pwd = (String) object;
                                    showLoadCancelable(false, "开通中...");
                                    HttpUtil.livePay(pwd, new HttpCallback() {
                                        @Override
                                        public void onSuccess(int code, String msg, String[] info) {
                                            dismissLoad();
                                            if (code == 0) {
                                                CommonSuccessActivity.forward(UserVipActivity.this, "开通金话筒", "开通成功", Constants.SUCCESS_PAGE_TYPE_USER_AUTH);
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
                            }))
                            .show();
                }
            });
        }
    }
}
