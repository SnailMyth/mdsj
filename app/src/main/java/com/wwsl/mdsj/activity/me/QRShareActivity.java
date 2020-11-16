package com.wwsl.mdsj.activity.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.adapter.QrShareAdapter;
import com.wwsl.mdsj.bean.QrSheraBean;
import com.wwsl.mdsj.bean.ShareBean;
import com.wwsl.mdsj.fragment.ShareDialog;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.share.ShareHelper;
import com.wwsl.mdsj.utils.BitmapUtil;
import com.wwsl.mdsj.utils.FileUtil;
import com.wwsl.mdsj.utils.SnackBarUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * @author:
 * @date: 2020/5/7 10:00
 * @description : xxxxx
 */
public class QRShareActivity extends AbsActivity implements View.OnClickListener {

    private String content;
    private String avatarUrl;
    private String tgCode;
    private String name;

    private LinearLayout tempLayout;
    private QrShareAdapter adapter;

    /*private ImageView ivLeftBack;
    private TextView tgCode;
    private TextView txName;
    private ImageView ivQrCode;
    private Bitmap mBitmap;
    private CircleImageView avatar;
    private ConstraintLayout tempLayout;
    private String path;*/

    public static void forward(Context context, String avatar, String name, String tgCode) {
        Intent intent = new Intent(context, QRShareActivity.class);
        intent.putExtra("avatar", avatar);
        intent.putExtra("name", name);
        intent.putExtra("tgCode", tgCode);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_qr_code_card2;
    }

    @Override
    protected void main() {
        content = AppConfig.getInstance().getQRContent();
        avatarUrl = getIntent().getStringExtra("avatar");
        tgCode = getIntent().getStringExtra("tgCode");
        name = getIntent().getStringExtra("name");

        findViewById(R.id.iv_left_back).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);

        tempLayout = findViewById(R.id.container);

        /*ivLeftBack = findViewById(R.id.iv_left_back);
        ivQrCode = findViewById(R.id.iv_qrCode);
        tgCode = findViewById(R.id.tgCode);
        avatar = findViewById(R.id.avatar);
        txName = findViewById(R.id.name);
        txName.setText(String.format("我是: %s", name));
        ImgLoader.displayAvatar(AppConfig.getInstance().getUserBean().getAvatar(), avatar);
        tgCode.setText(String.format("邀请码:%s", code));
        showQRImage();
        ivLeftBack.setOnClickListener(v -> finish());*/

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(manager);
        //一次划一页
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        adapter = new QrShareAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_back:
                finish();
                break;
            case R.id.iv_share:
                openShareWindow();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dismissLoad();
        getData();
    }

    private void getData() {
        HttpUtil.getQrShareBg(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length != 0) {
                    List<QrSheraBean> list = JSON.parseArray(Arrays.toString(info), QrSheraBean.class);
                    if (list != null) {
                        adapter.getData().clear();
                        adapter.notifyDataSetChanged();
                        for (QrSheraBean bean : list) {
                            bean.setCode(tgCode);
                            bean.setAvatarUrl(avatarUrl);
                            bean.setContent(content);
                            bean.setUserName(name);
                        }
                        adapter.getData().addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.cancel(HttpConst.SHAREBG);
    }

    ShareDialog shareDialog;

    public void openShareWindow() {
        if (shareDialog == null) {
            shareDialog = new ShareDialog(1);
            shareDialog.setListener((view1, object) -> {
                shareDialog.dismiss();
                showLoadCancelable(false, "正在处理中...");
                if (object instanceof ShareBean) {
                    ShareBean shareBean = (ShareBean) object;
                    Bitmap bitmap = captureView(tempLayout);
                    dismissLoad();
                    if (shareBean.getType() == Constants.SAVE_LOCAL) {
                        String str = BitmapUtil.getInstance().saveBitmap(bitmap);
                        FileUtil.saveImage(mActivity, new File(str));
                        if (!StrUtil.isEmpty(str)) {
                            SnackBarUtil.ShortSnackbar(tempLayout, String.format("保存成功:位置[%s]", str), SnackBarUtil.Info).show();
                        }
                    } else {
                        ShareHelper.shareTextWithImg(mActivity, shareBean, bitmap);
                    }
                } else {
                    dismissLoad();
                }
            });
        }
        shareDialog.show(getSupportFragmentManager(), "QRShareActivity");
    }

    public Bitmap captureView(View view) {
        // 根据View的宽高创建一个空的Bitmap
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.RGB_565);
        // 利用该Bitmap创建一个空的Canvas
        Canvas canvas = new Canvas(bitmap);
        // 绘制背景(可选)
        canvas.drawColor(Color.WHITE);
        // 将view的内容绘制到我们指定的Canvas上
        view.draw(canvas);
        return bitmap;
    }

}

