package com.wwsl.mdsj.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.CoinBean;

import java.util.List;

public class ChargeItemAdapter extends BaseQuickAdapter<CoinBean, BaseViewHolder> {
    private int page;

    public ChargeItemAdapter(@Nullable List<CoinBean> data,int page) {
        super(R.layout.item_charge_item, data);
        this.page = page;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CoinBean item) {
        helper.setText(R.id.tvCoin, item.getCoin());
        helper.setText(R.id.tvMoney, String.format("￥%s", item.getMoney()));
        View view = helper.getView(R.id.itemRoot);
        view.setBackgroundResource(item.isSelect() ? R.drawable.charge_item_selected : R.drawable.charge_item_normal);
    }

    public int getPage() {
        return page;
    }
}
