package com.wwsl.mdsj.activity.maodou;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.net.MdOrderShowBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class MdOrderAdapter extends BaseQuickAdapter<MdOrderShowBean, BaseViewHolder> {

    private int grey = Color.parseColor("#B1B8B0");

    public MdOrderAdapter(@Nullable List<MdOrderShowBean> data) {
        super(R.layout.item_md_order_sale, data);
        addChildClickViewIds(R.id.btnAction1, R.id.btnAction2, R.id.btnAction3);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MdOrderShowBean bean) {

        holder.setText(R.id.title, bean.getTitle());
        holder.setText(R.id.orderNum, bean.getOrderNum());
        holder.setText(R.id.time, bean.getTime());
        holder.setText(R.id.txTotalPrice, String.format("总价:%sR", bean.getTotalPrice()));

        if (bean.getType() == MdOrderFragment.TYPE_SALE) {
            //我的卖单
            if (bean.getSubType() == MdOrderSubFragment.ORDER_PROCESSING) {
                //进行中
                TextView btnAction1 = holder.getView(R.id.btnAction1);
                TextView txStatus = holder.getView(R.id.txStatus);
                if (bean.getStatus().equals("0")){
                    btnAction1.setVisibility(View.VISIBLE);
                    btnAction1.setText("取消交易");
                    txStatus.setVisibility(View.GONE);
                }else if (bean.getStatus().equals("1")){
                    txStatus.setVisibility(View.VISIBLE);
                    btnAction1.setVisibility(View.GONE);
                    txStatus.setText(bean.getStatusStr());
                }
            } else if (bean.getSubType() == MdOrderSubFragment.ORDER_CONFIRM) {
                //已打款

                holder.setText(R.id.btnAction1, "查看凭证");
                holder.setText(R.id.btnAction2, "我要投诉");
                holder.setText(R.id.btnAction3, "确认已收款");
                holder.setVisible(R.id.btnAction1, true);
                holder.setVisible(R.id.btnAction2, true);
                holder.setVisible(R.id.btnAction3, true);
                TextView textView = holder.getView(R.id.btnAction3);
                holder.getView(R.id.txStatus).setVisibility(View.GONE);
                textView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F95921")));
                textView.setTextColor(Color.parseColor("#F95921"));
            } else if (bean.getSubType() == MdOrderSubFragment.ORDER_FINISH) {
                //已完成
                if (bean.isSuccess()) {
                    //成功交易
                    holder.getView(R.id.txStatus).setVisibility(View.GONE);
                    holder.setVisible(R.id.txReason, false);
                    holder.setText(R.id.btnAction1, "查看凭证");
                    holder.setText(R.id.btnAction2, "已经完成");
                    holder.setVisible(R.id.btnAction1, true);
                    holder.setVisible(R.id.btnAction2, true);
                    TextView view = holder.getView(R.id.btnAction2);
                    view.setBackgroundTintList(ColorStateList.valueOf(grey));
                    view.setTextColor(grey);
                } else {
                    holder.getView(R.id.txStatus).setVisibility(View.GONE);
                    holder.setVisible(R.id.btnAction1, false);
                    holder.setVisible(R.id.btnAction2, false);
                    holder.setVisible(R.id.txReason, true);
                    holder.setText(R.id.txReason, bean.getReason());
                }
            }
        } else {
            //我的订单
            if (bean.getSubType() == MdOrderSubFragment.ORDER_PROCESSING) {
                //进行中
                holder.setText(R.id.btnAction1, "上传凭证");
                holder.setText(R.id.btnAction2, "查看收款码");
                holder.setText(R.id.btnAction3, "撤单");
                holder.setVisible(R.id.btnAction1, true);
                holder.setVisible(R.id.btnAction2, true);
                holder.setVisible(R.id.btnAction3, true);
                TextView view = holder.getView(R.id.btnAction3);
                view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffdbc788")));

            } else if (bean.getSubType() == MdOrderSubFragment.ORDER_CONFIRM) {
                //已付款
                holder.setText(R.id.btnAction1, "重新上传");
                holder.setText(R.id.btnAction2, "查看收款码");
                holder.setText(R.id.btnAction3, "等待确认中");
                holder.setVisible(R.id.btnAction1, true);
                holder.setVisible(R.id.btnAction2, true);
                holder.setVisible(R.id.btnAction3, true);
                TextView view = holder.getView(R.id.btnAction3);
                view.setBackgroundTintList(ColorStateList.valueOf(grey));
                view.setTextColor(grey);
            } else if (bean.getSubType() == MdOrderSubFragment.ORDER_FINISH) {
                //已完成
                if (bean.isSuccess()) {
                    //成功交易
                    holder.setVisible(R.id.txReason, false);
                    holder.setText(R.id.btnAction1, "查看凭证");
                    holder.setText(R.id.btnAction2, "已经完成");
                    holder.setVisible(R.id.btnAction1, true);
                    holder.setVisible(R.id.btnAction2, true);
                    TextView view = holder.getView(R.id.btnAction2);
                    view.setBackgroundTintList(ColorStateList.valueOf(grey));
                    view.setTextColor(grey);
                } else {
                    holder.setVisible(R.id.btnAction1, false);
                    holder.setVisible(R.id.btnAction2, false);
                    holder.setVisible(R.id.txReason, true);
                    holder.setText(R.id.txReason, bean.getReason());
                }
            }
        }
    }
}
