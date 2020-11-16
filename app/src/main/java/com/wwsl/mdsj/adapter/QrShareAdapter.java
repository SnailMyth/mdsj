package com.wwsl.mdsj.adapter;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.QrSheraBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.utils.BitmapUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class QrShareAdapter extends BaseQuickAdapter<QrSheraBean, BaseViewHolder> {

    private Bitmap mBitmap;
    private String path = "";

    public QrShareAdapter() {
        super(R.layout.item_qr_code_card);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, QrSheraBean bean) {
        TextView tgCode = holder.getView(R.id.tgCode);
        TextView name = holder.getView(R.id.name);
        ImageView imageView = holder.getView(R.id.iv_qrCode);

        tgCode.setText(String.format("邀请码:%s", bean.getCode()));
        name.setText(String.format("我是: %s", bean.getUserName()));

        if (mBitmap == null) {
            mBitmap = CodeUtils.createImage(bean.getContent(), 280, 280, null);
        }
        imageView.setImageBitmap(mBitmap);

        ImgLoader.display(AppConfig.getInstance().getUserBean().getAvatar(), R.mipmap.icon_avatar_placeholder, holder.getView(R.id.avatar));
        ImgLoader.display(bean.getThumb(), R.mipmap.img_load_fail, holder.getView(R.id.iv_bg));

        imageView.setOnLongClickListener(view -> {
            if (mBitmap == null) return true;
            if (StringUtil.isEmpty(path)) {
                try {
                    path = BitmapUtil.getInstance().saveBitmap(mBitmap);
                    if (new File(path).exists()) ToastUtil.show("位置: " + path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ToastUtil.show("二维码在 : " + path);
            }
            return true;
        });
    }

}
