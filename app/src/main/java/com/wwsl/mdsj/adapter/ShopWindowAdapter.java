package com.wwsl.mdsj.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.LiveShopWindowBean;
import com.wwsl.mdsj.glide.ImgLoader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShopWindowAdapter extends BaseQuickAdapter<LiveShopWindowBean, BaseViewHolder> {

    public ShopWindowAdapter(@Nullable List<LiveShopWindowBean> data) {
        super(R.layout.item_shop_goods, data);
        addChildClickViewIds(R.id.btnAdd, R.id.ivCollect);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, LiveShopWindowBean bean) {


        holder.setText(R.id.title, bean.getTitle());
//        holder.setText(R.id.getMoney, bean.getGainMoney());
        holder.setText(R.id.saleNum, String.format("已售:%s", bean.getSaleNum()));
        holder.setText(R.id.tvPrice, String.format("售价:%s", bean.getPrice()));
        ImgLoader.display(bean.getThumb(), holder.getView(R.id.ivThumb));

        if (bean.getShowType() == TYPE_LIVE) {
            holder.setVisible(R.id.btnAdd, true);
            holder.setText(R.id.btnAdd, bean.isAdd() ? "下架" : "上架");
            holder.setVisible(R.id.ivCollect, false);
        } else if (bean.getShowType() == TYPE_VIDEO) {
            holder.setVisible(R.id.btnAdd, true);
            holder.setVisible(R.id.ivCollect, false);
            holder.setText(R.id.btnAdd, "添加");
        } else if (bean.getShowType() == TYPE_SHOW) {
            holder.setBackgroundResource(R.id.ivCollect, bean.isCollect() ? R.mipmap.icon_goods_collect : R.mipmap.icon_goods_uncollect);
            holder.setVisible(R.id.btnAdd, false);
            holder.setVisible(R.id.ivCollect, true);
        }

    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, LiveShopWindowBean bean, @NotNull List<?> payloads) {
        for (int i = 0; i < payloads.size(); i++) {
            int payload = ((Integer) payloads.get(i));
            if (payload == PAYLOAD_LIVE_ADD) {
                if (bean.getShowType() == TYPE_VIDEO) {
                    holder.setText(R.id.btnAdd, bean.isAdd() ? "下架" : "添加");
                } else if (bean.getShowType() == TYPE_LIVE) {
                    holder.setText(R.id.btnAdd, bean.isAdd() ? "下架" : "上架");
                }
            } else if (payload == PAYLOAD_ITEM_COLLECT) {
                holder.setBackgroundResource(R.id.ivCollect, bean.isCollect() ? R.mipmap.icon_goods_collect : R.mipmap.icon_goods_uncollect);
            }
        }
    }

    public static final int PAYLOAD_LIVE_ADD = 0;
    public static final int PAYLOAD_ITEM_COLLECT = 1;

    public static final int TYPE_SHOW = 0;//显示橱窗
    public static final int TYPE_LIVE = 1;//直播橱窗
    public static final int TYPE_VIDEO = 2;//视频橱窗

}
