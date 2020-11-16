package com.wwsl.mdsj.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.shehuan.niv.NiceImageView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.utils.StringUtil;

import java.util.List;

public class GridVideoAdapter extends BaseQuickAdapter<VideoBean, BaseViewHolder> {
    public GridVideoAdapter(@Nullable List<VideoBean> data) {
        super(R.layout.item_gridvideo, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, VideoBean item) {


        helper.setText(R.id.tv_content, item.getTitle());
        helper.setText(R.id.tv_name, item.getUserBean().getUsername());
        helper.setText(R.id.tv_distance, StringUtil.getDistance(item.getDistance()));
        ImgLoader.displayAvatar(item.getUserBean().getAvatar(), helper.getView(R.id.iv_head));
        ImgLoader.display(item.getCoverUrl(), helper.getView(R.id.iv_cover));
    }
}
