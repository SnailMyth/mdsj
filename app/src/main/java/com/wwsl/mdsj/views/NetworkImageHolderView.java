package com.wwsl.mdsj.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.glide.ImgLoader;

public class NetworkImageHolderView implements Holder<String> {
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_banner_item, null);
        imageView = view.findViewById(R.id.iv_banner_img);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        // 图片
        ImgLoader.display(data, imageView);
    }
}