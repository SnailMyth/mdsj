package com.wwsl.mdsj.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.FansShowBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.utils.IconUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FansAdapter extends BaseQuickAdapter<FansShowBean, BaseViewHolder> {

    public FansAdapter(@Nullable List<FansShowBean> data) {
        super(R.layout.item_attention, data);
        addChildClickViewIds(R.id.tvFocus, R.id.avatar);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, FansShowBean bean) {
        ImgLoader.displayAvatar(bean.getAvatar(), holder.getView(R.id.avatar));
        holder.setText(R.id.name, bean.getUsername());
        holder.setText(R.id.sign, bean.getSignature());
        holder.setImageResource(R.id.sex, IconUtil.getSexIcon(Integer.parseInt(bean.getSex())));
        ;
        TextView tvFocus = holder.getView(R.id.tvFocus);
        if (bean.getType() == Constants.TYPE_FANS) {
            if (bean.getAttention1() == 1 && bean.getAttention2() == 1) {
                tvFocus.setVisibility(View.VISIBLE);
                tvFocus.setText("互相关注");
                tvFocus.setBackgroundResource(R.drawable.shape_followed);
            } else {
                tvFocus.setVisibility(View.VISIBLE);
                tvFocus.setText("回关");
                tvFocus.setBackgroundResource(R.drawable.shape_follow_btn);
            }
            tvFocus.setVisibility(View.VISIBLE);
        } else if (bean.getType() == Constants.TYPE_FOLLOW) {
            tvFocus.setVisibility(View.VISIBLE);
            tvFocus.setText("已关注");
            tvFocus.setBackgroundResource(R.drawable.shape_followed);
        } else if (bean.getType() == Constants.TYPE_FRIEND) {
            tvFocus.setVisibility(View.VISIBLE);
            tvFocus.setText("互相关注");
            tvFocus.setBackgroundResource(R.drawable.shape_followed);
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, FansShowBean item, @NotNull List<?> payloads) {
        for (Object p : payloads) {
            int payload = (int) p;
            if (payload == PAYLOAD_FOLLOW) {
                TextView tvFocus = holder.getView(R.id.tvFocus);
                tvFocus.setVisibility(View.VISIBLE);
            }
        }
    }

    public static int PAYLOAD_FOLLOW = 911;
}
