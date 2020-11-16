package com.wwsl.mdsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.luck.picture.lib.entity.LocalMedia;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.CommonSuccessActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.pay.PayCallback;
import com.wwsl.mdsj.pay.ali.AliPayBuilder;
import com.wwsl.mdsj.pay.wx.WxPayBuilder;
import com.wwsl.mdsj.upload.PictureUploadCallback;
import com.wwsl.mdsj.upload.PictureUploadQnImpl;
import com.wwsl.mdsj.utils.FileUriHelper;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserApplyPartnerActivity extends BaseActivity {
    private FileUriHelper fileUriHelper;
    private int type = 0;
    private String address;
    private EditText editRealName;
    private EditText editPhone;
    private EditText editIdCard;
    private ImageView ivFront;
    private ImageView ivBack;
    private com.rey.material.widget.EditText etRemark;
    private LocalMedia frontImg;
    private LocalMedia backImg;
    private String frontUrl;
    private String backUrl;

    private ConfigBean mConfigBean;
    private PictureUploadQnImpl nPictureUploadStrategy;

    /**
     * 1=微信
     * 2=支付宝
     * 3=银联
     */
    private int payType = 1;
    private CheckBox box1, box2, box3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_apply_partner;
    }

    @Override
    protected void init() {
        type = getIntent().getIntExtra("type", 0);
        address = getIntent().getStringExtra("address");
        AppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                mConfigBean = bean;
            }
        });
        fileUriHelper = new FileUriHelper(this);
        initView();

        this.openAlbumResultListener = (requestCode, result) -> {
            if (result != null && result.size() > 0) {
                if (requestCode == REQUEST_FRONT) {
                    frontImg = result.get(0);
                    ImgLoader.display(frontImg.getPath(), ivFront);
                } else if (requestCode == REQUEST_BACK) {
                    backImg = result.get(0);
                    ImgLoader.display(backImg.getPath(), ivBack);
                }
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void forward(Context context, int type, String address) {
        Intent intent = new Intent(context, UserApplyPartnerActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        editRealName = findViewById(R.id.editRealName);
        editPhone = findViewById(R.id.editPhone);
        editIdCard = findViewById(R.id.editIdCard);
        ivFront = findViewById(R.id.ivFront);
        ivBack = findViewById(R.id.ivBack);
        etRemark = findViewById(R.id.etRemark);

        //支付方式选择
        box1 = findViewById(R.id.wxCheck);
        box2 = findViewById(R.id.aliCheck);
        box3 = findViewById(R.id.bankCheck);

        findViewById(R.id.wxLayout).setOnClickListener(v -> {
            box1.setChecked(true);
            box2.setChecked(false);
            box3.setChecked(false);
        });
        findViewById(R.id.aliLayout).setOnClickListener(v -> {
            box1.setChecked(false);
            box2.setChecked(true);
            box3.setChecked(false);
        });
        findViewById(R.id.bankLayout).setOnClickListener(v -> {
            ToastUtil.show("暂未开通");
            box3.setChecked(false);
        });

    }

    private void submit() {

        String name = editRealName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入真实姓名", Toast.LENGTH_SHORT).show();
            return;
        }

        String phone = editPhone.getText().toString().trim();
        if (!StringUtil.isInteger(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        String idCard = editIdCard.getText().toString().trim();
        if (TextUtils.isEmpty(idCard)) {
            Toast.makeText(this, "请输入身份证号", Toast.LENGTH_SHORT).show();
            return;
        }


        if (null == frontImg || null == backImg) {
            Toast.makeText(this, "请选择身份证照片", Toast.LENGTH_SHORT).show();
            return;
        }

        String remark = etRemark.getText().toString().trim();

        if (mConfigBean != null) {
            if (nPictureUploadStrategy == null) {
                nPictureUploadStrategy = new PictureUploadQnImpl(mConfigBean);
            }

            List<File> files = new ArrayList<>();

            files.add(new File(fileUriHelper.getFilePathByUri(Uri.parse(frontImg.getPath()))));
            files.add(new File(fileUriHelper.getFilePathByUri(Uri.parse(backImg.getPath()))));
            showLoadCancelable(false, "发布中...");
            nPictureUploadStrategy.upload(files, new PictureUploadCallback() {
                @Override
                public void onSuccess(String url) {
                    String[] urls = url.split(",");
                    if (urls.length >= 2) {
                        frontUrl = urls[0];
                        backUrl = urls[1];

                        if (TextUtils.isEmpty(frontUrl) || TextUtils.isEmpty(backUrl)) {
                            Toast.makeText(mActivity, "上传失败,请稍后再试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (box2.isChecked()) payType = 2;

                        HttpUtil.applyPartner2(name, phone, idCard, frontUrl, backUrl, remark, type, address, payType, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                dismissLoad();
                                if (code == 0) {
                                    ToastUtil.show(msg);
                                    if (payType == 1) {
                                        wxPay(info);
                                    } else if (payType == 2) {
                                        aliPay(info);
                                    }
                                } else {
                                    ToastUtil.show(msg);
                                }
                            }

                            @Override
                            public void onError() {
                                super.onError();
                                dismissLoad();
                            }
                        });
                    }
                }

                @Override
                public void onFailure() {
                    ToastUtil.show("发布失败");
                    dismissLoad();
                }
            });
        }
    }


    public static final int REQUEST_FRONT = 1;
    public static final int REQUEST_BACK = 2;

    public void addFront(View view) {
        openAlbum(1, null, REQUEST_FRONT);
    }

    public void addBack(View view) {
        openAlbum(1, null, REQUEST_BACK);
    }

    public void doApply(View view) {
        submit();
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    private void release() {
        HttpUtil.cancel(HttpConst.APPLY_PARTNER);
        if (nPictureUploadStrategy != null) {
            nPictureUploadStrategy.cancel();
        }
    }

    private void aliPay(String[] infos) {
        if (!AppConfig.isAppExist(Constants.PACKAGE_NAME_ALI)) {
            ToastUtil.show(R.string.coin_ali_not_install);
            return;
        }
        if (infos == null || infos.length < 1) {
            return;
        }
        AliPayBuilder builder = new AliPayBuilder(this);
        try {
            JSONObject obj = new JSONObject(infos[0]);
            builder.identifyPay(obj.optString("pay_package"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPayCallback(new PayCallback() {
            @Override
            public void onSuccess() {
                ToastUtil.show("支付成功");
                finish();
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void wxPay(String[] infos) {
        if (!AppConfig.isAppExist(Constants.PACKAGE_NAME_WX)) {
            ToastUtil.show(R.string.coin_wx_not_install);
            return;
        }
        if (infos == null || infos.length < 1) {
            return;
        }
        WxPayBuilder builder = new WxPayBuilder(this);
        try {
            JSONObject obj = new JSONObject(infos[0]);
            String partnerid = obj.optString("partnerid");
            String prepayid = obj.optString("prepayid");
            String packageStr = obj.optString("package");
            String noncestr = obj.optString("noncestr");
            String timestamp = obj.optString("timestamp");
            String sign = obj.optString("sign");
            builder.payAuth(partnerid, prepayid, packageStr, noncestr, timestamp, sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPayCallback(new PayCallback() {
            @Override
            public void onSuccess() {
                ToastUtil.show("支付成功");
                finish();
            }

            @Override
            public void onFailed() {

            }
        });
    }
}
