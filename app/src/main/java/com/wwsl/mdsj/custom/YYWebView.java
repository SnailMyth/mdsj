package com.wwsl.mdsj.custom;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.permissionx.guolindev.PermissionX;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.utils.ToastUtil;

/**
 * Created by Administrator on 2016/11/18.
 */
@SuppressLint("SetJavaScriptEnabled")
public class YYWebView extends WebView {
    private Context context;
    private String title;
    private ValueCallback<Uri> mUploadMessage;
    private OnProgressListener progressListener;

    public YYWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && YYWebView.this.canGoBack()) {
                        YYWebView.this.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
        this.setWebChromeClient(new YYWebChromeClient());
        this.setWebViewClient(new YYWebViewClient());
        WebSettings ws = this.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setAllowFileAccess(true);
        ws.setDomStorageEnabled(true);
        ws.setBlockNetworkImage(false);
        ws.setBlockNetworkLoads(false);
        // 自适应屏幕
        ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        ws.setLoadWithOverviewMode(true);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public ValueCallback<Uri> getMUploadMessage() {
        return this.mUploadMessage;
    }

    /**
     * 支持缩放
     *
     * @param supportZoom         是否支持缩放
     * @param displayZoomControls 是否显示缩放按钮
     */
    public void setSupportZoom(boolean supportZoom, boolean displayZoomControls) {
        WebSettings ws = this.getSettings();
        // 设置可以支持缩放
        ws.setSupportZoom(supportZoom);
        ws.setBuiltInZoomControls(supportZoom);
        // 扩大比例的缩放
        ws.setUseWideViewPort(true);
        // 是否显示缩放按钮
        ws.setDisplayZoomControls(displayZoomControls);
    }

    public void setOnProgressListener(OnProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    private class YYWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//     YYWebView.this.title = title;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (progressListener != null) {
                progressListener.onProgressChanged(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    class YYWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 获取上下文, H5PayDemoActivity为当前页面
//            final Activity context = H5PayDemoActivity.this;

            // ------  对alipays:相关的scheme处理 -------
            if (url.startsWith("alipays:") || url.startsWith("alipay")) {
                try {
                    context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                } catch (Exception e) {
                    new AlertDialog.Builder(context)
                            .setMessage("未检测到支付宝客户端，请安装后重试。")
                            .setPositiveButton("立即安装", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri alipayUrl = Uri.parse("https://d.alipay.com");
                                    context.startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
                                }
                            }).setNegativeButton("取消", null).show();
                }
                return true;
            }
            // ------- 处理结束 -------

            if (url.contains("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppContext.sInstance.startActivity(intent);
            } else {
                view.loadUrl(url);
            }

            return true;
        }
    }

    public interface OnProgressListener {
        void onProgressChanged(int newProgress);
    }

}