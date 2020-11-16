package com.wwsl.mdsj.adapter.maodou;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.maodou.FamilyActiveBean;
import com.wwsl.mdsj.bean.maodou.PackageDetailBean;
import com.wwsl.mdsj.glide.ImgLoader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PackageDetailAdapter extends BaseQuickAdapter<PackageDetailBean, BaseViewHolder> {

    public PackageDetailAdapter(@Nullable List<PackageDetailBean> data) {
        super(R.layout.item_package_detail, data);
        addChildClickViewIds(R.id.ivCall);
    }


    @Override
    protected void convert(@NotNull BaseViewHolder holder, PackageDetailBean bean) {
        holder.setText(R.id.title, bean.getTitle());
        holder.setText(R.id.time, bean.getTime());
        holder.setImageResource(R.id.ivIcon, bean.getType() == 0 ? R.mipmap.icon_img_md : R.mipmap.icon_img_dd);
        TextView view = holder.getView(R.id.res);
        String prefix = "-";
        if (bean.isIncome()) {
            prefix = "+";
            view.setTextColor(Color.parseColor("#ff8cdb88"));
        }
        holder.setText(R.id.res, String.format("%s%s%s", prefix, bean.getNum(), bean.getPer()));
    }

}
