package com.wwsl.mdsj.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.MallWelfareBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author sushi
 * @description
 * @date 2020/7/20.
 */
public class MallWelfareAdapter extends BaseQuickAdapter<MallWelfareBean.WelfareBean, BaseViewHolder> {
    public MallWelfareAdapter(List<MallWelfareBean.WelfareBean> listData) {
        super(R.layout.item_mall_welfare, listData);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MallWelfareBean.WelfareBean mallWelfareBean) {
        Glide.with(getContext()).load(mallWelfareBean.getGoods_cover())
                .placeholder(R.mipmap.icon_launcher)
                .error(R.mipmap.icon_launcher)
                .into((ImageView) baseViewHolder.getView(R.id.avatar));
        baseViewHolder.setText(R.id.tv_title, mallWelfareBean.getGoods_name())
                .setText(R.id.tv_fans, "粉丝:" + mallWelfareBean.getUser_name())
                .setText(R.id.tv_douNum, mallWelfareBean.getPrice());
    }
}
