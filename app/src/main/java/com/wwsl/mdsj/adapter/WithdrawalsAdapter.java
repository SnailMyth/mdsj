package com.wwsl.mdsj.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.Withdrawals;
import com.wwsl.mdsj.views.dialog.wheelpick.DatePickerUtil;

import org.jetbrains.annotations.NotNull;

public class WithdrawalsAdapter extends BaseQuickAdapter<Withdrawals, BaseViewHolder> {

    private int type;

    public WithdrawalsAdapter(int type) {
        super(R.layout.item_withdrawals);
        this.type = type;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, Withdrawals bean) {
        TextView money = holder.getView(R.id.money);
        TextView time = holder.getView(R.id.time);
        TextView status = holder.getView(R.id.status);

        if (type == 2) {
            String value = "提现" + bean.getVotes() + "魅力值,到账" + bean.getMoney() + "元";
            money.setText(value);
        } else if (type == 3) {
            String value = "提现" + bean.getVotes() + "福利,到账" + bean.getMoney() + "元";
            money.setText(value);
        } else {
            String value = bean.getVotes() + "礼物值兑换" + bean.getMoney() + "豆丁";
            money.setText(value);
        }
        try {
            time.setText(DatePickerUtil.formatDate(Long.parseLong(bean.getAddtime() + "000"), "yyyy/MM/dd HH:mm"));
        } catch (Exception e) {
            time.setText("");
        }
        status.setText(bean.getStatus() == 1 ? "已完成" : "提现中");
        status.setEnabled(bean.getStatus() != 1);
    }
}
