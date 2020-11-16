package com.wwsl.mdsj.adapter.maodou;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.maodou.FamilyActiveBean;
import com.wwsl.mdsj.bean.maodou.MwTaskBean;
import com.wwsl.mdsj.glide.ImgLoader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FamilyActiveAdapter extends BaseQuickAdapter<FamilyActiveBean, BaseViewHolder> {

    public FamilyActiveAdapter(@Nullable List<FamilyActiveBean> data) {
        super(R.layout.item_family_active, data);
        addChildClickViewIds(R.id.ivCall);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, FamilyActiveBean bean) {
        holder.setText(R.id.tvName, bean.getName());
        holder.setText(R.id.singleActive, bean.getSingleActive());
        holder.setText(R.id.time, bean.getTime());
        holder.setText(R.id.allActive, bean.getAllActive());
        holder.setText(R.id.tvNum, String.format("%s/%s", bean.getTotalNum(), bean.getActiveNum()));
        ImgLoader.displayAvatar(bean.getAvatar(), holder.getView(R.id.ivAvatar));
    }

}
