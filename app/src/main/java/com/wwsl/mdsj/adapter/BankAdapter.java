package com.wwsl.mdsj.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.net.NetBankCardBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BankAdapter extends BaseQuickAdapter<NetBankCardBean, BaseViewHolder> {

    public BankAdapter(@Nullable List<NetBankCardBean> data) {
        super(R.layout.item_bank_card, data);
        addChildClickViewIds(R.id.txDelete);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, NetBankCardBean bean) {
        holder.setText(R.id.bankName, bean.getBankName());
        holder.setText(R.id.realName, bean.getRealName());
        String cardNumber = bean.getCardNumber();
        holder.setText(R.id.tvCard1, cardNumber.substring(0, 4));
        holder.setText(R.id.tvCard2, cardNumber.substring(cardNumber.length() - 5));
        holder.setVisible(R.id.txDelete, bean.isShowManager());
    }

}
