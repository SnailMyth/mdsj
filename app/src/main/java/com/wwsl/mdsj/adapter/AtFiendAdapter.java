package com.wwsl.mdsj.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.net.NetFriendBean;
import com.wwsl.mdsj.glide.ImgLoader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AtFiendAdapter extends BaseQuickAdapter<NetFriendBean, BaseViewHolder> {

    public AtFiendAdapter(@Nullable List<NetFriendBean> data) {
        super(R.layout.item_add_at_friend, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, NetFriendBean bean) {
        ImgLoader.displayAvatar(bean.getAvatar(), holder.getView(R.id.avatar));
        holder.setText(R.id.tvName, bean.getUsername());
    }
}
