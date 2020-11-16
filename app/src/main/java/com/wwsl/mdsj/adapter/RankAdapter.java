package com.wwsl.mdsj.adapter;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.main.RankFragment;
import com.wwsl.mdsj.bean.ListBean;
import com.wwsl.mdsj.glide.ImgLoader;

import java.util.List;

public class RankAdapter extends BaseQuickAdapter<ListBean, BaseViewHolder> {

    public RankAdapter(@Nullable List<ListBean> data) {
        super(R.layout.item_rank_list, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ListBean bean) {
        int position = helper.getAdapterPosition();
        View viewItemDivider = helper.getView(R.id.viewItemDivider);
        RelativeLayout layoutListItemBg = helper.getView(R.id.layoutListItemBg);
        TextView mOrder = (TextView) helper.getView(R.id.order);
        ImageView mAvatar = (ImageView) helper.getView(R.id.avatar);
        TextView mName = (TextView) helper.getView(R.id.name);
        TextView mVotes = (TextView) helper.getView(R.id.votes);
        TextView imgLiving = (TextView) helper.getView(R.id.imgLiving);
        TextView userLevel = (TextView) helper.getView(R.id.userLevel);
        if (position == 0) {
            viewItemDivider.setVisibility(View.GONE);
            layoutListItemBg.setBackgroundResource(R.drawable.bg_white_top);
        } else if (position == getData().size() - 1) {
            layoutListItemBg.setBackgroundResource(R.drawable.bg_white_bottom);
            viewItemDivider.setVisibility(View.VISIBLE);
        } else {
            layoutListItemBg.setBackgroundColor(Color.WHITE);
            viewItemDivider.setVisibility(View.VISIBLE);
        }
        mOrder.setText(String.format("%d", position + 2));
        ImgLoader.display(bean.getAvatarThumb(), mAvatar);
        mName.setText(bean.getUserNiceName());
        mVotes.setText(bean.getTotalCoinFormat());

        switch (bean.getType()) {
            case RankFragment.RANK_STAR:
                if (bean.getIsLiving() == 1) {
                    imgLiving.setVisibility(View.VISIBLE);
                }
                mVotes.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.icon_rank_votes_heart), null, null, null);
                break;
            case RankFragment.RANK_CONTRIBUTE:
                userLevel.setText(String.valueOf(bean.getLevel()));
                userLevel.setVisibility(View.VISIBLE);
                mVotes.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.icon_rank_votes_heart), null, null, null);
                break;
            case RankFragment.RANK_MAGNATE:
                userLevel.setText(String.valueOf(bean.getLevel()));
                userLevel.setVisibility(View.VISIBLE);
                break;
            case RankFragment.RANK_GAMBLER:
                userLevel.setText(String.valueOf(bean.getLevel()));
                userLevel.setVisibility(View.VISIBLE);
                break;
        }
    }
}
