package com.wwsl.mdsj.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.PartnerWelfareBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author sushi
 * @description
 * @date 2020/7/21.
 */
public class PartnerWelfareAdapter extends BaseQuickAdapter<PartnerWelfareBean, BaseViewHolder> {
    public PartnerWelfareAdapter(List<PartnerWelfareBean> listData) {
        super(R.layout.item_partner_welfare, listData);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PartnerWelfareBean partnerWelfareBean) {
        ImageView avatar = baseViewHolder.getView(R.id.avatar);
        if ("1".equals(partnerWelfareBean.type)) {
            avatar.setImageResource(R.mipmap.icon_partner_qu);
        } else if ("2".equals(partnerWelfareBean.type)) {
            avatar.setImageResource(R.mipmap.icon_partner_shi);
        } else if ("3".equals(partnerWelfareBean.type)) {
            avatar.setImageResource(R.mipmap.icon_partner_sheng);
        } else {
            avatar.setImageResource(R.mipmap.icon_partner_bigqu);
        }

        baseViewHolder.setText(R.id.tv_douNum, "+" + partnerWelfareBean.price + "豆丁")
                .setText(R.id.tv_time, partnerWelfareBean.profit_time)
                .setText(R.id.tv_type, partnerWelfareBean.area_name);

    }
}
