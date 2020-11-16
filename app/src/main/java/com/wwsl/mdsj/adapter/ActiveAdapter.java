package com.wwsl.mdsj.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.ActiveShowBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ActiveAdapter extends BaseQuickAdapter<ActiveShowBean, BaseViewHolder> {

    public ActiveAdapter(@Nullable List<ActiveShowBean> data) {
        super(R.layout.item_active, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ActiveShowBean bean) {
        holder.setText(R.id.tvDes, bean.getDes());
        holder.setText(R.id.tvTime, bean.getTime());
        holder.setText(R.id.tvStatus, bean.getStatus());
        TextView view = holder.getView(R.id.tvNum);
        view.setText(bean.getChangeNum());

        int color = Color.WHITE;
        if (bean.getAction().equals("setauth")||bean.getAction().equals("watchvideofuli")) {
            color = Color.parseColor("#21F945");
        } else if (bean.getAction().equals("transfer_maodou")) {
            color = Color.RED;
        } else {
            if (bean.getChangeNum().startsWith("-")) {
                color = Color.parseColor("#F95921");
            }
        }
        view.setTextColor(color);

    }
}
