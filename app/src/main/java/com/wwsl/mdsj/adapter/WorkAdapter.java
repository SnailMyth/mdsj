package com.wwsl.mdsj.adapter;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.utils.tiktok.NumUtils;

import java.util.List;

/**
 * @author :
 * @date : 2020/6/17 16:03
 * @description : WorkAdapter
 */
public class WorkAdapter extends BaseQuickAdapter<VideoBean, BaseViewHolder> {

    public WorkAdapter(List<VideoBean> data) {
        super(R.layout.item_work, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, VideoBean item) {

        if (item.getUid().equals(AppConfig.getInstance().getUid())) {
            helper.setVisible(R.id.viewImg, true);
            helper.setVisible(R.id.tv_viewNum, true);
        } else {
            helper.setVisible(R.id.viewImg, false);
            helper.setVisible(R.id.tv_viewNum, false);
        }

        ImgLoader.display(item.getCoverUrl(), R.mipmap.img_default_bg, helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_likecount, NumUtils.numberFilter(item.getLikeNum()));
        helper.setText(R.id.tv_viewNum, NumUtils.numberFilter(item.getViewOkNum()));
        helper.setVisible(R.id.txStatus, "0".equals(item.getStatus()));
    }
}
