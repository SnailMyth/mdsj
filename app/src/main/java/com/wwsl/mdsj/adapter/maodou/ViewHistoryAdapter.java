package com.wwsl.mdsj.adapter.maodou;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.maodou.ViewVideoHistoryBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ViewHistoryAdapter extends BaseQuickAdapter<ViewVideoHistoryBean, BaseViewHolder> {

    public ViewHistoryAdapter(@Nullable List<ViewVideoHistoryBean> data) {
        super(R.layout.item_video_view_history, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ViewVideoHistoryBean bean) {
        holder.setText(R.id.timeTitle, bean.getTitle());
        holder.setText(R.id.tvPercent, bean.getPercent());
        holder.setText(R.id.time, bean.getTime());
    }
}
