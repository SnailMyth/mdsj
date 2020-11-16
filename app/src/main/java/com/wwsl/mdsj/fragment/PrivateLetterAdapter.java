package com.wwsl.mdsj.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.bean.net.NetFriendBean;
import com.wwsl.mdsj.glide.ImgLoader;

import java.util.List;

public class PrivateLetterAdapter extends BaseQuickAdapter<NetFriendBean, BaseViewHolder> {

    public PrivateLetterAdapter(@Nullable List<NetFriendBean> data) {
        super(R.layout.item_private_letter, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NetFriendBean item) {

        ImgLoader.displayAvatar(item.getAvatar(), helper.getView(R.id.iv_head));

        helper.setText(R.id.tv_nickname, item.getUsername());
    }
}
