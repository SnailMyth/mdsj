package com.wwsl.mdsj.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.FarmerWelfareBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author sushi
 * @description
 * @date 2020/7/20.
 */
public class FarmerWelfareAdapter extends BaseQuickAdapter<FarmerWelfareBean, BaseViewHolder> {
    public FarmerWelfareAdapter(List<FarmerWelfareBean> listData) {
        super(R.layout.item_farmer_welfare, listData);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, FarmerWelfareBean bean) {

        holder.setText(R.id.txTitle, bean.getFrom());
        holder.setText(R.id.total, bean.getTotal());
        holder.setText(R.id.note, bean.getNote());
        holder.setText(R.id.time, bean.getAddtime());
        holder.setText(R.id.txDouding, bean.getMoney());

        Glide.with(getContext()).load(bean.getAvatar())
                .placeholder(R.mipmap.icon_launcher)
                .error(R.mipmap.icon_launcher)
                .into((ImageView) holder.getView(R.id.avatar));
    }
}
