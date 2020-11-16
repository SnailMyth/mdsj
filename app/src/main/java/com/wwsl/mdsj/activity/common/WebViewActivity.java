package com.wwsl.mdsj.activity.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;

import com.frame.fire.util.LogUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.me.QRShareActivity;
import com.wwsl.mdsj.activity.me.user.MyBalanceActivity;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.pay.PayCallback;
import com.wwsl.mdsj.pay.ali.AliPayBuilder;
import com.wwsl.mdsj.pay.wx.WxPayBuilder;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.wxapi.PayCallbackBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by cxf on 2018/9/25.
 */

public class WebViewActivity extends AbsActivity {

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private final int CHOOSE = 100;//Android 5.0以下的
    private final int CHOOSE_ANDROID_5 = 200;//Android 5.0以上的
    private ValueCallback<Uri> mValueCallback;
    private ValueCallback<Uri[]> mValueCallback2;

    /**
     * 99 家族推广购买产品
     */
    private int type;
    private final String xyShear = "mdsjsc://share_recommend";
    private final String xyWallet = "callapp:pay";
    private final String xyWxPay = "alipay";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void main() {
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.status_color));//#1E222D
        process();
        EventBus.getDefault().register(this);
        type = getIntent().getIntExtra("type", 0);
        String url = getIntent().getStringExtra(Constants.URL);
        LogUtils.e("firstUrl===" + url);

        findViewById(R.id.btn_close).setOnClickListener(view -> finish());
        mProgressBar = findViewById(R.id.progressbar);

        mWebView = findViewById(R.id.webView);
        mWebView.setBackgroundColor(Color.parseColor("#151922"));
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                String mUrl = request.getUrl().toString();
                LogUtils.e(mUrl);

                if (mUrl.equals(xyShear)) {
                    UserBean userBean = AppConfig.getInstance().getUserBean();
                    QRShareActivity.forward(mActivity, userBean.getAvatar(), userBean.getUsername(), userBean.getTgCode());
                } else if (WebUrlHelper.checkUrl(request.getUrl())) {
                    String host = request.getUrl().getHost();
                    if ("pay".equals(host)) {
                        payGoods(request.getUrl());
                    }
                } else if (mUrl.equals(xyWallet)) {
                    startActivity(new Intent(mContext, MyBalanceActivity.class));
                    finish();
                } else if (mUrl.startsWith(Constants.COPY_PREFIX)) {
                    String content = mUrl.substring(Constants.COPY_PREFIX.length());
                    if (!TextUtils.isEmpty(content)) {
                        copy(content);
                    }
                } else if (mUrl.startsWith(xyWxPay)) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(mUrl);
                    intent.setData(content_url);
                    startActivity(intent);
                } else {
                    if (!mUrl.contains("token")) {
                        if (mUrl.contains("?")) {
                            mUrl += "&uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
                        } else {
                            mUrl += "?uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
                        }
                    }
                    webView.loadUrl(mUrl);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(view.getTitle());
            }
        });
        mWebView.setWebChromeClient(new myWebChromeClient());

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.loadUrl(url);
    }

    public class myWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView webView, int i) {
            if (i == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                mProgressBar.setProgress(i);
            }
        }

        /*// For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            openImageChooserActivity(valueCallback);
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            openFileChooser(valueCallback);
        }*/

        //For Android  >= 4.1
        //腾讯X5内核不用去考虑那些版本兼容,只要重写openFileChooser
        @Override
        public void openFileChooser(ValueCallback<Uri> valueCallback, String s, String s1) {
//            super.openFileChooser(valueCallback, s, s1);
//            openFileChooser(valueCallback, s);

            mValueCallback = valueCallback;
            openImageChooserActivity();
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            mValueCallback2 = valueCallback;
            Intent intent = fileChooserParams.createIntent();
            startActivityForResult(intent, CHOOSE_ANDROID_5);
            return true;
        }

    }

    private void payGoods(Uri url) {
        String uType = url.getQueryParameter("type");
        String order = url.getQueryParameter("order_sn");
        String payType = url.getQueryParameter("pay_type");
        if ("wxpay".equals(payType)) {
            WxPayBuilder builder = new WxPayBuilder(this);
            builder.payGoods(order, uType, payType);
            //wx回调由handleWXPay的EventBus发出
        } else if ("alipay".equals(payType)) {
            AliPayBuilder builder = new AliPayBuilder(this);
            builder.payGoods(order, uType, payType);
            builder.setPayCallback(new PayCallback() {
                @Override
                public void onSuccess() {
                    ToastUtil.show("支付成功");
                    mWebView.clearHistory();
                    if (type == 99) {
                        String tgUrl = AppConfig.getInstance().getTgUrl();
                        if (Build.VERSION.SDK_INT < 18) {
                            mWebView.loadUrl("javascript:getresponse('" + tgUrl + "')");
                        } else {
                            mWebView.evaluateJavascript("javascript:getresponse('" + tgUrl + "')", s -> {
                            });
                        }
                    } else {
                        mWebView.loadUrl(AppConfig.getInstance().getMarketUrl());
                    }
                }

                @Override
                public void onFailed() {
                }
            });
        }
    }

    private void openImageChooserActivity() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        mActivity.startActivityForResult(Intent.createChooser(intent, WordUtil.getString(R.string.choose_flie)), CHOOSE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent == null) return;
        try {
            if (requestCode == CHOOSE) {
                //5.0以下选择图片后的回调
                processResult(resultCode, intent);
            } else if (requestCode == CHOOSE_ANDROID_5) {
                //5.0以上选择图片后的回调
                processResultAndroid5(resultCode, intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processResult(int resultCode, Intent intent) {
        if (mValueCallback == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            Uri result = intent.getData();
            mValueCallback.onReceiveValue(result);
        } else {
            mValueCallback.onReceiveValue(null);
        }
        mValueCallback = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void processResultAndroid5(int resultCode, Intent intent) {
        if (mValueCallback2 == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            mValueCallback2.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
        } else {
            mValueCallback2.onReceiveValue(null);
        }
        mValueCallback2 = null;
    }

    protected boolean canGoBack() {
        return mWebView != null && mWebView.canGoBack();
    }

    @Override
    public void onBackPressed() {
        if (isNeedExitActivity()) {
            finish();
        } else {
            if (canGoBack()) {
                mWebView.goBack();
            } else {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private boolean isNeedExitActivity() {
        if (mWebView != null) {
            String url = mWebView.getUrl();
            if (!TextUtils.isEmpty(url)) {
                return url.contains("g=Appapi&m=Auth&a=success")//身份认证成功页面
                        || url.contains("g=Appapi&m=Family&a=home");//家族申请提交成功页面

            }
        }
        return false;
    }

    public static void forward(Context context, String url) {
        if (url.contains("?")) {
            url += "&uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
        } else {
            url += "?uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
        }

        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }

    public static void forward2(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }

    public static void forward3(Context context, String url, int type) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public static void forwardForResult(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        ((Activity) context).startActivityForResult(intent, 0);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 复制到剪贴板
     */
    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(getString(R.string.copy_success));
    }

    /**
     * 微信支付回调
     */
    @Subscribe
    public void chargeBack(PayCallbackBean bean) {
        if (null != bean) {
            LogUtils.e("chargeBack(Web)===" + bean.errCode);
            switch (bean.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    ToastUtil.show("支付成功");
                    if (mWebView != null) {
                        mWebView.clearHistory();
                        if (type == 99) {
                            String tgUrl = AppConfig.getInstance().getTgUrl();
                            if (Build.VERSION.SDK_INT < 18) {
                                mWebView.loadUrl("javascript:getresponse('" + tgUrl + "')");
                            } else {
                                mWebView.evaluateJavascript("javascript:getresponse('" + tgUrl + "')", s -> {
                                });
                            }
                        } else {
                            mWebView.loadUrl(AppConfig.getInstance().getMarketUrl());
                        }
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    ToastUtil.show("用户取消");
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                default:
                    ToastUtil.show("支付失败");
                    break;
            }
        }
    }

}
