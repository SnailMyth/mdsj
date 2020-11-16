package com.wwsl.mdsj.adapter.maodou;


import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.maodou.NetTaskProcessBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.video.custom.NumberProgressBar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TaskProcessAdapter extends BaseQuickAdapter<NetTaskProcessBean, BaseViewHolder> {

    public TaskProcessAdapter(@Nullable List<NetTaskProcessBean> data) {
        super(R.layout.item_task_processing, data);
        addChildClickViewIds(R.id.txDelete);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, NetTaskProcessBean bean) {
        holder.setText(R.id.iconName, bean.getName());
        holder.setText(R.id.title, bean.getName());

        TextView view = holder.getView(R.id.tvHy);
        TextView tvExpired = holder.getView(R.id.tvExpired);
        holder.setText(R.id.tvHy, String.format("活跃度%s", bean.getActive()));

        if (!bean.isHistory()) {
            if ("已过期".equals(bean.getActive())) {
                view.setTextColor(Color.parseColor("#FE3919"));
            } else {
                view.setTextColor(Color.parseColor("#808080"));
            }
            tvExpired.setVisibility(View.VISIBLE);
        } else {
            tvExpired.setVisibility(View.GONE);
        }
        holder.setText(R.id.tvActiveDay, String.format("活跃度有效期:  %s天", bean.getActiveTime()));


        holder.setText(R.id.tvExpired, String.format("%s天后过期", bean.getExpireTime()));
        holder.setText(R.id.time, bean.getTime());

        TextView txSend = holder.getView(R.id.txSend);
        if ("2".equals(bean.getSource())) {
            txSend.setVisibility(View.VISIBLE);
        } else {
            txSend.setVisibility(View.GONE);
        }

        holder.setVisible(R.id.txDelete, bean.isHistory());

        NumberProgressBar progressBar = holder.getView(R.id.progressbar);
        progressBar.setMax(10000L);
        progressBar.setProgress(new BigDecimal(bean.getPercent()).multiply(new BigDecimal(100)).longValue());
        ImgLoader.display(bean.getIconUrl(), holder.getView(R.id.ivMw));


    }
}
