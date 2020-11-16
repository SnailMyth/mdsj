package com.wwsl.mdsj.activity.me;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frame.fire.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.activity.login.LoginActivity;
import com.wwsl.mdsj.activity.me.user.UserAccountManageActivity;
import com.wwsl.mdsj.adapter.SettingAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.SettingBean;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.DownloadUtil;
import com.wwsl.mdsj.utils.GlideCatchUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.VersionUtil;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.utils.cache.ProxyVideoCacheManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

/**
 * Created by cxf on 2018/9/30.
 */

public class SettingActivity extends BaseActivity implements OnItemClickListener<SettingBean> {

    private RecyclerView mRecyclerView;
    private Handler mHandler;
    private SettingAdapter mAdapter;
    private ConstraintLayout downloadLayout;
    private RingProgressBar loadPb;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void init() {
        setTitle(WordUtil.getString(R.string.setting));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        downloadLayout = findViewById(R.id.downloadLayout);
        loadPb = findViewById(R.id.loadPb);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        initMenu();

//        HttpUtil.getSettingList(new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                List<SettingBean> list = JSON.parseArray(Arrays.toString(info), SettingBean.class);
//                SettingBean bean = new SettingBean();
//                bean.setName(WordUtil.getString(R.string.setting_exit));
//                bean.setLast(true);
//                list.add(bean);
//                mAdapter = new SettingAdapter(SettingActivity.this, list, VersionUtil.getVersion(), getCacheSize());
//                mAdapter.setOnItemClickListener(SettingActivity.this);
//                mRecyclerView.setAdapter(mAdapter);
//            }
//        });
    }

    private void initMenu() {
        List<SettingBean> list = new ArrayList<>();
        list.add(SettingBean.builder().id(22).name("个人信息").href("").last(false).thumb("").build());
        list.add(SettingBean.builder().id(21).name("账号管理").href("").last(false).thumb("").build());
        list.add(SettingBean.builder().id(0).name("关于《毛豆视界》").href(HtmlConfig.WEB_LINK_ABOUT).last(false).thumb("").build());
        list.add(SettingBean.builder().id(0).name("隐私协议").href(HtmlConfig.WEB_LINK_PRIVACY_PROTOCOL).last(false).thumb("").build());
        list.add(SettingBean.builder().id(0).name("用户协议").href(HtmlConfig.WEB_LINK_USER_PROTOCOL).last(false).thumb("").build());
        list.add(SettingBean.builder().id(17).name("意见反馈").href(HtmlConfig.WEB_LINK_REPORT).last(false).thumb("").build());
        list.add(SettingBean.builder().id(18).name("清除缓存").href("").last(false).thumb("").build());
        list.add(SettingBean.builder().id(16).name("检查更新").href("").last(false).thumb("").build());
        list.add(SettingBean.builder().id(0).name(WordUtil.getString(R.string.setting_exit)).href("").last(true).thumb("").build());

        mAdapter = new SettingAdapter(SettingActivity.this, list, VersionUtil.getVersion(), getCacheSize());
        mAdapter.setOnItemClickListener(SettingActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected boolean isStatusBarWhite() {
        return false;
    }


    @Override
    public void onItemClick(SettingBean bean, int position) {
        String href = bean.getHref();
        if (TextUtils.isEmpty(href)) {
            if (bean.isLast()) {//退出登录
                logout();
            } else if (bean.getId() == Constants.SETTING_MODIFY_PWD) {//修改密码
                forwardModifyPwd();
            } else if (bean.getId() == Constants.SETTING_UPDATE_ID) {//检查更新
                checkVersion();
            } else if (bean.getId() == Constants.SETTING_CLEAR_CACHE) {//清除缓存
                clearCache(position);
            } else if (bean.getId() == Constants.SETTING_SET_PAY_PWD) {//设置支付密码
                forwardSetPayPwd();
            } else if (bean.getId() == Constants.SETTING_SET_ACCOUNT) {//账户管理
                forwardAccount();
            } else if (bean.getId() == Constants.SETTING_SET_USER_INFO) {//用户信息
                forwardUserInfo();
            }
        } else {
            if (bean.getId() == 17) {//意见反馈要在url上加版本号和设备号
                href += "&version=" + android.os.Build.VERSION.RELEASE + "&model=" + android.os.Build.MODEL;
            }
            WebViewActivity.forward(this, href);
        }
    }

    private void forwardUserInfo() {
        UserInfoEditActivity.forward(this);
    }

    private void forwardAccount() {
        UserAccountManageActivity.forward(this);
    }


    /**
     * 检查更新
     */
    private void checkVersion() {
        AppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (configBean.getMaintainSwitch() == 1) {//开启维护
                        DialogUtil.showSimpleTipDialog(SettingActivity.this, WordUtil.getString(R.string.main_maintain_notice), configBean.getMaintainTips());
                    }
                    if (!VersionUtil.isLatest(configBean.getVersion())) {
                        VersionUtil.showDialog(SettingActivity.this, true, configBean.getVersion(), configBean.getUpdateDes(), (view, object) -> {
                            if ((int) object == 0) {
                                return;
                            }
                            String url = configBean.getUpdateApkUrl();
                            if (!TextUtils.isEmpty(url)) {
                                try {
                                    DownloadUtil downloadUtil = new DownloadUtil();
                                    downloadLayout.setVisibility(View.VISIBLE);
                                    downloadUtil.download(Constants.APP_APK, getFilesDir(), Constants.APK_FILE_NAME, url, new DownloadUtil.Callback() {
                                        @Override
                                        public void onSuccess(File file) {
                                            downloadLayout.setVisibility(View.GONE);
                                            VersionUtil.installNormal(SettingActivity.this, file.getPath());
                                        }

                                        @Override
                                        public void onProgress(int progress) {
                                            loadPb.setProgress(progress);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            ToastUtil.show("下载失败");
                                            downloadLayout.setVisibility(View.GONE);
                                        }
                                    });
                                } catch (Exception e) {
                                    ToastUtil.show(R.string.version_download_url_error);
                                }
                            } else {
                                ToastUtil.show(R.string.version_download_url_error);
                            }
                        });
                    }
                }
            }
        });

    }

    private final static String TAG = "xxxx";

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

    /**
     * 修改密码
     */
    private void forwardModifyPwd() {
        ModifyPwdActivity.forward(this, 0);
    }

    /**
     * 设置支付密码
     */
    private void forwardSetPayPwd() {
        startActivity(new Intent(this, SetPayPwdActivity.class));
    }

    /**
     * 获取缓存
     */
    private String getCacheSize() {
        return GlideCatchUtil.getInstance().getCacheSize();
    }

    /**
     * 清除缓存
     */
    private void clearCache(final int position) {
        final Dialog dialog = DialogUtil.loadingDialog(this, getString(R.string.setting_clear_cache_ing), false);
        dialog.show();
        ProxyVideoCacheManager.clearAllCache(this);
        GlideCatchUtil.getInstance().clearImageAllCache();
        File gifGiftDir = new File(AppConfig.GIF_PATH);
        if (gifGiftDir.exists() && gifGiftDir.length() > 0) {
            gifGiftDir.delete();
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (mAdapter != null) {
                    mAdapter.setCacheString(getCacheSize());
                    mAdapter.notifyItemChanged(position);
                }
                ToastUtil.show(R.string.setting_clear_cache);
            }
        }, 2000);
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        HttpUtil.cancel(HttpConst.GET_SETTING_LIST);
        HttpUtil.cancel(HttpConst.GET_CONFIG);
        super.onDestroy();
    }


    public static void forward(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    public void backClick(View view) {
        finish();
    }

}
