package com.wwsl.mdsj.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qiniu.android.utils.StringUtils;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.me.ChargeActivity;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.L;

import org.jetbrains.annotations.NotNull;

/**
 * Created by cxf on 2018/10/19.
 * h5 dialog
 */


public class H5DialogFragment extends AbsDialogFragment {

    private String strH5;
    private WebView mWebView;
    private String url;
    private ProgressBar mProgressBar;
    private TextView titleView;
    private ImageView btnBack;

    public void setH5(String strH5) {
        this.strH5 = strH5;
    }

    public void setUrl(String str) {
        url = checkUrlToken(str);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_h5;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(AppContext.sInstance, 450);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mWebView = mRootView.findViewById(R.id.activeWebView);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressbar);
        titleView = mRootView.findViewById(R.id.titleView);
        btnBack = mRootView.findViewById(R.id.btn_back);

        initWebView();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    dismiss();
                }
            }
        });
    }

    private void initWebView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals("callapp:pay")) {
                    //跳至"我的账户"
                    startActivity(new Intent(mContext, ChargeActivity.class));
                    dismiss();
                } else if (url.startsWith("alipay")) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                } else {
                    url = checkUrlToken(url);
                    L.e("H5-------->" + url);
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                titleView.setText(view.getTitle());
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }
        });


        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
    }

    @NotNull
    private String checkUrlToken(String url) {
        if (!url.contains("token")) {
            if (url.contains("?")) {
                url += "&uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
            } else {
                url += "?uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
            }
        }
        return url;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!StringUtils.isNullOrEmpty(strH5)) {
            mWebView.loadDataWithBaseURL(null, strH5, "text/html", "UTF-8", null);
        } else if (!StringUtils.isNullOrEmpty(url)) {
            mWebView.loadUrl(url);
        }
    }

    public interface ActionListener {
        void onItemClick(String type);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
