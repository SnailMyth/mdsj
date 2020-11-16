package com.wwsl.mdsj.activity.maodou;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.frame.fire.util.LogUtils;
import com.lxj.xpopup.XPopup;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.maodou.NetMdStallListBean;
import com.wwsl.mdsj.dialog.MdEditDialog;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.upload.PictureUploadCallback;
import com.wwsl.mdsj.upload.PictureUploadQnImpl;
import com.wwsl.mdsj.utils.BitmapUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.FileUriHelper;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.dialog.InputPwdDialog;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MdStallP2PActivity extends BaseActivity {

    private ImageView btnMd;
    private ImageView btnDd;
    private ImageView ivLeft;
    private ImageView ivPackage;
    private TextView tvTitle1;
    private TextView tvName1;
    private ConstraintLayout leftStall1;
    private TextView tvTitle2;
    private TextView tvName2;
    private ConstraintLayout leftStall2;
    private TextView tvTitle3;
    private TextView tvName3;
    private ConstraintLayout leftStall3;
    private TextView tvTitle4;
    private TextView tvName4;
    private ConstraintLayout rightStall1;
    private TextView tvTitle5;
    private TextView tvName5;
    private ConstraintLayout rightStall2;

    private List<TextView> titles;
    private List<TextView> names;
    private List<ConstraintLayout> layouts;
    private FileUriHelper fileUriHelper;
    private OnDialogCallBackListener listener;

    private List<NetMdStallListBean> data;
    private PictureUploadQnImpl mUploadStrategy;
    private final static String TAG = "MdStallP2PActivity";

    @Override
    protected int setLayoutId() {
        return R.layout.activity_md_stall_p2p;
    }

    @Override
    protected void init() {
        data = new ArrayList<>();
        fileUriHelper = new FileUriHelper(this);
        initView();
        initAnim();
        initListener();
        HttpUtil.getTodayPrice();//刷新今日价格
        mUploadStrategy = new PictureUploadQnImpl(AppConfig.getInstance().getConfig());
    }


    private void initListener() {
        this.openAlbumResultListener = (requestCode, result) -> {
            if (result != null && result.size() > 0) {
                mdEditDialog.loadQrImage(result.get(0).getPath());
            }
        };


        listener = (view, object) -> {
            if (object instanceof HashMap) {
                Consignment((HashMap) object);
            } else if (object instanceof NetMdStallListBean) {
                //订购
                orderMd((NetMdStallListBean) object);
            } else {
                int i = (int) object;
                switch (i) {
                    case 0:
                        openAlbum(1, new ArrayList<>(), 1);
                        break;
                    case 1:
                        //保存二维码
                        if (null != view) {
                            ImageView img = (ImageView) view;
                            Bitmap image = ((RoundedDrawable) img.getDrawable()).getSourceBitmap();
                            ToastUtil.show("保存成功,位置:" + BitmapUtil.getInstance().saveBitmap(image));
                        }
                        break;
                }
            }
        };
    }

    private void orderMd(NetMdStallListBean data) {

        new XPopup.Builder(MdStallP2PActivity.this)
                .hasShadowBg(true)
                .customAnimator(new DialogUtil.DialogAnimator())
                .asCustom(new InputPwdDialog(MdStallP2PActivity.this, "", (view, object) -> {
                    String pwd = (String) object;
                    showLoadCancelable(false, "订购中...");
                    HttpUtil.orderMd(data.getId(), pwd, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            ToastUtil.show(msg);
                            dismissLoad();
                            if (code == 200) {
                                LogUtils.e(TAG, "onSuccess: 订购成功");
                                loadData();
                            }
                        }

                        @Override
                        public void onError() {
                            dismissLoad();
                        }
                    });
                }))
                .show();
    }

    /**
     * 寄售
     *
     * @param data
     */
    private void Consignment(HashMap<String, String> data) {
        String num = data.get("num");
        String price = data.get("price");
        String qrUrl = data.get("qrUrl");
        List<File> files = new ArrayList<>();

        String realUrl = fileUriHelper.getFilePathByUri(Uri.parse(qrUrl));
        files.add(new File(realUrl));

        new XPopup.Builder(MdStallP2PActivity.this)
                .hasShadowBg(true)
                .customAnimator(new DialogUtil.DialogAnimator())
                .asCustom(new InputPwdDialog(MdStallP2PActivity.this, "", new OnDialogCallBackListener() {
                    @Override
                    public void onDialogViewClick(View view, Object object) {
                        String pwd = (String) object;
                        showLoadCancelable(false, "寄售中...");
                        mUploadStrategy.upload(files, new PictureUploadCallback() {
                            @Override
                            public void onSuccess(String url) {
                                HttpUtil.saleMd(num, price, url, pwd, new HttpCallback() {
                                    @Override
                                    public void onSuccess(int code, String msg, String[] info) {
                                        ToastUtil.show(msg);
                                        if (code == 200) {
                                            LogUtils.e(TAG, "onSuccess: 寄售成功");
                                            loadData();
                                        }
                                        dismissLoad();
                                    }

                                    @Override
                                    public void onError() {
                                        dismissLoad();
                                    }
                                });
                            }

                            @Override
                            public void onFailure() {
                                dismissLoad();
                                ToastUtil.show("图片上传失败");
                            }
                        });
                    }
                }))
                .show();
    }

    private void loadData() {
        HttpUtil.getStallList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 200) {
                    runOnUiThread(() -> {
                        for (int i = 0; i < 5; i++) {
                            layouts.get(i).clearAnimation();
                            layouts.get(i).setVisibility(View.INVISIBLE);
                        }
                        List<NetMdStallListBean> beans = JSON.parseArray(Arrays.toString(info), NetMdStallListBean.class);
                        if (beans != null) {
                            data.clear();
                            data.addAll(beans);
                            for (int i = 0; i < beans.size(); i++) {
                                NetMdStallListBean bean = beans.get(i);
                                titles.get(i).setText(String.format("%s个毛豆/%sR", bean.getNumber(), bean.getPrice()));
                                names.get(i).setText(String.format("%s的地摊", bean.getNickName()));
                                layouts.get(i).setVisibility(View.VISIBLE);
                                layouts.get(i).setAnimation(smallAnimationSet);
                            }
                            if (beans.size() > 0) {
                                scaleAnim.start();
                            }
                        }
                    });

                } else {
                    if (code == 201) {
                        ToastUtil.show("暂时没有人摆摊");
                        for (int i = 0; i < 5; i++) {
                            layouts.get(i).clearAnimation();
                            layouts.get(i).setVisibility(View.INVISIBLE);
                        }
                        data.clear();
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            }
        });
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, MdStallP2PActivity.class);
        context.startActivity(intent);
    }

    MdEditDialog mdEditDialog;

    public void clickMd(View view) {

        if (!AppConfig.getInstance().isIdentifyIdCard()) {
            DialogUtil.showSimpleDialog(this, "您还没有实名认证不可卖毛豆", "请去认证:我的->个人空间->设置->账号管理->身份认证", true, new DialogUtil.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    dialog.dismiss();
                }
            });
        } else {
            if (mdEditDialog == null) {
                mdEditDialog = (MdEditDialog) new XPopup.Builder(this)
                        .hasShadowBg(false)
                        .customAnimator(new DialogUtil.DialogAnimator())
                        .asCustom(new MdEditDialog(this, listener, MdEditDialog.TYPE_SALE, null));
            }
            mdEditDialog.show();
        }
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        btnMd = findViewById(R.id.btnMd);
        btnDd = findViewById(R.id.btnDd);
        ivLeft = findViewById(R.id.ivLeft);
        ivPackage = findViewById(R.id.ivPackage);
        tvTitle1 = findViewById(R.id.tvTitle1);
        tvName1 = findViewById(R.id.tvName1);
        leftStall1 = findViewById(R.id.leftStall1);
        tvTitle2 = findViewById(R.id.tvTitle2);
        tvName2 = findViewById(R.id.tvName2);
        leftStall2 = findViewById(R.id.leftStall2);
        tvTitle3 = findViewById(R.id.tvTitle3);
        tvName3 = findViewById(R.id.tvName3);
        leftStall3 = findViewById(R.id.leftStall3);
        tvTitle4 = findViewById(R.id.tvTitle4);
        tvName4 = findViewById(R.id.tvName4);
        rightStall1 = findViewById(R.id.rightStall1);
        tvTitle5 = findViewById(R.id.tvTitle5);
        tvName5 = findViewById(R.id.tvName5);
        rightStall2 = findViewById(R.id.rightStall2);
        titles = new ArrayList<>();
        names = new ArrayList<>();
        layouts = new ArrayList<>();
        titles.add(tvTitle1);
        titles.add(tvTitle2);
        titles.add(tvTitle3);
        titles.add(tvTitle4);
        titles.add(tvTitle5);

        names.add(tvName1);
        names.add(tvName2);
        names.add(tvName3);
        names.add(tvName4);
        names.add(tvName5);

        layouts.add(leftStall1);
        layouts.add(leftStall2);
        layouts.add(leftStall3);
        layouts.add(rightStall1);
        layouts.add(rightStall2);
    }

    private TranslateAnimation shakeAnim;

    private ScaleAnimation scaleAnim;
    private AnimationSet smallAnimationSet;

    public void initAnim() {
        shakeAnim = new TranslateAnimation(0, 0, 0, 20);
        scaleAnim = new ScaleAnimation(0, 1, 0, 1);
        scaleAnim.setDuration(500);

        shakeAnim.setDuration(2000);           //设置动画持续时间
        shakeAnim.setRepeatCount(Animation.INFINITE);         //设置重复次数
        shakeAnim.setRepeatMode(Animation.REVERSE);    //反方向执行

        smallAnimationSet = new AnimationSet(false);
        smallAnimationSet.addAnimation(scaleAnim);
        smallAnimationSet.addAnimation(shakeAnim);
    }


    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    private void release() {
        scaleAnim.cancel();
        shakeAnim.cancel();
        scaleAnim = null;
        shakeAnim = null;
        if (mUploadStrategy != null) {
            mUploadStrategy.cancel();
        }
    }

    public void goDetail(View view) {
        switch (view.getId()) {
            case R.id.leftStall1:
                goNext(0);
                break;
            case R.id.leftStall2:
                goNext(1);
                break;
            case R.id.leftStall3:
                goNext(2);
                break;
            case R.id.rightStall1:
                goNext(3);
                break;
            case R.id.rightStall2:
                goNext(4);
                break;
        }
    }

    public void goNext(int i) {
        if (i < 0 || i > data.size()) return;
        new XPopup.Builder(this)
                .hasShadowBg(false)
                .customAnimator(new DialogUtil.DialogAnimator())
                .asCustom(new MdEditDialog(this, listener, MdEditDialog.TYPE_BUY, data.get(i)))
                .show();
    }

    public void goOrder(View view) {
        P2pOrderActivity.forward(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public void refresh(View view) {
        loadData();
    }
}
